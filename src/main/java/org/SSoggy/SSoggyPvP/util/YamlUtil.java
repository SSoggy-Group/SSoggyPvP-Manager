package org.SSoggy.SSoggyPvP.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public final class YamlUtil {

    private static final Logger LOGGER = Logger.getLogger(YamlUtil.class.getName());

    private YamlUtil() {}

    private static File getSafeFile(File dataFolder, String filename) {
        if (dataFolder == null || filename == null) return null;
        try {
            File file = new File(dataFolder, filename).getCanonicalFile();
            File folder = dataFolder.getCanonicalFile();
            if (file.toPath().startsWith(folder.toPath())) {
                return file;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public static ConfigurationSection loadSection(File dataFolder, String filename, String sectionKey) {
        File file = getSafeFile(dataFolder, filename);
        if (file == null) {
            LOGGER.log(Level.WARNING, "Blocked potential path traversal attempt: {0}", filename);
            return null;
        }

        if (!file.exists()) {
            // file doesn't exist - expected on first run
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection(sectionKey);
        
        if (section == null) {
            // file exists but section is missing - log for debugging
            LOGGER.log(
                Level.FINE, 
                "Configuration section ''{0}'' not found in {1}", 
                new Object[]{sectionKey, filename}
            );
        }
        
        return section;
    }

    public static void saveConfig(YamlConfiguration config, File dataFolder, String filename, 
                                   Logger logger, String errorMessage) {
        File file = getSafeFile(dataFolder, filename);
        if (file == null) {
            logger.log(Level.WARNING, "Blocked potential path traversal attempt while saving: {0}", filename);
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, errorMessage, e);
        }
    }
}
