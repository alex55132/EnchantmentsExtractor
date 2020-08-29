package com.alexthecaster.EnchantmentsExtractor.Main;


import com.alexthecaster.EnchantmentsExtractor.Commands.Commands;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static Economy econ = null;
    public static boolean isEconomyEnabled = false;
    public static boolean isEnchantmentSolutionEnabled = false;

    @Override
    public void onEnable() {
        plugin = this;

        plugin.getLogger().info("Intializing EnchantmentsExtractor...");
        this.saveDefaultConfig();

        try {
            plugin.getCommand("disenchant").setExecutor(new Commands());

            //Check the config for the economy option enabled
            isEconomyEnabled = this.getConfig().getBoolean("economyEnabled");

            if(isEconomyEnabled) {
                if (!setupEconomy() ) {
                    plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                    getServer().getPluginManager().disablePlugin(this);
                    return;
                }
            }

            isEnchantmentSolutionEnabled = plugin.getServer().getPluginManager().isPluginEnabled("EnchantmentSolution");

            if (isEnchantmentSolutionEnabled) {
                plugin.getLogger().info("ES integration enabled");
            }


        } catch (NullPointerException exception) {
            plugin.getLogger().severe("An error ocurred while initializing");
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Disabling EnchantmentsExtractor");
    }
}

