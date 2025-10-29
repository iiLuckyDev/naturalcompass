package fr.constantdevs.naturalcompass.listener;

import fr.constantdevs.NaturalCompass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public record PlayerMovementListener(NaturalCompass plugin) implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (plugin.getItemManager().isNaturalCompass(mainHand)) {
            // Check if player has a target set
            if (plugin.getSearchManager().hasTarget(player)) {
                plugin.getItemManager().updateCompassRotation(mainHand, plugin.getSearchManager().getTargetLocation(player), player);
            }
        }
    }
}