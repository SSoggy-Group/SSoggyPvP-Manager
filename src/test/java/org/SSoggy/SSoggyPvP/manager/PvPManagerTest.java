package org.SSoggy.SSoggyPvP.manager;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.model.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PvPManagerTest {

    private PvPTogglePlugin plugin;
    private PvPManager pvpManager;
    private Logger logger;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        plugin = mock(PvPTogglePlugin.class);
        logger = mock(Logger.class);
        FileConfiguration config = mock(FileConfiguration.class);

        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getLogger()).thenReturn(logger);
        when(plugin.getDataFolder()).thenReturn(tempDir);

        pvpManager = new PvPManager(plugin);
    }

    @Test
    void testLoadDataInvalidUuid() throws IOException {
        File dataFile = new File(tempDir, "playerdata.yml");
        String yamlContent = "players:\n" +
                "  invalid-uuid-format:\n" +
                "    pvp-enabled: true\n" +
                "    total-playtime-seconds: 100\n" +
                "    processed-cycles: 1\n" +
                "    pvp-debt-seconds: 0\n" +
                "  " + UUID.randomUUID().toString() + ":\n" +
                "    pvp-enabled: false\n" +
                "    total-playtime-seconds: 50\n" +
                "    processed-cycles: 0\n" +
                "    pvp-debt-seconds: 10\n";
        Files.writeString(dataFile.toPath(), yamlContent);

        pvpManager.loadData();

        // Verify invalid UUID was skipped (1 valid UUID loaded)
        assertEquals(1, pvpManager.getAllPlayerData().size());

        // Verify logger was called for invalid UUID
        verify(logger).log(eq(java.util.logging.Level.WARNING),
                eq("Skipping invalid UUID in playerdata.yml: {0}"),
                eq("invalid-uuid-format"));
    }
}
