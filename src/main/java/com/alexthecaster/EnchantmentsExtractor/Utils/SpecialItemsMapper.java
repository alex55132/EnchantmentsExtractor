package com.alexthecaster.EnchantmentsExtractor.Utils;

import org.bukkit.Material;

public class SpecialItemsMapper {
    public Material getSpecialMaterial(String materialName) {
        Material materialToReturn = null;

        switch (materialName) {
            case "LAPIS_LAZULI": {
                materialToReturn = Material.LAPIS_LAZULI;
                break;
            }
            case "PURPLE_DYE": {
                materialToReturn = Material.PURPLE_DYE;
                break;
            }
            case "CYAN_DYE": {
                materialToReturn = Material.CYAN_DYE;
                break;
            }
            case "LIGHT_GRAY_DYE": {
                materialToReturn = Material.LIGHT_GRAY_DYE;
                break;
            }
            case "GRAY_DYE": {
                materialToReturn = Material.GRAY_DYE;
                break;
            }
            case "PINK_DYE": {
                materialToReturn = Material.PINK_DYE;
                break;
            }
            case "LIME_DYE": {
                materialToReturn = Material.LIME_DYE;
                break;
            }
            case "LIGHT_BLUE_DYE": {
                materialToReturn = Material.LIGHT_BLUE_DYE;
                break;
            }
            case "MAGENTA_DYE": {
                materialToReturn = Material.MAGENTA_DYE;
                break;
            }
            case "ORANGE_DYE": {
                materialToReturn = Material.ORANGE_DYE;
                break;
            }
        }

        return materialToReturn;
    }
}
