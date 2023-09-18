package com.runicrealms.plugin.pvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.pvp.RunicPvP;
import com.runicrealms.plugin.pvp.duel.Duel;
import com.runicrealms.plugin.pvp.duel.DuelRequest;
import com.runicrealms.plugin.pvp.duel.IDuelRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("duel")
@SuppressWarnings("unused")
public class CMDDuel extends BaseCommand {

    private static final String DUEL_PREFIX = ChatColor.RED + "[Duel] " + ChatColor.GOLD + "» ";

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public static void onCommandDuel(Player sender, @Default("Unknown User") String targetName) {
        if (RunicCore.getCombatAPI().isInCombat(sender.getUniqueId())) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot send a duel request in combat!");
            return;
        }
        if (RunicCore.getRegionAPI().isSafezone(sender.getLocation())) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot send a duel request in a safe zone!");
            return;
        }
        if (!sender.getLocation().getWorld().getName().equals("Alterra")) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot send a duel request in the instance world!");
            return;
        }
        for (DuelRequest duelRequest : RunicPvP.getDuelManager().getDuelRequests()) {
            if (duelRequest.getSender().equals(sender)) {
                sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You already have an outstanding challenge!");
                return;
            }
        }
        if (targetName.isEmpty()) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "Player not found!");
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "Player not found!");
            return;
        }
        if (RunicCore.getPartyAPI().hasParty(sender.getUniqueId())
                && RunicCore.getPartyAPI().getParty(sender.getUniqueId()).getMembersWithLeader().contains(target)) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot challenge a player in your party!");
            return;
        }
        if (RunicCore.getCombatAPI().isInCombat(target.getUniqueId())) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "That player is in combat!");
            return;
        }
        if (RunicPvP.getDuelManager().isInDuel(sender)) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot send a challenge while in a duel!");
            return;
        }
        if (RunicPvP.getDuelManager().isInDuel(target)) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "That player is already in a duel!");
            return;
        }
        if (target.equals(sender)) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot challenge yourself!");
            return;
        }
        if (RunicPvP.getDuelManager().getDuelRequest(target) != null) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "This player already has an outstanding challenge!");
            return;
        }
        for (DuelRequest duelRequest : RunicPvP.getDuelManager().getDuelRequests()) {
            if (duelRequest.getSender().equals(target)) {
                sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "This player already has an outstanding challenge!");
                return;
            }
        }
        if (target.getLocation().distanceSquared(sender.getLocation()) > Duel.getDuelRadius() * Duel.getDuelRadius()) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "This player is too far away!");
            return;
        }
        DuelRequest duelRequest = new DuelRequest(sender, Bukkit.getPlayer(targetName));
        RunicPvP.getDuelManager().getDuelRequests().add(duelRequest);
    }

    public static String getDuelPrefix() {
        return DUEL_PREFIX;
    }

    @Subcommand("accept|a")
    @Conditions("is-player")
    public void onCommandAccept(Player player) {
        DuelRequest duelRequest = RunicPvP.getDuelManager().getDuelRequest(player);
        if (duelRequest != null) {
            if (RunicCore.getRegionAPI().isSafezone(player.getLocation())) {
                player.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot accept a duel request in a safe zone!");
                return;
            }
            if (!player.getLocation().getWorld().getName().equals("Alterra")) {
                player.sendMessage(DUEL_PREFIX + ChatColor.RED + "You cannot accept a duel request in the instance world!");
                return;
            }
            if (RunicPvP.getDuelManager().getCountdownSet().contains(player.getUniqueId())) {
                player.sendMessage(DUEL_PREFIX + ChatColor.RED + "Your duel is beginning!");
                return;
            }
            if (RunicPvP.getAPI().isDueling(player)) {
                player.sendMessage(DUEL_PREFIX + ChatColor.RED + "You have already accepted a duel!");
                return;
            }
            duelRequest.processDuelRequest(IDuelRequest.DuelRequestResult.ACCEPTED);
        } else
            player.sendMessage(DUEL_PREFIX + ChatColor.RED + "You have no outstanding duel challenges!");
    }

    @Subcommand("deny|d|reject|cancel")
    @Conditions("is-player")
    public void onCommandDeny(Player player) {
        DuelRequest duelRequest = RunicPvP.getDuelManager().getDuelRequest(player);
        if (duelRequest != null)
            duelRequest.processDuelRequest(IDuelRequest.DuelRequestResult.DENIED);
        else
            player.sendMessage(DUEL_PREFIX + ChatColor.RED + "You have no outstanding duel challenges!");
    }
}
