package com.runicrealms.plugin.api;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.utilities.NametagUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RunicPvPAPI {

    public static boolean isOutlaw(Player player) {
        return RunicCoreAPI.getPlayerCache(player).getIsOutlaw();
    }

    public static void toggleOutlaw(Player player) {
        // toggle their current outlaw status from whatever it currently is, set their rating to default EVERY toggle
        // if its higher than the base
        RunicCoreAPI.getPlayerCache(player).setOutlaw(!RunicPvPAPI.isOutlaw(player));
        NametagUtil.updateNametag(player);
        String status = RunicPvPAPI.isOutlaw(player) ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED";
        player.sendMessage(ChatColor.YELLOW + "Your outlaw status is now: " + status);
        if (RunicCoreAPI.getPlayerCache(player).getRating() > RunicCoreAPI.getBaseOutlawRating()) {
            RunicCoreAPI.getPlayerCache(player).setRating(RunicCoreAPI.getBaseOutlawRating());
            player.sendMessage(ChatColor.GRAY + "Your outlaw rating was above the default rating, so it has been reset.");
        }
    }

    /**
     * Checks whether two players are allowed to hit each other.
     * @param damager player attempting to hit player
     * @param victim entity (player) being hit
     * @return true if both players are dueling or are BOTH outlaws
     */
    public static boolean isPlayerValidTarget(Player damager, Entity victim) {
        boolean bothOutlaws = RunicPvPAPI.isOutlaw(damager) && RunicPvPAPI.isOutlaw((Player) victim);
        boolean areDueling = RunicPvP.getDuelManager().areDueling(damager, (Player) victim);
        boolean damagerInSafezone = RunicCoreAPI.isSafezone(damager.getLocation());
        boolean victimInSafezone = RunicCoreAPI.isSafezone(victim.getLocation());
        return (bothOutlaws || areDueling) && (!damagerInSafezone && !victimInSafezone);
    }
}
