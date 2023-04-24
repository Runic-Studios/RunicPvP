package com.runicrealms.plugin.model;

import com.runicrealms.plugin.RunicCore;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OutlawData implements SessionDataRedis {
    private boolean isOutlaw = false;

    @SuppressWarnings("unused")
    public OutlawData() {
        // Default constructor for Spring
    }

    /**
     * Constructor for Redis
     *
     * @param uuid of the player
     * @param slot of the character
     */
    public OutlawData(UUID uuid, Jedis jedis, int slot) {
        try {
            String database = RunicCore.getDataAPI().getMongoDatabase().getName();
            boolean isOutlaw = Boolean.parseBoolean(jedis.get(database + ":" + getJedisKey(uuid, slot) + ":toggled"));
            this.setOutlaw(isOutlaw);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("There was a problem loading outlaw data!");
            ex.printStackTrace();
        }
    }

    /**
     * @param uuid of the player
     * @param slot of the character
     * @return a string representing the location in jedis
     */
    public static String getJedisKey(UUID uuid, int slot) {
        return uuid + ":character:" + slot + ":outlaw";
    }

    @Override
    public Map<String, String> getDataMapFromJedis(UUID uuid, Jedis jedis, int... ints) {
        return null;
    }

    @Override
    public List<String> getFields() {
        return null;
    }

    @Override
    public Map<String, String> toMap(UUID uuid, int... ints) {
        return null;
    }

    @Override
    public void writeToJedis(UUID uuid, Jedis jedis, int... slot) {
        String database = RunicCore.getDataAPI().getMongoDatabase().getName();
        jedis.set(database + ":" + getJedisKey(uuid, slot[0]) + ":toggled", String.valueOf(this.isOutlaw));
        jedis.expire(database + ":" + getJedisKey(uuid, slot[0]) + ":toggled", RunicCore.getRedisAPI().getExpireTime());
    }

    public boolean isOutlaw() {
        return isOutlaw;
    }

    public void setOutlaw(boolean outlaw) {
        isOutlaw = outlaw;
    }

}
