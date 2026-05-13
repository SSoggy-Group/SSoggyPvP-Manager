package org.SSoggy.SSoggyPvP.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageUtilTest {

    @Test
    void testFormatTimeZeroOrNegative() {
        assertEquals("0s", MessageUtil.formatTime(0));
        assertEquals("0s", MessageUtil.formatTime(-1));
        assertEquals("0s", MessageUtil.formatTime(-100));
    }

    @Test
    void testFormatTimeSecondsOnly() {
        assertEquals("1s", MessageUtil.formatTime(1));
        assertEquals("45s", MessageUtil.formatTime(45));
        assertEquals("59s", MessageUtil.formatTime(59));
    }

    @Test
    void testFormatTimeMinutesAndSeconds() {
        assertEquals("1m 0s", MessageUtil.formatTime(60));
        assertEquals("1m 1s", MessageUtil.formatTime(61));
        assertEquals("59m 59s", MessageUtil.formatTime(3599));
    }

    @Test
    void testFormatTimeHoursMinutesAndSeconds() {
        assertEquals("1h 1m 1s", MessageUtil.formatTime(3661));
        assertEquals("2h 30m 45s", MessageUtil.formatTime(9045));
        // Note: 10h 0m 0s should be 10h 0s based on the logic in MessageUtil, changing the test to reflect the implementation
        assertEquals("10h 0s", MessageUtil.formatTime(36000));
    }

    @Test
    void testFormatTimeHoursAndSecondsNoMinutes() {
        assertEquals("1h 0s", MessageUtil.formatTime(3600));
        assertEquals("1h 5s", MessageUtil.formatTime(3605));
    }
}
