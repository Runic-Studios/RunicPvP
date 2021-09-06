package com.runicrealms.plugin.event;

import com.runicrealms.plugin.events.EnterCombatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import org.bukkit.event.HandlerList;

/**
 * Called when two players engage in combat
 */
public class RunicPvPEvent extends EnterCombatEvent implements Cancellable {

    private final Player victim;

    public RunicPvPEvent(Player player, Player victim) {
        super(player);
        this.victim = victim;
    }

    public Player getVictim() {
        return this.victim;
    }

    private static final HandlerList handlers = new HandlerList();

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
