package com.tcn.dimensionalpocketsii.integration.jei;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.recipe.UpgradeStationRecipe;

import mezz.jei.api.recipe.RecipeType;

public class PocketsRecipeTypes {

	public static final RecipeType<UpgradeStationRecipe> UPGRADING = RecipeType.create(DimensionalPockets.MOD_ID, "upgrade_category", UpgradeStationRecipe.class);
}