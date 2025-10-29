package fr.constantdevs.naturalcompass.config;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.constantdevs.NaturalCompass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class ConfigManager {

    private final NaturalCompass plugin;
    private final File configFile;
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    // Configuration value
    private int searchTimeout;
    private List<Integer> tierRadii;
    private boolean showCoordinates;
    private boolean searchingMessageEnabled;
    private boolean foundMessageEnabled;
    private boolean debugLogging;
    private boolean detailLogging;
    private List<String> excludedBiomes;
    private boolean recipesEnabled;

    // Biomes configuration
    private final File biomesFile;
    private final YamlConfigurationLoader biomesLoader;
    private CommentedConfigurationNode biomesRoot;
    private Map<String, ItemStack> biomeIcons;
    private Map<String, String> biomeDimensions;

    // Provider configuration
    private final File providerFile;
    private final YamlConfigurationLoader providerLoader;
    private CommentedConfigurationNode providerRoot;
    private Map<String, ItemStack> providerIcons;

    public ConfigManager(NaturalCompass plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.loader = YamlConfigurationLoader.builder()
                .file(configFile)
                .build();

        // Initialize biomes configuration
        this.biomesFile = new File(plugin.getDataFolder(), "biomes.yml");
        this.biomesLoader = YamlConfigurationLoader.builder()
                .file(biomesFile)
                .build();

        // Initialize provider configuration
        this.providerFile = new File(plugin.getDataFolder(), "providers.yml");
        this.providerLoader = YamlConfigurationLoader.builder()
                .file(providerFile)
                .build();
    }

    public void load() {
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        try {
            root = loader.load();
            loadValues();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config.yml!", e);
        }

        // Load biomes configuration
        if (!biomesFile.exists()) {
            plugin.saveResource("biomes.yml", false);
        }
        try {
            biomesRoot = biomesLoader.load();
            loadBiomeIcons();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load biomes.yml!", e);
        }

        // Load provider configuration
        if (!providerFile.exists()) {
            plugin.saveResource("providers.yml", false);
        }
        try {
            providerRoot = providerLoader.load();
            loadProviderIcons();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load providers.yml!", e);
        }
    }

    private void loadValues() {
        searchTimeout = root.node("search", "timeout").getInt(5);
        tierRadii = List.of(
                root.node("search", "tiers", "tier1").getInt(1000),
                root.node("search", "tiers", "tier2").getInt(2500),
                root.node("search", "tiers", "tier3").getInt(5000),
                root.node("search", "tiers", "tier4").getInt(10000),
                root.node("search", "tiers", "tier5").getInt(25000)
        );
        showCoordinates = root.node("display", "show-coordinates").getBoolean(true);
        searchingMessageEnabled = root.node("display", "messages", "searching").getBoolean(true);
        foundMessageEnabled = root.node("display", "messages", "found").getBoolean(true);
        debugLogging = root.node("logging", "debug").getBoolean(false);
        detailLogging = root.node("logging", "detaillogging").getBoolean(true);
        try {
            excludedBiomes = root.node("biomes", "excluded").getList(String.class, Collections.emptyList());
        } catch (Exception e) {
            excludedBiomes = Collections.emptyList();
        }
        recipesEnabled = root.node("recipes", "enabled").getBoolean(true);
    }

    private void loadBiomeIcons() {
        biomeIcons = new HashMap<>();
        biomeDimensions = new HashMap<>();
        CommentedConfigurationNode biomesNode = biomesRoot.node("biomes");
        plugin.getLogger().info("Loading biome icons from biomes.yml...");
        for (Map.Entry<Object, CommentedConfigurationNode> entry : biomesNode.childrenMap().entrySet()) {
            String biome = entry.getKey().toString();
            String materialName = entry.getValue().node("icon").getString();
            String dimension = entry.getValue().node("dimension").getString();
            if (materialName != null) {
                if (materialName.equals("PLAYER_HEAD")) {
                    String texture = entry.getValue().node("texture").getString();
                    if (texture != null) {
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) head.getItemMeta();
                        if (meta != null) {
                            PlayerProfile profile = plugin.getServer().createProfile(java.util.UUID.randomUUID());
                            profile.getProperties().add(new ProfileProperty("textures", texture));
                            meta.setPlayerProfile(profile);
                            head.setItemMeta(meta);
                            biomeIcons.put(biome, head);
                            plugin.getLogger().fine("Loaded custom head icon for biome '" + biome + "'");
                        }
                    } else {
                        plugin.getLogger().warning("No texture specified for PLAYER_HEAD icon for biome '" + biome + "', skipping.");
                    }
                } else {
                    Material material = Material.matchMaterial(materialName);
                    if (material != null) {
                        if (material.isItem()) {
                            biomeIcons.put(biome, new ItemStack(material));
                            plugin.getLogger().fine("Loaded icon for biome '" + biome + "': " + materialName);
                        } else {
                            plugin.getLogger().warning("Material '" + materialName + "' is not an item for biome '" + biome + "', using default.");
                        }
                    } else {
                        plugin.getLogger().warning("Invalid material '" + materialName + "' for biome '" + biome + "', skipping.");
                    }
                }
            } else {
                plugin.getLogger().warning("No icon specified for biome '" + biome + "', skipping.");
            }
            biomeDimensions.put(biome, Objects.requireNonNullElse(dimension, "overworld"));
        }
        plugin.getLogger().info("Loaded " + biomeIcons.size() + " biome icons.");
    }

    private void loadProviderIcons() {
        providerIcons = new HashMap<>();
        CommentedConfigurationNode providersNode = providerRoot.node("providers");
        plugin.getLogger().info("Loading provider icons from providers.yml...");
        for (Map.Entry<Object, CommentedConfigurationNode> entry : providersNode.childrenMap().entrySet()) {
            String provider = entry.getKey().toString();
            String materialName = entry.getValue().node("icon").getString();
            if (materialName != null) {
                if (materialName.equals("PLAYER_HEAD")) {
                    String texture = entry.getValue().node("texture").getString();
                    if (texture != null) {
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) head.getItemMeta();
                        if (meta != null) {
                            PlayerProfile profile = plugin.getServer().createProfile(java.util.UUID.randomUUID());
                            profile.getProperties().add(new ProfileProperty("textures", texture));
                            meta.setPlayerProfile(profile);
                            head.setItemMeta(meta);
                            providerIcons.put(provider, head);
                            plugin.getLogger().fine("Loaded custom head icon for provider '" + provider + "'");
                        }
                    } else {
                        plugin.getLogger().warning("No texture specified for PLAYER_HEAD icon for provider '" + provider + "', skipping.");
                    }
                } else {
                    Material material = Material.matchMaterial(materialName);
                    if (material != null) {
                        if (material.isItem()) {
                            providerIcons.put(provider, new ItemStack(material));
                            plugin.getLogger().fine("Loaded icon for provider '" + provider + "': " + materialName);
                        } else {
                            plugin.getLogger().warning("Material '" + materialName + "' is not an item for provider '" + provider + "', using default.");
                        }
                    } else {
                        plugin.getLogger().warning("Invalid material '" + materialName + "' for provider '" + provider + "', skipping.");
                    }
                }
            } else {
                plugin.getLogger().warning("No icon specified for provider '" + provider + "', skipping.");
            }
        }
        plugin.getLogger().info("Loaded " + providerIcons.size() + " provider icons.");
    }

    public List<Integer> getTierRadii() {
        return tierRadii;
    }

    public boolean isShowCoordinates() {
        return showCoordinates;
    }

    public List<String> getExcludedBiomes() {
        return excludedBiomes;
    }

    public boolean isRecipesEnabled() {
        return recipesEnabled;
    }

    public void setRecipesEnabled(boolean recipesEnabled) throws SerializationException {
        this.recipesEnabled = recipesEnabled;
        root.node("recipes", "enabled").set(recipesEnabled);
    }

    public void setShowCoordinates(boolean showCoordinates) throws SerializationException {
        this.showCoordinates = showCoordinates;
        root.node("display", "show-coordinates").set(showCoordinates);
    }

    public void setExcludedBiomes(List<String> excludedBiomes) {
        this.excludedBiomes = excludedBiomes;
        try {
            root.node("biomes", "excluded").set(excludedBiomes);
            save();
        } catch (SerializationException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save excluded biomes!", e);
        }
    }

    public Map<String, ItemStack> getBiomeIcons() {
        return biomeIcons;
    }

    public Map<String, String> getBiomeDimensions() {
        return biomeDimensions;
    }

    public Map<String, ItemStack> getProviderIcons() {
        return providerIcons;
    }

    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config.yml!", e);
        }

        // Save biomes configuration
        try {
            biomesLoader.save(biomesRoot);
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save biomes.yml!", e);
        }

        // Save provider configuration
        try {
            providerLoader.save(providerRoot);
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save providers.yml!", e);
        }
    }
}
