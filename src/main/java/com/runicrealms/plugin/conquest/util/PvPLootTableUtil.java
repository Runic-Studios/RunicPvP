package com.runicrealms.plugin.conquest.util;

import com.runicrealms.plugin.item.lootchests.WeightedRandomBag;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PvPLootTableUtil {

    public static WeightedRandomBag<ItemStack> conquestLootTable() {

        Random rand = new Random();

        // create a loot table object
        WeightedRandomBag<ItemStack> epicLootTable = new WeightedRandomBag<>();

//        // add the gear chance
//        ItemStack epicArmor = ItemUtils.generateEpicArmor();
//        ItemStack epicWeapon = ItemUtil.generateEpicWeapon();
//
//        // currency
//        ItemStack coin = CurrencyUtil.goldCoin(rand.nextInt(10 - 5) + 5);
//
//        // food
//        ItemStack bread = RunicCoreAPI.getMythicItem("Bread", rand, 2, 4);
//
//        // materials
//        ItemStack spruceWood = RunicCoreAPI.getMythicItem("SpruceWood", rand, 3, 5);
//        ItemStack oakWood = RunicCoreAPI.getMythicItem("OakWood", rand, 3, 5);
//        ItemStack thread = RunicCoreAPI.getMythicItem("Thread", rand, 3, 5);
//        ItemStack animalHide = RunicCoreAPI.getMythicItem("AnimalHide", rand, 3, 5);
//        ItemStack uncutRuby = RunicCoreAPI.getMythicItem("UncutRuby", rand, 2, 3);
//        ItemStack uncutSapphire = RunicCoreAPI.getMythicItem("UncutSapphire", rand, 2, 3);
//        ItemStack uncutOpal = RunicCoreAPI.getMythicItem("UncutOpal", rand, 2, 3);
//        ItemStack uncutEmerald = RunicCoreAPI.getMythicItem("UncutEmerald", rand, 2, 3);
//        ItemStack uncutDiamond = RunicCoreAPI.getMythicItem("UncutDiamond", rand, 2, 3);
//        ItemStack bottle = RunicCoreAPI.getMythicItem("Bottle", rand, 3, 5);
//        ItemStack pufferfish = RunicCoreAPI.getMythicItem("Pufferfish", rand, 2, 3);
//
//        // gatherting tools (tier 4)
//        ItemStack gatheringAxe = GatheringUtil.getGatheringTool(Material.IRON_AXE, 4);
//        ItemStack gathertingHoe = GatheringUtil.getGatheringTool(Material.IRON_HOE, 4);
//        ItemStack gatheringPick = GatheringUtil.getGatheringTool(Material.IRON_PICKAXE, 4);
//        ItemStack gatheringRod = GatheringUtil.getGatheringTool(Material.FISHING_ROD, 4);
//
//        // potions
//        ItemStack healthPotion = ItemUtils.generatePotion("healing", 60);
//        ItemStack manaPotion = ItemUtils.generatePotion("mana", 60);
//
//        // add entries to table
//        epicLootTable.addEntry(epicArmor,  15.0);
//        epicLootTable.addEntry(epicWeapon,  15.0);
//        epicLootTable.addEntry(coin, 50.0);
//        epicLootTable.addEntry(bread, 35.0);
//
//        epicLootTable.addEntry(spruceWood, 8.0);
//        epicLootTable.addEntry(oakWood, 8.0);
//        epicLootTable.addEntry(thread, 8.0);
//        epicLootTable.addEntry(animalHide, 8.0);
//        epicLootTable.addEntry(uncutRuby, 8.0);
//        epicLootTable.addEntry(uncutSapphire, 8.0);
//        epicLootTable.addEntry(uncutOpal, 8.0);
//        epicLootTable.addEntry(uncutEmerald, 8.0);
//        epicLootTable.addEntry(uncutDiamond, 8.0);
//
//        epicLootTable.addEntry(bottle, 8.0);
//        epicLootTable.addEntry(pufferfish, 8.0);
//
//        epicLootTable.addEntry(gatheringAxe, 2.0);
//        epicLootTable.addEntry(gathertingHoe, 2.0);
//        epicLootTable.addEntry(gatheringPick, 2.0);
//        epicLootTable.addEntry(gatheringRod, 4.0);
//
//        epicLootTable.addEntry(healthPotion, 15.0);
//        epicLootTable.addEntry(manaPotion, 15.0);

        return epicLootTable;
    }
}
