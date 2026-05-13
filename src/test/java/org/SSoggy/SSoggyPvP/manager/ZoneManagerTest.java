package org.SSoggy.SSoggyPvP.manager;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.model.PvPZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ZoneManagerTest {

    private PvPTogglePlugin pluginMock;
    private ZoneManager zoneManager;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        pluginMock = mock(PvPTogglePlugin.class);
        when(pluginMock.getLogger()).thenReturn(Logger.getLogger("PvPTest"));
        zoneManager = new ZoneManager(pluginMock);
        playerUUID = UUID.randomUUID();
    }

    @Test
    void testCreateZoneNullSelection() {
        // selections map is empty
        assertFalse(zoneManager.createZone("zone1", playerUUID));
    }

    @Test
    void testCreateZoneMissingFirstLocation() {
        Location loc2 = mock(Location.class);
        zoneManager.setPosition(playerUUID, 1, loc2);
        assertFalse(zoneManager.createZone("zone2", playerUUID));
    }

    @Test
    void testCreateZoneMissingSecondLocation() {
        Location loc1 = mock(Location.class);
        zoneManager.setPosition(playerUUID, 0, loc1);
        assertFalse(zoneManager.createZone("zone3", playerUUID));
    }

    @Test
    void testCreateZoneNullWorldFirstLocation() {
        Location loc1 = mock(Location.class);
        Location loc2 = mock(Location.class);
        when(loc1.getWorld()).thenReturn(null);
        when(loc2.getWorld()).thenReturn(mock(World.class));
        when(loc1.clone()).thenReturn(loc1);
        when(loc2.clone()).thenReturn(loc2);

        zoneManager.setPosition(playerUUID, 0, loc1);
        zoneManager.setPosition(playerUUID, 1, loc2);

        assertFalse(zoneManager.createZone("zone4", playerUUID));
    }

    @Test
    void testCreateZoneNullWorldSecondLocation() {
        Location loc1 = mock(Location.class);
        Location loc2 = mock(Location.class);
        when(loc1.getWorld()).thenReturn(mock(World.class));
        when(loc2.getWorld()).thenReturn(null);
        when(loc1.clone()).thenReturn(loc1);
        when(loc2.clone()).thenReturn(loc2);

        zoneManager.setPosition(playerUUID, 0, loc1);
        zoneManager.setPosition(playerUUID, 1, loc2);

        assertFalse(zoneManager.createZone("zone5", playerUUID));
    }

    @Test
    void testCreateZoneMismatchedWorlds() {
        World world1 = mock(World.class);
        when(world1.getName()).thenReturn("world1");
        World world2 = mock(World.class);
        when(world2.getName()).thenReturn("world2");

        Location loc1 = mock(Location.class);
        Location loc2 = mock(Location.class);
        when(loc1.getWorld()).thenReturn(world1);
        when(loc2.getWorld()).thenReturn(world2);
        when(loc1.clone()).thenReturn(loc1);
        when(loc2.clone()).thenReturn(loc2);

        zoneManager.setPosition(playerUUID, 0, loc1);
        zoneManager.setPosition(playerUUID, 1, loc2);

        assertFalse(zoneManager.createZone("zone6", playerUUID));
    }

    @Test
    void testCreateZoneSuccess() {
        World world = mock(World.class);
        when(world.getName()).thenReturn("test_world");

        Location loc1 = mock(Location.class);
        when(loc1.getWorld()).thenReturn(world);
        when(loc1.getBlockX()).thenReturn(10);
        when(loc1.getBlockY()).thenReturn(64);
        when(loc1.getBlockZ()).thenReturn(10);
        when(loc1.clone()).thenReturn(loc1);

        Location loc2 = mock(Location.class);
        when(loc2.getWorld()).thenReturn(world);
        when(loc2.getBlockX()).thenReturn(20);
        when(loc2.getBlockY()).thenReturn(100);
        when(loc2.getBlockZ()).thenReturn(20);
        when(loc2.clone()).thenReturn(loc2);

        zoneManager.setPosition(playerUUID, 0, loc1);
        zoneManager.setPosition(playerUUID, 1, loc2);

        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getScheduler).thenReturn(scheduler);

            // Do not actually run the async task
            when(scheduler.runTaskAsynchronously(any(org.bukkit.plugin.Plugin.class), any(Runnable.class)))
                    .thenReturn(null);

            boolean result = zoneManager.createZone("SuccessfulZone", playerUUID);

            assertTrue(result);
            PvPZone zone = zoneManager.getZone("successfulzone");
            assertNotNull(zone);
            assertEquals("SuccessfulZone", zone.getName());
            assertEquals("test_world", zone.getWorldName());
            assertEquals(10, zone.getX1());
            assertEquals(20, zone.getX2());

            verify(scheduler, times(1)).runTaskAsynchronously(eq(pluginMock), any(Runnable.class));
        }
    }
}
