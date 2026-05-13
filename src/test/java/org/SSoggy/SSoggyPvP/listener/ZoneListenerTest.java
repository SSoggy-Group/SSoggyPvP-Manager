package org.SSoggy.SSoggyPvP.listener;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.manager.ZoneManager;
import org.SSoggy.SSoggyPvP.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

class ZoneListenerTest {

    private PvPTogglePlugin plugin;
    private FileConfiguration config;
    private ZoneManager zoneManager;
    private Logger logger;
    private ZoneListener listener;

    private Player player;
    private PlayerInventory inventory;
    private PlayerInteractEvent event;
    private Block block;
    private Location blockLocation;
    private ItemStack wandItem;
    private ItemMeta wandMeta;

    @BeforeEach
    void setUp() {
        plugin = mock(PvPTogglePlugin.class);
        config = mock(FileConfiguration.class);
        zoneManager = mock(ZoneManager.class);
        logger = mock(Logger.class);

        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getZoneManager()).thenReturn(zoneManager);
        when(plugin.getLogger()).thenReturn(logger);

        when(config.getString("zone-wand-material")).thenReturn("GOLDEN_AXE");
        when(config.getInt("zone-exit-cooldowns.chat", 3)).thenReturn(3);
        when(config.getInt("zone-exit-cooldowns.actionbar", 0)).thenReturn(0);

        listener = new ZoneListener(plugin);

        player = mock(Player.class);
        inventory = mock(PlayerInventory.class);
        event = mock(PlayerInteractEvent.class);
        block = mock(Block.class);
        blockLocation = mock(Location.class);
        wandItem = mock(ItemStack.class);
        wandMeta = mock(ItemMeta.class);

        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(player.getInventory()).thenReturn(inventory);

        when(event.getPlayer()).thenReturn(player);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(event.getClickedBlock()).thenReturn(block);

        when(block.getLocation()).thenReturn(blockLocation);
        when(block.getX()).thenReturn(10);
        when(block.getY()).thenReturn(64);
        when(block.getZ()).thenReturn(10);

        when(wandItem.getType()).thenReturn(Material.GOLDEN_AXE);
        when(wandItem.hasItemMeta()).thenReturn(true);
        when(wandItem.getItemMeta()).thenReturn(wandMeta);
        when(wandMeta.hasDisplayName()).thenReturn(true);
        when(wandMeta.getDisplayName()).thenReturn(ChatColor.YELLOW + "PvP Zone Selector");

        when(inventory.getItemInMainHand()).thenReturn(wandItem);
        when(event.getItem()).thenReturn(wandItem);
    }

    @Test
    void testInteractNoPermission() {
        when(player.hasPermission("pvptoggle.admin")).thenReturn(false);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractOffHand() {
        when(event.getHand()).thenReturn(EquipmentSlot.OFF_HAND);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractNullItem() {
        when(inventory.getItemInMainHand()).thenReturn(null);
        when(event.getItem()).thenReturn(null);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractAirItem() {
        when(wandItem.getType()).thenReturn(Material.AIR);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractWrongMaterial() {
        when(wandItem.getType()).thenReturn(Material.WOODEN_AXE);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractNoItemMeta() {
        when(wandItem.hasItemMeta()).thenReturn(false);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractNoDisplayName() {
        when(wandMeta.hasDisplayName()).thenReturn(false);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractWrongDisplayName() {
        when(wandMeta.getDisplayName()).thenReturn(ChatColor.YELLOW + "Wrong Wand");

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractNullBlock() {
        when(event.getClickedBlock()).thenReturn(null);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }

    @Test
    void testInteractLeftClickBlock() {
        when(event.getAction()).thenReturn(Action.LEFT_CLICK_BLOCK);

        try (MockedStatic<MessageUtil> mockedMessageUtil = mockStatic(MessageUtil.class)) {
            listener.onPlayerInteract(event);

            verify(event).setCancelled(true);
            verify(zoneManager).setPosition(player.getUniqueId(), 0, blockLocation);
            mockedMessageUtil.verify(() -> MessageUtil.send(eq(player), contains("Position 1")));
        }
    }

    @Test
    void testInteractRightClickBlock() {
        when(event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);

        try (MockedStatic<MessageUtil> mockedMessageUtil = mockStatic(MessageUtil.class)) {
            listener.onPlayerInteract(event);

            verify(event).setCancelled(true);
            verify(zoneManager).setPosition(player.getUniqueId(), 1, blockLocation);
            mockedMessageUtil.verify(() -> MessageUtil.send(eq(player), contains("Position 2")));
        }
    }

    @Test
    void testInteractLeftClickAir() {
        when(event.getAction()).thenReturn(Action.LEFT_CLICK_AIR);

        listener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(zoneManager, never()).setPosition(any(), anyInt(), any());
    }
}
