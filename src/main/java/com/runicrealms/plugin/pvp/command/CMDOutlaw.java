package com.runicrealms.plugin.pvp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.runicrealms.plugin.common.RunicCommon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("outlaw")
@CommandPermission("runic.op")
public class CMDOutlaw extends BaseCommand {

    @CatchUnknown
    @Default
    @CommandCompletion("@players status")
    @CommandPermission("runic.op")
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("status")) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online! You can manually view their LP data with /lp user <player> meta info");
                return;
            }
            boolean isOutlaw = RunicCommon.getPvPAPI().isOutlaw(target);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has outlaw mode currently " + (isOutlaw ? "enabled" : "disabled"));
        } else {
            Player target;
            if (args.length == 0) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "You cannot toggle your outlaw status from the console!");
                    return;
                }
                target = player;
            } else {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "That player is not online! You can manually modify their LP data with /lp user <player> meta ...");
                    return;
                }
            }
            RunicCommon.getPvPAPI().toggleOutlaw(target, true);
            boolean isOutlaw = RunicCommon.getPvPAPI().isOutlaw(target);
            sender.sendMessage(ChatColor.GREEN + (isOutlaw ? "Enabled" : "Disabled") + " outlaw mode for " + target.getName());
        }
    }

}
