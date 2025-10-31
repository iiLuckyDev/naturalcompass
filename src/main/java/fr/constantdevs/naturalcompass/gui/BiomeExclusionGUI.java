package fr.constantdevs.naturalcompass.gui;

import fr.constantdevs.NaturalCompass;
import fr.constantdevs.naturalcompass.config.ConfigManager;
import fr.constantdevs.naturalcompass.items.ItemManager;
import fr.constantdevs.naturalcompass.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiomeExclusionGUI extends PaginatedGUI {

    private final NaturalCompass plugin;
    private final ConfigManager configManager;
    private final List<String> biomes;
    private final World.Environment selectedEnvironment;

    public BiomeExclusionGUI(NaturalCompass plugin, World.Environment environment) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.biomes = new ArrayList<>();
        this.selectedEnvironment = environment;
        loadBiomes();
    }

    public void loadBiomes() {
        this.biomes.clear();
        this.biomes.addAll(NaturalCompass.getInstance().getGuiManager().getBiomesForDimension(selectedEnvironment));
        Collections.sort(this.biomes);
        this.totalPages = (int) Math.ceil((double) this.biomes.size() / maxItemsPerPage);
    }

    @Override
    public void displayPage(int page) {
        this.page = page;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("Biome Exclusion (" + selectedEnvironment.name() + ") (" + (page + 1) + "/" + totalPages + ")"));

        addBorder();
        addNavigationButtons();

        // Replace next with black glass if no next page
        if (page >= totalPages - 1) {
            ItemStack nextPlaceholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta nextMeta = nextPlaceholder.getItemMeta();
            if (nextMeta != null) {
                nextMeta.displayName(Component.text(""));
                nextPlaceholder.setItemMeta(nextMeta);
            }
            inventory.setItem(26, nextPlaceholder);
        }

        int startIndex = page * maxItemsPerPage;
        for (int i = 0; i < maxItemsPerPage; i++) {
            int biomeIndex = startIndex + i;
            if (biomeIndex >= biomes.size()) {
                break;
            }
            String biomeName = biomes.get(biomeIndex);
            ItemStack biomeItem = createBiomeItem(biomeName, configManager.getExcludedBiomes().contains(biomeName));
            addItem(biomeItem);
        }
    }

    private ItemStack createBiomeItem(String biomeName, boolean excluded) {
        ItemStack item;
        if (excluded) {
            item = new ItemStack(Material.BARRIER);
        } else {
            ItemStack icon = Utils.getBiomeIcon(biomeName);
            item = icon.clone();
        }
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text(Utils.formatBiomeName(biomeName)));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(biomeName, NamedTextColor.GRAY));
            lore.add(Component.text(excluded ? "Click to include" : "Click to exclude"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public void handleClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.isSimilar(ItemManager.NEXT_PAGE)) {
            displayPage(page + 1);
            open(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            return;
        }

        if (clickedItem.isSimilar(ItemManager.PREVIOUS_PAGE)) {
            if (page > 0) {
                displayPage(page - 1);
                open(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
            return;
        }

        if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) return;


        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        String biomeName = null;
        if (meta.hasLore()) {
            List<Component> lore = meta.lore();
            if (lore != null && !lore.isEmpty()) {
                biomeName = Utils.componentToString(lore.getFirst());
            }
        }
        if (biomeName == null) return;

        List<String> excludedBiomes = new ArrayList<>(configManager.getExcludedBiomes());

        if (excludedBiomes.contains(biomeName)) {
            excludedBiomes.remove(biomeName);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        } else {
            excludedBiomes.add(biomeName);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

        configManager.setExcludedBiomes(excludedBiomes);
        plugin.getLogger().info("\u001B[32m[Natural Compass] 🧭 Excluded biomes list updated in config.yml.\u001B[0m");

        // Refresh GUI for everyone who has it open
        for (var entry : plugin.getGuiManager().getExclusionGUIs().entrySet()) {
            BiomeExclusionGUI gui = entry.getValue();
            gui.displayPage(gui.page);
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null) {
                gui.open(p);
            }
        }
    }
}
