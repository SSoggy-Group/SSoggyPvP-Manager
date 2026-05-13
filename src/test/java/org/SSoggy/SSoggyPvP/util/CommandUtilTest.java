package org.SSoggy.SSoggyPvP.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandUtilTest {

    @Test
    void testRequirePlayer_Success() {
        Player mockPlayer = mock(Player.class);
        String errorMessage = "You must be a player.";

        Player result = CommandUtil.requirePlayer(mockPlayer, errorMessage);

        assertSame(mockPlayer, result, "Should return the same player object");
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    void testRequirePlayer_Failure() {
        CommandSender mockSender = mock(CommandSender.class);
        String errorMessage = "&cYou must be a player.";

        Player result = CommandUtil.requirePlayer(mockSender, errorMessage);

        assertNull(result, "Should return null for non-player sender");
        // MessageUtil uses ChatColor.translateAlternateColorCodes('&', message)
        // § is the character used for colors in Bukkit
        verify(mockSender).sendMessage("§cYou must be a player.");
    }
}
