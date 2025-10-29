package fr.constantdevs.naturalcompass.gui;

import fr.constantdevs.NaturalCompass;
import fr.constantdevs.naturalcompass.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiomeSelectionGUI extends PaginatedGUI {

    private final NaturalCompass plugin;
    private final List<String> biomes;
    private final String provider;

    public BiomeSelectionGUI(Player player, String provider) {
        this(player, provider, 0);
    }

    public BiomeSelectionGUI(Player player, String provider, int initialPage) {
        this.plugin = NaturalCompass.getInstance();
        this.provider = provider;
        this.biomes = new ArrayList<>(plugin.getGuiManager().getBiomesForProviderAndDimension(provider, player.getWorld().getEnvironment()));
        this.biomes.removeAll(plugin.getConfigManager().getExcludedBiomes());
        Collections.sort(this.biomes);
        this.totalPages = (int) Math.ceil((double) this.biomes.size() / maxItemsPerPage);
        this.page = Math.max(0, Math.min(initialPage, totalPages - 1)); // Clamp to valid range
    }

    @Override
    public void displayPage(int page) {
        this.page = page;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("Select a Biome from " + provider + " (" + (page + 1) + "/" + totalPages + ")"));

        addBorder();
        addNavigationButtons();

        int startIndex = page * maxItemsPerPage;
        for (int i = 0; i < maxItemsPerPage; i++) {
            int biomeIndex = startIndex + i;
            if (biomeIndex >= biomes.size()) {
                break;
            }
            String biomeName = biomes.get(biomeIndex);
            ItemStack biomeItem = createBiomeItem(biomeName);
            addItem(biomeItem);
        }
    }

    private ItemStack createBiomeItem(String biomeName) {
        ItemStack item = Utils.getBiomeIcon(biomeName);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(Utils.formatBiomeName(biomeName), NamedTextColor.GREEN));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(biomeName, NamedTextColor.GRAY));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void handleClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.isSimilar(fr.constantdevs.naturalcompass.items.ItemManager.NEXT_PAGE)) {
            if (page < totalPages - 1) {
                BiomeSelectionGUI newGUI = new BiomeSelectionGUI(player, provider, page + 1);
                newGUI.displayPage(page + 1);
                newGUI.open(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        } else if (clickedItem.isSimilar(fr.constantdevs.naturalcompass.items.ItemManager.PREVIOUS_PAGE)) {
            if (page > 0) {
                BiomeSelectionGUI newGUI = new BiomeSelectionGUI(player, provider, page - 1);
                newGUI.displayPage(page - 1);
                newGUI.open(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        } else if (isBiomeItem(clickedItem)) {
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            String biomeName = null;
            if (meta.hasLore()) {
                List<Component> lore = meta.lore();
                if (lore != null && !lore.isEmpty()) {
                    biomeName = Utils.componentToString(lore.getFirst());
                }
            }
            if (biomeName != null) {
                plugin.getSearchManager().setTargetBiome(player, biomeName);
                player.closeInventory();
                player.sendMessage(Component.text("Target biome set to " + biomeName, NamedTextColor.GREEN));
            }
        }
    }

    private boolean isBiomeItem(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type != Material.AIR && type != Material.BLACK_STAINED_GLASS_PANE;
    }
}
