package fr.constantdevs.naturalcompass.search;

import fr.constantdevs.naturalcompass.NaturalCompass;
import fr.constantdevs.naturalcompass.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SearchManager {

    private final NaturalCompass plugin;
    private final Map<UUID, String> targetBiomes = new HashMap<>();

    public SearchManager(NaturalCompass plugin) {
        this.plugin = plugin;
    }

    public void setTargetBiome(Player player, String biome) {
        targetBiomes.put(player.getUniqueId(), biome);
    }

    public void startSearch(Player player) {
        String targetBiomeName = targetBiomes.get(player.getUniqueId());
        if (targetBiomeName == null) {
            player.sendMessage(Component.text("You haven't selected a biome yet!", NamedTextColor.RED));
            return;
        }

        NamespacedKey key = NamespacedKey.fromString(targetBiomeName);
        if (key == null) {
            player.sendMessage(Component.text("Invalid biome: " + targetBiomeName, NamedTextColor.RED));
            return;
        }
        Biome targetBiome = Registry.BIOME.get(key);
        if (targetBiome == null) {
            player.sendMessage(Component.text("Invalid biome: " + targetBiomeName, NamedTextColor.RED));
            return;
        }

        int tier = plugin.getItemManager().getCompassTier(player.getInventory().getItemInMainHand());
        int radius = plugin.getConfigManager().getTierRadii().get(tier - 1);

        player.sendActionBar(Component.text("Searching for " + targetBiomeName + "...", NamedTextColor.YELLOW));

        Bukkit.getAsyncScheduler().runNow(plugin, task -> {
            Location playerLocation = player.getLocation();
            var searchResult = player.getWorld().locateNearestBiome(playerLocation, radius, targetBiome);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (searchResult != null) {
                    player.setCompassTarget(searchResult.getLocation());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    // Calculate angle and set custom model data for texture
                    Location playerLoc = player.getLocation();
                    double dx = searchResult.getLocation().getX() - playerLoc.getX();
                    double dz = searchResult.getLocation().getZ() - playerLoc.getZ();
                    double angle = Math.atan2(dz, dx) * 180 / Math.PI;
                    if (angle < 0) angle += 360;
                    int frame = (int) Math.round(angle / 11.25) % 32;

                    ItemStack compass = player.getInventory().getItemInMainHand();
                    if (compass != null && compass.getType() == Material.COMPASS && plugin.getItemManager().isNaturalCompass(compass)) {
                        ItemMeta meta = compass.getItemMeta();
                        if (meta != null) {
                            meta.setCustomModelData(1000 + frame);
                            compass.setItemMeta(meta);
                        }
                    }

                    Component message = Component.text("Found " + targetBiomeName + "!", NamedTextColor.GREEN);
                    if (plugin.getConfigManager().isShowCoordinates()) {
                        message = message.append(Utils.formatLocation(searchResult.getLocation()));
                    }
                    player.sendActionBar(message);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    player.sendActionBar(Component.text(targetBiomeName + " not found within " + radius + " blocks.", NamedTextColor.RED));
                }
            });
        });
    }
}