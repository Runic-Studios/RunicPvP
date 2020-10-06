package com.runicrealms.plugin.conquest;

import org.bukkit.Location;

import java.util.Set;

public class ConquestManager {

    Conquest activeConquest;
    Set<Location> lootChests;

    public ConquestManager() {
        // announce 3 (30) min ahead
        // spawn, set activeConquest to the CP
        // location checks once it spawns
        // remove, set activeConquest to null
    }

    private void activateConquest() {
        // rand
        // select from enum
        // make new conquest w/ enum values
    }

    public Set<Location> getLootChests() {
        return lootChests;
    }
}
