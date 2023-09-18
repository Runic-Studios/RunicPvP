package com.runicrealms.plugin.pvp.listener;

import com.runicrealms.plugin.pvp.RunicPvP;
import com.runicrealms.plugin.api.event.ScoreboardUpdateEvent;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ScoreboardListener implements Listener {

    @EventHandler(priority = EventPriority.LOW) // early
    public void onScoreboardUpdate(ScoreboardUpdateEvent event) {
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
        boolean isOutlaw = RunicPvP.getAPI().isOutlaw(event.getPlayer(), slot);
        event.setOutlaw(isOutlaw);
    }
}
