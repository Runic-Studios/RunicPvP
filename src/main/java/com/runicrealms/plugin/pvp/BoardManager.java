package com.runicrealms.plugin.pvp;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
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
        Hologram azanaHologram = HolographicDisplaysAPI.get(RunicPvP.inst()).createHologram(locAzana);
        azanaHologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
        azanaHologram.getLines().appendText(BOARD_TITLE);
        // dmr
        Location locDMR = new Location(Bukkit.getWorld("Alterra"), -12.5, 34.5, -550.5);
        Hologram deadMansHologram = HolographicDisplaysAPI.get(RunicPvP.inst()).createHologram(locDMR);
        deadMansHologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
        deadMansHologram.getLines().appendText(BOARD_TITLE);
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
