package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.character.api.CharacterQuitEvent;
import com.runicrealms.plugin.event.RunicPvPEvent;
import com.runicrealms.plugin.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PvPListener implements Listener {

    private final Set<UUID> playersFightingPlayers = new HashSet<>();

    @EventHandler
    public void onPvPCombat(RunicPvPEvent e) {
        EnterCombatEvent.tagPlayerAndPartyInCombat(e.getPlayer());
        EnterCombatEvent.tagPlayerAndPartyInCombat(e.getVictim());
        e.getPlayer().sendMessage(ChatColor.DARK_RED + "You have entered PvP combat!");
        e.getVictim().sendMessage(ChatColor.DARK_RED + "You have entered PvP combat!");
        playersFightingPlayers.add(e.getPlayer().getUniqueId());
        playersFightingPlayers.add(e.getVictim().getUniqueId());
    }
    // todo: create a leave combat event and remove players from set during that

    /*
    For spell effects like knock-up, blind, etc.
     */
    @EventHandler
    public void onSpellVerify(EnemyVerifyEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        if ((!RunicPvPAPI.isOutlaw(((Player) e.getVictim())) || !RunicPvPAPI.isOutlaw(e.getCaster()))
                && !RunicPvP.getDuelManager().areDueling(e.getCaster(), (Player) e.getVictim()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onSpellDamage(SpellDamageEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        if (!canCreatePvPEvent(e.getPlayer(), e.getVictim()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        if (!canCreatePvPEvent(e.getPlayer(), e.getVictim()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // runs LAST
    public void onRunicDeath(RunicDeathEvent e) {
        if (e.getKiller() == null) return;
        if (!(e.getKiller()[0] instanceof Player)) return;
        Player killer = (Player) e.getKiller()[0];
        if (!RunicPvPAPI.isOutlaw((killer)) || !RunicPvPAPI.isOutlaw(e.getVictim())) return;
        if (RunicPvP.getDuelManager().areDueling(killer, e.getVictim())) return;
        if (e.isCancelled()) return;
        RunicPvP.getOutlawManager().onKill(killer, e.getVictim());
    }

    @EventHandler(priority = EventPriority.HIGHEST) // runs LAST
    public void onQuit(CharacterQuitEvent e) {
        if (!playersFightingPlayers.contains(e.getPlayer().getUniqueId())) return;
        // todo combat logging logic here
    }

    /**
     * Checks if two players are valid opponents (both outlaws, both in PvP zone, etc.) and flags them in PvP combat if so
     *
     * @param player the player who fired the attack
     * @param entity the entity to receive the attack
     * @return true if a PvP event is created
     */
    private boolean canCreatePvPEvent(Player player, Entity entity) {
        if (RunicPvPAPI.isPlayerValidTarget(player, entity)) {
            Player victim = (Player) entity;
            RunicPvPEvent runicPvPEvent = new RunicPvPEvent(player, victim);
            Bukkit.getPluginManager().callEvent(runicPvPEvent);
            return true;
        } else
            return false;
    }
}
