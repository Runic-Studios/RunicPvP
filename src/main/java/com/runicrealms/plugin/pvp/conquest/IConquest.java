package com.runicrealms.plugin.pvp.conquest;

import com.runicrealms.plugin.runicitems.loot.chest.LootChest;
import io.lumine.mythic.api.mobs.MythicMob;

public interface IConquest {

    CapturePoint getCapturePoint(); // radius for now, update to grid to be more accurate

    MythicMob getMythicMob();

    LootChest getLootChest();

    void spawnLootChest();
}
