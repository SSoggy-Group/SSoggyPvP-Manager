package org.SSoggy.SSoggyPvP.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageUtilTest {

    @Test
    public void testFormatTime_zeroAndNegativeSeconds() {
        assertEquals("0s", MessageUtil.formatTime(0), "0 seconds should format to '0s'");
        assertEquals("0s", MessageUtil.formatTime(-5), "Negative seconds should format to '0s'");
        assertEquals("0s", MessageUtil.formatTime(-3600), "Negative hours should format to '0s'");
    }

    @Test
    public void testFormatTime_onlySeconds() {
        assertEquals("1s", MessageUtil.formatTime(1), "1 second should format to '1s'");
        assertEquals("59s", MessageUtil.formatTime(59), "59 seconds should format to '59s'");
    }

    @Test
    public void testFormatTime_minutesAndSeconds() {
        assertEquals("1m 0s", MessageUtil.formatTime(60), "60 seconds should format to '1m 0s'");
        assertEquals("1m 5s", MessageUtil.formatTime(65), "65 seconds should format to '1m 5s'");
        assertEquals("59m 59s", MessageUtil.formatTime(3599), "3599 seconds should format to '59m 59s'");
    }

    @Test
    public void testFormatTime_hoursMinutesAndSeconds() {
        assertEquals("1h 0s", MessageUtil.formatTime(3600), "3600 seconds should format to '1h 0s' as per current implementation");
        assertEquals("1h 1m 5s", MessageUtil.formatTime(3665), "3665 seconds should format to '1h 1m 5s'");
        assertEquals("2h 0s", MessageUtil.formatTime(7200), "7200 seconds should format to '2h 0s'");
        assertEquals("2h 30m 45s", MessageUtil.formatTime(9045), "9045 seconds should format to '2h 30m 45s'");
        assertEquals("24h 0s", MessageUtil.formatTime(86400), "86400 seconds should format to '24h 0s'");
    }
}
