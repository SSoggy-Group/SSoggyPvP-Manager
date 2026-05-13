package org.SSoggy.SSoggyPvP.manager;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.model.PlayerData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PvPManagerTest {

    private PvPTogglePlugin pluginMock;
    private FileConfiguration configMock;
    private PvPManager pvpManager;

    @BeforeEach
    void setUp() {
        pluginMock = mock(PvPTogglePlugin.class);
        configMock = mock(FileConfiguration.class);

        when(pluginMock.getConfig()).thenReturn(configMock);
        when(configMock.getBoolean("debug", false)).thenReturn(false);

        pvpManager = new PvPManager(pluginMock);
    }

    @Test
    void testGetPlayerData_NewPlayer_DefaultPvPTrue() {
        UUID playerUuid = UUID.randomUUID();
        when(configMock.getBoolean("default-pvp-state", false)).thenReturn(true);

        PlayerData data = pvpManager.getPlayerData(playerUuid);

        assertNotNull(data);
        assertTrue(data.isPvpEnabled());

        // Verify it was called
        verify(configMock).getBoolean("default-pvp-state", false);
    }

    @Test
    void testGetPlayerData_NewPlayer_DefaultPvPFalse() {
        UUID playerUuid = UUID.randomUUID();
        when(configMock.getBoolean("default-pvp-state", false)).thenReturn(false);

        PlayerData data = pvpManager.getPlayerData(playerUuid);

        assertNotNull(data);
        assertFalse(data.isPvpEnabled());

        // Verify it was called
        verify(configMock).getBoolean("default-pvp-state", false);
    }

    @Test
    void testGetPlayerData_ExistingPlayer() {
        UUID playerUuid = UUID.randomUUID();
        when(configMock.getBoolean("default-pvp-state", false)).thenReturn(true);

        // First call creates the data
        PlayerData data1 = pvpManager.getPlayerData(playerUuid);
        assertNotNull(data1);
        assertTrue(data1.isPvpEnabled());

        // Change config mock just to be sure it's not checked again
        when(configMock.getBoolean("default-pvp-state", false)).thenReturn(false);

        // Second call should return the exact same object from the map
        PlayerData data2 = pvpManager.getPlayerData(playerUuid);
        assertSame(data1, data2);
        assertTrue(data2.isPvpEnabled());

        // Verify config was checked exactly once for default-pvp-state
        verify(configMock, times(1)).getBoolean("default-pvp-state", false);
    }
}
