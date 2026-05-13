package org.SSoggy.SSoggyPvP.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.manager.PlaytimeManager;
import org.SSoggy.SSoggyPvP.manager.PvPManager;
import org.SSoggy.SSoggyPvP.model.PlayerData;
import org.SSoggy.SSoggyPvP.util.MessageUtil;

class PlayerListenerTest {

    private PvPTogglePlugin plugin;
    private PvPManager pvpManager;
    private PlaytimeManager playtimeManager;
    private PlayerListener listener;
    private Player player;
    private BukkitScheduler scheduler;
    private MockedStatic<Bukkit> mockedBukkit;
    private MockedStatic<MessageUtil> mockedMessageUtil;
    private final UUID playerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        plugin = mock(PvPTogglePlugin.class);
        pvpManager = mock(PvPManager.class);
        playtimeManager = mock(PlaytimeManager.class);
        player = mock(Player.class);
        scheduler = mock(BukkitScheduler.class);

        when(plugin.getPvPManager()).thenReturn(pvpManager);
        when(plugin.getPlaytimeManager()).thenReturn(playtimeManager);
        when(player.getUniqueId()).thenReturn(playerId);

        listener = new PlayerListener(plugin);

        mockedBukkit = mockStatic(Bukkit.class);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(scheduler);

        mockedMessageUtil = mockStatic(MessageUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedBukkit != null) {
            mockedBukkit.close();
        }
        if (mockedMessageUtil != null) {
            mockedMessageUtil.close();
        }
    }

    @Test
    void testOnPlayerJoin_NoDebt() {
        PlayerData data = new PlayerData();
        data.setPvpDebtSeconds(0);
        when(pvpManager.getPlayerData(playerId)).thenReturn(data);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "Joined");
        listener.onPlayerJoin(event);

        verify(scheduler, never()).runTaskLater(any(), any(Runnable.class), anyLong());
    }

    @Test
    void testOnPlayerJoin_HasDebt_HasBypass() {
        PlayerData data = new PlayerData();
        data.setPvpDebtSeconds(100);
        when(pvpManager.getPlayerData(playerId)).thenReturn(data);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(true);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "Joined");
        listener.onPlayerJoin(event);

        verify(scheduler, never()).runTaskLater(any(), any(Runnable.class), anyLong());
    }

    @Test
    void testOnPlayerJoin_HasDebt_NoBypass_PlayerOnline() {
        PlayerData data = new PlayerData();
        data.setPvpDebtSeconds(100);
        when(pvpManager.getPlayerData(playerId)).thenReturn(data);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(false);
        when(player.isOnline()).thenReturn(true);

        mockedMessageUtil.when(() -> MessageUtil.formatTime(100)).thenReturn("1m 40s");

        PlayerJoinEvent event = new PlayerJoinEvent(player, "Joined");
        listener.onPlayerJoin(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskLater(eq(plugin), runnableCaptor.capture(), eq(40L));

        // Execute the scheduled task
        runnableCaptor.getValue().run();

        mockedMessageUtil.verify(() -> MessageUtil.send(eq(player), eq("&c&l⚔ You have forced PvP time remaining: &f1m 40s")));
    }

    @Test
    void testOnPlayerJoin_HasDebt_NoBypass_PlayerOffline() {
        PlayerData data = new PlayerData();
        data.setPvpDebtSeconds(100);
        when(pvpManager.getPlayerData(playerId)).thenReturn(data);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(false);
        when(player.isOnline()).thenReturn(false);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "Joined");
        listener.onPlayerJoin(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskLater(eq(plugin), runnableCaptor.capture(), eq(40L));

        // Execute the scheduled task
        runnableCaptor.getValue().run();

        // Should not send message if offline
        mockedMessageUtil.verify(() -> MessageUtil.send(any(), any()), never());
    }

    @Test
    void testOnPlayerQuit() {
        PlayerQuitEvent event = new PlayerQuitEvent(player, "Quit");
        listener.onPlayerQuit(event);

        verify(pvpManager).requestSave();
    }
}
