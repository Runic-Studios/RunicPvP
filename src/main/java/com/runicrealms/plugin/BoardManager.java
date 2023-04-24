package com.runicrealms.plugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class BoardManager {

    private static final String BOARD_TITLE = ChatColor.RED + "" + ChatColor.BOLD + "Wanted Outlaws";

    /**
     *
     */
    public BoardManager() {
        // azana
        Location locAzana = new Location(Bukkit.getWorld("Alterra"), -760.5, 38.5, 121.5);
        Hologram azanaHologram = HologramsAPI.createHologram(RunicPvP.inst(), locAzana);
        azanaHologram.getVisibilityManager().setVisibleByDefault(true);
        azanaHologram.appendTextLine(BOARD_TITLE);
        // dmr
        Location locDMR = new Location(Bukkit.getWorld("Alterra"), -12.5, 34.5, -550.5);
        Hologram deadMansHologram = HologramsAPI.createHologram(RunicPvP.inst(), locDMR);
        deadMansHologram.getVisibilityManager().setVisibleByDefault(true);
        deadMansHologram.appendTextLine(BOARD_TITLE);
        // task
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RunicPvP.inst(),
                () -> {
                    refreshBoard(azanaHologram);
                    refreshBoard(deadMansHologram);
                }, 0, 5 * 20L);
    }

    private void refreshBoard(Hologram hologram) {
//        hologram.clearLines();
//        hologram.appendTextLine(BOARD_TITLE);
//        Map<Player, Integer> ratings = RunicPvP.getOutlawManager().getOutlawRatings();
//        int count = 0;
//        for (Map.Entry<Player, Integer> player : ratings.entrySet()) {
//            if (count >= 5) return;
//            hologram.appendTextLine
//                    (
//                            player.getKey().getName() + " " +
//                            ChatColor.YELLOW + "| " +
//                            ChatColor.RED + "[" + player.getValue() + "]"
//                    );
//            count++;
//        }
    }
}
