package com.runicrealms.plugin.pvp.duel;

import org.bukkit.entity.Player;

public interface IDuelRequest {

    void beginCountdown(Player challenger, Player defender);

    boolean countdownStarted();

    DuelRequestResult getDuelRequestResult();

    Player getRecipient();

    long getRequestTime();

    Player getSender();

    void processDuelRequest(DuelRequestResult duelRequestResult);

    enum DuelRequestResult {
        SENT,
        ACCEPTED,
        DENIED,
        TIMEOUT
    }

}
