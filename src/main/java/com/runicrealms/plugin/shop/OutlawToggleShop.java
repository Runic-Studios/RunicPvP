package com.runicrealms.plugin.shop;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicItemShop;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OutlawToggleShop implements RunicItemShop {

    private static final int LOAD_DELAY = 10;
    private final Map<Integer, RunicShopItem> availableItems;

    public OutlawToggleShop() {
        availableItems = new HashMap<>();
        Bukkit.getScheduler().scheduleSyncDelayedTask(RunicPvP.inst(), () -> {
            try {
                ItemStack toggleItem = new ItemStack(Material.STONE_SWORD);
                ItemMeta meta = toggleItem.getItemMeta();
                meta.setLore(Collections.singletonList(""));
                toggleItem.setItemMeta(meta);
                RunicShopItem runicShopItem = new RunicShopItem(0, "coin", iconWithLore(toggleItem), runShopBuy());
                runicShopItem.setRemovePayment(false);
                availableItems.put(4, runicShopItem);
            } catch (Exception e) {
                Bukkit.getLogger().info(ChatColor.DARK_RED + "Error: runic item template id not found!");
                e.printStackTrace();
            }
            RunicCoreAPI.registerRunicItemShop(this);
        }, LOAD_DELAY * 20L);
    }

    @Override
    public Map<Integer, RunicShopItem> getContents() {
        return availableItems;
    }

    /**
     * Size of the shop minus the title row
     * @return size of shop minus title row (smallest size 9)
     */
    @Override
    public int getShopSize() {
        return 9;
    }

    @Override
    public ItemStack getIcon() {
        ItemStack vendorItem = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = vendorItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Toggle Outlaw Mode");
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "if youre above, will reset your score!"));
            vendorItem.setItemMeta(meta);
        }
        return vendorItem;
    }

    /**
     * From RunicNPCS
     * @return ID of NPC in config
     */
    @Override
    public Collection<Integer> getNpcIds() {
        return Arrays.asList(55, 56);
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Head Outlaw Garrett";
    }

    private RunicItemRunnable runShopBuy() {
        // attempt to give player item (does not drop on floor)
        // player.getInventory().addItem(tierSetItem);
        return RunicPvPAPI::toggleOutlaw;
    }

    private ItemStack iconWithLore(ItemStack is) {
        ItemStack iconWithLore = is.clone();
        ItemMeta meta = iconWithLore.getItemMeta();
        if (meta != null && meta.getLore() != null) {
            List<String> lore = meta.getLore();
            lore.addAll(Arrays.asList(
                    "This will toggle outaw and reset score if",
                    "",
                    ChatColor.GOLD + "Price: " + ChatColor.GREEN + ChatColor.BOLD + "FREE"));
            meta.setLore(lore);
            iconWithLore.setItemMeta(meta);
        }
        return iconWithLore;
    }
}
