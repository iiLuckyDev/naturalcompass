package fr.constantdevs.naturalcompass.items;

import fr.constantdevs.naturalcompass.NaturalCompass;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.papermc.paper.datacomponent.DataComponentTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ItemManager {

    private final NaturalCompass plugin;
    private final NamespacedKey tierKey;

    public static final ItemStack PREVIOUS_PAGE = createButton(Material.ARROW, "Previous Page");
    public static final ItemStack NEXT_PAGE = createButton(Material.ARROW, "Next Page");


    public ItemManager(NaturalCompass plugin) {
        this.plugin = plugin;
        this.tierKey = new NamespacedKey(plugin, "compass_tier");
    }

    public ItemStack createCompass(int tier) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();

        if (meta != null) {
            // Set display name and lore
            meta.displayName(Component.text("Natural Compass", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Tier " + tier, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Right-click to select a biome.", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Left-click to find it.", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);

            // Store tier in PDC
            meta.getPersistentDataContainer().set(tierKey, PersistentDataType.INTEGER, tier);

            // 1. Apply all meta changes to the item FIRST.
            compass.setItemMeta(meta);

            // 2. Now, add the data component to the item.
            // This will no longer be erased by setItemMeta.
            CustomModelData customModelData = CustomModelData.customModelData()
                    .addString("naturecompass")
                    .build();
            compass.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);

        }

        return compass;
    }

    public int getCompassTier(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(tierKey, PersistentDataType.INTEGER, 0);
    }

    public boolean isNaturalCompass(ItemStack item) {
        return getCompassTier(item) > 0;
    }

    private static ItemStack createButton(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName, NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
        }
        return item;
    }
}