package com.runicrealms.plugin.conquest;

import com.runicrealms.plugin.RunicPvP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.Set;

public class ConquestManager {

    private static final int CONQUEST_SPAWN_TIME = 3; // 30
    Conquest activeConquest;
    Set<Location> lootChests;

    /**
     *
     */
    public ConquestManager() {

        // announce (30, 15, 5) min ahead
        beginSpawnTask();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(RunicPvP.inst(), () -> {
            activeConquest = activateConquest();
            beginLocationCheck();
        }, CONQUEST_SPAWN_TIME);
    }

    /**
     *
     */
    private void beginSpawnTask() { }

    /**
     *
     * @return
     */
    private Conquest activateConquest() {
        Random rand = new Random();
        ConquestEnum conquestEnum = ConquestEnum.values()[rand.nextInt(2)]; // inclusive, exclusive
        return new Conquest(conquestEnum.getCapturePoint(), conquestEnum.getMythicMob(), conquestEnum.getLootChest());
    }

    /**
     *
     */
    private void beginLocationCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (activeConquest.getCapturePoint().currentProgress >= activeConquest.getCapturePoint().getTotalProgress()) {
                    this.cancel();
                    shutdownConquest();
                }
                Bukkit.broadcastMessage("Location check"); // todo: write this logic in capture point
            }
        }.runTaskTimerAsynchronously(RunicPvP.inst(), 0, 5L);
    }

    /**
     *
     */
    private void shutdownConquest() {
        activeConquest.spawnLootChest();
        activeConquest.getCapturePoint().captureThePoint();
        activeConquest = null; // clear conquest
    }

    public Set<Location> getLootChests() {
        return lootChests;
    }
}
