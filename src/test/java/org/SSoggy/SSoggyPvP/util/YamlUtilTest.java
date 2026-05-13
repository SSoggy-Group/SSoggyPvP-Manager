package org.SSoggy.SSoggyPvP.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNull;

public class YamlUtilTest {

    @TempDir
    File dataFolder;

    @Test
    public void testLoadSectionPathTraversal() {
        ConfigurationSection section = YamlUtil.loadSection(dataFolder, "../config.yml", "someKey");
        assertNull(section, "Should return null for path traversal attempt");
    }

    @Test
    public void testLoadSectionNullDataFolder() {
        ConfigurationSection section = YamlUtil.loadSection(null, "config.yml", "someKey");
        assertNull(section, "Should return null for null data folder");
    }

    @Test
    public void testLoadSectionNullFilename() {
        ConfigurationSection section = YamlUtil.loadSection(dataFolder, null, "someKey");
        assertNull(section, "Should return null for null filename");
    }

    @Test
    public void testSaveConfigPathTraversal() {
        YamlConfiguration config = new YamlConfiguration();
        Logger logger = Logger.getLogger("TestLogger");
        YamlUtil.saveConfig(config, dataFolder, "../config.yml", logger, "Error");

        File file = new File(dataFolder.getParentFile(), "config.yml");
        org.junit.jupiter.api.Assertions.assertFalse(file.exists(), "Should not create file outside data folder");
    }
}
