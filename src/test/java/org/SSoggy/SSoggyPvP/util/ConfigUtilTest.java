package org.SSoggy.SSoggyPvP.util;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigUtilTest {

    @Test
    void testGetWandMaterial_ValidMaterial() {
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getString("zone-wand-material")).thenReturn("STICK");

        Material material = ConfigUtil.getWandMaterial(config);

        assertEquals(Material.STICK, material);
    }

    @Test
    void testGetWandMaterial_InvalidMaterial() {
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getString("zone-wand-material")).thenReturn("INVALID_MAT");

        Material material = ConfigUtil.getWandMaterial(config);

        assertEquals(Material.BLAZE_ROD, material);
    }

    @Test
    void testGetWandMaterial_NullMaterial() {
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getString("zone-wand-material")).thenReturn(null);

        Material material = ConfigUtil.getWandMaterial(config);

        assertEquals(Material.BLAZE_ROD, material);
    }

    @Test
    void testGetWandMaterial_LowerCaseMaterial() {
        FileConfiguration config = mock(FileConfiguration.class);
        when(config.getString("zone-wand-material")).thenReturn("stick");

        Material material = ConfigUtil.getWandMaterial(config);

        assertEquals(Material.STICK, material);
    }
}
