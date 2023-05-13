package com.runicrealms.plugin.listener;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.event.ScoreboardUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ScoreboardListener implements Listener {

    @EventHandler(priority = EventPriority.LOW) // early
    public void onScoreboardUpdate(ScoreboardUpdateEvent event) {
        int slot = RunicCore.getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
        boolean isOutlaw = RunicPvP.getAPI().isOutlaw(event.getPlayer(), slot);
        event.setOutlaw(isOutlaw);
    }
}