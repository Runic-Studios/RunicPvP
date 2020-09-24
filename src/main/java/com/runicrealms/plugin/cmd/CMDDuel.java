package com.runicrealms.plugin.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.duel.Duel;
import com.runicrealms.plugin.duel.DuelRequest;
import com.runicrealms.plugin.duel.IDuelRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("duel")
public class CMDDuel extends BaseCommand {

    private static final String DUEL_PREFIX = ChatColor.RED + "[Duel] " + ChatColor.GOLD + "» ";

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    public static void onCommandDuel(Player sender, @Default("Unknown User") String targetName) {
        for (DuelRequest duelRequest : RunicPvP.getDuelManager().getDuelRequests()) {
            if (duelRequest.getSender().equals(sender)) {
                sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You already have an outstanding challenge!");
                return;
            }
        }
        if(targetName.isEmpty()) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "Player not found!");
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            sender.sendMessage(DUEL_PREFIX + ChatColor.RED + "Player not found!");
            return;
        }
        if(target.equals(sender)) {
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

    @Subcommand("accept|a")
    @Conditions("is-player")
    public void onCommandAccept(Player player) {
        DuelRequest duelRequest = RunicPvP.getDuelManager().getDuelRequest(player);
        if (duelRequest != null)
            duelRequest.processDuelRequest(IDuelRequest.DuelRequestResult.ACCEPTED);
        else
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

    public static String getDuelPrefix() {
        return DUEL_PREFIX;
    }
}
