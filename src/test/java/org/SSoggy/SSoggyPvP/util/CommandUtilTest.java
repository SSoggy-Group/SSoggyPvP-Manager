package org.SSoggy.SSoggyPvP.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandUtilTest {

    @Test
    void testRequirePlayer_WithPlayer() {
        // Arrange
        Player mockPlayer = mock(Player.class);
        String errorMessage = "You must be a player!";

        // Act
        Player result = CommandUtil.requirePlayer(mockPlayer, errorMessage);

        // Assert
        assertNotNull(result, "Result should not be null when sender is a player");
        assertEquals(mockPlayer, result, "Result should be the same player object");
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    public void testRequirePlayer_WithNonPlayer() {
        // Arrange
        CommandSender mockSender = mock(CommandSender.class);
        String errorMessage = "You must be a player!";
        String translatedError = org.bukkit.ChatColor.translateAlternateColorCodes('&', errorMessage);

        // Act
        Player result = CommandUtil.requirePlayer(mockSender, errorMessage);

        // Assert
        assertNull(result, "Result should be null when sender is not a player");
        // Verify that the error message is sent (translated)
        verify(mockSender).sendMessage(translatedError);
    }
}
