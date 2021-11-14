package com.runicrealms.plugin.shop;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.manager.OutlawManager;
import com.runicrealms.plugin.utilities.ChatUtils;
import com.runicrealms.plugin.utilities.CurrencyUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PvPShopFactory {

    public PvPShopFactory() {
        getOutlawToggleShop();
        getOutlawVendor();
    }

    private RunicShopGeneric getOutlawToggleShop() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        shopItems.add(new RunicShopItem(0, "Coin", iconWithLore(toggleOutlawIcon()), runShopBuy()));
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Head Outlaw Garrett", Arrays.asList(55, 56), shopItems);
    }

    private static final int OUTLAW_VENDOR_RATING_THRESHOLD = 2000;
    private final ItemStack outlawFlail = RunicItemsAPI.generateItemFromTemplate("outlaw-remorse").generateItem();
    private final ItemStack outlawWand = RunicItemsAPI.generateItemFromTemplate("outlaw-cursed-blackwood-twig").generateItem();
    private final ItemStack outlawDagger = RunicItemsAPI.generateItemFromTemplate("outlaw-vagabonds-letter-opener").generateItem();
    private final ItemStack outlawShield = RunicItemsAPI.generateItemFromTemplate("outlaw-fallen-soldiers-shield").generateItem();

    private RunicShopGeneric getOutlawVendor() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        shopItems.add(new RunicShopItem(750, "Coin", outlawFlail, runOutlawVendorBuy(outlawFlail, 750)));
        shopItems.add(new RunicShopItem(750, "Coin", outlawWand, runOutlawVendorBuy(outlawWand, 750)));
        shopItems.add(new RunicShopItem(750, "Coin", outlawDagger, runOutlawVendorBuy(outlawDagger, 750)));
        shopItems.add(new RunicShopItem(750, "Coin", outlawShield, runOutlawVendorBuy(outlawShield, 750)));
        shopItems.forEach(runicShopItem -> runicShopItem.setRemovePayment(false));
        return new RunicShopGeneric(45, ChatColor.YELLOW + "Outlaw Vendor", Arrays.asList(53, 54), shopItems);
    }

    /**
     * This...
     *
     * @param itemToPurchase
     * @param price
     * @return
     */
    private RunicItemRunnable runOutlawVendorBuy(ItemStack itemToPurchase, int price) {
        return player -> {
            int outlawRating = RunicCoreAPI.getPlayerCache(player).getRating();
            if (outlawRating >= OUTLAW_VENDOR_RATING_THRESHOLD) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "You've purchased an Outlaw Vendor item!");
                ItemRemover.takeItem(player, CurrencyUtil.goldCoin(), price);
                RunicItemsAPI.addItem(player.getInventory(), itemToPurchase);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                player.sendMessage
                        (
                                ChatColor.RED + "You must have earned at least " +
                                        ChatColor.YELLOW + OUTLAW_VENDOR_RATING_THRESHOLD + ChatColor.RED +
                                        " outlaw rating to purchase this!"
                        );
            }
        };
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
            lore.add(ChatColor.GRAY + "Lv. Min " + ChatColor.WHITE + RunicPvPAPI.getMinimumOutlawLevel());
            lore.add("");
            lore.addAll(ChatUtils.formattedText(
                    "&7This will toggle &4&LOUTLAW &4&lMODE&7! While in this mode, " +
                            "you receive &f" + (int) (OutlawManager.getPercentBonus() * 100) +
                            "% &7additional experience from mobs and can be &cdamaged by other " +
                            "players &7while not in safe zones! Disabling Outlaw Mode " +
                            "while above the default rating (" + RunicCoreAPI.getBaseOutlawRating() +
                            ") will RESET your rating."));
            meta.setLore(lore);
            iconWithLore.setItemMeta(meta);
        }
        return iconWithLore;
    }
}
