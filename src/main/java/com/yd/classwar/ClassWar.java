package com.yd.classwar;

import com.yd.classwar.commands.*;
import com.yd.classwar.listeners.GameListener;
import com.yd.classwar.listeners.GuiClickListener;
import com.yd.classwar.listeners.PlayerInteractListener;
import com.yd.classwar.managers.GameManager;
import com.yd.classwar.managers.PointManager;
import com.yd.classwar.managers.RankManager;
import com.yd.classwar.managers.ShopManager;
import com.yd.classwar.utils.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;


public class ClassWar extends JavaPlugin {

    private RankManager rankManager;
    private PointManager pointManager;
    private ShopManager shopManager;
    private GameManager gameManager;
    private ConfigUtil configUtil;

    @Override
    public void onEnable() {
        // Utils
        configUtil = new ConfigUtil(this);

        // Managers
        rankManager = new RankManager(this);
        pointManager = new PointManager(this, rankManager);
        shopManager = new ShopManager(this, configUtil);
        gameManager = new GameManager(this, rankManager, pointManager, shopManager);

        rankManager.setPointManager(pointManager);

        // Commands
        getCommand("등급전쟁").setExecutor(new StartGameCommand(gameManager));
        getCommand("운영자").setExecutor(new AdminModeCommand(rankManager));
        getCommand("참가자").setExecutor(new ParticipantModeCommand(rankManager));
        getCommand("관리자아이템").setExecutor(new AdminItemCommand(this, rankManager));
        getCommand("등급설정").setExecutor(new RankSettingsCommand(this, rankManager));
        getCommand("체력상점").setExecutor(new HealthShopCommand(this, shopManager));
        getCommand("무기상점").setExecutor(new WeaponShopCommand(this, shopManager));
        getCommand("마나상점").setExecutor(new ManaShopCommand(this, shopManager));
        getCommand("등급교환").setExecutor(new ExchangeRankCommand());

        // Listeners
        getServer().getPluginManager().registerEvents(new GuiClickListener(this, rankManager, shopManager, gameManager), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, shopManager, rankManager, gameManager), this);
        getServer().getPluginManager().registerEvents(new GameListener(this, gameManager, rankManager, pointManager), this);

        getLogger().info("&aClassWar Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("&aClassWar Plugin Disabled");
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public PointManager getPointManager() {
        return pointManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
}