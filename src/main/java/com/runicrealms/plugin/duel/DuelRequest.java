package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.cmd.CMDDuel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DuelRequest implements IDuelRequest {

    private static final String DUEL_PREFIX = CMDDuel.getDuelPrefix();
    private static final long REQUEST_TIMEOUT = 20; // how long does a duel request last

    private boolean countdownStarted;
    private final long requestTime;
    private final Player sender;
    private final Player recipient;
    private final DuelRequestResult duelRequestResult;

    public DuelRequest(Player sender, Player recipient) {
        this.countdownStarted = false;
        this.requestTime = System.currentTimeMillis(); // used for timeout checks
        this.sender = sender;
        this.recipient = recipient;
        this.duelRequestResult = DuelRequestResult.SENT;
        processDuelRequest(duelRequestResult);
    }

    @Override
    public boolean countdownStarted() {
        return countdownStarted;
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
            this.countdownStarted = true; // prevent request from expiring
            beginCountdown(sender, recipient);

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
            RunicPvP.getDuelManager().getDuelRequests().remove(this);
        }
    }

    @Override
    public void beginCountdown(Player challenger, Player defender) {
        DuelRequest request = this;
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= Duel.getCountdown()) {
                    challenger.sendTitle(ChatColor.DARK_RED + "Fight!", "", 10, 40, 10);
                    defender.sendTitle(ChatColor.DARK_RED + "Fight!", "", 10, 40, 10);
                    RunicPvP.getDuelManager().getDuelRequests().remove(request);
                    Duel duel = new Duel(challenger, defender);
                    RunicPvP.getDuelManager().getCurrentDuels().add(duel);
                    this.cancel();
                    if (!RunicCore.getCacheManager().getLoadedPlayers().contains(challenger)) { // challenger quits
                        duel.setDuelResult(IDuel.DuelResult.DEFEAT);
                        duel.endDuel(duel.getDuelResult());
                    }
                    if (!RunicCore.getCacheManager().getLoadedPlayers().contains(defender)) { // defender quits
                        duel.setDuelResult(IDuel.DuelResult.VICTORY);
                        duel.endDuel(duel.getDuelResult());
                    }
                    return;
                }
                challenger.sendTitle
                        (
                                ChatColor.RED + "Duel beginning in ",
                                ChatColor.YELLOW + "" + (Duel.getCountdown() - count), 10, 40, 10
                        );
                defender.sendTitle
                        (
                                ChatColor.RED + "Duel beginning in ",
                                ChatColor.YELLOW + "" + (Duel.getCountdown() - count), 10, 40, 10
                        );
                count++;
            }
        }.runTaskTimerAsynchronously(RunicPvP.inst(), 0, 20L);
    }

    public static long getRequestTimeout() {
        return REQUEST_TIMEOUT;
    }
}
