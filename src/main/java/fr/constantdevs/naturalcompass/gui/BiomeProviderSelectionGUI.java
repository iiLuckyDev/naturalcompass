package fr.constantdevs.naturalcompass.gui;

import fr.constantdevs.NaturalCompass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class BiomeProviderSelectionGUI {

    private final Inventory inventory;

    public BiomeProviderSelectionGUI(World.Environment environment) {
        this.inventory = Bukkit.createInventory(null, 27, Component.text("Select Biome Provider"));

        List<String> providers = fr.constantdevs.naturalcompass.biome.BiomeManager.getProvidersForDimension(environment);
        Collections.sort(providers);

        // Add borders and navigation placeholders
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.displayName(Component.text(""));
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, border);
            }
        }

        // Position providers based on count
        int providerCount = providers.size();
        int startSlot;
        if (providerCount == 1) {
            startSlot = 13;
        } else if (providerCount == 2) {
            startSlot = 12;
        } else if (providerCount == 3) {
            startSlot = 11;
        } else {
            startSlot = 10; // fallback
        }

        for (int i = 0; i < providerCount && i < 7; i++) { // limit to 7 for safety
            ItemStack item = getProviderIcon(providers.get(i));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String providerName = providers.get(i);
                String formattedName = providerName.substring(0, 1).toUpperCase() + providerName.substring(1);
                meta.displayName(Component.text(formattedName));
                item.setItemMeta(meta);
            }
            inventory.setItem(startSlot + i, item);
        }
    }

    private ItemStack getProviderIcon(String provider) {
        ItemStack icon = NaturalCompass.getInstance().getConfigManager().getProviderIcons().get(provider);
        return icon != null ? icon.clone() : new ItemStack(Material.STONE);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }
}
