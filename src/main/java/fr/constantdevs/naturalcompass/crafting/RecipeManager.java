package fr.constantdevs.naturalcompass.crafting;

import fr.constantdevs.NaturalCompass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ShapedRecipe;

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
                Material material = Material.getMaterial(matName);
                if (material != null) {
                    recipe.setIngredient(ingredientKey.charAt(0), material);
                }
            }
        }

        return recipe;
    }

    public void unregisterRecipes() {
        for (NamespacedKey key : registeredRecipes) {
            plugin.getServer().removeRecipe(key);
        }
        registeredRecipes.clear();
    }
}