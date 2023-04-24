package com.runicrealms.plugin.model;

import com.runicrealms.plugin.RunicCore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.UUID;

@Document(collection = "pvp")
@SuppressWarnings("unused")
public class PvpData implements SessionDataMongo {
    @Id
    private ObjectId id;
    @Field("playerUuid")
    private UUID uuid;
    private HashMap<Integer, OutlawData> outlawDataMap = new HashMap<>();

    @SuppressWarnings("unused")
    public PvpData() {
        // Default constructor for Spring
    }

    /**
     * Constructor for new players
     */
    public PvpData(ObjectId id, UUID uuid, int slot) {
        this.id = id;
        this.uuid = uuid;
        this.outlawDataMap.put(slot, new OutlawData());
    }

    /**
     * Constructor for Redis
     *
     * @param slotToLoad the slot of the character to load (-1 to load all slots)
     */
    public PvpData(UUID uuid, Jedis jedis, int slotToLoad) {
        this.uuid = uuid;
        String database = RunicCore.getDataAPI().getMongoDatabase().getName();
        if (slotToLoad == -1) { // Load all slots
            // Load OutlawData from Redis
            for (int slot = 1; slot <= RunicCore.getDataAPI().getMaxCharacterSlot(); slot++) {
                // No data for slot
                if (!jedis.smembers(database + ":" + uuid + ":pvpData").contains(String.valueOf(slot)))
                    continue;
                outlawDataMap.put(slot, new OutlawData(uuid, jedis, slot));
            }
        } else {
            if (jedis.smembers(database + ":" + uuid + ":pvpData").contains(String.valueOf(slotToLoad))) {
                outlawDataMap.put(slotToLoad, new OutlawData(uuid, jedis, slotToLoad));
            } else {
                outlawDataMap.put(slotToLoad, new OutlawData());
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public PvpData addDocumentToMongo() {
        MongoTemplate mongoTemplate = RunicCore.getDataAPI().getMongoTemplate();
        return mongoTemplate.save(this);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public HashMap<Integer, OutlawData> getOutlawDataMap() {
        return outlawDataMap;
    }

    public void setOutlawDataMap(HashMap<Integer, OutlawData> outlawDataMap) {
        this.outlawDataMap = outlawDataMap;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void writeToJedis() {
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            String database = RunicCore.getDataAPI().getMongoDatabase().getName();
            // Inform the server that this player should be saved to mongo on next task (jedis data is refreshed)
            jedis.sadd(database + ":" + "markedForSave:pvp", this.uuid.toString());
            // Save outlaw data
            for (int slot : this.outlawDataMap.keySet()) {
                // Ensure the system knows that there is data in redis
                jedis.sadd(database + ":" + this.uuid + ":pvpData", String.valueOf(slot));
                jedis.expire(database + ":" + this.uuid + ":pvpData", RunicCore.getRedisAPI().getExpireTime());
                OutlawData outlawData = this.outlawDataMap.get(slot);
                outlawData.writeToJedis(this.uuid, jedis, slot);
            }
        }
    }

}
