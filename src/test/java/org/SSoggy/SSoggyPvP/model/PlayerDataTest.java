package org.SSoggy.SSoggyPvP.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDataTest {

    private PlayerData playerData;

    @BeforeEach
    void setUp() {
        playerData = new PlayerData();
    }

    @Test
    void testDefaultConstructor() {
        assertFalse(playerData.isPvpEnabled(), "PvP should be disabled by default");
        assertEquals(0L, playerData.getTotalPlaytimeSeconds(), "Total playtime should be 0 by default");
        assertEquals(0, playerData.getProcessedCycles(), "Processed cycles should be 0 by default");
        assertEquals(0L, playerData.getPvpDebtSeconds(), "PvP debt should be 0 by default");
    }

    @Test
    void testSetPvpEnabled() {
        playerData.setPvpEnabled(true);
        assertTrue(playerData.isPvpEnabled(), "PvP should be enabled");

        playerData.setPvpEnabled(false);
        assertFalse(playerData.isPvpEnabled(), "PvP should be disabled");
    }

    @Test
    void testSetTotalPlaytimeSeconds() {
        playerData.setTotalPlaytimeSeconds(3600L);
        assertEquals(3600L, playerData.getTotalPlaytimeSeconds(), "Total playtime should be updated");

        playerData.setTotalPlaytimeSeconds(0L);
        assertEquals(0L, playerData.getTotalPlaytimeSeconds(), "Total playtime should be updated to 0");
    }

    @Test
    void testSetProcessedCycles() {
        playerData.setProcessedCycles(5);
        assertEquals(5, playerData.getProcessedCycles(), "Processed cycles should be updated");

        playerData.setProcessedCycles(0);
        assertEquals(0, playerData.getProcessedCycles(), "Processed cycles should be updated to 0");
    }

    @Test
    void testSetPvpDebtSecondsPositive() {
        playerData.setPvpDebtSeconds(1800L);
        assertEquals(1800L, playerData.getPvpDebtSeconds(), "PvP debt should be set to positive value");
    }

    @Test
    void testSetPvpDebtSecondsZero() {
        playerData.setPvpDebtSeconds(0L);
        assertEquals(0L, playerData.getPvpDebtSeconds(), "PvP debt should be set to 0");
    }

    @Test
    void testSetPvpDebtSecondsNegativeClampsToZero() {
        playerData.setPvpDebtSeconds(-500L);
        assertEquals(0L, playerData.getPvpDebtSeconds(), "PvP debt should be clamped to 0 when negative value is set");
    }
}
