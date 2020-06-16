package com.alexthecaster.EnchantmentsExtractor.Main;

import com.alexthecaster.EnchantmentsExtractor.Commands.Commands;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        plugin.getLogger().info("Intializing EnchantmentsExtractor...");

        try {
            plugin.getCommand("disenchant").setExecutor(new Commands());

        } catch (NullPointerException exception) {
            plugin.getLogger().severe("An error ocurred while initializing");
        }
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Disabling EnchantmentsExtractor");
    }
}

