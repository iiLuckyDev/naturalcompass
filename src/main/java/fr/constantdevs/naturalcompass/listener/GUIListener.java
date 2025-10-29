package fr.constantdevs.naturalcompass.listener;

import fr.constantdevs.NaturalCompass;
import fr.constantdevs.naturalcompass.gui.BiomeSelectionGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Objects;

public record GUIListener(NaturalCompass plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws SerializationException {
        Component title = event.getView().title();
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(title);

        if (plainTitle.startsWith("Select a Biome from ")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            String provider = parseProviderFromTitle(plainTitle);
            int currentPage = parsePageFromTitle(plainTitle);
            BiomeSelectionGUI biomeSelectionGUI = new BiomeSelectionGUI(player, provider, currentPage);
            biomeSelectionGUI.handleClick(player, event.getCurrentItem());
        } else if (plainTitle.equals("NaturalCompass Admin")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getItemMeta() == null) {
                return;
            }

            String itemName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(clickedItem.getItemMeta().displayName()));

            switch (itemName) {
                case "Biome Exclusion":
                    plugin.getGuiManager().openWorldSelectionGUI(player);
                    break;
                case "Toggle Recipes":
                    plugin.getConfigManager().setRecipesEnabled(!plugin.getConfigManager().isRecipesEnabled());
                    plugin.getConfigManager().save();
                    player.sendMessage(Component.text("Recipes toggled.", NamedTextColor.GREEN));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    plugin.getGuiManager().openAdminGUI(player);
                    break;
                case "Toggle Coordinates":
                    plugin.getConfigManager().setShowCoordinates(!plugin.getConfigManager().isShowCoordinates());
                    plugin.getConfigManager().save();
                    player.sendMessage(Component.text("Coordinates toggled.", NamedTextColor.GREEN));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    plugin.getGuiManager().openAdminGUI(player);
                    break;
                case "Reload Config":
                    plugin.reload();
                    player.sendMessage(Component.text("Config reloaded.", NamedTextColor.GREEN));
                    break;
            }
        } else if (plainTitle.startsWith("Biome Exclusion")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            plugin.getGuiManager().getBiomeExclusionGUI(player).handleClick(player, event.getCurrentItem());
        } else if (plainTitle.equals("Select World for Biome Exclusion")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getItemMeta() != null) {
                String worldName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(clickedItem.getItemMeta().displayName()));
                World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    plugin.getGuiManager().openBiomeExclusionGUI(player, world.getEnvironment());
                }
            }
        } else if (plainTitle.equals("Select Biome Provider")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getItemMeta() != null) {
                String itemName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(clickedItem.getItemMeta().displayName()));
                String provider = itemName.toLowerCase(); // since display is capitalized, but map key is lowercase
                BiomeSelectionGUI biomeSelectionGUI = new BiomeSelectionGUI(player, provider);
                biomeSelectionGUI.displayPage(0);
                biomeSelectionGUI.open(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Removed GUI removal to prevent null reference during programmatic refresh
    }

    private int parsePageFromTitle(String title) {
        // Title format: "Select a Biome (currentPage/totalPages)"
        int start = title.indexOf('(');
        int slash = title.indexOf('/', start);
        if (start != -1 && slash != -1) {
            try {
                return Integer.parseInt(title.substring(start + 1, slash)) - 1; // Convert to 0-based
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private String parseProviderFromTitle(String title) {
        // Title format: "Select a Biome from provider (currentPage/totalPages)"
        int fromIndex = title.indexOf("from ") + 5;
        int spaceIndex = title.indexOf(' ', fromIndex);
        if (spaceIndex == -1) spaceIndex = title.length();
        return title.substring(fromIndex, spaceIndex).trim();
    }
}