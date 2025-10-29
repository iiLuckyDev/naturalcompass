package fr.constantdevs.naturalcompass.gui;

import fr.constantdevs.naturalcompass.NaturalCompass;
import fr.constantdevs.naturalcompass.config.ConfigManager;
import fr.constantdevs.naturalcompass.items.ItemManager;
import fr.constantdevs.naturalcompass.util.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiomeExclusionGUI extends PaginatedGUI {

    private final NaturalCompass plugin;
    private final ConfigManager configManager;
    private final List<String> biomes;

    public BiomeExclusionGUI(NaturalCompass plugin, Player player) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.biomes = new ArrayList<>();
        loadBiomes(player);
    }

    public void loadBiomes(Player player) {
        this.biomes.clear();
        this.biomes.addAll(NaturalCompass.getInstance().getGuiManager().getBiomesForDimension(player.getWorld().getEnvironment()));
        Collections.sort(this.biomes);
        this.totalPages = (int) Math.ceil((double) this.biomes.size() / maxItemsPerPage);
    }

    @Override
    public void displayPage(int page) {
        this.page = page;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("Biome Exclusion (" + (page + 1) + "/" + totalPages + ")"));

        addBorder();
        addNavigationButtons();

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
        ItemStack item = new ItemStack(excluded ? Material.BARRIER : Utils.getBiomeIcon(biomeName));
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text(Utils.formatBiomeName(biomeName)));
            meta.lore(Collections.singletonList(Component.text(excluded ? "Click to include" : "Click to exclude")));
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
        if (meta == null || !meta.hasDisplayName()) return;

        String biomeName = Utils.revertFormattedBiomeName(Utils.componentToString(meta.displayName()));
        List<String> excludedBiomes = new ArrayList<>(configManager.getExcludedBiomes());

        if (excludedBiomes.contains(biomeName)) {
            excludedBiomes.remove(biomeName);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        } else {
            excludedBiomes.add(biomeName);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

        configManager.setExcludedBiomes(excludedBiomes);
        plugin.getLogger().info("Excluded biomes list updated in config.yml.");

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