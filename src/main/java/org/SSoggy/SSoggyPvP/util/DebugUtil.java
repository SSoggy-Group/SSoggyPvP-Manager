package org.SSoggy.SSoggyPvP.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DebugUtil {

    private DebugUtil() {}

    public static void logDebug(boolean debugEnabled, Logger logger, String message, Object... params) {
        if (debugEnabled) {
            logger.log(Level.INFO, "[DEBUG] " + message, params);
        }
    }
}
