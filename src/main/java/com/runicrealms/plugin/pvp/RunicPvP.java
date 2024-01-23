package com.runicrealms.plugin.pvp;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.runicrealms.plugin.common.RunicAPI;
import com.runicrealms.plugin.common.plugin.annotation.OnShutdown;
import com.runicrealms.plugin.common.plugin.annotation.OnStartup;
import com.runicrealms.plugin.common.plugin.RunicPlugin;
import com.runicrealms.plugin.pvp.command.CMDDuel;
import com.runicrealms.plugin.pvp.conquest.ConquestManager;
import com.runicrealms.plugin.pvp.duel.DuelManager;
import com.runicrealms.plugin.pvp.listener.OutlawExpListener;
import com.runicrealms.plugin.pvp.listener.PartyListener;
import com.runicrealms.plugin.pvp.listener.PvPListener;
import com.runicrealms.plugin.pvp.listener.ScoreboardListener;
import com.runicrealms.plugin.pvp.shop.PvPShopFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class RunicPvP extends RunicPlugin {
    public static final double OUTLAW_BONUS_EXP_PERCENT = 0.15; // exp and rep
    public static final int MINIMUM_OUTLAW_LEVEL = 25;

    private static Plugin plugin;
    private static TaskChainFactory taskChainFactory;
    private static ConquestManager conquestManager;
    private static DuelManager duelManager;
    private static PaperCommandManager commandManager;

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

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    @OnShutdown
    public void onShutdown() {
        plugin = null;
        conquestManager = null;
        duelManager = null;
        commandManager = null;
        taskChainFactory = null;
    }

    @OnStartup
    public void onStartup() {
        plugin = this;
        taskChainFactory = BukkitTaskChainFactory.create(this);
        RunicAPI.registerPvPAPI(new RunicPvPManager()); // initialize API
        conquestManager = new ConquestManager();
        duelManager = new DuelManager();
        Bukkit.getServer().getPluginManager().registerEvents(new PvPListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OutlawExpListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PartyListener(), this);
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
