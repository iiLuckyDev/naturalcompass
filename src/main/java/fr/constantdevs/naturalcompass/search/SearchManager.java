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
    private final Map<UUID, Boolean> searching = new HashMap<>();
    private final Map<UUID, Long> lastSearchTimes = new HashMap<>();

    public SearchManager(NaturalCompass plugin) {
        this.plugin = plugin;
    }

    public void setTargetBiome(Player player, String biome) {
        targetBiomes.put(player.getUniqueId(), biome);
        // Don't start rotation task yet - wait for search
    }

    public void startSearch(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastSearchTimes.get(playerId);
        if (lastTime != null && currentTime - lastTime < plugin.getConfigManager().getSearchCooldownSeconds() * 1000L) {
            long remaining = (plugin.getConfigManager().getSearchCooldownSeconds() * 1000L - (currentTime - lastTime)) / 1000;
            player.sendMessage(Component.text("Search is on cooldown. Please wait " + remaining + " seconds.", NamedTextColor.RED));
            return;
        }

        if (targetLocations.containsKey(playerId)) {
            searching.put(playerId, false); // Cancel any ongoing search
            stopRotationTask(playerId);
            targetLocations.remove(playerId);
            player.sendActionBar(Component.text("Previous search cancelled.", NamedTextColor.YELLOW));
        }

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

        plugin.getLogger().info("\u001B[32m[Natural Compass] 🧭 Player " + player.getName() + " started searching for " + targetBiomeName + "\u001B[0m");

        searching.put(playerId, true); // Mark as searching

        Bukkit.getAsyncScheduler().runNow(plugin, task -> {
            if (!searching.getOrDefault(playerId, false)) return; // Check if cancelled

            Location playerLocation = player.getLocation();
            var searchResult = player.getWorld().locateNearestBiome(playerLocation, radius, targetBiome);

            if (!searching.getOrDefault(playerId, false)) return; // Check again after search

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!searching.getOrDefault(playerId, false)) return; // Final check

                if (searchResult != null) {
                    plugin.getLogger().info("\u001B[32m[Natural Compass] 🧭 Found " + targetBiomeName + " for " + player.getName() + " at " + searchResult.getLocation() + "\u001B[0m");
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
                    plugin.getLogger().info("\u001B[32m[Natural Compass] 🧭 No " + targetBiomeName + " found for " + player.getName() + " within " + radius + " blocks\u001B[0m");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    player.sendActionBar(Component.text(targetBiomeName + " not found within " + radius + " blocks.", NamedTextColor.RED));
                }

                searching.put(playerId, false); // Mark as finished
                lastSearchTimes.put(playerId, System.currentTimeMillis()); // Update last search time
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
        searching.clear();
        lastSearchTimes.clear();
    }
}
