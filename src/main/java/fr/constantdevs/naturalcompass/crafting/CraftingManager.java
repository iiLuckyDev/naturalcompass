package fr.constantdevs.naturalcompass.crafting;

import fr.constantdevs.NaturalCompass;
public class CraftingManager {

    private final RecipeManager recipeManager;

    public CraftingManager(NaturalCompass plugin) {
        this.recipeManager = new RecipeManager(plugin);
    }

    public void loadRecipes() {
        recipeManager.loadRecipes();
    }

    public void unloadRecipes() {
        recipeManager.unregisterRecipes();
    }
}