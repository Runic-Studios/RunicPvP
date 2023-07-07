package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.SafeZoneLocation;
import com.runicrealms.plugin.api.event.AllyVerifyEvent;
import com.runicrealms.plugin.api.event.RunicPvPEvent;
import com.runicrealms.plugin.events.EnemyVerifyEvent;
import com.runicrealms.plugin.events.EnterCombatEvent;
import com.runicrealms.plugin.events.LeaveCombatEvent;
import com.runicrealms.plugin.events.MagicDamageEvent;
import com.runicrealms.plugin.events.PhysicalDamageEvent;
import com.runicrealms.plugin.events.RunicDeathEvent;
import com.runicrealms.plugin.player.CombatType;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
        // If combat is an option, these are not allies
        if (RunicPvP.getAPI().playersCanFight(event.getCaster(), recipient)) {
            event.setCancelled(true);
            return;
        }
        // If the caster or recipient is an outlaw, cancel event if players not in party
        if (RunicPvP.getAPI().isOutlaw(event.getCaster(), RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getCaster().getUniqueId()))
                || RunicPvP.getAPI().isOutlaw(recipient, RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(recipient.getUniqueId()))) {
            if (!RunicCore.getPartyAPI().isPartyMember(event.getCaster().getUniqueId(), recipient)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * For spell effects like knock-up, blind, etc. Cancels them
     */
    @EventHandler
    public void onEnemyVerify(EnemyVerifyEvent event) {
        if (!(event.getVictim() instanceof Player victim)) return;
        Player caster = event.getCaster();
        int slotCaster = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(caster.getUniqueId());
        int slotVictim = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(victim.getUniqueId());
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
        EnterCombatEvent.tagPlayerAndPartyInCombat(event.getVictim(), CombatType.PLAYER); // Player is tagged in parent event
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
    public void onQuit(PlayerQuitEvent event) {
        if (!playersFightingPlayers.containsKey(event.getPlayer().getUniqueId())) return;
        Player combatLogger = event.getPlayer();
        Player lastPlayerWhoTheyFought = Bukkit.getPlayer(playersFightingPlayers.get(combatLogger.getUniqueId()));
        // Teleport player to their inn if they combat log
        Location fromLogout = combatLogger.getLocation();
        Location location = SafeZoneLocation.getLocationFromItemStack(combatLogger.getInventory().getItem(8));
        if (location != null) {
            combatLogger.teleport(location);
        }
        RunicDeathEvent runicDeathEvent = new RunicDeathEvent(combatLogger, fromLogout, lastPlayerWhoTheyFought);
        Bukkit.getPluginManager().callEvent(runicDeathEvent);
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
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(event.getPlayer(), CombatType.PLAYER));
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(victim, CombatType.PLAYER));
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
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(event.getPlayer(), CombatType.PLAYER));
                Bukkit.getPluginManager().callEvent(new EnterCombatEvent(victim, CombatType.PLAYER));
            }
        }
    }
}
