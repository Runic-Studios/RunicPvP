package com.runicrealms.plugin.api.event;

import com.runicrealms.plugin.events.EnterCombatEvent;
import com.runicrealms.plugin.player.CombatManager;
import org.bukkit.entity.Player;

/**
 * Called when two players engage in combat
 */
public class RunicPvPEvent extends EnterCombatEvent {

    private final Player victim;

    public RunicPvPEvent(Player player, Player victim) {
        super(player, CombatManager.CombatType.PLAYER);
        this.victim = victim;
    }

    public Player getVictim() {
        return this.victim;
    }
}
