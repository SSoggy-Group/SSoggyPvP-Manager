package org.SSoggy.SSoggyPvP.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PvPZoneTest {

    private PvPZone zone;
    private final String worldName = "test_world";
    private final String zoneName = "test_zone";

    @BeforeEach
    void setUp() {
        // (10, 64, 10) to (20, 100, 20)
        PvPZone.Corners corners = new PvPZone.Corners(10, 64, 10, 20, 100, 20);
        zone = new PvPZone(zoneName, worldName, corners);
    }

    @Test
    void testCornersRecord() {
        PvPZone.Corners corners = new PvPZone.Corners(1, 2, 3, 4, 5, 6);
        assertEquals(1, corners.x1());
        assertEquals(2, corners.y1());
        assertEquals(3, corners.z1());
        assertEquals(4, corners.x2());
        assertEquals(5, corners.y2());
        assertEquals(6, corners.z2());
    }

    @Test
    void testConstructorNormalizesCoordinates() {
        // Swap corners: (20, 100, 20) to (10, 64, 10)
        PvPZone.Corners corners = new PvPZone.Corners(20, 100, 20, 10, 64, 10);
        PvPZone swappedZone = new PvPZone(zoneName, worldName, corners);

        assertEquals(10, swappedZone.getX1());
        assertEquals(64, swappedZone.getY1());
        assertEquals(10, swappedZone.getZ1());
        assertEquals(20, swappedZone.getX2());
        assertEquals(100, swappedZone.getY2());
        assertEquals(20, swappedZone.getZ2());
    }

    @Test
    void testGetName() {
        assertEquals(zoneName, zone.getName());
    }

    @Test
    void testGetWorldName() {
        assertEquals(worldName, zone.getWorldName());
    }

    @Test
    void testContainsInside() {
        Location loc = mock(Location.class);
        World world = mock(World.class);
        when(loc.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn(worldName);
        when(loc.getBlockX()).thenReturn(15);
        when(loc.getBlockY()).thenReturn(80);
        when(loc.getBlockZ()).thenReturn(15);

        assertTrue(zone.contains(loc));
    }

    @Test
    void testContainsOnEdge() {
        Location loc = mock(Location.class);
        World world = mock(World.class);
        when(loc.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn(worldName);
        when(loc.getBlockX()).thenReturn(10);
        when(loc.getBlockY()).thenReturn(64);
        when(loc.getBlockZ()).thenReturn(10);

        assertTrue(zone.contains(loc));
    }

    @Test
    void testContainsOutsideX() {
        Location loc = mock(Location.class);
        World world = mock(World.class);
        when(loc.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn(worldName);
        when(loc.getBlockX()).thenReturn(9);
        when(loc.getBlockY()).thenReturn(80);
        when(loc.getBlockZ()).thenReturn(15);

        assertFalse(zone.contains(loc));
    }

    @Test
    void testContainsWrongWorld() {
        Location loc = mock(Location.class);
        World world = mock(World.class);
        when(loc.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("other_world");
        when(loc.getBlockX()).thenReturn(15);
        when(loc.getBlockY()).thenReturn(80);
        when(loc.getBlockZ()).thenReturn(15);

        assertFalse(zone.contains(loc));
    }

    @Test
    void testContainsNull() {
        assertFalse(zone.contains(null));
    }
}
