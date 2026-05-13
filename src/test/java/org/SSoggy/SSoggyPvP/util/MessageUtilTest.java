package org.SSoggy.SSoggyPvP.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageUtilTest {

    @Test
    void testSendActionBar() {
        Player player = mock(Player.class);
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);

        MessageUtil.sendActionBar(player, "&aHello");

        verify(spigot).sendMessage(eq(ChatMessageType.ACTION_BAR), any(TextComponent.class));
    }

    @Test
    void testSendActionBarNullOrEmpty() {
        Player player = mock(Player.class);
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);

        MessageUtil.sendActionBar(player, null);
        MessageUtil.sendActionBar(player, "");

        verifyNoInteractions(spigot);
    }

    @Test
    void testSend() {
        CommandSender sender = mock(CommandSender.class);
        MessageUtil.send(sender, "&aHello");
        verify(sender).sendMessage("§aHello");
    }

    @Test
    void testSendNullOrEmpty() {
        CommandSender sender = mock(CommandSender.class);
        MessageUtil.send(sender, null);
        MessageUtil.send(sender, "");
        verifyNoInteractions(sender);
    }

    @Test
    void testFormatTime() {
        assertEquals("0s", MessageUtil.formatTime(0));
        assertEquals("0s", MessageUtil.formatTime(-5));
        assertEquals("1s", MessageUtil.formatTime(1));
        assertEquals("1m 0s", MessageUtil.formatTime(60));
        assertEquals("1m 1s", MessageUtil.formatTime(61));
        assertEquals("1h 0s", MessageUtil.formatTime(3600));
        assertEquals("1h 1m 1s", MessageUtil.formatTime(3661));
        assertEquals("2h 15m 30s", MessageUtil.formatTime(8130));
    }
}
