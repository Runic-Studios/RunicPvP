package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicPvP;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Duel implements IDuel {

    private final Player challenger;
    private final Player defender;
    private DuelResult duelResult;

    public Duel(Player challenger, Player defender) {
        this.challenger = challenger;
        this.defender = defender;
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
        return challenger.getLocation();
    }

    @Override
    public DuelResult getDuelResult() {
        return null;
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
        }
        RunicPvP.getDuelManager().getCurrentDuels().remove(this);
    }
}
