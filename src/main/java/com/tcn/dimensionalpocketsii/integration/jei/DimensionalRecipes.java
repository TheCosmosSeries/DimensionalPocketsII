package com.tcn.dimensionalpocketsii.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tcn.dimensionalpocketsii.core.management.PocketsRecipeManager;
import com.tcn.dimensionalpocketsii.core.recipe.UpgradeStationRecipe;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
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
		List<UpgradeStationRecipe> testList = new ArrayList<UpgradeStationRecipe>();
		
		getRecipes(recipeManager, (RecipeType<UpgradeStationRecipe>) PocketsRecipeManager.RECIPE_TYPE_UPGRADE_STATION.get()).forEach((holder) -> {
			testList.add(holder.value());
		});
		
		return testList;
	}
	
	private static <C extends RecipeInput, T extends Recipe<C>> Collection<RecipeHolder<T>> getRecipes(RecipeManager recipeManager, RecipeType<T> recipeType) {
		List<RecipeHolder<T>> recipes = recipeManager.getAllRecipesFor(recipeType);
		return (Collection<RecipeHolder<T>>) recipes;
	}
}