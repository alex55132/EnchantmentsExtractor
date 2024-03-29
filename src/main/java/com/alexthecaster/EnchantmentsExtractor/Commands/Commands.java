package com.alexthecaster.EnchantmentsExtractor.Commands;

import com.alexthecaster.EnchantmentsExtractor.Main.Main;
import com.alexthecaster.EnchantmentsExtractor.Utils.SpecialItemsMapper;
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

import java.util.List;
import java.util.Map;


public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandName, String[] args) {

        if (commandName.equalsIgnoreCase("disenchant")) {

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

                    //Boolean to check if the player can pay the disenchant
                    boolean allEnchantsRemoved = true;


                    //Check if the player has enough material for paying
                    if (enchantmentsSize != 0) {
                        //Iterate all the enchantments
                        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                            //Get both the enchantment type and the level of the enchantment
                            Enchantment enchantment = entry.getKey();

                            int maxLevel = enchantment.getMaxLevel();
                            int level = entry.getValue();

                            if(level <= maxLevel) {
                                //Boolean to check if the extraction is available after the payment
                                boolean paymentCompleted = false;
                                double disenchantPrice = 0;
                                String materialString = "";
                                Material payMaterial = Material.AIR;
                                int materialCost = 0;

                                if (Main.isEconomyEnabled) {
                                    disenchantPrice = Main.plugin.getConfig().getDouble("disenchantPrice");

                                    //Check if the user has the money to pay the disenchantment
                                    if (Main.econ.has(p, disenchantPrice)) {
                                        Main.econ.withdrawPlayer(p, disenchantPrice);
                                        paymentCompleted = true;
                                    } else {
                                        allEnchantsRemoved = false;
                                    }
                                } else {
                                    int materialAmount = 0;

                                    materialString = Main.plugin.getConfig().getString("materialPayment");
                                    SpecialItemsMapper specialItemsMapper = new SpecialItemsMapper();

                                    Material specialMaterial = specialItemsMapper.getSpecialMaterial(materialString);

                                    if(specialMaterial != null) {
                                        payMaterial = specialMaterial;
                                    } else {
                                        payMaterial = Material.getMaterial(materialString);
                                    }



                                    if (payMaterial == null) {
                                        p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                                ChatColor.GREEN +  Main.plugin.getConfig().getString(Main.languageMessagesString + "InvalidMaterialError"));

                                        //If not material found use the default
                                        payMaterial = Material.DIAMOND;
                                    }


                                    for (int i = 0; i < items.length; i++) {
                                        ItemStack item = items[i];

                                        if (item != null && item.getType() == payMaterial) {
                                            materialAmount += item.getAmount();
                                        }
                                    }

                                    //Get the custom price if set
                                    int materialBasePrice = Main.plugin.getConfig().getInt("materialCustomPrice.basePrice");
                                    int perLevelPrice = Main.plugin.getConfig().getInt("materialCustomPrice.perLevelPrice");

                                    //If custom prices are set, use them, if not, use the default
                                    if (materialBasePrice > -1 && perLevelPrice > -1) {
                                        materialCost = materialBasePrice + (perLevelPrice * level);
                                    } else {
                                        materialCost = level + 1;
                                    }

                                    //Check if the player can pay the disenchant with materials
                                    if ((materialCost <= materialAmount)) {
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

                                        paymentCompleted = true;

                                        enchantmentsCounter--;
                                    } else {
                                        allEnchantsRemoved = false;
                                    }
                                }

                                if (paymentCompleted) {
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

                                }
                            } else {

                                allEnchantsRemoved = false;
                                p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                        ChatColor.GREEN + Main.plugin.getConfig().getString(Main.languageMessagesString + "InvalidEnchantmentLevel"));

                            }

                        }

                        //Get the final message
                        if (allEnchantsRemoved) {
                            p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                    ChatColor.GREEN +  Main.plugin.getConfig().getString(Main.languageMessagesString + "EnchantmentExtractionSuccess"));
                        } else {
                            //No enchantment was extracted, the operation failed
                            if (enchantmentsCounter == enchantmentsSize) {
                                p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                        ChatColor.GREEN + Main.plugin.getConfig().getString(Main.languageMessagesString + "NoEnchantmentExtracted"));
                            } else {
                                //Some enchantments were extracted
                                p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                        ChatColor.GREEN + Main.plugin.getConfig().getString(Main.languageMessagesString + "SomeEnchantmentExtracted"));
                            }
                        }

                    } else {
                        p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                                ChatColor.GREEN + Main.plugin.getConfig().getString(Main.languageMessagesString + "NoEnchantmentInItem"));
                    }
                } else {
                    p.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                            ChatColor.GREEN + Main.plugin.getConfig().getString(Main.languageMessagesString + "NoPermissionMessage"));
                }

            } else {
                sender.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "EnchantmentsExtractor" + ChatColor.YELLOW + "] " +
                        ChatColor.GREEN + Main.plugin.getConfig().getString(Main.languageMessagesString + "ConsoleError"));
            }

            return true;
        }

        return false;
    }
}
