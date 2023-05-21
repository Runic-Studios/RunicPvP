package com.runicrealms.plugin.shop;

import com.runicrealms.plugin.RunicPvP;
import com.runicrealms.plugin.common.util.ChatUtils;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class PvPShopFactory {

    public PvPShopFactory() {
        loadOutlawToggleShop();
    }

    private ItemStack iconWithLore(ItemStack is) {
        ItemStack iconWithLore = is.clone();
        ItemMeta meta = iconWithLore.getItemMeta();
        if (meta != null && meta.getLore() != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Toggle Outlaw Mode");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Lv. Min " + ChatColor.WHITE + RunicPvP.getAPI().getMinimumOutlawLevel());
            lore.add("");
            lore.addAll(ChatUtils.formattedText(
                    "&7This will toggle &4&LOUTLAW &4&lMODE&7! While in this mode, " +
                            "you receive &f" + (int) (RunicPvP.getOutlawBonusExpPercent() * 100) +
                            "% &7additional experience from mobs and can be &cdamaged by other " +
                            "players &7while not in safe zones!"));
            meta.setLore(lore);
            iconWithLore.setItemMeta(meta);
        }
        return iconWithLore;
    }

    private void loadOutlawToggleShop() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        Map<String, Integer> requiredItems = new HashMap<>() {{
            put("coin", 0);
        }};
        shopItems.add(new RunicShopItem(requiredItems, iconWithLore(toggleOutlawIcon()), runShopBuy()));
        new RunicShopGeneric(9, ChatColor.YELLOW + "Head Outlaw Garrett", Arrays.asList(55, 56), shopItems);
    }

    private RunicItemRunnable runShopBuy() {
        return RunicPvP.getAPI()::toggleOutlaw;
    }

    public ItemStack toggleOutlawIcon() {
        ItemStack vendorItem = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = vendorItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Toggle Outlaw Mode");
            meta.setLore(Collections.singletonList(""));
            vendorItem.setItemMeta(meta);
        }
        return vendorItem;
    }
}
