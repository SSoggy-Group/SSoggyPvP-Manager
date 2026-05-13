package org.SSoggy.SSoggyPvP.listener;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.manager.PvPManager;
import org.SSoggy.SSoggyPvP.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PlayerListenerTest {

    private PlayerListener listener;
    private PvPTogglePlugin plugin;
    private PvPManager pvpManager;
    private Player player;
    private PlayerData playerData;
    private MockedStatic<Bukkit> mockedBukkit;
    private BukkitScheduler scheduler;
    private Server server;
    private UUID uuid;

    @BeforeEach
    void setUp() {
        plugin = mock(PvPTogglePlugin.class);
        pvpManager = mock(PvPManager.class);
        player = mock(Player.class);
        scheduler = mock(BukkitScheduler.class);
        server = mock(Server.class);

        uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn("TestPlayer");

        playerData = new PlayerData();
        when(plugin.getPvPManager()).thenReturn(pvpManager);
        when(pvpManager.getPlayerData(uuid)).thenReturn(playerData);

        listener = new PlayerListener(plugin);

        mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
    }

    @AfterEach
    void tearDown() {
        if (mockedBukkit != null) {
            mockedBukkit.close();
        }
    }

    @Test
    void testOnPlayerJoinNoDebt() {
        playerData.setPvpDebtSeconds(0);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "Joined");

        listener.onPlayerJoin(event);

        verify(pvpManager).getPlayerData(uuid);
        verifyNoInteractions(scheduler);
    }

    @Test
    void testOnPlayerJoinWithDebtAndBypass() {
        playerData.setPvpDebtSeconds(100);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(true);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "Joined");

        listener.onPlayerJoin(event);

        verify(pvpManager).getPlayerData(uuid);
        verifyNoInteractions(scheduler);
    }

    @Test
    void testOnPlayerJoinWithDebt() {
        playerData.setPvpDebtSeconds(100);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(false);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "Joined");

        listener.onPlayerJoin(event);

        verify(pvpManager).getPlayerData(uuid);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskLater(eq(plugin), runnableCaptor.capture(), eq(40L));

        Runnable task = runnableCaptor.getValue();

        // Test when player is offline
        when(player.isOnline()).thenReturn(false);
        task.run();
        verify(player, never()).sendMessage(anyString());

        // Test when player is online
        when(player.isOnline()).thenReturn(true);
        // MessageUtil uses Spigot API inside, we can't fully test string formatting due to ChatColor, but let's test just the fact it calls something or we can mock MessageUtil?
        // Wait, MessageUtil.send takes sender, string and uses sender.sendMessage
        // We can just verify sendMessage on player

        task.run();
        // sendMessage uses ChatColor.translateAlternateColorCodes
        verify(player).sendMessage(anyString());
    }

    @Test
    void testOnPlayerQuit() {
        PlayerQuitEvent event = new PlayerQuitEvent(player, "Quit");

        listener.onPlayerQuit(event);

        verify(pvpManager).requestSave();
    }
}
