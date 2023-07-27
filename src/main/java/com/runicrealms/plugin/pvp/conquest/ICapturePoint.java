package com.runicrealms.plugin.pvp.conquest;

import com.runicrealms.plugin.party.Party;
import org.bukkit.Location;

import java.util.HashMap;

public interface ICapturePoint {

    int getCurrentProgress();
    int getTotalProgress(); // time needed to cap the point
    int getRadius(); // how big is the point
    Location getLocation();
    HashMap<Party, Integer> getActiveParties();

    void tickPoint();
    void captureThePoint();

}
