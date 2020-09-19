package com.runicrealms.plugin.duel;

import org.bukkit.entity.Player;

public interface IDuelRequest {

    enum DuelRequestResult {
        SENT,
        ACCEPTED,
        DENIED,
        TIMEOUT
    }

    long getRequestTime();
    Player getSender();
    Player getRecipient();
    DuelRequestResult getDuelRequestResult();

    /*
    ???
     */
    void processDuelRequest(DuelRequestResult duelRequestResult);

}
