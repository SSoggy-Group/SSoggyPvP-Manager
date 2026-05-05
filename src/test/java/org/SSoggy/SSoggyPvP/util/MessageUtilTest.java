package org.SSoggy.SSoggyPvP.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageUtilTest {

    @Test
    public void testFormatTime_Zero() {
        assertEquals("0s", MessageUtil.formatTime(0));
    }

    @Test
    public void testFormatTime_Negative() {
        assertEquals("0s", MessageUtil.formatTime(-1));
        assertEquals("0s", MessageUtil.formatTime(-3600));
        assertEquals("0s", MessageUtil.formatTime(-60));
    }

    @Test
    public void testFormatTime_PositiveSeconds() {
        assertEquals("45s", MessageUtil.formatTime(45));
        assertEquals("1s", MessageUtil.formatTime(1));
        assertEquals("59s", MessageUtil.formatTime(59));
    }

    @Test
    public void testFormatTime_PositiveMinutes() {
        assertEquals("1m 5s", MessageUtil.formatTime(65));
        assertEquals("1m 0s", MessageUtil.formatTime(60));
        assertEquals("59m 59s", MessageUtil.formatTime(3599));
    }

    @Test
    public void testFormatTime_PositiveHours() {
        assertEquals("1h 1m 1s", MessageUtil.formatTime(3661));
        assertEquals("1h 0s", MessageUtil.formatTime(3600));
        assertEquals("2h 30m 45s", MessageUtil.formatTime(9045));
    }
}
