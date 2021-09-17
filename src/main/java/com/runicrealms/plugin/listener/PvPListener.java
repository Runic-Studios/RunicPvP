package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.character.api.CharacterQuitEvent;
import com.runicrealms.plugin.event.RunicPvPEvent;
import com.runicrealms.plugin.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;

public class PvPListener implements Listener {

    private final Map<UUID, UUID> playersFightingPlayers = new HashMap<>();

    @EventHandler
    public void onPvPCombat(RunicPvPEvent e) {
        EnterCombatEvent.tagPlayerAndPartyInCombat(e.getVictim()); // player is tagged in parent event
        if (!playersFightingPlayers.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You have entered PvP combat!");
        }
        playersFightingPlayers.put(e.getPlayer().getUniqueId(), e.getVictim().getUniqueId()); // ALWAYS map our attacker to our victim
        if (!playersFightingPlayers.containsKey(e.getVictim().getUniqueId())) {
            e.getVictim().sendMessage(ChatColor.DARK_RED + "You have entered PvP combat!");
            playersFightingPlayers.put(e.getVictim().getUniqueId(), e.getPlayer().getUniqueId()); // CONDITIONALLY map our victim to our attacker
        }
    }

    @EventHandler
    public void onLeaveCombat(LeaveCombatEvent e) {
        playersFightingPlayers.remove(e.getPlayer().getUniqueId());
        e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You have left PvP combat!");
    }

    /*
    For spell effects like knock-up, blind, etc.
     */
    @EventHandler
    public void onSpellVerify(EnemyVerifyEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        if ((!RunicPvPAPI.isOutlaw(((Player) e.getVictim())) || !RunicPvPAPI.isOutlaw(e.getCaster()))
                && !RunicPvPAPI.areDueling(e.getCaster(), (Player) e.getVictim()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onSpellDamage(SpellDamageEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        Player victim = (Player) e.getVictim();
        if (!playersCanFight(e.getPlayer(), victim)) e.setCancelled(true);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        Player victim = (Player) e.getVictim();
        if (!playersCanFight(e.getPlayer(), victim)) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // runs LAST
    public void onRunicDeath(RunicDeathEvent e) {
        if (e.getVictim().getGameMode() == GameMode.CREATIVE) return;
        if (e.getKiller() == null) return;
        if (!(e.getKiller()[0] instanceof Player)) return;
        Player killer = (Player) e.getKiller()[0];
        if (!RunicPvPAPI.isOutlaw((killer)) || !RunicPvPAPI.isOutlaw(e.getVictim())) return;
        if (RunicPvPAPI.areDueling(killer, e.getVictim())) return;
        if (e.isCancelled()) return;
        RunicPvP.getOutlawManager().onKill(killer, e.getVictim());
    }

    @EventHandler(priority = EventPriority.LOWEST) // runs FIRST
    public void onQuit(CharacterQuitEvent e) {
        if (!playersFightingPlayers.containsKey(e.getPlayer().getUniqueId())) return;
        Player combatLogger = e.getPlayer();
        Player lastPlayerWhoTheyFought = Bukkit.getPlayer(playersFightingPlayers.get(combatLogger.getUniqueId()));
        RunicDeathEvent runicDeathEvent = new RunicDeathEvent(combatLogger, lastPlayerWhoTheyFought);
        Bukkit.getPluginManager().callEvent(runicDeathEvent);
    }


    /**
     * Checks whether two players are able to engage in combat.
     * If it's a PvP event (everything except dueling), it flags them for PvP combat.
     * Otherwise, it places them in regular combat (duels).
     *
     * @param player the attack
     * @param victim the defender
     * @return true if they can fight
     */
    private boolean playersCanFight(Player player, Player victim) {
        if (canCreatePvPEvent(player, victim)) {
            RunicPvPEvent runicPvPEvent = new RunicPvPEvent(player, victim);
            Bukkit.getPluginManager().callEvent(runicPvPEvent);
            return true;
        } else if (RunicPvPAPI.areDueling(player, victim)) {
            EnterCombatEvent enterCombatEventPlayerOne = new EnterCombatEvent(player);
            Bukkit.getPluginManager().callEvent(enterCombatEventPlayerOne);
            EnterCombatEvent enterCombatEventPlayerTwo = new EnterCombatEvent(victim);
            Bukkit.getPluginManager().callEvent(enterCombatEventPlayerTwo);
            return true;
        } else {
            return false;
        }
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
            return !RunicPvPAPI.areDueling(player, victim);
        } else {
            return false;
        }
    }
}
