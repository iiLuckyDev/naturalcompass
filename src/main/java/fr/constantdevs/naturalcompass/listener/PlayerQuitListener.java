package fr.constantdevs.naturalcompass.listener;

import fr.constantdevs.NaturalCompass;
import fr.constantdevs.naturalcompass.items.ItemManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerQuitListener implements Listener {

    private final ItemManager itemManager;

    public PlayerQuitListener(NaturalCompass plugin) {
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.COMPASS && itemManager.isNaturalCompass(item)) {
                CustomModelData customModelData = CustomModelData.customModelData()
                        .addFloat(194)
                        .build();
                item.resetData(DataComponentTypes.CUSTOM_MODEL_DATA);
                item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
            }
        }
    }
}