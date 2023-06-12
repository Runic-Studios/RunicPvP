package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.events.RunicCombatExpEvent;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Gives additional experience to outlawed players
 */
public class OutlawExpListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onExpGain(RunicCombatExpEvent event) {
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
        if (!RunicPvP.getAPI().isOutlaw(event.getPlayer(), slot)) return;
        if (event.isCancelled()) return;
        if (event.getRunicExpSource() != RunicCombatExpEvent.RunicExpSource.MOB) return; // only mobs
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("dungeons")) return; // no outlaw bonus in dungeons
        event.setBonus(RunicCombatExpEvent.BonusType.OUTLAW, RunicPvP.getOutlawBonusExpPercent());
    }
}
