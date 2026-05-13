package org.SSoggy.SSoggyPvP.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerDataTest {

    @Test
    void testSetPvpDebtSecondsNegative() {
        PlayerData data = new PlayerData();
        data.setPvpDebtSeconds(-5L);
        assertEquals(0L, data.getPvpDebtSeconds(), "Setting a negative debt should result in 0");
    }

    @Test
    void testSetPvpDebtSecondsZero() {
        PlayerData data = new PlayerData();
        data.setPvpDebtSeconds(0L);
        assertEquals(0L, data.getPvpDebtSeconds(), "Setting 0 debt should result in 0");
    }

    @Test
    void testSetPvpDebtSecondsPositive() {
        PlayerData data = new PlayerData();
        data.setPvpDebtSeconds(100L);
        assertEquals(100L, data.getPvpDebtSeconds(), "Setting a positive debt should result in the same value");
    }
}
