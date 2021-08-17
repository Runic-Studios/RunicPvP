package com.runicrealms.plugin;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.runicrealms.plugin.cmd.CMDDuel;
import com.runicrealms.plugin.conquest.ConquestManager;
import com.runicrealms.plugin.duel.DuelManager;
import com.runicrealms.plugin.listener.OutlawBonusListener;
import com.runicrealms.plugin.listener.PvPListener;
import com.runicrealms.plugin.manager.BoardManager;
import com.runicrealms.plugin.manager.OutlawManager;
import com.runicrealms.plugin.shop.OutlawToggleShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicPvP extends JavaPlugin {

    private static Plugin plugin;
    private static BoardManager boardManager;
    private static ConquestManager conquestManager;
    private static DuelManager duelManager;
    private static OutlawManager outlawManager;
    private static PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        plugin = this;
        boardManager = new BoardManager();
        conquestManager = new ConquestManager();
        duelManager = new DuelManager();
        outlawManager = new OutlawManager();
        Bukkit.getServer().getPluginManager().registerEvents(new PvPListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OutlawBonusListener(), this);
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CMDDuel());
        commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player)) throw new ConditionFailedException("This command cannot be run from console!");
        });
        commandManager.getCommandConditions().addCondition("is-op", context -> {
            if (!context.getIssuer().getIssuer().isOp()) throw new ConditionFailedException("You must be an operator to run this command!");
        });
        /*
        Shops
         */
        new OutlawToggleShop();
    }

    @Override
    public void onDisable() {
        plugin = null;
        boardManager = null;
        conquestManager = null;
        duelManager = null;
        outlawManager = null;
        commandManager = null;
    }

    public static Plugin inst() { // Get the plugin instance
        return plugin;
    }
    public static BoardManager getBoardManager() {
        return boardManager;
    }
    public static ConquestManager getConquestManager() {
        return conquestManager;
    }
    public static DuelManager getDuelManager() {
        return duelManager;
    }
    public static OutlawManager getOutlawManager() {
        return outlawManager;
    }
    public static PaperCommandManager getCommandManager() {
        return commandManager;
    }
}
