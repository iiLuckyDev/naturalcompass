package fr.constantdevs;

import fr.constantdevs.naturalcompass.biome.BiomeManager;
import fr.constantdevs.naturalcompass.config.ConfigManager;
import fr.constantdevs.naturalcompass.crafting.CraftingManager;
import fr.constantdevs.naturalcompass.gui.GUIManager;
import fr.constantdevs.naturalcompass.items.ItemManager;
import fr.constantdevs.naturalcompass.listener.CompassInteractionListener;
import fr.constantdevs.naturalcompass.listener.GUIListener;
import fr.constantdevs.naturalcompass.search.SearchManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class NaturalCompass extends JavaPlugin {

    private static NaturalCompass instance;

    private ConfigManager configManager;
    private ItemManager itemManager;
    private CraftingManager craftingManager;
    private GUIManager guiManager;
    private SearchManager searchManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.itemManager = new ItemManager(this);
        this.craftingManager = new CraftingManager(this);
        this.searchManager = new SearchManager(this);
        this.guiManager = new GUIManager(this);
        BiomeManager.init();

        // Load configurations and recipes
        reload();

        // Register listeners
        getServer().getPluginManager().registerEvents(new CompassInteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new fr.constantdevs.naturalcompass.listener.PlayerMovementListener(this), this);

        // Register command
        Objects.requireNonNull(getCommand("naturalcompass")).setExecutor(new fr.constantdevs.naturalcompass.command.NaturalCompassCommand(this));

        getLogger().info("NaturalCompass has been enabled!");
    }

    @Override
    public void onDisable() {
        craftingManager.unloadRecipes();
        searchManager.stopAllRotationTasks();
        getLogger().info("NaturalCompass has been disabled!");
    }

    public void reload() {
        configManager.load();
        craftingManager.loadRecipes();
    }

    public static NaturalCompass getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }
}
