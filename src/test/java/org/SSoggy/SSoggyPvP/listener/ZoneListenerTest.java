package org.SSoggy.SSoggyPvP.listener;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.manager.ZoneManager;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ZoneListenerTest {

    @Mock
    private PvPTogglePlugin plugin;
    @Mock
    private FileConfiguration config;
    @Mock
    private ZoneManager zoneManager;
    @Mock
    private Player player;
    @Mock
    private PlayerInteractEvent event;
    @Mock
    private PlayerInventory inventory;
    @Mock
    private ItemMeta itemMeta;
    @Mock
    private Block block;

    private ZoneListener zoneListener;
    private final UUID playerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(plugin.getConfig()).thenReturn(config);
        when(config.getString("zone-wand-material")).thenReturn("BLAZE_ROD");
        when(config.getInt("zone-exit-cooldowns.chat", 3)).thenReturn(3);
        when(config.getInt("zone-exit-cooldowns.actionbar", 0)).thenReturn(0);

        zoneListener = new ZoneListener(plugin);
    }

    @Test
    void onPlayerInteract_NoAdminPermission_ReturnsEarly() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(false);

        zoneListener.onPlayerInteract(event);

        verify(event).getPlayer();
        verify(player).hasPermission("pvptoggle.admin");
        verifyNoMoreInteractions(player, event);
    }

    @Test
    void onPlayerInteract_OffHand_ReturnsEarly() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.OFF_HAND);

        zoneListener.onPlayerInteract(event);

        verify(event).getPlayer();
        verify(player).hasPermission("pvptoggle.admin");
        verify(event).getHand();
        verifyNoMoreInteractions(player, event);
    }

    @Test
    void onPlayerInteract_NotZoneWand_ReturnsEarly() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = new ItemStack(Material.STICK); // Not BLAZE_ROD
        when(inventory.getItemInMainHand()).thenReturn(item);

        zoneListener.onPlayerInteract(event);

        verifyNoInteractions(block);
    }

    @Test
    void onPlayerInteract_NoItemMeta_ReturnsEarly() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.BLAZE_ROD);
        when(item.hasItemMeta()).thenReturn(false);
        when(inventory.getItemInMainHand()).thenReturn(item);

        zoneListener.onPlayerInteract(event);

        verifyNoInteractions(block);
    }

    @Test
    void onPlayerInteract_WrongItemName_ReturnsEarly() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.BLAZE_ROD);
        when(item.hasItemMeta()).thenReturn(true);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn("Wrong Name");
        when(inventory.getItemInMainHand()).thenReturn(item);

        zoneListener.onPlayerInteract(event);

        verifyNoInteractions(block);
    }

    @Test
    void onPlayerInteract_NoBlockClicked_ReturnsEarly() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.BLAZE_ROD);
        when(item.hasItemMeta()).thenReturn(true);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn(ChatColor.YELLOW + "PvP Zone Selector");
        when(inventory.getItemInMainHand()).thenReturn(item);

        when(event.getClickedBlock()).thenReturn(null);

        zoneListener.onPlayerInteract(event);

        verify(event).getClickedBlock();
    }

    @Test
    void onPlayerInteract_LeftClick_SetsPosition1() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.BLAZE_ROD);
        when(item.hasItemMeta()).thenReturn(true);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn(ChatColor.YELLOW + "PvP Zone Selector");
        when(inventory.getItemInMainHand()).thenReturn(item);

        when(event.getClickedBlock()).thenReturn(block);
        when(event.getAction()).thenReturn(Action.LEFT_CLICK_BLOCK);

        Location location = new Location(null, 1, 2, 3);
        when(block.getLocation()).thenReturn(location);
        when(block.getX()).thenReturn(1);
        when(block.getY()).thenReturn(2);
        when(block.getZ()).thenReturn(3);

        when(player.getUniqueId()).thenReturn(playerId);
        when(plugin.getZoneManager()).thenReturn(zoneManager);

        zoneListener.onPlayerInteract(event);

        verify(event).setCancelled(true);
        verify(zoneManager).setPosition(playerId, 0, location);
    }

    @Test
    void onPlayerInteract_RightClick_SetsPosition2() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.BLAZE_ROD);
        when(item.hasItemMeta()).thenReturn(true);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn(ChatColor.YELLOW + "PvP Zone Selector");
        when(inventory.getItemInMainHand()).thenReturn(item);

        when(event.getClickedBlock()).thenReturn(block);
        when(event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);

        Location location = new Location(null, 4, 5, 6);
        when(block.getLocation()).thenReturn(location);
        when(block.getX()).thenReturn(4);
        when(block.getY()).thenReturn(5);
        when(block.getZ()).thenReturn(6);

        when(player.getUniqueId()).thenReturn(playerId);
        when(plugin.getZoneManager()).thenReturn(zoneManager);

        zoneListener.onPlayerInteract(event);

        verify(event).setCancelled(true);
        verify(zoneManager).setPosition(playerId, 1, location);
    }

    @Test
    void onPlayerInteract_ActionOther_DoesNothing() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.BLAZE_ROD);
        when(item.hasItemMeta()).thenReturn(true);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn(ChatColor.YELLOW + "PvP Zone Selector");
        when(inventory.getItemInMainHand()).thenReturn(item);

        when(event.getClickedBlock()).thenReturn(block);
        when(event.getAction()).thenReturn(Action.PHYSICAL); // Not left/right click block

        zoneListener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(plugin, never()).getZoneManager();
    }

    @Test
    void onPlayerInteract_LeftClickAir_DoesNothing() {
        when(event.getPlayer()).thenReturn(player);
        when(player.hasPermission("pvptoggle.admin")).thenReturn(true);
        when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(player.getInventory()).thenReturn(inventory);

        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.BLAZE_ROD);
        when(item.hasItemMeta()).thenReturn(true);
        when(item.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.hasDisplayName()).thenReturn(true);
        when(itemMeta.getDisplayName()).thenReturn(ChatColor.YELLOW + "PvP Zone Selector");
        when(inventory.getItemInMainHand()).thenReturn(item);

        when(event.getClickedBlock()).thenReturn(block);
        when(event.getAction()).thenReturn(Action.LEFT_CLICK_AIR);

        zoneListener.onPlayerInteract(event);

        verify(event, never()).setCancelled(true);
        verify(plugin, never()).getZoneManager();
    }
}
