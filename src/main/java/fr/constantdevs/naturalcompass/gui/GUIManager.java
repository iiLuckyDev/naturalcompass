package fr.constantdevs.naturalcompass.gui;

import fr.constantdevs.naturalcompass.NaturalCompass;
import fr.constantdevs.naturalcompass.util.Utils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class GUIManager {

    private final NaturalCompass plugin;
    private final Map<UUID, BiomeExclusionGUI> exclusionGUIs = new HashMap<>();

    public GUIManager(NaturalCompass plugin) {
        this.plugin = plugin;
    }

    public void openBiomeSelectionGUI(Player player) {
        BiomeSelectionGUI biomeSelectionGUI = new BiomeSelectionGUI(player);
        biomeSelectionGUI.displayPage(0);
        biomeSelectionGUI.open(player);
    }

    public List<String> getBiomesForDimension(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> Arrays.asList("minecraft:plains", "minecraft:sunflower_plains", "minecraft:snowy_plains",
                    "minecraft:ice_spikes", "minecraft:desert", "minecraft:swamp", "minecraft:mangrove_swamp",
                    "minecraft:forest", "minecraft:flower_forest", "minecraft:birch_forest", "minecraft:dark_forest",
                    "minecraft:taiga", "minecraft:snowy_taiga", "minecraft:jungle", "minecraft:bamboo_jungle",
                    "minecraft:badlands", "minecraft:savanna", "minecraft:mushroom_fields", "minecraft:mountain",
                    "minecraft:ocean", "minecraft:deep_ocean", "minecraft:cold_ocean", "minecraft:lukewarm_ocean",
                    "minecraft:warm_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_lukewarm_ocean",
                    "minecraft:deep_warm_ocean", "minecraft:dripstone_caves", "minecraft:lush_caves",
                    "minecraft:cherry_grove", "minecraft:pale_garden");
            case NETHER -> Arrays.asList("minecraft:crimson_forest", "minecraft:soul_sand_valley",
                    "minecraft:basalt_deltas", "minecraft:nether_wastes", "minecraft:warped_forest");
            case THE_END -> Arrays.asList("minecraft:end_highlands", "minecraft:end_midlands", "minecraft:end_barrens");
            default -> new ArrayList<>();
        };
    }

    public void openAdminGUI(Player player) {
        Inventory gui = AdminGUI.createAdminGUI(plugin.getConfigManager());
        Utils.addStateLore(Objects.requireNonNull(gui.getItem(14)), plugin.getConfigManager().isShowCoordinates());
        player.openInventory(gui);
    }
    public void openBiomeExclusionGUI(Player player) {
        BiomeExclusionGUI biomeExclusionGUI = exclusionGUIs.computeIfAbsent(player.getUniqueId(), uuid -> new BiomeExclusionGUI(plugin, player));
        biomeExclusionGUI.loadBiomes(player);
        biomeExclusionGUI.displayPage(0);
        biomeExclusionGUI.open(player);
    }

    public BiomeExclusionGUI getBiomeExclusionGUI(Player player) {
        return exclusionGUIs.get(player.getUniqueId());
    }

    public Map<UUID, BiomeExclusionGUI> getExclusionGUIs() {
        return exclusionGUIs;
    }
}