package com.runicrealms.plugin.duel;

import org.bukkit.entity.Player;

public interface IDuelRequest {

    enum DuelRequestResult {
        SENT,
        ACCEPTED,
        DENIED,
        TIMEOUT
    }

    boolean countdownStarted();
    long getRequestTime();
    Player getSender();
    Player getRecipient();
    DuelRequestResult getDuelRequestResult();

    /*
    ???
     */
    void beginCountdown(Player challenger, Player defender);
    void processDuelRequest(DuelRequestResult duelRequestResult);

}
