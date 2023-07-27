package com.runicrealms.plugin.pvp.duel;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IDuel {

    enum DuelResult {
        VICTORY,
        DEFEAT,
        FORFEIT
    }

    Player getChallenger();
    Player getDefender();
    Location getDuelLocation();
    DuelResult getDuelResult();

    void setDuelResult(DuelResult duelResult);
    void endDuel(DuelResult duelResult);
}

