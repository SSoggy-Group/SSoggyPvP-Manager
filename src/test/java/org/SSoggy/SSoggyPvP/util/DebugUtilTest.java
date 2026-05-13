package org.SSoggy.SSoggyPvP.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DebugUtilTest {

    @Test
    void testLogDebugWhenEnabled() {
        Logger logger = mock(Logger.class);
        String message = "test message";
        Object[] params = {"param1", "param2"};

        DebugUtil.logDebug(true, logger, message, params);

        verify(logger, times(1)).log(eq(Level.INFO), eq("[DEBUG] " + message), eq(params));
    }

    @Test
    void testLogDebugWhenDisabled() {
        Logger logger = mock(Logger.class);
        String message = "test message";
        Object[] params = {"param1", "param2"};

        DebugUtil.logDebug(false, logger, message, params);

        verify(logger, never()).log(any(Level.class), anyString(), any(Object[].class));
    }

    @Test
    void testConstructorIsPrivate() throws Exception {
        Constructor<DebugUtil> constructor = DebugUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
