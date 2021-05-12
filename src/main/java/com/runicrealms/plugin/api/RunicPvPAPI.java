package com.runicrealms.plugin.api;

import com.runicrealms.plugin.RunicPvP;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RunicPvPAPI {

    public static boolean isOutlaw(Player pl) {
        return RunicCoreAPI.getPlayerCache(pl).getIsOutlaw();
    }

    /**
     * Checks whether two players are allowed to hit each other.
     * @param damager player attempting to hit player
     * @param victim entity (player) being hit
     * @return true if both players are dueling or are BOTH outlaws
     */
    public static boolean isPlayerValidTarget(Player damager, Entity victim) {
        return (RunicPvPAPI.isOutlaw(damager) && RunicPvPAPI.isOutlaw((Player) victim))
                || RunicPvP.getDuelManager().areDueling(damager, (Player) victim);
    }
}
