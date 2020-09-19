package com.runicrealms.plugin.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.duel.DuelRequest;
import com.runicrealms.plugin.duel.IDuelRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

@CommandAlias("party")
public class CMDDuel extends BaseCommand {

    private static final String DUEL_PREFIX = ChatColor.RED + "[Duel] " + ChatColor.GOLD + "»";

    public CMDDuel() {
//        RunicCore.getCommandManager().getCommandCompletions().registerAsyncCompletion("party-invite", context -> {
//            if (RunicCore.getPartyManager().getPlayerParty(context.getPlayer()) == null) return new ArrayList<>();
//            if (RunicCore.getPartyManager().getPlayerParty(context.getPlayer()).getLeader() != context.getPlayer()) return new ArrayList<>();
//            Set<String> players = new HashSet<>();
//            for (Player player : Bukkit.getOnlinePlayers()) {
//                if (RunicCore.getPartyManager().getPlayerParty(player) == null) {
//                    players.add(player.getName());
//                }
//            }
//            return players;
//        });
//        RunicCore.getCommandManager().getCommandCompletions().registerAsyncCompletion("party-join", context -> {
//            if (RunicCore.getPartyManager().getPlayerParty(context.getPlayer()) != null) return new ArrayList<>();
//            Set<String> invites = new HashSet<>();
//            for (Party party : RunicCore.getPartyManager().getParties()) {
//                for (Party.Invite invite : party.getInvites()) {
//                    if (invite.getPlayer() == context.getPlayer()) {
//                        invites.add(party.getLeader().getName());
//                    }
//                }
//            }
//            return invites;
//        });
//        RunicCore.getCommandManager().getCommandCompletions().registerAsyncCompletion("party-kick", context -> {
//            if (RunicCore.getPartyManager().getPlayerParty(context.getPlayer()) == null) return new ArrayList<>();
//            if (RunicCore.getPartyManager().getPlayerParty(context.getPlayer()).getLeader() != context.getPlayer()) return new ArrayList<>();
//            Set<String> members = new HashSet<>();
//            RunicCore.getPartyManager().getPlayerParty(context.getPlayer()).getMembers().forEach(member -> members.add(member.getName()));
//            return members;
//        });
    }

    @Default
    @Syntax("<player>")
    @CommandCompletion("@online")
    public static void onCommandDuel(Player sender, @Default("Unknown User") String targetName) {
        if(targetName.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        Player target = Bukkit.getPlayer(targetName);

        if(target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        DuelRequest duelRequest = new DuelRequest(sender, Bukkit.getPlayer(targetName));
        RunicPvP.getDuelManager().getDuelRequests().add(duelRequest);
    }

//    @Default
//    @CatchUnknown
//    @Subcommand("help|h")
//    public void onCommandHelp(Player player) {
//        player.sendMessage(ChatColor.translateAlternateColorCodes('&', DUEL_PREFIX + " &aAvailable commands: &eaccept, deny"));
//    }

//    @Subcommand("challenge|request|c|r")
//    @Syntax("<player>")
//    @CommandCompletion("@duel-challenge")
//    @Conditions("is-player")
//    public void onCommandInvite(Player player, String[] args) {
//
//    }

    @Subcommand("accept|a")
    @Conditions("is-player")
    public void onCommandAccept(Player player) {
        Stream<DuelRequest> requests = RunicPvP.getDuelManager().getDuelRequests().stream();
        DuelRequest duelRequest = null;
        if (requests.findFirst().filter(n -> n.getRecipient().equals(player)).isPresent())
            duelRequest = requests.findFirst().filter(n -> n.getRecipient().equals(player)).get();
        if (duelRequest != null)
            duelRequest.processDuelRequest(IDuelRequest.DuelRequestResult.ACCEPTED);
        else
            player.sendMessage(DUEL_PREFIX + ChatColor.RED + "You have no outstanding duel challenges!");
    }

    @Subcommand("deny|d|reject|cancel")
    @Conditions("is-player")
    public void onCommandDeny(Player player) {

    }
}
