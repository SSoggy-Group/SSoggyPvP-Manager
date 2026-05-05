package org.SSoggy.SSoggyPvP.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PvPZoneTest {

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
        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);
        Location loc = new Location(world, 15, 80, 15);

        assertTrue(zone.contains(loc));
    }

    @Test
    void testContainsOnEdge() {
        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);

        assertTrue(zone.contains(new Location(world, 10, 64, 10)), "Should be true: Min corner");
        assertTrue(zone.contains(new Location(world, 20, 100, 20)), "Should be true: Max corner");
    }

    @Test
    void testContainsOutside() {
        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);

        assertFalse(zone.contains(new Location(world, 9, 80, 15)), "Should be false: X below min");
        assertFalse(zone.contains(new Location(world, 21, 80, 15)), "Should be false: X above max");
        assertFalse(zone.contains(new Location(world, 15, 63, 15)), "Should be false: Y below min");
        assertFalse(zone.contains(new Location(world, 15, 101, 15)), "Should be false: Y above max");
        assertFalse(zone.contains(new Location(world, 15, 80, 9)), "Should be false: Z below min");
        assertFalse(zone.contains(new Location(world, 15, 80, 21)), "Should be false: Z above max");
    }

    @Test
    void testContainsWrongWorld() {
        World world = mock(World.class);
        when(world.getName()).thenReturn("other_world");
        Location loc = new Location(world, 15, 80, 15);

        assertFalse(zone.contains(loc));
    }

    @Test
    void testContainsNullWorld() {
        Location loc = new Location(null, 15, 80, 15);
        assertFalse(zone.contains(loc));
    }

    @Test
    void testContainsNull() {
        assertFalse(zone.contains(null));
    }
}
