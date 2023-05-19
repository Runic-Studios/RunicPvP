package com.runicrealms.plugin;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.runicrealms.plugin.command.CMDDuel;
import com.runicrealms.plugin.conquest.ConquestManager;
import com.runicrealms.plugin.duel.DuelManager;
import com.runicrealms.plugin.listener.NameTagListener;
import com.runicrealms.plugin.listener.OutlawExpListener;
import com.runicrealms.plugin.listener.PartyListener;
import com.runicrealms.plugin.listener.PvPListener;
import com.runicrealms.plugin.listener.ScoreboardListener;
import com.runicrealms.plugin.model.MongoTask;
import com.runicrealms.plugin.shop.PvPShopFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicPvP extends JavaPlugin {
    public static final double OUTLAW_BONUS_EXP_PERCENT = 0.15; // exp and rep
    public static final int MINIMUM_OUTLAW_LEVEL = 25;

    private static Plugin plugin;
    private static TaskChainFactory taskChainFactory;
    private static ConquestManager conquestManager;
    private static DuelManager duelManager;
    private static PaperCommandManager commandManager;
    private static RunicPvPManager runicPvPManager;
    private static MongoTask mongoTask;

    /*
    Getters for Plugin and managers
     */
    public static Plugin inst() { // Get the plugin instance
        return plugin;
    }

    public static ConquestManager getConquestManager() {
        return conquestManager;
    }

    public static DuelManager getDuelManager() {
        return duelManager;
    }

    public static PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public static double getOutlawBonusExpPercent() {
        return OUTLAW_BONUS_EXP_PERCENT;
    }

    public static RunicPvPManager getAPI() {
        return runicPvPManager;
    }

    public static MongoTask getMongoTask() {
        return mongoTask;
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    @Override
    public void onDisable() {
        plugin = null;
        conquestManager = null;
        duelManager = null;
        commandManager = null;
        runicPvPManager = null;
        mongoTask = null;
        taskChainFactory = null;
    }

    @Override
    public void onEnable() {
        plugin = this;
        taskChainFactory = BukkitTaskChainFactory.create(this);
        runicPvPManager = new RunicPvPManager(); // initialize API
        conquestManager = new ConquestManager();
        duelManager = new DuelManager();
        mongoTask = new MongoTask();
        Bukkit.getServer().getPluginManager().registerEvents(new PvPListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OutlawExpListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PartyListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new NameTagListener(), this);
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CMDDuel());
        commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player))
                throw new ConditionFailedException("This command cannot be run from console!");
        });
        commandManager.getCommandConditions().addCondition("is-op", context -> {
            if (!context.getIssuer().getIssuer().isOp())
                throw new ConditionFailedException("You must be an operator to run this command!");
        });
        /*
        Shops
         */
        new PvPShopFactory();
    }
}
