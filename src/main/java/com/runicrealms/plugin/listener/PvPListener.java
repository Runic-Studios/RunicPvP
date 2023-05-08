package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.event.AllyVerifyEvent;
import com.runicrealms.plugin.api.event.RunicPvPEvent;
import com.runicrealms.plugin.character.api.CharacterQuitEvent;
import com.runicrealms.plugin.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPListener implements Listener {

    private final Map<UUID, UUID> playersFightingPlayers = new HashMap<>();

    /**
     * Prevent heals during duels, against other outlaws
     */
    @EventHandler(priority = EventPriority.LOWEST) // first
    public void onAllyVerify(AllyVerifyEvent event) {
        if (!(event.getRecipient() instanceof Player recipient)) return;
        if (event.getRecipient().equals(event.getCaster())) return; // caster healed itself
        if (RunicPvP.getAPI().playersCanFight(event.getCaster(), recipient))
            event.setCancelled(true);
    }

    /**
     * For spell effects like knock-up, blind, etc. Cancels them
     */
    @EventHandler
    public void onEnemyVerify(EnemyVerifyEvent event) {
        if (!(event.getVictim() instanceof Player victim)) return;
        Player caster = event.getCaster();
        int slotCaster = RunicCore.getCharacterAPI().getCharacterSlot(caster.getUniqueId());
        int slotVictim = RunicCore.getCharacterAPI().getCharacterSlot(victim.getUniqueId());
        boolean isOutlawCaster = RunicPvP.getAPI().isOutlaw(caster, slotCaster);
        boolean isOutlawVictim = RunicPvP.getAPI().isOutlaw(victim, slotVictim);
        if (!( // Negation
                (isOutlawCaster && isOutlawVictim) // Both players are outlaw
                        || RunicPvP.getAPI().areDueling(caster, victim) // They are dueling
        ))
            event.setCancelled(true);
    }

    @EventHandler
    public void onLeaveCombat(LeaveCombatEvent event) {
        if (playersFightingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            playersFightingPlayers.remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You have left PvP combat!");
        }
    }

    @EventHandler
    public void onPvPCombat(RunicPvPEvent event) {
        if (event.isCancelled()) return;
        EnterCombatEvent.tagPlayerAndPartyInCombat(event.getVictim()); // player is tagged in parent event
        if (!playersFightingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "You have entered PvP combat! Logging out counts as death!");
        }
        playersFightingPlayers.put(event.getPlayer().getUniqueId(), event.getVictim().getUniqueId()); // ALWAYS map our attacker to our victim
        if (!playersFightingPlayers.containsKey(event.getVictim().getUniqueId())) {
            event.getVictim().sendMessage(ChatColor.DARK_RED + "You have entered PvP combat! Logging out counts as death!");
            playersFightingPlayers.put(event.getVictim().getUniqueId(), event.getPlayer().getUniqueId()); // CONDITIONALLY map our victim to our attacker
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) // runs FIRST
    public void onQuit(CharacterQuitEvent event) {
        if (!playersFightingPlayers.containsKey(event.getPlayer().getUniqueId())) return;
        Player combatLogger = event.getPlayer();
        Player lastPlayerWhoTheyFought = Bukkit.getPlayer(playersFightingPlayers.get(combatLogger.getUniqueId()));
        RunicDeathEvent runicDeathEvent = new RunicDeathEvent(combatLogger, combatLogger.getLocation(), lastPlayerWhoTheyFought);
        Bukkit.getScheduler().runTask(RunicPvP.inst(), () -> Bukkit.getPluginManager().callEvent(runicDeathEvent)); // Sync
    }

    /**
     * Allow damage to go through during duels or PvP interactions
     */
    @EventHandler
    public void onSpellDamage(MagicDamageEvent event) {
        if (!(event.getVictim() instanceof Player victim)) return;
        if (!RunicPvP.getAPI().playersCanFight(event.getPlayer(), victim)) {
            event.setCancelled(true);
        } else {
            if (RunicPvP.getAPI().canCreatePvPEvent(event.getPlayer(), victim)) {
                Bukkit.getPluginManager().callEvent(new RunicPvPEvent(event.getPlayer(), victim));
            } else {
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(event.getPlayer()));
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(victim));
            }
        }
    }

    /**
     * Allow damage to go through during duels or PvP interactions
     */
    @EventHandler
    public void onWeaponDamage(PhysicalDamageEvent event) {
        if (!(event.getVictim() instanceof Player victim)) return;
        if (!RunicPvP.getAPI().playersCanFight(event.getPlayer(), victim)) {
            event.setCancelled(true);
        } else {
            if (RunicPvP.getAPI().canCreatePvPEvent(event.getPlayer(), victim)) {
                Bukkit.getPluginManager().callEvent(new RunicPvPEvent(event.getPlayer(), victim));
            } else {
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(event.getPlayer()));
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(victim));
            }
        }
    }
}
