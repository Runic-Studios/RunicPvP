package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.cmd.CMDDuel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelRequest implements IDuelRequest {

    private static final String DUEL_PREFIX = CMDDuel.getDuelPrefix();
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
            sender.sendMessage
                    (
                            DUEL_PREFIX + ChatColor.RED + "You have sent a duel request to " +
                            ChatColor.WHITE + recipient.getName() +
                            ChatColor.RED + "!"
                    );
            recipient.sendMessage
                    (
                            DUEL_PREFIX + ChatColor.RED + "You have received a duel request from " +
                            ChatColor.WHITE + sender.getName() +
                            ChatColor.RED + "! Type " +
                            ChatColor.GREEN + "/duel accept " +
                            ChatColor.RED + "or " +
                            ChatColor.DARK_RED + "/duel deny"
                    );

        } else if (duelRequestResult == DuelRequestResult.ACCEPTED) {
            sender.sendMessage
                    (
                            DUEL_PREFIX + ChatColor.WHITE + recipient.getName() +
                            ChatColor.GREEN + " has accepted your duel request!"
                    );
            recipient.sendMessage(DUEL_PREFIX + ChatColor.GREEN + "Duel accepted!");
            RunicPvP.getDuelManager().getDuelRequests().remove(this);
            Duel duel = new Duel(sender, recipient);
            RunicPvP.getDuelManager().getCurrentDuels().add(duel);

        } else if (duelRequestResult == DuelRequestResult.DENIED) {
            sender.sendMessage
                    (
                            DUEL_PREFIX + ChatColor.WHITE + recipient.getName() +
                            ChatColor.DARK_RED + " has denied your duel request!"
                    );
            recipient.sendMessage(DUEL_PREFIX + ChatColor.DARK_RED + "Duel denied!");
            RunicPvP.getDuelManager().getDuelRequests().remove(this);

        } else {
            // timeout & errors
            getSender().sendMessage
                    (
                            DUEL_PREFIX + ChatColor.GRAY + "Your duel challenge to " +
                            ChatColor.WHITE + recipient.getName() +
                            ChatColor.GRAY + " has expired."
                    );
            getRecipient().sendMessage
                    (
                            DUEL_PREFIX + ChatColor.GRAY + "Your duel challenge from " +
                            ChatColor.WHITE + sender.getName() +
                            ChatColor.GRAY + " has expired."
                    );
        }
        RunicPvP.getDuelManager().getDuelRequests().remove(this);
    }

    public static long getRequestTimeout() {
        return REQUEST_TIMEOUT;
    }
}
