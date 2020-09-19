package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicPvP;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Duel implements IDuel {

    private static final int DUEL_RADIUS = 30; // max blocks players can leave from starting position before forfeiting
    private final Player challenger;
    private final Player defender;
    private final Location duelLocation;
    private DuelResult duelResult;

    public Duel(Player challenger, Player defender) {
        this.challenger = challenger;
        this.defender = defender;
        duelLocation = challenger.getLocation();
    }

    @Override
    public Player getChallenger() {
        return challenger;
    }

    @Override
    public Player getDefender() {
        return defender;
    }

    @Override
    public Location getDuelLocation() {
        return duelLocation;
    }

    @Override
    public DuelResult getDuelResult() {
        return duelResult;
    }

    @Override
    public void setDuelResult(DuelResult duelResult) {
        this.duelResult = duelResult;
    }

    @Override
    public void endDuel(DuelResult duelResult) {
        if (duelResult == DuelResult.VICTORY) {
            // challenger won
        } else if (duelResult == DuelResult.DEFEAT) {
            // challenger lost
        } else {
            // forfeit
            if (duelLocation.distanceSquared(challenger.getLocation()) > (DUEL_RADIUS * DUEL_RADIUS)) {
                endDuel(IDuel.DuelResult.FORFEIT);
                challenger.sendMessage(ChatColor.RED + "You left the duel area and forfeited the duel!");
            } else if (duelLocation.distanceSquared(defender.getLocation()) > (DUEL_RADIUS * DUEL_RADIUS)) {
                endDuel(IDuel.DuelResult.FORFEIT);
                defender.sendMessage(ChatColor.RED + "You left the duel area and forfeited the duel!");
            }
        }
        RunicPvP.getDuelManager().getCurrentDuels().remove(this);
    }

    public static int getDuelRadius() {
        return DUEL_RADIUS;
    }
}
