package com.runicrealms.plugin.api;

import com.runicrealms.plugin.duel.DuelManager;
import com.runicrealms.plugin.utilities.NametagUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RunicPvPAPI {

    /**
     * Checks whether the two given players are currently dueling
     *
     * @param playerOne first player to check
     * @param playerTwo second player to check
     * @return true if the players are dueling
     */
    public static boolean areDueling(Player playerOne, Player playerTwo) {
        return DuelManager.areDueling(playerOne, playerTwo);
    }

    /**
     * Checks if the given player is an outlaw
     *
     * @param player to check
     * @return true if the player is an outlaw
     */
    public static boolean isOutlaw(Player player) {
        return RunicCoreAPI.getPlayerCache(player).getIsOutlaw();
    }

    /**
     * Toggle a player's outlaw status
     *
     * @param player to toggle
     */
    public static void toggleOutlaw(Player player) {
        /*
        toggle their current outlaw status from whatever it currently is set their rating to default EVERY toggle
        (if it is higher than base value)
         */
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
     *
     * @param damager player attempting to hit player
     * @param victim  entity (player) being hit
     * @return true if both players are dueling or are BOTH outlaws
     */
    public static boolean isPlayerValidTarget(Player damager, Entity victim) {
        boolean bothOutlaws = RunicPvPAPI.isOutlaw(damager) && RunicPvPAPI.isOutlaw((Player) victim);
        boolean areDueling = RunicPvPAPI.areDueling(damager, (Player) victim);
        boolean damagerInSafezone = RunicCoreAPI.isSafezone(damager.getLocation());
        boolean victimInSafezone = RunicCoreAPI.isSafezone(victim.getLocation());
        return (bothOutlaws || areDueling) && (!damagerInSafezone && !victimInSafezone);
    }
}
