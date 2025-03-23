package com.tcn.dimensionalpocketsii.integration.jei;

import java.util.Collection;
import java.util.List;

import com.tcn.dimensionalpocketsii.core.crafting.UpgradeStationRecipe;
import com.tcn.dimensionalpocketsii.core.management.CraftingManager;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class DimensionalRecipes {
	private final RecipeManager recipeManager;

	public DimensionalRecipes() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel world = minecraft.level;
		this.recipeManager = world.getRecipeManager();
	}
	
	public List<UpgradeStationRecipe> getSmithingRecipes(IRecipeCategory<UpgradeStationRecipe> stationCategory) {
		return (List<UpgradeStationRecipe>) getRecipes(recipeManager, CraftingManager.RECIPE_TYPE_UPGRADE_STATION);
	}
	
	private static <C extends Container, T extends Recipe<C>> Collection<T> getRecipes(RecipeManager recipeManager, RecipeType<T> recipeType) {
		List<T> recipes = recipeManager.getAllRecipesFor(recipeType);
		return (Collection<T>) recipes;
	}
}