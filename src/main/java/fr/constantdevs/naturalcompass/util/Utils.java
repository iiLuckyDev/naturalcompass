package fr.constantdevs.naturalcompass.util;

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
        String name = biomeName;
        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }
        String[] words = name.toLowerCase().split("_");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            formattedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return formattedName.toString().trim();
    }

    public static String componentToString(Component component) {
        if (component == null) {
            return "";
        }
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String revertFormattedBiomeName(String formattedName) {
        return "minecraft:" + formattedName.toLowerCase().replace(" ", "_");
    }

    public static Material getBiomeIcon(String biomeName) {
        return switch (biomeName) {
            case "minecraft:plains" -> Material.GRASS_BLOCK;
            case "minecraft:sunflower_plains" -> Material.SUNFLOWER;
            case "minecraft:snowy_plains" -> Material.SNOW_BLOCK;
            case "minecraft:ice_spikes" -> Material.ICE;
            case "minecraft:desert" -> Material.SAND;
            case "minecraft:swamp" -> Material.LILY_PAD;
            case "minecraft:mangrove_swamp" -> Material.MANGROVE_ROOTS;
            case "minecraft:forest" -> Material.OAK_LOG;
            case "minecraft:flower_forest" -> Material.POPPY;
            case "minecraft:birch_forest" -> Material.BIRCH_LOG;
            case "minecraft:dark_forest" -> Material.DARK_OAK_LOG;
            case "minecraft:taiga" -> Material.SPRUCE_LOG;
            case "minecraft:snowy_taiga" -> Material.SNOW;
            case "minecraft:jungle" -> Material.JUNGLE_LOG;
            case "minecraft:bamboo_jungle" -> Material.BAMBOO;
            case "minecraft:badlands" -> Material.RED_SAND;
            case "minecraft:savanna" -> Material.ACACIA_LOG;
            case "minecraft:mushroom_fields" -> Material.RED_MUSHROOM_BLOCK;
            case "minecraft:mountain" -> Material.STONE;
            case "minecraft:ocean" -> Material.WATER_BUCKET;
            case "minecraft:deep_ocean" -> Material.BLUE_STAINED_GLASS;
            case "minecraft:cold_ocean" -> Material.BLUE_ICE;
            case "minecraft:lukewarm_ocean" -> Material.LIGHT_BLUE_STAINED_GLASS;
            case "minecraft:warm_ocean" -> Material.YELLOW_STAINED_GLASS;
            case "minecraft:deep_cold_ocean" -> Material.PACKED_ICE;
            case "minecraft:deep_lukewarm_ocean" -> Material.CYAN_STAINED_GLASS;
            case "minecraft:deep_warm_ocean" -> Material.ORANGE_STAINED_GLASS;
            case "minecraft:dripstone_caves" -> Material.DRIPSTONE_BLOCK;
            case "minecraft:lush_caves" -> Material.MOSS_BLOCK;
            case "minecraft:crimson_forest" -> Material.CRIMSON_STEM;
            case "minecraft:soul_sand_valley" -> Material.SOUL_SAND;
            case "minecraft:basalt_deltas" -> Material.BASALT;
            case "minecraft:nether_wastes" -> Material.NETHERRACK;
            case "minecraft:warped_forest" -> Material.WARPED_STEM;
            case "minecraft:the_end" -> Material.END_STONE;
            case "minecraft:end_highlands" -> Material.END_STONE;
            case "minecraft:end_midlands" -> Material.END_STONE;
            case "minecraft:end_barrens" -> Material.END_STONE;
            case "minecraft:cherry_grove" -> Material.CHERRY_LOG;
            case "minecraft:pale_garden" -> Material.PALE_OAK_LOG;
            default -> Material.GRASS_BLOCK;
        };
    }
}