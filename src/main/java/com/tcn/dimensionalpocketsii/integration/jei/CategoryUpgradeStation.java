package com.tcn.dimensionalpocketsii.integration.jei;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.DimReference.INTEGRATION.JEI;
import com.tcn.dimensionalpocketsii.core.crafting.UpgradeStationRecipe;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CategoryUpgradeStation implements IRecipeCategory<UpgradeStationRecipe> {
	
	private IGuiHelper helper;
	private final IDrawable background;
	
	public CategoryUpgradeStation(IGuiHelper helperIn) {
		this.helper = helperIn;
		background = helper.createDrawable(RESOURCE.UPGRADE_STATION_JEI, 0, 0, 114, 72);
	}

	@Override
	public ResourceLocation getUid() {
		return JEI.UPGRADE_UID;
	}

	@Override
	public Class<? extends UpgradeStationRecipe> getRecipeClass() {
		return UpgradeStationRecipe.class;
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
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBusManager.MODULE_UPGRADE_STATION));
	}

	@Override
	public void setIngredients(UpgradeStationRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredientList());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, UpgradeStationRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, true, 27, 27);
		
		stacks.init(1, true, 6, 6);
		stacks.init(2, true, 27, 6);
		stacks.init(3, true, 48, 6);
		
		stacks.init(4, true, 6, 27);
		stacks.init(5, true, 48, 27);
		
		stacks.init(6, true, 6, 48);
		stacks.init(7, true, 27, 48);
		stacks.init(8, true, 48, 48);
		
		stacks.init(9, false, 90, 27);
		
		stacks.set(ingredients);
	}
}