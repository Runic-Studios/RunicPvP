package com.runicrealms.plugin.pvp.shop;

import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.plugin.common.util.ChatUtils;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.pvp.RunicPvP;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            lore.add(ChatColor.GRAY + "Lv. Min " + ChatColor.WHITE + RunicCommon.getPvPAPI().getMinimumOutlawLevel());
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
        ArrayList<RunicShopItem> shopItems = new ArrayList<>();
        shopItems.add(new RunicShopItem(0, iconWithLore(toggleOutlawIcon()), runShopBuy()));
        new RunicShopGeneric(9, ChatColor.YELLOW + "Head Outlaw Garrett", Arrays.asList(55, 56), shopItems);
    }

    private RunicItemRunnable runShopBuy() {
        return RunicCommon.getPvPAPI()::toggleOutlaw;
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
