package com.runicrealms.plugin.duel;

import org.bukkit.entity.Player;

public class DuelRequest implements IDuelRequest {

    private final Player sender;
    private final Player recipient;
    private DuelRequestResult duelRequestResult;

    public DuelRequest(Player sender, Player recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

    @Override
    public Player getSender() {
        return sender;
    }

    @Override
    public Player getRecipient() {
        return recipient;
    }

    @Override
    public DuelRequestResult getDuelRequestResult() {
        return null;
    }

    @Override
    public void processDuelRequest(DuelRequestResult duelRequestResult) {

    }
}
