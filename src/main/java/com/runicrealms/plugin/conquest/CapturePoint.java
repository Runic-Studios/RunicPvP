package com.runicrealms.plugin.conquest;

import com.runicrealms.plugin.party.Party;
import org.bukkit.Location;

import java.util.HashMap;

public class CapturePoint implements ICapturePoint {

    int currentProgress;
    int totalProgress;
    int radius;
    Location location;
    HashMap<Party, Integer> activeParties;

    public CapturePoint(int radius, Location location) {
        currentProgress = 0;
        totalProgress = 100;
        this.radius = radius;
        this.location = location;
        activeParties = new HashMap<>();
    }

    @Override
    public int getCurrentProgress() {
        return currentProgress;
    }

    @Override
    public int getTotalProgress() {
        return totalProgress;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public HashMap<Party, Integer> getActiveParties() {
        return activeParties;
    }

    @Override
    public void tickPoint() {
        // todo: grab all parties in radius, then associate party w/ value. if more than one, say 'contested' and print nothing
    }

    @Override
    public void captureThePoint() {
        // todo: firework, print message 'its over!'
    }
}
