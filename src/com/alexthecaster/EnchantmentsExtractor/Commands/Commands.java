package com.alexthecaster.EnchantmentsExtractor.Commands;

import com.alexthecaster.EnchantmentsExtractor.Main.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;


public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String nombreComando, String[] args) {

        if (nombreComando.equalsIgnoreCase("disenchant")) {

            if (sender instanceof Player) {
                Player p = (Player) sender;
                //Check for permission
                if (p.hasPermission("enchantmentsextractor.disenchant")) {
                    ItemStack itemInMainHand = p.getInventory().getItemInMainHand();

                    Map<Enchantment, Integer> enchantments = itemInMainHand.getEnchantments();

                    //Save how many enchantments do we have
                    int enchantmentsSize = enchantments.size();
                    int enchantmentsCounter = enchantmentsSize;

                    PlayerInventory playerInv = p.getInventory();

                    //Get the inventory content
                    ItemStack[] items = playerInv.getContents();

                    int materialAmount = 0;

                    //TODO: SUPPORT FOR CUSTOM MATERIALS?
                    Material payMaterial = Material.DIAMOND;

                    for (int i = 0; i < items.length; i++) {
                        ItemStack item = items[i];

                        if (item != null && item.getType() == payMaterial) {
                            materialAmount += item.getAmount();
                        }
                    }

                    //Boolean to check if the player can pay the disenchant
                    boolean allEnchantsRemoved = true;

                    //Check if the player has enough material for paying
                    if (enchantmentsSize != 0) {
                        //Iterate all the enchantments
                        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                            //Get both the enchantment type and the level of the enchantment
                            Enchantment enchantment = entry.getKey();
                            int level = entry.getValue();

                            int materialCost = level + 1;

                            //Check if the player can pay the
                            if ((materialCost <= materialAmount)) {
                                //Remove the enchantment from the item
                                itemInMainHand.removeEnchantment(enchantment);

                                //Create the enchanted book item
                                ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);

                                ItemMeta meta = enchantedBook.getItemMeta();

                                //Get the enchantmentStorageMeta
                                if (meta instanceof EnchantmentStorageMeta) {
                                    //Add the enchantment to the item meta
                                    EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                                    enchantmentStorageMeta.addStoredEnchant(enchantment, level, false);
                                }
                                //Give the meta to the item
                                enchantedBook.setItemMeta(meta);
                                //Drop the item
                                p.getWorld().dropItem(p.getLocation(), enchantedBook);

                                //Take the pay material out of the player
                                boolean paid = false;
                                int slotCounter = 0;
                                int materialPayRemaining = materialCost;

                                while (!paid && (slotCounter < items.length)) {
                                    ItemStack item = items[slotCounter];
                                    //Check if there is the payMaterial in that slot
                                    if (item != null && item.getType() == payMaterial) {

                                        int amountInSlot = item.getAmount();

                                        //If the amount of materials in the slot is superior to the amount of money to pay delete the materials and break the loop
                                        if (amountInSlot > materialPayRemaining) {
                                            amountInSlot -= materialPayRemaining;

                                            item.setAmount(amountInSlot);

                                            paid = true;
                                        } else {
                                            materialPayRemaining -= amountInSlot;
                                            item.setAmount(0);
                                        }
                                    }

                                    slotCounter++;
                                }

                                enchantmentsCounter--;
                            } else {
                                allEnchantsRemoved = false;
                            }
                        }

                        //Get the final message
                        if (allEnchantsRemoved) {
                            p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                    ChatColor.GREEN + "Enchantments extracted!");
                        } else {
                            //No enchantment was extracted, the operation failed
                            if (enchantmentsCounter == enchantmentsSize) {
                                p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                        ChatColor.GREEN + " No enchantments were extracted. Check if you have enough material for it!");
                            } else {
                                //Some enchantments were extracted
                                p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                        ChatColor.GREEN + " Some enchantments were not extracted! Do you have enough material?");
                            }
                        }

                    } else {
                        p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                ChatColor.GREEN + "The current item doesn't have any enchantment!");
                    }
                } else {
                    p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                            ChatColor.GREEN + "You dont have permissions to use this command!");
                }

            } else {
                Main.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                        ChatColor.GREEN + "This is not a console command!");
            }

            return true;
        }

        return false;
    }
}
