package com.runicrealms.plugin.manager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.RunicCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class BoardManager {

    private static final String BOARD_TITLE = ChatColor.RED + "" + ChatColor.BOLD + "Wanted Outlaws";

    public BoardManager() {
        Location loc = new Location(Bukkit.getWorld("Alterra"), -12.5, 34.5, -550.5);
        Hologram hologram = HologramsAPI.createHologram(RunicPvP.inst(), loc);
        hologram.getVisibilityManager().setVisibleByDefault(true);
        hologram.appendTextLine(BOARD_TITLE);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RunicPvP.inst(),
                () -> refreshBoard(hologram), 0, 5 * 20L);
    }

    private void refreshBoard(Hologram hologram) {
        hologram.clearLines();
        hologram.appendTextLine(BOARD_TITLE);
        Map<Player, Integer> ratings = RunicCoreAPI.getOutlawRatings();
        int count = 0;
        for (Map.Entry<Player, Integer> player : ratings.entrySet()) {
            if (count >= 5) return;
            hologram.appendTextLine
                    (
                            player.getKey().getName() + " " +
                            ChatColor.YELLOW + "| " +
                            ChatColor.RED + "[" + player.getValue() + "]"
                    );
            count++;
        }
    }
}
