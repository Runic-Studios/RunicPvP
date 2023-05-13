package com.runicrealms.plugin.duel;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.command.CMDDuel;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Duel implements IDuel {
    private static final int COUNTDOWN = 5;
    private static final String DUEL_PREFIX = CMDDuel.getDuelPrefix();
    private static final int DUEL_RADIUS = 64; // max blocks players can leave from starting position before forfeiting
    private final Player challenger;
    private final Player defender;
    private final Location duelLocation;
    private DuelResult duelResult;

    public Duel(Player challenger, Player defender) {
        this.challenger = challenger;
        this.defender = defender;
        duelLocation = challenger.getLocation();
    }

    public static int getCountdown() {
        return COUNTDOWN;
    }

    public static int getDuelRadius() {
        return DUEL_RADIUS;
    }

    private void endMessage(Player winner, Player loser) {
        winner.sendMessage
                (
                        DUEL_PREFIX + "You won your duel against " +
                                ChatColor.WHITE + loser.getName() + ChatColor.RED + "!"
                );
        winner.playSound(winner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        winner.playSound(loser.getLocation(), Sound.ENTITY_PLAYER_DEATH, 0.5f, 1.0f);
        loser.sendMessage
                (
                        DUEL_PREFIX + "You lost your duel against " +
                                ChatColor.WHITE + winner.getName() + ChatColor.RED + "!"
                );
    }

    @Override
    public Player getChallenger() {
        return challenger;
    }

    @Override
    public Player getDefender() {
        return defender;
    }

    @Override
    public Location getDuelLocation() {
        return duelLocation;
    }

    @Override
    public DuelResult getDuelResult() {
        return duelResult;
    }

    @Override
    public void setDuelResult(DuelResult duelResult) {
        this.duelResult = duelResult;
    }

    @Override
    public void endDuel(DuelResult duelResult) {
        if (duelResult == DuelResult.VICTORY) {
            // challenger won
            endMessage(challenger, defender);
        } else if (duelResult == DuelResult.DEFEAT) {
            // challenger lost
            endMessage(defender, challenger);
        } else {
            // forfeit
            if (duelLocation.distanceSquared(challenger.getLocation()) > (DUEL_RADIUS * DUEL_RADIUS)) {
                challenger.sendMessage(DUEL_PREFIX + ChatColor.RED + "You left the duel area and forfeited the duel!");
                defender.sendMessage
                        (
                                DUEL_PREFIX + ChatColor.WHITE + challenger.getName() +
                                        ChatColor.RED + " left the duel area and forfeited the duel!"
                        );
                defender.playSound(defender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
            } else if (duelLocation.distanceSquared(defender.getLocation()) > (DUEL_RADIUS * DUEL_RADIUS)) {
                defender.sendMessage(DUEL_PREFIX + ChatColor.RED + "You left the duel area and forfeited the duel!");
                challenger.sendMessage
                        (
                                DUEL_PREFIX + ChatColor.WHITE + defender.getName() +
                                        ChatColor.RED + " left the duel area and forfeited the duel!"
                        );
                challenger.playSound(challenger.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
            }
        }
        RunicPvP.getDuelManager().getCurrentDuels().remove(this);
    }
}
