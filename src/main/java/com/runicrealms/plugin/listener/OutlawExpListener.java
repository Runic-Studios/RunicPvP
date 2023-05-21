package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.events.RunicExpEvent;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Gives additional experience to outlawed players
 */
public class OutlawExpListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onExpGain(RunicExpEvent event) {
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
        if (!RunicPvP.getAPI().isOutlaw(event.getPlayer(), slot)) return;
        if (event.isCancelled()) return;
        if (event.getRunicExpSource() != RunicExpEvent.RunicExpSource.MOB) return; // only mobs
        int bonus = (int) (event.getOriginalAmount() * RunicPvP.getOutlawBonusExpPercent());
        event.setFinalAmount(event.getFinalAmount() + bonus);
    }
}
