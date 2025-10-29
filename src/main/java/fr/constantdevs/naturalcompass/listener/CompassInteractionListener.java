package fr.constantdevs.naturalcompass.listener;

import fr.constantdevs.NaturalCompass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public record CompassInteractionListener(NaturalCompass plugin) implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !plugin.getItemManager().isNaturalCompass(item)) {
            return;
        }

        event.setCancelled(true);

        if (event.getAction().isRightClick()) {
            // Open Biome Selection GUI
            plugin.getGuiManager().openBiomeSelectionGUI(event.getPlayer());
        } else if (event.getAction().isLeftClick()) {
            // Start Biome Search
            plugin.getSearchManager().startSearch(event.getPlayer());
        }
    }
}