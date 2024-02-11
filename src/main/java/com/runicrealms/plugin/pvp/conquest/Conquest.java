package com.runicrealms.plugin.pvp.conquest;

import com.runicrealms.plugin.pvp.RunicPvP;
import com.runicrealms.plugin.runicitems.loot.chest.LootChest;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.Material;

public class Conquest implements IConquest {

    CapturePoint capturePoint;
    MythicMob mythicMob;
    LootChest lootChest;

    public Conquest(CapturePoint capturePoint, MythicMob mythicMob, LootChest lootChest) {
        this.capturePoint = capturePoint;
        this.mythicMob = mythicMob;
        this.lootChest = lootChest;
    }

    @Override
    public CapturePoint getCapturePoint() {
        return capturePoint;
    }

    @Override
    public MythicMob getMythicMob() {
        return mythicMob;
    }

    @Override
    public LootChest getLootChest() {
        return lootChest;
    }

    @Override
    public void spawnLootChest() {
        Location loc = capturePoint.getLocation();
        loc.getBlock().setType(Material.CHEST);
        RunicPvP.getConquestManager().getLootChests().add(loc);
    }
}
