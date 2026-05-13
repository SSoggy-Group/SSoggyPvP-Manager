package org.SSoggy.SSoggyPvP.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

class YamlUtilTest {

    @Test
    void testPathTraversal_loadSection(@TempDir File dataFolder) {
        // Path traversal should be blocked and return null
        assertNull(YamlUtil.loadSection(dataFolder, "../config.yml", "test"));
        assertNull(YamlUtil.loadSection(dataFolder, "../../etc/passwd", "test"));
        assertNull(YamlUtil.loadSection(dataFolder, "nested/../../config.yml", "test"));
    }

    @Test
    void testPathTraversal_saveConfig(@TempDir File dataFolder) {
        YamlConfiguration config = new YamlConfiguration();
        Logger logger = Mockito.mock(Logger.class);

        // Check save config using invalid paths.
        // It shouldn't create a file outside the dataFolder
        YamlUtil.saveConfig(config, dataFolder, "../config.yml", logger, "Error");
        File parentDirFile = new File(dataFolder.getParentFile(), "config.yml");
        assertFalse(parentDirFile.exists(), "File should not be created outside data folder");
        verify(logger).log(Level.WARNING, "Blocked potential path traversal attempt while saving: {0}", "../config.yml");

        YamlUtil.saveConfig(config, dataFolder, "../../etc/passwd", logger, "Error");
        verify(logger).log(Level.WARNING, "Blocked potential path traversal attempt while saving: {0}", "../../etc/passwd");

        YamlUtil.saveConfig(config, dataFolder, "nested/../../config.yml", logger, "Error");
        verify(logger).log(Level.WARNING, "Blocked potential path traversal attempt while saving: {0}", "nested/../../config.yml");
    }

    @Test
    void testValidFile_loadSection(@TempDir File dataFolder) throws IOException {
        File file = new File(dataFolder, "config.yml");
        file.createNewFile();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("test.key", "value");
        config.save(file);

        assertNotNull(YamlUtil.loadSection(dataFolder, "config.yml", "test"));
    }

    @Test
    void testValidFile_loadSection_nulls() {
        assertNull(YamlUtil.loadSection(null, "config.yml", "test"));
        assertNull(YamlUtil.loadSection(new File("."), null, "test"));
    }

    @Test
    void testValidFile_saveConfig(@TempDir File dataFolder) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("test.key", "value");
        Logger logger = Mockito.mock(Logger.class);

        YamlUtil.saveConfig(config, dataFolder, "config.yml", logger, "Error");

        File file = new File(dataFolder, "config.yml");
        assertTrue(file.exists());
    }

    @Test
    void testValidFile_saveConfig_nulls() {
        YamlConfiguration config = new YamlConfiguration();
        Logger logger = Mockito.mock(Logger.class);

        YamlUtil.saveConfig(config, null, "config.yml", logger, "Error");
        verify(logger).log(Level.WARNING, "Blocked potential path traversal attempt while saving: {0}", "config.yml");

        YamlUtil.saveConfig(config, new File("."), null, logger, "Error");
        verify(logger).log(Level.WARNING, "Blocked potential path traversal attempt while saving: {0}", (Object) null);
    }
}
