package com.runicrealms.plugin.shop;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.manager.OutlawManager;
import com.runicrealms.plugin.utilities.ChatUtils;
import com.runicrealms.plugin.utilities.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OutlawToggleShop {

    public OutlawToggleShop() {
        LinkedHashMap<ItemStack, RunicShopItem> shopItems = new LinkedHashMap<>();
        shopItems.put(toggleOutlawIcon(), new RunicShopItem(0, "Coin", iconWithLore(toggleOutlawIcon()), runShopBuy()));
        new RunicShopGeneric(9, ChatColor.YELLOW + "Head Outlaw Garrett", Arrays.asList(55, 56), shopItems);
    }

    private RunicItemRunnable runShopBuy() {
        return RunicPvPAPI::toggleOutlaw;
    }

    public ItemStack toggleOutlawIcon() {
        ItemStack vendorItem = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = vendorItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Head Outlaw Garrett");
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Toggle Outlaw Mode!"));
            vendorItem.setItemMeta(meta);
        }
        return vendorItem;
    }

    private ItemStack iconWithLore(ItemStack is) {
        ItemStack iconWithLore = is.clone();
        ItemMeta meta = iconWithLore.getItemMeta();
        if (meta != null && meta.getLore() != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Toggle Outlaw Mode");
            List<String> lore = meta.getLore();
            lore.addAll(ChatUtils.formattedText(
                    "&7This will toggle &4&LOUTLAW &4&lMODE&7! While in this mode, " +
                            "you receive &f" + (int) (OutlawManager.getPercentBonus() * 100) +
                            "% &7additional experience from mobs and can be &cdamaged by other " +
                            "players &7while not in safe zones! Disabling Outlaw Mode " +
                            "while above the default rating (" + RunicCoreAPI.getBaseOutlawRating() +
                            ") will RESET your rating."));
            lore.add("");
            lore.add(ColorUtil.format("&6Price: &a&lFREE"));
            meta.setLore(lore);
            iconWithLore.setItemMeta(meta);
        }
        return iconWithLore;
    }
}
