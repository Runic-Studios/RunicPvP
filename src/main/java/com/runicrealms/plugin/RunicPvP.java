package com.runicrealms.plugin;

import com.runicrealms.plugin.manager.BoardManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicPvP extends JavaPlugin {

    private static Plugin plugin;
    private static BoardManager boardManager;

    @Override
    public void onEnable() {
        plugin = this;
        boardManager = new BoardManager();
    }

    @Override
    public void onDisable() {
        plugin = null;
        boardManager = null;
    }

    public static Plugin inst() { // Get the plugin instance
        return plugin;
    }
    public static BoardManager getBoardManager() {
        return boardManager;
    }
}
