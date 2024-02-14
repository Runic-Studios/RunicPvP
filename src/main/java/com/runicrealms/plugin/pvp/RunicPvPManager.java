package com.runicrealms.plugin.pvp;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.plugin.common.api.PvPData;
import com.runicrealms.plugin.common.api.RunicPvPAPI;
import com.runicrealms.plugin.rdb.RunicDatabase;
import com.runicrealms.plugin.rdb.event.CharacterDeleteEvent;
import com.runicrealms.plugin.rdb.event.CharacterQuitEvent;
import com.runicrealms.plugin.rdb.event.CharacterSelectEvent;
import com.runicrealms.plugin.utilities.NameTagColorHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Implementation of API class
 */
public class RunicPvPManager implements Listener, RunicPvPAPI {
    private final HashMap<UUID, PlayerPvPData> pvpDataMap = new HashMap<>();

    public RunicPvPManager() {
        Bukkit.getPluginManager().registerEvents(this, RunicPvP.inst());
    }

    @Override
    public boolean areDueling(Player playerOne, Player playerTwo) {
        return RunicPvP.getDuelManager().areDueling(playerOne, playerTwo);
    }

    @Override
    public boolean canCreatePvPEvent(Player player, Player victim) {
        if (RunicCommon.getPvPAPI().playersCanFight(player, victim)) {
            return !RunicCommon.getPvPAPI().areDueling(player, victim);
        } else {
            return false;
        }
    }

    @Override
    public int getMinimumOutlawLevel() {
        return RunicPvP.MINIMUM_OUTLAW_LEVEL;
    }

    @Override
    public boolean isDueling(Player player) {
        return RunicPvP.getDuelManager().isInDuel(player);
    }

    @Override
    public boolean isOutlaw(Player player, int slot) {
        return pvpDataMap.get(player.getUniqueId()).isOutlawEnabled(slot);
    }

    @Override
    public boolean isOutlaw(Player player) {
        return pvpDataMap.get(player.getUniqueId()).isOutlawEnabled();
    }

    @Override
    public boolean playersCanFight(Player player, Player victim) {
        // No combat in dungeon world
        if (player.getWorld().getName().equalsIgnoreCase("dungeons") || victim.getWorld().getName().equalsIgnoreCase("dungeons"))
            return false;
        if (RunicCore.getPartyAPI().isPartyMember(player.getUniqueId(), victim)) return false;
        int slotPlayer = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(player.getUniqueId());
        int slotVictim = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(victim.getUniqueId());
        boolean bothOutlaws = RunicCommon.getPvPAPI().isOutlaw(player, slotPlayer) && RunicCommon.getPvPAPI().isOutlaw(victim, slotVictim);
        boolean areDueling = RunicCommon.getPvPAPI().areDueling(player, victim);
        boolean damagerInSafezone = RunicCore.getRegionAPI().isSafezone(player.getLocation());
        boolean victimInSafezone = RunicCore.getRegionAPI().isSafezone(victim.getLocation());
        return (bothOutlaws || areDueling) && (!damagerInSafezone && !victimInSafezone);
    }

    @Override
    public void toggleOutlaw(Player player) {
        toggleOutlaw(player, false);
    }

    @Override
    public void toggleOutlaw(Player player, boolean ignoreLevelRequirement) {
        if (!ignoreLevelRequirement && player.getLevel() < RunicPvP.MINIMUM_OUTLAW_LEVEL) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            player.sendMessage
                    (
                            ChatColor.RED + "You must reach level " +
                                    ChatColor.WHITE + RunicPvP.MINIMUM_OUTLAW_LEVEL +
                                    ChatColor.RED + " to become an outlaw!"
                    );
            return;
        }
        if (RunicCore.getPartyAPI().hasParty(player.getUniqueId())) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "You cannot toggle outlaw mode while in a party!");
            return;
        }
        UUID uuid = player.getUniqueId();
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(uuid);
        // Toggle their current outlaw status from whatever it currently is
        boolean isOutlaw = isOutlaw(player, slot);
        this.pvpDataMap.get(player.getUniqueId()).setOutlawEnabled(slot, !isOutlaw);
        NameTagColorHandler.setGlobalColor(player, !isOutlaw ? NameTagColorHandler.NameColor.OUTlAW : NameTagColorHandler.NameColor.DEFAULT);
        RunicCore.getScoreboardAPI().updatePlayerScoreboard(player);
    }

    @Override
    public @Nullable PvPData getPvPData(Player player) {
        return pvpDataMap.get(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCharacterDelete(CharacterDeleteEvent event) {
        PlayerPvPData data = pvpDataMap.get(event.getPlayer().getUniqueId());

        if (data == null) {
            RunicPvP.inst().getLogger().log(Level.SEVERE, "There was an error getting " + event.getPlayer().getName() + "'s pvp data from the cache!");
            return;
        }

        data.setOutlawEnabled(event.getSlot(), false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCharacterQuit(CharacterQuitEvent event) {
        pvpDataMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCharacterSelect(CharacterSelectEvent event) {
        event.getPluginsToLoadData().add("pvp");
        long startMillis = System.currentTimeMillis();
        pvpDataMap.put(event.getPlayer().getUniqueId(), new PlayerPvPData(
                event.getPlayer().getUniqueId(),
                () -> {
                    event.getPluginsToLoadData().remove("pvp");
                    Bukkit.getLogger().log(Level.INFO, "RunicPvP took: " + (System.currentTimeMillis() - startMillis) + "ms to load");
                }));
    }

}
