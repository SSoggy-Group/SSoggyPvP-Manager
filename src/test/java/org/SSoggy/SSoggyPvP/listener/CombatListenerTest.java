package org.SSoggy.SSoggyPvP.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.logging.Logger;

import org.SSoggy.SSoggyPvP.PvPTogglePlugin;
import org.SSoggy.SSoggyPvP.manager.PvPManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CombatListenerTest {

    private CombatListener listener;
    private PvPTogglePlugin plugin;
    private PvPManager pvpManager;
    private FileConfiguration config;
    private Logger logger;

    @BeforeEach
    void setUp() {
        plugin = mock(PvPTogglePlugin.class);
        pvpManager = mock(PvPManager.class);
        config = mock(FileConfiguration.class);
        logger = mock(Logger.class);

        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getPvPManager()).thenReturn(pvpManager);
        when(plugin.getLogger()).thenReturn(logger);

        // default config return values
        when(config.getBoolean("debug", false)).thenReturn(false);
        when(config.getString(eq("messages.pvp-blocked-attacker"), anyString())).thenReturn("Attacker PvP blocked");
        when(config.getString(eq("messages.pvp-blocked-victim"), anyString())).thenReturn("Victim PvP blocked");

        listener = new CombatListener(plugin);
    }

    @Test
    void testNotPlayerVictim() {
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);
        Entity nonPlayerEntity = mock(Entity.class);
        when(event.getEntity()).thenReturn(nonPlayerEntity);

        listener.onEntityDamageByEntity(event);

        verify(event, never()).getDamager();
        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    void testNullAttacker() {
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);
        Player victim = mock(Player.class);
        Entity nonPlayerDamager = mock(Entity.class);
        when(event.getEntity()).thenReturn(victim);
        when(event.getDamager()).thenReturn(nonPlayerDamager);

        listener.onEntityDamageByEntity(event);

        verify(event, never()).setCancelled(anyBoolean());
        verify(plugin, never()).getPvPManager();
    }

    @Test
    void testSelfDamage() {
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);
        Player player = mock(Player.class);
        when(event.getEntity()).thenReturn(player);
        when(event.getDamager()).thenReturn(player);

        listener.onEntityDamageByEntity(event);

        verify(event, never()).setCancelled(anyBoolean());
        verify(plugin, never()).getPvPManager();
    }

    @Test
    void testAttackerPvPDisabled() {
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);
        Player victim = mock(Player.class);
        Player attacker = mock(Player.class);
        Player.Spigot attackerSpigot = mock(Player.Spigot.class);

        when(attacker.spigot()).thenReturn(attackerSpigot);

        when(event.getEntity()).thenReturn(victim);
        when(event.getDamager()).thenReturn(attacker);

        when(pvpManager.isEffectivePvPEnabled(attacker)).thenReturn(false);
        when(pvpManager.isEffectivePvPEnabled(victim)).thenReturn(true);

        listener.onEntityDamageByEntity(event);

        verify(event).setCancelled(true);
        verify(attacker).sendMessage(contains("Attacker PvP blocked"));
    }

    @Test
    void testVictimPvPDisabled() {
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);
        Player victim = mock(Player.class);
        Player attacker = mock(Player.class);
        Player.Spigot attackerSpigot = mock(Player.Spigot.class);

        when(attacker.spigot()).thenReturn(attackerSpigot);

        when(event.getEntity()).thenReturn(victim);
        when(event.getDamager()).thenReturn(attacker);

        when(pvpManager.isEffectivePvPEnabled(attacker)).thenReturn(true);
        when(pvpManager.isEffectivePvPEnabled(victim)).thenReturn(false);

        listener.onEntityDamageByEntity(event);

        verify(event).setCancelled(true);
        verify(attacker).sendMessage(contains("Victim PvP blocked"));
    }

    @Test
    void testPvPAllowed() {
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);
        Player victim = mock(Player.class);
        Player attacker = mock(Player.class);

        when(event.getEntity()).thenReturn(victim);
        when(event.getDamager()).thenReturn(attacker);

        when(pvpManager.isEffectivePvPEnabled(attacker)).thenReturn(true);
        when(pvpManager.isEffectivePvPEnabled(victim)).thenReturn(true);

        listener.onEntityDamageByEntity(event);

        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    void testResolvePlayerAttacker() {
        // Direct player
        EntityDamageByEntityEvent eventDirect = mock(EntityDamageByEntityEvent.class);
        Player victim1 = mock(Player.class);
        Player attacker1 = mock(Player.class);
        when(eventDirect.getEntity()).thenReturn(victim1);
        when(eventDirect.getDamager()).thenReturn(attacker1);
        when(pvpManager.isEffectivePvPEnabled(any(Player.class))).thenReturn(true);

        listener.onEntityDamageByEntity(eventDirect);
        verify(pvpManager).isEffectivePvPEnabled(attacker1);

        // Projectile
        EntityDamageByEntityEvent eventProj = mock(EntityDamageByEntityEvent.class);
        Player victim2 = mock(Player.class);
        Projectile projectile = mock(Projectile.class);
        Player shooter = mock(Player.class);
        when(projectile.getShooter()).thenReturn(shooter);
        when(eventProj.getEntity()).thenReturn(victim2);
        when(eventProj.getDamager()).thenReturn(projectile);

        listener.onEntityDamageByEntity(eventProj);
        verify(pvpManager).isEffectivePvPEnabled(shooter);

        // Tameable
        EntityDamageByEntityEvent eventTame = mock(EntityDamageByEntityEvent.class);
        Player victim3 = mock(Player.class);
        Tameable tameable = mock(Tameable.class);
        Player owner = mock(Player.class);
        when(tameable.isTamed()).thenReturn(true);
        when(tameable.getOwner()).thenReturn(owner);
        when(eventTame.getEntity()).thenReturn(victim3);
        when(eventTame.getDamager()).thenReturn(tameable);

        listener.onEntityDamageByEntity(eventTame);
        verify(pvpManager).isEffectivePvPEnabled(owner);
    }
}
