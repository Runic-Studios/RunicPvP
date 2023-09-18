package com.runicrealms.plugin.pvp.listener;

import com.runicrealms.plugin.pvp.RunicPvP;
import com.runicrealms.plugin.events.RunicCombatExpEvent;
import com.runicrealms.plugin.professions.event.RunicGatheringExpEvent;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Gives additional experience to outlawed players
 */
public class OutlawExpListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCombatExpGain(RunicCombatExpEvent event) {
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
        if (!RunicPvP.getAPI().isOutlaw(event.getPlayer(), slot)) return;
        if (event.isCancelled()) return;
        if (event.getRunicExpSource() != RunicCombatExpEvent.RunicExpSource.MOB) return; // only mobs
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("dungeons")) return; // no outlaw bonus in dungeons
        event.setBonus(RunicCombatExpEvent.BonusType.OUTLAW, RunicPvP.getOutlawBonusExpPercent());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onGatheringExpGain(RunicGatheringExpEvent event) {
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
        if (!RunicPvP.getAPI().isOutlaw(event.getPlayer(), slot)) return;
        if (event.isCancelled()) return;
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("dungeons")) return; // no outlaw bonus in dungeons
        event.setBonus(RunicGatheringExpEvent.BonusType.OUTLAW, RunicPvP.getOutlawBonusExpPercent());
    }

}
