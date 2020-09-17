package com.runicrealms.plugin.cmd;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.utilities.NametagUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDOutlaw implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // outlaw <player.name>
        if (!sender.isOp()) return true;

        Player pl = Bukkit.getPlayer(args[0]);
        if (pl == null) return true;

        // toggle their current outlaw status from whatever it currently is, set their rating to default EVERY toggle
        // if its higher than the base
        RunicCoreAPI.getPlayerCache(pl).setOutlaw(!RunicPvPAPI.isOutlaw(pl));
        if (RunicCoreAPI.getPlayerCache(pl).getRating() > RunicCoreAPI.getBaseOutlawRating())
            RunicCoreAPI.getPlayerCache(pl).setRating(RunicCoreAPI.getBaseOutlawRating());

        NametagUtil.updateNametag(pl);
        return true;
    }
}