package fr.constantdevs.naturalcompass.crafting;

import fr.constantdevs.NaturalCompass;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecipeManager {

    private final NaturalCompass plugin;
    private final List<NamespacedKey> registeredRecipes = new ArrayList<>();

    public RecipeManager(NaturalCompass plugin) {
        this.plugin = plugin;
    }

    public void loadRecipes() {
        unregisterRecipes();

        if (!plugin.getConfigManager().isRecipesEnabled()) {
            return;
        }

        ConfigurationSection recipesSection = plugin.getConfig().getConfigurationSection("recipes");
        if (recipesSection == null) {
            return;
        }

        Set<String> recipeKeys = recipesSection.getKeys(false);
        for (String key : recipeKeys) {
            if (key.equals("enabled")) {
                continue;
            }

            ConfigurationSection recipeConfig = recipesSection.getConfigurationSection(key);
            if (recipeConfig == null) {
                continue;
            }

            try {
                int tier = Integer.parseInt(key.replace("tier", ""));
                ShapedRecipe recipe = createRecipe(recipeConfig, tier);
                if (recipe != null) {
                    plugin.getServer().addRecipe(recipe);
                    registeredRecipes.add(recipe.getKey());
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid recipe key: " + key);
            }
        }
    }

    private ShapedRecipe createRecipe(ConfigurationSection config, int tier) {
        List<String> shape = config.getStringList("shape");
        if (shape.isEmpty()) {
            return null;
        }

        NamespacedKey key = new NamespacedKey(plugin, "natural_compass_tier" + tier);
        ShapedRecipe recipe = new ShapedRecipe(key, plugin.getItemManager().createCompass(tier));
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = config.getConfigurationSection("ingredients");
        if (ingredients == null) {
            return null;
        }

        for (String ingredientKey : ingredients.getKeys(false)) {
            String matName = ingredients.getString(ingredientKey);
            if (matName != null) {
                if (matName.startsWith("TIER")) {
                    try {
                        int tierNum = Integer.parseInt(matName.substring(4));

                        // Créer tous les variants de boussole avec float custom model data de 178 à 209
                        List<ItemStack> compassVariants = new ArrayList<>();
                        for (int cmdFloat = 178; cmdFloat <= 209; cmdFloat++) {
                            ItemStack variant = createCompassWithFloatCMD(tierNum, cmdFloat);
                            compassVariants.add(variant);
                        }

                        // Utiliser RecipeChoice.ExactChoice avec tous les variants
                        RecipeChoice.ExactChoice choice = new RecipeChoice.ExactChoice(compassVariants);
                        recipe.setIngredient(ingredientKey.charAt(0), choice);

                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid tier reference: " + matName);
                    }
                } else {
                    Material material = Material.getMaterial(matName);
                    if (material != null) {
                        recipe.setIngredient(ingredientKey.charAt(0), material);
                    }
                }
            }
        }

        return recipe;
    }

    /**
     * Crée une boussole avec un CustomModelData float spécifique en utilisant les Data Components
     */
    private ItemStack createCompassWithFloatCMD(int tier, float customModelDataFloat) {
        // Obtenir la boussole de base du tier (avec nom, lore, persistent data, etc.)
        ItemStack compass = plugin.getItemManager().createCompass(tier);

        // Créer le CustomModelData avec la valeur float
        CustomModelData customModelData = CustomModelData.customModelData()
                .addFloat(customModelDataFloat)
                .build();

        // Appliquer le data component à l'item
        compass.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);

        return compass;
    }

    public void unregisterRecipes() {
        for (NamespacedKey key : registeredRecipes) {
            plugin.getServer().removeRecipe(key);
        }
        registeredRecipes.clear();
    }
}