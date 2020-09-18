package com.runicrealms.plugin;

import com.runicrealms.plugin.cmd.CMDOutlaw;
import com.runicrealms.plugin.duel.DuelManager;
import com.runicrealms.plugin.listener.OutlawListener;
import com.runicrealms.plugin.manager.BoardManager;
import com.runicrealms.plugin.manager.OutlawManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicPvP extends JavaPlugin {

    private static Plugin plugin;
    private static BoardManager boardManager;
    private static DuelManager duelManager;
    private static OutlawManager outlawManager;

    @Override
    public void onEnable() {
        plugin = this;
        boardManager = new BoardManager();
        duelManager = new DuelManager();
        outlawManager = new OutlawManager();
        Bukkit.getServer().getPluginManager().registerEvents(new OutlawListener(), this);
        getCommand("outlaw").setExecutor(new CMDOutlaw());
    }

    @Override
    public void onDisable() {
        plugin = null;
        boardManager = null;
        duelManager = null;
        outlawManager = null;
    }

    public static Plugin inst() { // Get the plugin instance
        return plugin;
    }
    public static BoardManager getBoardManager() {
        return boardManager;
    }
    public static DuelManager getDuelManager() {
        return duelManager;
    }
    public static OutlawManager getOutlawManager() {
        return outlawManager;
    }
}
