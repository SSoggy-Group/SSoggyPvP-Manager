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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerListenerTest {

    @Mock private PvPTogglePlugin plugin;
    @Mock private PvPManager pvpManager;
    @Mock private Player player;
    @Mock private Server server;
    @Mock private BukkitScheduler scheduler;
    @Mock private Player.Spigot spigotMock;

    private PlayerListener listener;
    private PlayerData playerData;

    @BeforeEach
    void setUp() {
        when(server.getLogger()).thenReturn(Logger.getLogger("Minecraft"));
        Bukkit.setServer(server);
        lenient().when(server.getScheduler()).thenReturn(scheduler);

        listener = new PlayerListener(plugin);
        playerData = new PlayerData();

        // leniency for when not needed
        lenient().when(plugin.getPvPManager()).thenReturn(pvpManager);
        lenient().when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        lenient().when(pvpManager.getPlayerData(any(UUID.class))).thenReturn(playerData);
    }

    @AfterEach
    void tearDown() throws Exception {
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, null);
    }

    @Test
    void testOnPlayerJoin_NoDebt() {
        playerData.setPvpDebtSeconds(0);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "joined");
        listener.onPlayerJoin(event);

        verify(scheduler, never()).runTaskLater(any(org.bukkit.plugin.Plugin.class), any(Runnable.class), anyLong());
    }

    @Test
    void testOnPlayerJoin_WithDebt_Bypass() {
        playerData.setPvpDebtSeconds(100);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(true);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "joined");
        listener.onPlayerJoin(event);

        verify(scheduler, never()).runTaskLater(any(org.bukkit.plugin.Plugin.class), any(Runnable.class), anyLong());
    }

    @Test
    void testOnPlayerJoin_WithDebt_NoBypass_Online() {
        playerData.setPvpDebtSeconds(100);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(false);
        when(player.isOnline()).thenReturn(true);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "joined");
        listener.onPlayerJoin(event);

        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskLater(eq(plugin), taskCaptor.capture(), eq(40L));

        taskCaptor.getValue().run();

        verify(player).sendMessage(anyString());
        // Could also verify the exact string if we want, ignoring ChatColor translation
    }

    @Test
    void testOnPlayerJoin_WithDebt_NoBypass_Offline() {
        playerData.setPvpDebtSeconds(100);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(false);
        when(player.isOnline()).thenReturn(false);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "joined");
        listener.onPlayerJoin(event);

        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskLater(eq(plugin), taskCaptor.capture(), eq(40L));

        taskCaptor.getValue().run();

        // Should not send message if offline
        verify(player, never()).sendMessage(anyString());
        verify(player, never()).spigot(); // just in case
    }

    @Test
    void testOnPlayerQuit() {
        PlayerQuitEvent event = new PlayerQuitEvent(player, "quit");
        listener.onPlayerQuit(event);

        verify(pvpManager).requestSave();
    }
}
