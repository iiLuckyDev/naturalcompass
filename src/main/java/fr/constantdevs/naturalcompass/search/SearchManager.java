package fr.constantdevs.naturalcompass.search;

import fr.constantdevs.NaturalCompass;
import fr.constantdevs.naturalcompass.util.Utils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SearchManager {

    private final NaturalCompass plugin;
    private final Map<UUID, String> targetBiomes = new HashMap<>();
    private final Map<UUID, Location> targetLocations = new HashMap<>();
    private final Map<UUID, BukkitTask> rotationTasks = new HashMap<>();

    public SearchManager(NaturalCompass plugin) {
        this.plugin = plugin;
    }

    public void setTargetBiome(Player player, String biome) {
        targetBiomes.put(player.getUniqueId(), biome);
        // Don't start rotation task yet - wait for search
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
        Registry<@NotNull Biome> biomeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
        Biome targetBiome = biomeRegistry.get(key);
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

                    targetLocations.put(player.getUniqueId(), searchResult.getLocation());
                    ItemStack compass = player.getInventory().getItemInMainHand();
                    if (compass.getType() == Material.COMPASS && plugin.getItemManager().isNaturalCompass(compass)) {
                        plugin.getItemManager().updateCompassRotation(compass, searchResult.getLocation(), player);
                        startRotationTask(player);
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

    private void startRotationTask(Player player) {
        UUID playerId = player.getUniqueId();
        stopRotationTask(playerId);

        Location targetLocation = player.getCompassTarget();

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!plugin.getItemManager().isNaturalCompass(mainHand)) return;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                stopRotationTask(playerId);
                return;
            }

            ItemStack currentMainHand = player.getInventory().getItemInMainHand();
            if (!plugin.getItemManager().isNaturalCompass(currentMainHand)) {
                stopRotationTask(playerId);
                return;
            }

            plugin.getItemManager().updateCompassRotation(currentMainHand, targetLocation, player);
        }, 0L, 1L); // Update every tick

        rotationTasks.put(playerId, task);
    }

    private void stopRotationTask(UUID playerId) {
        BukkitTask task = rotationTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }

    public boolean hasTarget(Player player) {
        return targetLocations.containsKey(player.getUniqueId());
    }

    public Location getTargetLocation(Player player) {
        return targetLocations.get(player.getUniqueId());
    }

    public void stopAllRotationTasks() {
        for (BukkitTask task : rotationTasks.values()) {
            task.cancel();
        }
        rotationTasks.clear();
        targetLocations.clear();
        targetBiomes.clear();
    }
}