package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.command.CMDDuel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DuelRequest implements IDuelRequest {

    private static final String DUEL_PREFIX = CMDDuel.getDuelPrefix();
    private static final long REQUEST_TIMEOUT = 20; // how long does a duel request last
    private final long requestTime;
    private final Player sender;
    private final Player recipient;
    private final DuelRequestResult duelRequestResult;
    private boolean countdownStarted;

    public DuelRequest(Player sender, Player recipient) {
        this.countdownStarted = false;
        this.requestTime = System.currentTimeMillis(); // used for timeout checks
        this.sender = sender;
        this.recipient = recipient;
        this.duelRequestResult = DuelRequestResult.SENT;
        processDuelRequest(duelRequestResult);
    }

    public static long getRequestTimeout() {
        return REQUEST_TIMEOUT;
    }

    @Override
    public void beginCountdown(Player challenger, Player defender) {
        RunicPvP.getDuelManager().getCountdownSet().add(challenger.getUniqueId());
        RunicPvP.getDuelManager().getCountdownSet().add(defender.getUniqueId());
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
                    RunicPvP.getDuelManager().getCountdownSet().remove(challenger.getUniqueId());
                    RunicPvP.getDuelManager().getCountdownSet().remove(defender.getUniqueId());
                    this.cancel();
                    if (!RunicCore.getCharacterAPI().getLoadedCharacters().contains(challenger.getUniqueId())) { // challenger quits
                        duel.setDuelResult(IDuel.DuelResult.DEFEAT);
                        duel.endDuel(duel.getDuelResult());
                    }
                    if (!RunicCore.getCharacterAPI().getLoadedCharacters().contains(defender.getUniqueId())) { // defender quits
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

    @Override
    public boolean countdownStarted() {
        return countdownStarted;
    }

    @Override
    public DuelRequestResult getDuelRequestResult() {
        return duelRequestResult;
    }

    @Override
    public Player getRecipient() {
        return recipient;
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
                                    ChatColor.RED + "!"
                    );
            sendClickableMessages(sender, recipient);

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

    /**
     * Sends the clickable component of the duel message
     *
     * @param sender    who sent the request
     * @param recipient of the request
     */
    private void sendClickableMessages(Player sender, Player recipient) {
        String clickableAccept = ChatColor.GREEN + "[Accept]";
        String clickableDeny = ChatColor.DARK_RED + "[Deny]";
        TextComponent componentAccept = new TextComponent(clickableAccept);
        componentAccept.setClickEvent(new ClickEvent
                (
                        ClickEvent.Action.RUN_COMMAND,
                        "/duel accept"
                ));
        componentAccept.setHoverEvent(new HoverEvent
                (
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(ChatColor.GREEN + "Accept " + ChatColor.WHITE + sender.getName() + ChatColor.GREEN + "'s duel")
                ));
        TextComponent componentDeny = new TextComponent(clickableDeny);
        componentDeny.setClickEvent(new ClickEvent
                (
                        ClickEvent.Action.RUN_COMMAND,
                        "/duel deny"
                ));
        componentDeny.setHoverEvent(new HoverEvent
                (
                        HoverEvent.Action.SHOW_TEXT,
                        new Text(ChatColor.DARK_RED + "Deny " + ChatColor.WHITE + sender.getName() + ChatColor.DARK_RED + "'s duel")
                ));
        recipient.spigot().sendMessage
                (
                        new TextComponent(ChatColor.RED + "Click "),
                        componentAccept,
                        new TextComponent(ChatColor.RED + " or "),
                        componentDeny
                );
    }
}
