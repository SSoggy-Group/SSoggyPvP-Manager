package org.SSoggy.SSoggyPvP.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.model.PvPZone;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ZoneManagerTest {

    @Mock
    private PvPTogglePlugin plugin;

    private ZoneManager zoneManager;
    private MockedStatic<Bukkit> mockedBukkit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedBukkit = mockStatic(Bukkit.class);
        Server mockServer = mock(Server.class);
        BukkitScheduler mockScheduler = mock(BukkitScheduler.class);

        when(mockServer.getScheduler()).thenReturn(mockScheduler);
        mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
        mockedBukkit.when(Bukkit::getScheduler).thenReturn(mockScheduler);

        zoneManager = new ZoneManager(plugin);
    }

    @AfterEach
    void tearDown() {
        mockedBukkit.close();
    }

    @Test
    void testIsInForcedPvPZone_NullLocation() {
        assertFalse(zoneManager.isInForcedPvPZone(null));
    }

    @Test
    void testIsInForcedPvPZone_NullWorld() {
        Location loc = new Location(null, 10, 10, 10);
        assertFalse(zoneManager.isInForcedPvPZone(loc));
    }

    @Test
    void testIsInForcedPvPZone_EmptyZones() {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        Location loc = new Location(world, 10, 10, 10);

        assertFalse(zoneManager.isInForcedPvPZone(loc));
    }

    @Test
    void testIsInForcedPvPZone_InZone() {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        UUID playerId = UUID.randomUUID();
        zoneManager.setPosition(playerId, 0, new Location(world, 0, 0, 0));
        zoneManager.setPosition(playerId, 1, new Location(world, 20, 20, 20));

        assertTrue(zoneManager.createZone("testZone", playerId));

        Location inside = new Location(world, 10, 10, 10);
        assertTrue(zoneManager.isInForcedPvPZone(inside));
    }

    @Test
    void testIsInForcedPvPZone_OutsideZone() {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        UUID playerId = UUID.randomUUID();
        zoneManager.setPosition(playerId, 0, new Location(world, 0, 0, 0));
        zoneManager.setPosition(playerId, 1, new Location(world, 20, 20, 20));

        assertTrue(zoneManager.createZone("testZone", playerId));

        Location outside = new Location(world, 30, 30, 30);
        assertFalse(zoneManager.isInForcedPvPZone(outside));
    }

    @Test
    void testIsInForcedPvPZone_DifferentWorld() {
        World world1 = mock(World.class);
        when(world1.getName()).thenReturn("world");

        World world2 = mock(World.class);
        when(world2.getName()).thenReturn("world_nether");

        UUID playerId = UUID.randomUUID();
        zoneManager.setPosition(playerId, 0, new Location(world1, 0, 0, 0));
        zoneManager.setPosition(playerId, 1, new Location(world1, 20, 20, 20));

        assertTrue(zoneManager.createZone("testZone", playerId));

        Location otherWorldLoc = new Location(world2, 10, 10, 10);
        assertFalse(zoneManager.isInForcedPvPZone(otherWorldLoc));
    }
}
