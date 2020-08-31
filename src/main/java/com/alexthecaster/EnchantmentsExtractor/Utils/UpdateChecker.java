package com.alexthecaster.EnchantmentsExtractor.Utils;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private JavaPlugin plugin;
    private int pluginId;

    //Constructor
    public UpdateChecker(JavaPlugin plugin, int id) {
        this.plugin = plugin;
        this.pluginId = id;
    }

    //Method to check if the version is the same
    public void generateUpdateMessage(String version) {
        if (this.plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
            plugin.getServer().broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                    ChatColor.GREEN + "There is not a new update available.");
        } else {
            plugin.getServer().broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                    ChatColor.GREEN + "There is a new update available.");
        }
    }

    //Method to get the last released version from the spigot api
    public void getLastReleasedVersion(final Consumer<String> consumer) {
        final int pluginId = this.pluginId;

            plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        //Get the stream
                        InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource="+pluginId).openStream();

                        //Create the scanner
                        Scanner scanner = new Scanner(is);

                        //Send the data to the consumer
                        if(scanner.hasNext()) {
                            consumer.accept(scanner.next());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    }

}
