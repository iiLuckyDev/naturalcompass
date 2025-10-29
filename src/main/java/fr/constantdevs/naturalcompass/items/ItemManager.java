package fr.constantdevs.naturalcompass.items;

import fr.constantdevs.NaturalCompass;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final NamespacedKey tierKey;

    public static final ItemStack PREVIOUS_PAGE = createButton("Previous Page");
    public static final ItemStack NEXT_PAGE = createButton("Next Page");


    public ItemManager(NaturalCompass plugin) {
        this.tierKey = new NamespacedKey(plugin, "compass_tier");
    }

    public ItemStack createCompass(int tier) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();

        if (meta != null) {
            // Set display name and lore based on tier
            String tierName = getTierName(tier);
            NamedTextColor tierColor = getTierColor(tier);
            meta.displayName(Component.text(tierName + " Compass", tierColor).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Tier " + tier + " - " + getTierName(tier), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Search Radius: " + getTierRadius(tier) + " blocks", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
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
                    .addFloat(178)
                    .build();
            compass.resetData(DataComponentTypes.CUSTOM_MODEL_DATA);
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

    public void updateCompassRotation(ItemStack compass, Location targetLocation, Player player) {
        if (!isNaturalCompass(compass)) return;

        Location playerLoc = player.getLocation();
        double angle = calculateAngle(playerLoc, targetLocation, player);

        // Normalize angle to 0-360 range
        angle = (angle + 360) % 360;

        // Calculate frame based on angle (0-31 for 32 frames)
        int frame = (int) Math.round(angle / 11.25) % 32;

        ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            CustomModelData customModelData = CustomModelData.customModelData()
                    .addFloat(178 + frame)
                    .build();
            compass.resetData(DataComponentTypes.CUSTOM_MODEL_DATA);
            compass.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
        }
    }

    private double calculateAngle(Location from, Location to, Player player) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        // 1. Get the angle of the target vector (player-to-target)
        // We use atan2(-dz, dx) to get a standard mathematical angle.
        // In Minecraft: +dx is East (X-axis), -dz is North (Y-axis)
        // This gives 0 = East, 90 = North, 180 = West, -90/270 = South
        double relativeAngle = getRelativeAngle(player, dz, dx);

        // 5. Normalize the angle to a 0-360 range and reverse by 180 degrees
        // (e.g., -90 degrees becomes 270 degrees)
        // This makes it easy to calculate the frame (0-31).
        return ((relativeAngle + 180) + 360) % 360;
    }

    private static double getRelativeAngle(Player player, double dz, double dx) {
        double targetAngle = Math.toDegrees(Math.atan2(-dz, dx));

        // 2. Get the player's yaw
        double playerYaw = player.getLocation().getYaw();

        // 3. Convert the player's yaw to the same standard mathematical angle
        // Minecraft Yaw: 0 = South, -90 = East, 90 = West, 180 = North
        // Our 'playerAngle': 0 = East, 90 = North, 180 = West, -90 = South
        // The formula to convert is (-yaw - 90)
        double playerAngle = -playerYaw - 90;

        // 4. Calculate the relative angle
        // This is the angle from the player's line-of-sight to the target.
        // If they are equal, the result is 0 (straight ahead).
        // A negative result is to the left, a positive result is to the right.
        return playerAngle - targetAngle;
    }

    private String getTierName(int tier) {
        return switch (tier) {
            case 1 -> "Basic";
            case 2 -> "Advanced";
            case 3 -> "Expert";
            case 4 -> "Master";
            case 5 -> "Legendary";
            default -> "Unknown";
        };
    }

    private NamedTextColor getTierColor(int tier) {
        return switch (tier) {
            case 1 -> NamedTextColor.WHITE;
            case 2 -> NamedTextColor.GREEN;
            case 3 -> NamedTextColor.BLUE;
            case 4 -> NamedTextColor.LIGHT_PURPLE;
            case 5 -> NamedTextColor.GOLD;
            default -> NamedTextColor.GRAY;
        };
    }

    private int getTierRadius(int tier) {
        return switch (tier) {
            case 1 -> 1000;
            case 2 -> 2500;
            case 3 -> 5000;
            case 4 -> 10000;
            case 5 -> 25000;
            default -> 0;
        };
    }

    private static ItemStack createButton(String displayName) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName, NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
        }
        return item;
    }
}