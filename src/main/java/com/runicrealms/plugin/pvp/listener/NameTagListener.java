package com.runicrealms.plugin.pvp.listener;

import com.runicrealms.plugin.pvp.RunicPvP;
import com.runicrealms.plugin.api.event.NameTagEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Changes player's name color to red upon switching to outlaw
 */
public class NameTagListener implements Listener {


    @EventHandler(priority = EventPriority.LOW) // early
    public void onNameTagEvent(NameTagEvent event) {
        boolean isOutlaw = RunicPvP.getAPI().isOutlaw(event.getPlayer(), event.getSlot());
        if (isOutlaw) {
            event.setNameColor(ChatColor.DARK_RED);
        }
    }

}
