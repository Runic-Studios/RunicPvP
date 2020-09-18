package com.runicrealms.plugin.duel;

import org.bukkit.entity.Player;

public interface IDuelRequest {

    enum DuelRequestResult {
        ACCEPTED,
        DENIED,
        TIMEOUT
    }

    Player getSender();
    Player getRecipient();
    DuelRequestResult getDuelRequestResult();

    /*
    ???
     */
    void processDuelRequest(DuelRequestResult duelRequestResult);

}
