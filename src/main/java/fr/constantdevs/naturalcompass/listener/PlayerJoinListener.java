package fr.constantdevs.naturalcompass.listener;

import fr.constantdevs.NaturalCompass;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoinListener(NaturalCompass plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfigManager().isRecipesEnabled()) {
            Player player = event.getPlayer();
            player.discoverRecipe(new NamespacedKey(plugin, "tier1"));
            player.discoverRecipe(new NamespacedKey(plugin, "tier2"));
            player.discoverRecipe(new NamespacedKey(plugin, "tier3"));
            player.discoverRecipe(new NamespacedKey(plugin, "tier4"));
            player.discoverRecipe(new NamespacedKey(plugin, "tier5"));
        }
    }
}
