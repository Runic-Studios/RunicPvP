package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.character.api.CharacterQuitEvent;
import com.runicrealms.plugin.events.RunicDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class DuelManager implements Listener {

    private static final HashSet<DuelRequest> duelRequests = new HashSet<>();
    private static final HashSet<Duel> currentDuels = new HashSet<>();

    public DuelManager() {
        RunicPvP.inst().getServer().getPluginManager().registerEvents(this, RunicPvP.inst());
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(RunicPvP.inst(), this::tryRequestTimeout, 0, 20L);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(RunicPvP.inst(), this::checkDuelRadius, 0, 20L);
    }

    /**
     * Forfeit duels on logout
     */
    @EventHandler
    public void onQuit(CharacterQuitEvent e) {
        for (Duel duel : getCurrentDuels()) {
            if (e.getPlayer().equals(duel.getChallenger()) || e.getPlayer().equals(duel.getDefender())) {
                if (e.getPlayer() == duel.getChallenger())
                    duel.setDuelResult(IDuel.DuelResult.DEFEAT);
                else
                    duel.setDuelResult(IDuel.DuelResult.VICTORY);
                duel.endDuel(duel.getDuelResult());
            }
        }
    }

    /**
     * Lose duel on death
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRunicDeath(RunicDeathEvent e) {
        for (Duel duel : getCurrentDuels()) {
            if (e.getVictim().equals(duel.getChallenger()) || e.getVictim().equals(duel.getDefender())) {
                e.setCancelled(true);
                if (e.getVictim() == duel.getChallenger())
                    duel.setDuelResult(IDuel.DuelResult.DEFEAT);
                else
                    duel.setDuelResult(IDuel.DuelResult.VICTORY);
                duel.endDuel(duel.getDuelResult());
            }
        }
    }

    /**
     * Automatically timeout duel requests after a duration
     */
    private void tryRequestTimeout() {
        for (DuelRequest duelRequest : duelRequests) {
            long startTime = duelRequest.getRequestTime();
            if (!duelRequest.countdownStarted() && System.currentTimeMillis() - startTime > (DuelRequest.getRequestTimeout() * 1000)) {
                duelRequest.processDuelRequest(IDuelRequest.DuelRequestResult.TIMEOUT);
            }
        }
    }

    /**
     * Periodically check to ensure dueling players don't leave duel zone
     */
    private void checkDuelRadius() {
        for (Duel duel : getCurrentDuels()) {
            Location startingLoc = duel.getDuelLocation();
            Location challengerLoc = duel.getChallenger().getLocation();
            Location defenderLoc = duel.getDefender().getLocation();
            if (startingLoc.distanceSquared(challengerLoc) > (Duel.getDuelRadius() * Duel.getDuelRadius())
                    || startingLoc.distanceSquared(defenderLoc) > (Duel.getDuelRadius() * Duel.getDuelRadius())) {
                duel.endDuel(IDuel.DuelResult.FORFEIT);
            }
        }
    }

    public HashSet<DuelRequest> getDuelRequests() {
        return duelRequests;
    }

    public DuelRequest getDuelRequest(Player recipient) {
        DuelRequest duelRequest = null;
        for (DuelRequest request : RunicPvP.getDuelManager().getDuelRequests()) {
            if (request.getRecipient().equals(recipient))
                duelRequest = request;
        }
        return duelRequest;
    }

    public static Set<Duel> getCurrentDuels() {
        return Collections.synchronizedSet(currentDuels);
    }

    /**
     * Checks whether a single player is currently dueling.
     *
     * @param player player to check
     * @return true if player is dueling
     */
    public boolean isInDuel(Player player) {
        for (Duel duel : getCurrentDuels()) {
            if (duel.getChallenger().equals(player) || duel.getDefender().equals(player))
                return true;
        }
        return false;
    }

    /**
     * Checks whether both players are dueling each other!
     *
     * @param damager player who is attacking
     * @param victim  player who is defending
     * @return true if players are dueling each other
     */
    public static boolean areDueling(Player damager, Player victim) {
        for (Duel duel : getCurrentDuels()) {
            if (duel.getChallenger().equals(damager) && duel.getDefender().equals(victim)) {
                return true;
            } else if (duel.getDefender().equals(damager) && duel.getChallenger().equals(victim)) {
                return true;
            }
        }
        return false;
    }
}
