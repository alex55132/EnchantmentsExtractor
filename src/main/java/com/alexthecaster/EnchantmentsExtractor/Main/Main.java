package com.alexthecaster.EnchantmentsExtractor.Main;


import com.alexthecaster.EnchantmentsExtractor.Commands.Commands;
import com.alexthecaster.EnchantmentsExtractor.Utils.UpdateChecker;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static Economy econ = null;
    public static boolean isEconomyEnabled = false;
    public static String languageMessagesString = "";

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



            plugin.getLogger().warning(this.getConfig().getString("messages.language"));

            switch(this.getConfig().getString("messages.language")) {
                case "eng":
                    languageMessagesString = "messages.eng.";
                    break;
                case "esp":
                    languageMessagesString = "messages.esp.";
                    break;
                default:
                    languageMessagesString = "messages.eng.";
                    break;
            }

            UpdateChecker checker = new UpdateChecker(this, 80315);

            checker.getLastReleasedVersion(checker::generateUpdateMessage);


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

