package com.runicrealms.plugin.conquest;

import com.runicrealms.plugin.item.lootchests.LootChest;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public interface IConquest {

    CapturePoint getCapturePoint(); // radius for now, update to grid to be more accurate
    MythicMob getMythicMob();
    LootChest getLootChest();

    void spawnLootChest();
}
