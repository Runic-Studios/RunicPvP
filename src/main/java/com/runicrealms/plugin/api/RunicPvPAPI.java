package com.runicrealms.plugin.api;

import com.runicrealms.plugin.PvPData;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface RunicPvPAPI {

    /**
     * Checks whether the two given players are currently dueling
     *
     * @param playerOne first player to check
     * @param playerTwo second player to check
     * @return true if the players are dueling
     */
    boolean areDueling(Player playerOne, Player playerTwo);

    /**
     * Checks if two players are valid opponents (both outlaws, both in PvP zone, etc.)
     * AND a valid PvP event can be called. This is false if the players are dueling or in the arena.
     *
     * @param player the player who fired the attack
     * @param victim the player to receive the attack
     * @return true if a PvP event can be created
     */
    boolean canCreatePvPEvent(Player player, Player victim);

    /**
     * Returns the minimum level required to flag for PvP
     *
     * @return minimum pvp flag level
     */
    int getMinimumOutlawLevel();

    /**
     * Checks whether the current player is dueling
     *
     * @param player to check
     * @return true if the player is in a duel
     */
    boolean isDueling(Player player);

    /**
     * Checks if the given player is an outlaw
     *
     * @param player to check
     * @param slot   of the character
     * @return true if the player is an outlaw
     */
    boolean isOutlaw(Player player, int slot);

    /**
     * @param uuid       of the player
     * @param slotToLoad of the character (-1 to load all slots, used for mongo task)
     * @return a future, which at some point will retrieve their data from redis
     */
    PvPData loadPvpData(UUID uuid);

    /**
     * Checks whether two players are allowed to hit each other.
     *
     * @param player player attempting to hit player
     * @param victim entity (player) being hit
     * @return true if both entities are players AND they are dueling or are BOTH outlaws
     */
    boolean playersCanFight(Player player, Player victim);

    /**
     * Toggle a player's outlaw status
     *
     * @param player to toggle
     */
    void toggleOutlaw(Player player);
}
