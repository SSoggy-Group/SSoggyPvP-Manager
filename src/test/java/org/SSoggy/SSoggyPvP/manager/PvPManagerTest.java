package org.SSoggy.SSoggyPvP.manager;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.model.PlayerData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PvPManagerTest {

    @Mock private PvPTogglePlugin plugin;
    @Mock private ZoneManager zoneManager;
    @Mock private FileConfiguration config;
    @Mock private Logger logger;
    @Mock private Player player;
    @Mock private World world;

    private PvPManager pvpManager;
    private final UUID playerUUID = UUID.randomUUID();
    private Location playerLocation;

    @BeforeEach
    void setUp() {
        when(plugin.getConfig()).thenReturn(config);
        when(config.getBoolean("debug", false)).thenReturn(false);
        when(config.getBoolean("default-pvp-state", false)).thenReturn(false);
        when(plugin.getLogger()).thenReturn(logger);

        pvpManager = new PvPManager(plugin);

        // Setup player
        when(player.getUniqueId()).thenReturn(playerUUID);
        when(player.getName()).thenReturn("TestPlayer");

        // Setup simple location
        playerLocation = new Location(world, 0, 0, 0);
        lenient().when(player.getLocation()).thenReturn(playerLocation);

        // Setup plugin's zone manager
        lenient().when(plugin.getZoneManager()).thenReturn(zoneManager);
    }

    @Test
    void isEffectivePvPEnabled_AllFalse_ReturnsFalse() {
        // Default state: toggle false, not in zone, no debt
        when(zoneManager.isInForcedPvPZone(playerLocation)).thenReturn(false);

        assertFalse(pvpManager.isEffectivePvPEnabled(player));
    }

    @Test
    void isEffectivePvPEnabled_ToggleTrue_ReturnsTrue() {
        // Toggle on
        pvpManager.getPlayerData(playerUUID).setPvpEnabled(true);
        lenient().when(zoneManager.isInForcedPvPZone(playerLocation)).thenReturn(false);

        assertTrue(pvpManager.isEffectivePvPEnabled(player));
    }

    @Test
    void isEffectivePvPEnabled_InZone_ReturnsTrue() {
        // Toggle off, but in zone
        when(zoneManager.isInForcedPvPZone(playerLocation)).thenReturn(true);

        assertTrue(pvpManager.isEffectivePvPEnabled(player));
    }

    @Test
    void isEffectivePvPEnabled_HasDebt_ReturnsTrue() {
        // Toggle off, not in zone, but has debt
        pvpManager.getPlayerData(playerUUID).setPvpDebtSeconds(100);
        lenient().when(zoneManager.isInForcedPvPZone(playerLocation)).thenReturn(false);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(false);

        assertTrue(pvpManager.isEffectivePvPEnabled(player));
    }

    @Test
    void isEffectivePvPEnabled_HasDebtWithBypass_ReturnsFalse() {
        // Toggle off, not in zone, has debt but has bypass permission
        pvpManager.getPlayerData(playerUUID).setPvpDebtSeconds(100);
        when(zoneManager.isInForcedPvPZone(playerLocation)).thenReturn(false);
        when(player.hasPermission("pvptoggle.bypass")).thenReturn(true);

        assertFalse(pvpManager.isEffectivePvPEnabled(player));
    }
}
