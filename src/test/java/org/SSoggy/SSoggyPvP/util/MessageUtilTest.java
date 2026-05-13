package org.SSoggy.SSoggyPvP.util;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageUtilTest {

    @Mock
    private CommandSender sender;

    @Test
    void testSend_NullMessage() {
        MessageUtil.send(sender, null);
        verifyNoInteractions(sender);
    }

    @Test
    void testSend_EmptyMessage() {
        MessageUtil.send(sender, "");
        verifyNoInteractions(sender);
    }

    @Test
    void testSend_NormalMessage() {
        MessageUtil.send(sender, "Hello World");
        verify(sender).sendMessage("Hello World");
    }

    @Test
    void testSend_MessageWithColorCodes() {
        MessageUtil.send(sender, "&cRed &aGreen");
        // ChatColor.translateAlternateColorCodes('&', "&cRed &aGreen") -> "§cRed §aGreen"
        verify(sender).sendMessage("§cRed §aGreen");
    }
}
