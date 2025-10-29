package fr.constantdevs.naturalcompass.util;

import fr.constantdevs.NaturalCompass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Formats a location into a clickable text component.
     *
     * @param location The location to format.
     * @return The formatted text component.
     */
    public static Component formatLocation(Location location) {
        return Component.text()
                .append(Component.text(" [", NamedTextColor.GRAY))
                .append(Component.text(location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ(), NamedTextColor.GREEN))
                .append(Component.text("]", NamedTextColor.GRAY))
                .build();
    }

    /**
     * Adds lore and enchantment to an item based on its enabled state.
     *
     * @param item    The item to modify.
     * @param enabled The enabled state.
     */
    public static void addStateLore(ItemStack item, boolean enabled) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore = new ArrayList<>();
            if (enabled) {
                lore.add(Component.text("Enabled", NamedTextColor.GREEN));
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                lore.add(Component.text("Disabled", NamedTextColor.RED));
                meta.removeEnchant(Enchantment.UNBREAKING);
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
    }

    public static String formatBiomeName(String biomeName) {
        String[] parts = biomeName.split(":", 2);
        String key = parts.length > 1 ? parts[1] : biomeName;
        String[] words = key.toLowerCase().split("_");
        StringBuilder formattedKey = new StringBuilder();
        for (String word : words) {
            formattedKey.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return formattedKey.toString().trim();
    }

    public static String componentToString(Component component) {
        if (component == null) {
            return "";
        }
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static ItemStack getBiomeIcon(String biomeName) {
        ItemStack icon = NaturalCompass.getInstance().getConfigManager().getBiomeIcons().get(biomeName);
        if (icon == null) {
            NaturalCompass.getInstance().getLogger().warning("No icon found for biome '" + biomeName + "', using default GRASS_BLOCK.");
            return new ItemStack(Material.GRASS_BLOCK);
        }
        return icon.clone();
    }
}