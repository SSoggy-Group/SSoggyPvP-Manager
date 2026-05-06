package org.SSoggy.SSoggyPvP.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.model.PlayerData;
import org.SSoggy.SSoggyPvP.util.MessageUtil;

import java.util.UUID;
import java.util.logging.Level;

public class PlaytimeManager {

    private final PvPTogglePlugin plugin;
    private BukkitTask tickTask;
    private BukkitTask saveTask;
    
    // cached config values (updated on reload)
    private long cycleSeconds;
    private long forcedDebtSeconds;
    private boolean soloAccumulate;
    private boolean soloForced;

    public PlaytimeManager(PvPTogglePlugin plugin) {
        this.plugin = plugin;
        loadConfigValues();
    }
    
    public void loadConfigValues() {
        // Fallback hierarchy for backwards compatibility: minutes-per-cycle -> hours-per-cycle -> 1 hour
        int minutesPerCycleConfig = plugin.getConfig().getInt("playtime.minutes-per-cycle", -1);
        if (minutesPerCycleConfig == -1) {
            int hoursPerCycle = plugin.getConfig().getInt("playtime.hours-per-cycle", 1);
            if (hoursPerCycle < 1) {
                plugin.getLogger().log(Level.WARNING, "[PvPToggle] Invalid value for ''playtime.hours-per-cycle'' ({0}); using 1 instead.", hoursPerCycle);
                hoursPerCycle = 1;
            }
            this.cycleSeconds = (long) hoursPerCycle * 60 * 60;
        } else if (minutesPerCycleConfig < 1) {
            plugin.getLogger().log(Level.WARNING, "[PvPToggle] Invalid value for ''playtime.minutes-per-cycle'' ({0}); using 60 instead.", minutesPerCycleConfig);
            this.cycleSeconds = 60L * 60;
        } else {
            this.cycleSeconds = (long) minutesPerCycleConfig * 60;
        }

        // Fallback hierarchy for forced time: forced-seconds -> forced-minutes -> 20 minutes
        int forcedSecondsConfig = plugin.getConfig().getInt("playtime.forced-seconds", -1);
        if (forcedSecondsConfig == -1) {
            int forcedMinutes = plugin.getConfig().getInt("playtime.forced-minutes", 20);
            if (forcedMinutes < 0) {
                plugin.getLogger().log(Level.WARNING, "[PvPToggle] Invalid value for ''playtime.forced-minutes'' ({0}); using 20 instead.", forcedMinutes);
                forcedMinutes = 20;
            }
            this.forcedDebtSeconds = (long) forcedMinutes * 60;
        } else if (forcedSecondsConfig < 0) {
            plugin.getLogger().log(Level.WARNING, "[PvPToggle] Invalid value for ''playtime.forced-seconds'' ({0}); using 1200 instead.", forcedSecondsConfig);
            this.forcedDebtSeconds = 1200L;
        } else {
            this.forcedDebtSeconds = forcedSecondsConfig;
        }

        this.soloAccumulate = plugin.getConfig().getBoolean("playtime.solo-accumulate", true);
        this.soloForced = plugin.getConfig().getBoolean("playtime.solo-forced", false);
    }

    public void startTracking() {
        // 1 second tick
        tickTask = new BukkitRunnable() {
            @Override
            public void run() {
                updatePlayerTimesAndDebt();
            }
        }.runTaskTimer(plugin, 20L, 20L);

        // auto-save (async to avoid blocking)
        long saveIntervalTicks = plugin.getConfig().getInt("save-interval", 5) * 60L * 20L;
        saveTask = new BukkitRunnable() {
            @Override
            public void run() {
                saveDataAsync();
            }
        }.runTaskTimer(plugin, saveIntervalTicks, saveIntervalTicks);
    }
    
    private void saveDataAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPvPManager().saveData();
            plugin.getZoneManager().saveZones();
        });
    }

    public void stopTracking() {
        if (tickTask != null) tickTask.cancel();
        if (saveTask != null) saveTask.cancel();
    }
    
    @Deprecated(forRemoval = true, since = "1.0.0")
    public void cleanupPlayer(UUID playerId) {
        // no-op: throttling removed since task already runs at 1-second intervals
    }

    private void updatePlayerTimesAndDebt() {
        // cache online player count once per tick instead of reading multiple times
        int onlinePlayerCount = Bukkit.getOnlinePlayers().size();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPvPManager().getPlayerData(player.getUniqueId());

            if (soloAccumulate || onlinePlayerCount >= 2) {
                data.setTotalPlaytimeSeconds(data.getTotalPlaytimeSeconds() + 1);
            }

            checkAndApplyCycleMilestones(player, data);
            decrementPlayerDebt(player, data, onlinePlayerCount);
        }
    }

    private void checkAndApplyCycleMilestones(Player player, PlayerData data) {
        int currentCycles = (int) (data.getTotalPlaytimeSeconds() / cycleSeconds);
        if (currentCycles <= data.getProcessedCycles()) return;

        int newCycles = currentCycles - data.getProcessedCycles();
        data.setProcessedCycles(currentCycles);

        if (!player.hasPermission("pvptoggle.bypass")) {
            long additionalDebt = newCycles * forcedDebtSeconds;
            boolean alreadyInDebt = data.getPvpDebtSeconds() > 0;
            data.setPvpDebtSeconds(data.getPvpDebtSeconds() + additionalDebt);

            if (alreadyInDebt) {
                MessageUtil.send(player,
                        "&c&l⚔ Forced PvP extended! &7Duration: &f"
                                + MessageUtil.formatTime(data.getPvpDebtSeconds()));
            } else {
                MessageUtil.send(player,
                        "&c&l⚔ Forced PvP activated! &7Duration: &f"
                                + MessageUtil.formatTime(data.getPvpDebtSeconds()));
            }
        }
    }

    private void decrementPlayerDebt(Player player, PlayerData data, int onlinePlayerCount) {
        if (data.getPvpDebtSeconds() <= 0 || player.hasPermission("pvptoggle.bypass")) return;

        boolean isDecreasing = soloForced || onlinePlayerCount >= 2;

        if (isDecreasing) {
            data.setPvpDebtSeconds(data.getPvpDebtSeconds() - 1);
        }

        if (data.getPvpDebtSeconds() <= 0) {
            data.setPvpDebtSeconds(0);
            MessageUtil.send(player, "&a&l⚔ Your forced PvP period has ended!");
            MessageUtil.sendActionBar(player, "&a✓ Forced PvP ended");
        } else {
            // action bar shown once per second (task runs every 20 ticks / 1 second)
            String status = isDecreasing
                    ? "&c⚔ Forced PvP"
                    : "&e⚔ Forced PvP &7(paused — solo)";
            MessageUtil.sendActionBar(player,
                    status + " &7| &f"
                            + MessageUtil.formatTime(data.getPvpDebtSeconds())
                            + " &7remaining");
        }
    }
}
