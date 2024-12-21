package com.tcn.dimensionalpocketsii.integration.jei;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.ModReferences.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.core.recipe.UpgradeStationRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CategoryUpgradeStation implements IRecipeCategory<UpgradeStationRecipe> {
	
	private IGuiHelper helper;
	private final IDrawable background;
	
	public CategoryUpgradeStation(IGuiHelper helperIn) {
		this.helper = helperIn;
		background = helper.createDrawable(RESOURCE.UPGRADE_STATION_JEI, 0, 0, 114, 72);
	}
	
	@Override
	public Component getTitle() {
		return ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.integration.jei.upgrade_category");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModRegistrationManager.MODULE_UPGRADE_STATION.get()));
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder recipeLayout, UpgradeStationRecipe recipe, IFocusGroup ingredients) {
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 28, 28).addIngredients(recipe.getFocusIngredient());
		
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 7,  7).addIngredients(recipe.getTopIngredient(0));
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 28, 7).addIngredients(recipe.getTopIngredient(1));
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 49, 7).addIngredients(recipe.getTopIngredient(2));

		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 7,  28).addIngredients(recipe.getMiddleIngredient(0));
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 49, 28).addIngredients(recipe.getMiddleIngredient(1));

		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 7,  49).addIngredients(recipe.getBottomIngredient(0));
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 28, 49).addIngredients(recipe.getBottomIngredient(1));
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 49, 49).addIngredients(recipe.getBottomIngredient(2));

		recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 91, 28).addItemStack(recipe.getResultItemStack());
	}

	@Override
	public RecipeType<UpgradeStationRecipe> getRecipeType() {
		return DimensionalJEIPlugin.UPGRADING;
	}
}