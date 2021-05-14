package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.events.RunicExpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/*
Gives additional experience and player reputation to outlaws
 */
public class OutlawBonusListener implements Listener {

    private static final double PERCENT_BONUS = 0.20;

    @EventHandler(priority = EventPriority.LOWEST) // fires FIRST
    public void onExpGain(RunicExpEvent e) {
        if (!RunicPvPAPI.isOutlaw(e.getPlayer())) return;
        if (e.isCancelled()) return;
        if (e.getRunicExpSource() != RunicExpEvent.RunicExpSource.MOB
                && e.getRunicExpSource() != RunicExpEvent.RunicExpSource.PARTY) return; // only mobs or party kills
        int bonus = (int) (e.getOriginalAmount() * PERCENT_BONUS);
        e.setFinalAmount(e.getFinalAmount() + bonus);
    }
}
