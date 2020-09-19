package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicPvP;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelRequest implements IDuelRequest {

    private static final long REQUEST_TIMEOUT = 20; // how long does a duel request last
    private final long requestTime;
    private final Player sender;
    private final Player recipient;
    private final DuelRequestResult duelRequestResult;

    public DuelRequest(Player sender, Player recipient) {
        this.requestTime = System.currentTimeMillis(); // used for timeout checks
        this.sender = sender;
        this.recipient = recipient;
        this.duelRequestResult = DuelRequestResult.SENT;
        processDuelRequest(duelRequestResult);
    }

    @Override
    public long getRequestTime() {
        return requestTime;
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
        return duelRequestResult;
    }

    @Override
    public void processDuelRequest(DuelRequestResult duelRequestResult) {
        if (duelRequestResult == DuelRequestResult.SENT) {

        } else if (duelRequestResult == DuelRequestResult.ACCEPTED) {

            recipient.sendMessage(ChatColor.GREEN + "Duel accepted!");
        } else if (duelRequestResult == DuelRequestResult.DENIED) {

        } else {
            // timeout & errors
            getSender().sendMessage
                    (
                            ChatColor.GRAY + "Your duel challenge to " +
                            ChatColor.WHITE + getRecipient().getName() +
                            ChatColor.GRAY + " has expired."
                    );
            getRecipient().sendMessage
                    (
                            ChatColor.GRAY + "Your duel challenge from " +
                            ChatColor.WHITE + getSender().getName() +
                            ChatColor.GRAY + " has expired."
                    );
        }
        RunicPvP.getDuelManager().getDuelRequests().remove(this);
    }

    public static long getRequestTimeout() {
        return REQUEST_TIMEOUT;
    }
}
