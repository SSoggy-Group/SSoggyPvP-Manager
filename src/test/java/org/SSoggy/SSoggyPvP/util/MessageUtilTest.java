package org.SSoggy.SSoggyPvP.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

class MessageUtilTest {

    @Test
    void testSendValidMessage() {
        CommandSender sender = mock(CommandSender.class);
        MessageUtil.send(sender, "&aHello World");
        verify(sender).sendMessage("§aHello World");
    }

    @Test
    void testSendNullMessage() {
        CommandSender sender = mock(CommandSender.class);
        MessageUtil.send(sender, null);
        verifyNoInteractions(sender);
    }

    @Test
    void testSendEmptyMessage() {
        CommandSender sender = mock(CommandSender.class);
        MessageUtil.send(sender, "");
        verifyNoInteractions(sender);
    }

    @Test
    void testSendActionBarValidMessage() {
        Player player = mock(Player.class);
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);

        MessageUtil.sendActionBar(player, "&aHello Action Bar");

        verify(spigot).sendMessage(eq(ChatMessageType.ACTION_BAR), any(TextComponent.class));
    }

    @Test
    void testSendActionBarNullMessage() {
        Player player = mock(Player.class);
        MessageUtil.sendActionBar(player, null);
        verifyNoInteractions(player);
    }

    @Test
    void testSendActionBarEmptyMessage() {
        Player player = mock(Player.class);
        MessageUtil.sendActionBar(player, "");
        verifyNoInteractions(player);
    }

    @Test
    void testFormatTime() {
        assertEquals("0s", MessageUtil.formatTime(0));
        assertEquals("0s", MessageUtil.formatTime(-5));
        assertEquals("5s", MessageUtil.formatTime(5));
        assertEquals("1m 0s", MessageUtil.formatTime(60));
        assertEquals("1m 5s", MessageUtil.formatTime(65));
        assertEquals("1h 0s", MessageUtil.formatTime(3600));
        assertEquals("1h 1m 5s", MessageUtil.formatTime(3665));
        assertEquals("2h 0s", MessageUtil.formatTime(7200));
        assertEquals("2h 15m 30s", MessageUtil.formatTime(8130));
    }
}
