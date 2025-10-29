package fr.constantdevs.naturalcompass.gui;

import fr.constantdevs.naturalcompass.config.ConfigManager;
import fr.constantdevs.naturalcompass.util.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AdminGUI {

    public static Inventory createAdminGUI(ConfigManager configManager) {
        Inventory gui = Bukkit.createInventory(null, 27, Component.text("NaturalCompass Admin"));

        // Add borders
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.displayName(Component.text(""));
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                gui.setItem(i, border);
            }
        }

        // Biome Exclusion Item
        ItemStack biomeExclusion = new ItemStack(Material.BARRIER);
        ItemMeta biomeExclusionMeta = biomeExclusion.getItemMeta();
        if (biomeExclusionMeta != null) {
            biomeExclusionMeta.displayName(Component.text("Biome Exclusion"));
            biomeExclusion.setItemMeta(biomeExclusionMeta);
        }
        gui.setItem(11, biomeExclusion);

        // Toggle Recipes Item
        ItemStack recipes = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta recipesMeta = recipes.getItemMeta();
        if (recipesMeta != null) {
            recipesMeta.displayName(Component.text("Toggle Recipes"));
            recipes.setItemMeta(recipesMeta);
        }
        Utils.addStateLore(recipes, configManager.isRecipesEnabled());
        gui.setItem(13, recipes);

        // Show Coordinates Enabled/Disabled Item
        ItemStack coordinates = new ItemStack(Material.MAP);
        ItemMeta coordinatesMeta = coordinates.getItemMeta();
        if (coordinatesMeta != null) {
            coordinatesMeta.displayName(Component.text("Toggle Coordinates"));
            coordinates.setItemMeta(coordinatesMeta);
        }
        Utils.addStateLore(coordinates, configManager.isShowCoordinates());
        gui.setItem(15, coordinates);

        // Reload Config Item
        ItemStack reload = new ItemStack(Material.REDSTONE);
        ItemMeta reloadMeta = reload.getItemMeta();
        if (reloadMeta != null) {
            reloadMeta.displayName(Component.text("Reload Config"));
            reload.setItemMeta(reloadMeta);
        }
        gui.setItem(22, reload); // bottom center

        return gui;
    }
}