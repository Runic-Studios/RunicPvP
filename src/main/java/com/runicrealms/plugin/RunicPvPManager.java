package com.runicrealms.plugin;

import com.runicrealms.libs.taskchain.TaskChain;
import com.runicrealms.libs.taskchain.TaskChainAbortAction;
import com.runicrealms.plugin.api.RunicPvPAPI;
import com.runicrealms.plugin.character.api.CharacterDeleteEvent;
import com.runicrealms.plugin.character.api.CharacterQuitEvent;
import com.runicrealms.plugin.character.api.CharacterSelectEvent;
import com.runicrealms.plugin.database.event.MongoSaveEvent;
import com.runicrealms.plugin.model.CharacterField;
import com.runicrealms.plugin.model.OutlawData;
import com.runicrealms.plugin.model.PvpData;
import com.runicrealms.plugin.utilities.NametagHandler;
import com.runicrealms.runicitems.RunicItems;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Implementation of API class
 */
public class RunicPvPManager implements Listener, RunicPvPAPI {
    public static final TaskChainAbortAction<Player, String, ?> CONSOLE_LOG = new TaskChainAbortAction<>() {
        public void onAbort(TaskChain<?> chain, Player player, String message) {
            Bukkit.getLogger().log(Level.SEVERE, ChatColor.translateAlternateColorCodes('&', message));
        }
    };
    private final HashMap<UUID, PvpData> pvpDataMap = new HashMap<>();

    public RunicPvPManager() {
        Bukkit.getPluginManager().registerEvents(this, RunicPvP.inst());
    }

    @Override
    public boolean areDueling(Player playerOne, Player playerTwo) {
        return RunicPvP.getDuelManager().areDueling(playerOne, playerTwo);
    }

    @Override
    public boolean canCreatePvPEvent(Player player, Player victim) {
        if (RunicPvP.getAPI().playersCanFight(player, victim)) {
            return !RunicPvP.getAPI().areDueling(player, victim);
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
        return pvpDataMap.get(player.getUniqueId()).getOutlawDataMap().get(slot).isOutlaw();
    }

    @Override
    public PvpData loadPvpData(UUID uuid, int slotToLoad) {
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            // Step 1: Check if inventory data is cached in redis
            Set<String> redisPvpList = RunicCore.getRedisAPI().getRedisDataSet(uuid, "pvpData", jedis);
            boolean dataInRedis = RunicCore.getRedisAPI().determineIfDataInRedis(redisPvpList, slotToLoad);
            if (dataInRedis) {
                return new PvpData(uuid, jedis, slotToLoad);
            }
            // Step 2: Check the mongo database
            Query query = new Query();
            query.addCriteria(Criteria.where(CharacterField.PLAYER_UUID.getField()).is(uuid));
            MongoTemplate mongoTemplate = RunicCore.getDataAPI().getMongoTemplate();
            PvpData result = mongoTemplate.findOne(query, PvpData.class);
            if (result != null) {
                // Handles case where there is data for the player, but not this character
                if (result.getOutlawDataMap().get(slotToLoad) == null) {
                    result.getOutlawDataMap().put(slotToLoad, new OutlawData());
                }
                result.writeToJedis();
                return result;
            }
            // Step 3: If no data is found, we create some data and save it to the collection
            PvpData newData = new PvpData
                    (
                            new ObjectId(),
                            uuid,
                            slotToLoad
                    );
            newData.addDocumentToMongo();
            newData.writeToJedis();
            return newData;
        }
    }

    @Override
    public boolean playersCanFight(Player player, Player victim) {
        if (RunicCore.getPartyAPI().isPartyMember(player.getUniqueId(), victim)) return false;
        int slotPlayer = RunicCore.getCharacterAPI().getCharacterSlot(player.getUniqueId());
        int slotVictim = RunicCore.getCharacterAPI().getCharacterSlot(victim.getUniqueId());
        boolean bothOutlaws = RunicPvP.getAPI().isOutlaw(player, slotPlayer) && RunicPvP.getAPI().isOutlaw(victim, slotVictim);
        boolean areDueling = RunicPvP.getAPI().areDueling(player, victim);
        boolean damagerInSafezone = RunicCore.getRegionAPI().isSafezone(player.getLocation());
        boolean victimInSafezone = RunicCore.getRegionAPI().isSafezone(victim.getLocation());
        return (bothOutlaws || areDueling) && (!damagerInSafezone && !victimInSafezone);
    }

    @Override
    public void toggleOutlaw(Player player) {
        if (player.getLevel() < RunicPvP.MINIMUM_OUTLAW_LEVEL) {
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
        int slot = RunicCore.getCharacterAPI().getCharacterSlot(uuid);
        // Toggle their current outlaw status from whatever it currently is
        boolean isOutlaw = isOutlaw(player, slot);
        this.pvpDataMap.get(player.getUniqueId()).getOutlawDataMap().get(slot).setOutlaw(!isOutlaw);
        NametagHandler.updateNametag(player, slot);
        RunicCore.getScoreboardAPI().updatePlayerScoreboard(player);
        // Update their data async in Redis, then send sync message
        TaskChain<?> chain = RunicItems.newChain();
        chain
                .asyncFirst(() -> {
                            try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
                                this.pvpDataMap.get(uuid).getOutlawDataMap().get(slot).writeToJedis(uuid, jedis, slot);
                                return null;
                            }
                        }
                ).syncLast(ignored -> {
                    String status = isOutlaw(player, slot)
                            ? ChatColor.RED + "ENABLED!"
                            : ChatColor.GREEN + "DISABLED";
                    player.sendMessage(ChatColor.YELLOW + "Your outlaw status is now: " + status);
                })
                .execute();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCharacterDelete(CharacterDeleteEvent event) {
        event.getPluginsToDeleteData().add("pvp");
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int slot = event.getSlot();
        // Removes player from the save task
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            String database = RunicCore.getDataAPI().getMongoDatabase().getName();
            jedis.srem(database + ":markedForSave:pvp", String.valueOf(player.getUniqueId()));
        }
        // 1. Delete from Redis
        String database = RunicCore.getDataAPI().getMongoDatabase().getName();
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            jedis.srem(database + ":" + uuid + ":pvpData", String.valueOf(slot));
        }
        // 2. Delete from Mongo
        Query query = new Query();
        query.addCriteria(Criteria.where(CharacterField.PLAYER_UUID.getField()).is(uuid));
        Update update = new Update();
        update.unset("outlawDataMap." + slot);
        MongoTemplate mongoTemplate = RunicCore.getDataAPI().getMongoTemplate();
        mongoTemplate.updateFirst(query, update, PvpData.class);
        // 3. Mark this deletion as complete
        event.getPluginsToDeleteData().remove("pvp");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCharacterQuit(CharacterQuitEvent event) {
        pvpDataMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCharacterSelect(CharacterSelectEvent event) {
        // For benchmarking
        long startTime = System.nanoTime();
        event.getPluginsToLoadData().add("pvp");
        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();
        int slot = event.getSlot();
        TaskChain<?> chain = RunicItems.newChain();
        chain
                .asyncFirst(() -> loadPvpData(uuid, slot))
                .abortIfNull(CONSOLE_LOG, player, "RunicPvP failed to load on select!")
                .syncLast(pvpData -> {
                    pvpDataMap.put(event.getPlayer().getUniqueId(), pvpData);
                    event.getPluginsToLoadData().remove("pvp");
                    // Calculate elapsed time
                    long endTime = System.nanoTime();
                    long elapsedTime = endTime - startTime;
                    // Log elapsed time in milliseconds
                    Bukkit.getLogger().info("RunicPvP took: " + elapsedTime / 1_000_000 + "ms to load");
                })
                .execute();
    }

    @EventHandler
    public void onMongoSave(MongoSaveEvent event) {
        // Cancel the task timer
        RunicPvP.getMongoTask().getTask().cancel();
        // Manually save all data (flush players marked for save)
        RunicPvP.getMongoTask().saveAllToMongo(() -> event.markPluginSaved("pvp"));
    }

}
