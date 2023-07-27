package com.runicrealms.plugin.pvp.conquest;

import com.runicrealms.plugin.item.lootchests.LootChest;
import io.lumine.mythic.api.mobs.MythicMob;

public enum ConquestEnum {

    POINT_1(capturePoint1(), summoner1(), lootChest1()),
    POINT_2(capturePoint2(), summoner2(), lootChest2());

    CapturePoint capturePoint;
    MythicMob mythicMob;
    LootChest lootChest;

    ConquestEnum(CapturePoint capturePoint, MythicMob mythicMob, LootChest lootChest) {
        this.capturePoint = capturePoint;
        this.mythicMob = mythicMob;
        this.lootChest = lootChest;
    }

    static CapturePoint capturePoint1() {
        return null;
    }

    static CapturePoint capturePoint2() {
        return null;
    }

    static MythicMob summoner1() {
        return null;
    }

    static MythicMob summoner2() {
        return null;
    }

    static LootChest lootChest1() {
        return null;
    }

    static LootChest lootChest2() {
        return null;
    }

    public CapturePoint getCapturePoint() {
        return capturePoint;
    }

    public MythicMob getMythicMob() {
        return mythicMob;
    }

    public LootChest getLootChest() {
        return lootChest;
    }
}
