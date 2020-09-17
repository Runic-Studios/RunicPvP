package com.runicrealms.plugin.api;

import org.bukkit.entity.Player;

public class RunicPvPAPI {

    public static boolean isOutlaw(Player pl) {
        return RunicCoreAPI.getPlayerCache(pl).getIsOutlaw();
    }
}
