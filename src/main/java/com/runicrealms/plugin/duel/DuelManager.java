package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.events.RunicDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;

@SuppressWarnings("deprecation")
public class DuelManager implements Listener {

    private static final int DUEL_RADIUS = 30; // max blocks players can leave from starting position before forfeiting
    private final HashSet<DuelRequest> duelRequests;
    private final HashSet<Duel> currentDuels;

    public DuelManager() {
        duelRequests = new HashSet<>();
        currentDuels = new HashSet<>();
        RunicPvP.inst().getServer().getPluginManager().registerEvents(this, RunicPvP.inst());
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(RunicPvP.inst(), this::checkDuelRadius, 0, 20L);
    }

    @EventHandler
    public void onRunicDeath(RunicDeathEvent e) {
        for (Duel duel : currentDuels) {
            if (e.getVictim().equals(duel.getChallenger()) || e.getVictim().equals(duel.getDefender())) {
                e.setCancelled(true);
                if (e.getKiller() == duel.getChallenger())
                    duel.setDuelResult(IDuel.DuelResult.VICTORY);
                else
                    duel.setDuelResult(IDuel.DuelResult.DEFEAT);
                duel.endDuel(duel.getDuelResult());
            }
        }
    }

    private void checkDuelRadius() {
        for (Duel duel : currentDuels) {
            Location startingLoc = duel.getDuelLocation();
            Location challengerLoc = duel.getChallenger().getLocation();
            Location defenderLoc = duel.getDefender().getLocation();
            if (startingLoc.distanceSquared(challengerLoc) > (DUEL_RADIUS * DUEL_RADIUS)) {
                duel.endDuel(IDuel.DuelResult.FORFEIT);
                duel.getChallenger().sendMessage(ChatColor.RED + "You left the duel area and forfeited the duel!");
            } else if (startingLoc.distanceSquared(defenderLoc) > (DUEL_RADIUS * DUEL_RADIUS)) {
                duel.endDuel(IDuel.DuelResult.FORFEIT);
                duel.getDefender().sendMessage(ChatColor.RED + "You left the duel area and forfeited the duel!");
            }
        }
    }

    public HashSet<DuelRequest> getDuelRequests() {
        return duelRequests;
    }
    public HashSet<Duel> getCurrentDuels() {
        return currentDuels;
    }
    public boolean areDueling(Player challenger, Player defender) {
        return currentDuels.stream().anyMatch(n -> n.getChallenger() == challenger && n.getDefender() == defender);
    }
}
