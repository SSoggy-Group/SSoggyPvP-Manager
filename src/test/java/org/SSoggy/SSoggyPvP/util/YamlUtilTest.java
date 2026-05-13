package org.SSoggy.SSoggyPvP.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class YamlUtilTest {

    private File dataFolder;

    @BeforeEach
    public void setUp() throws IOException {
        dataFolder = Files.createTempDirectory("ssoggypvp_test").toFile();
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (dataFolder != null && dataFolder.exists()) {
            Files.walk(dataFolder.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void testLoadSection_NullDataFolder() {
        assertNull(YamlUtil.loadSection(null, "config.yml", "testSection"));
    }

    @Test
    public void testLoadSection_NullFilename() {
        assertNull(YamlUtil.loadSection(dataFolder, null, "testSection"));
    }

    @Test
    public void testLoadSection_PathTraversal() {
        assertNull(YamlUtil.loadSection(dataFolder, "../config.yml", "testSection"));
        assertNull(YamlUtil.loadSection(dataFolder, "../../etc/passwd", "testSection"));
    }

    @Test
    public void testLoadSection_FileDoesNotExist() {
        assertNull(YamlUtil.loadSection(dataFolder, "doesnotexist.yml", "testSection"));
    }

    @Test
    public void testLoadSection_SectionMissing() throws IOException {
        File f = new File(dataFolder, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("otherSection.key", "value");
        config.save(f);

        assertNull(YamlUtil.loadSection(dataFolder, "config.yml", "testSection"));
    }

    @Test
    public void testLoadSection_SectionExists() throws IOException {
        File f = new File(dataFolder, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("testSection.key", "value");
        config.save(f);

        ConfigurationSection section = YamlUtil.loadSection(dataFolder, "config.yml", "testSection");
        assertNotNull(section);
        assertEquals("value", section.getString("key"));
    }
}
