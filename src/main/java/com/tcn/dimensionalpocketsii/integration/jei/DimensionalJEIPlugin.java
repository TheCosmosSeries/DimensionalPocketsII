package com.tcn.dimensionalpocketsii.integration.jei;

import javax.annotation.Nullable;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.core.recipe.UpgradeStationRecipe;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleAnvil;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleUpgradeStation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class DimensionalJEIPlugin implements IModPlugin {
	
	@Nullable
	private IRecipeCategory<UpgradeStationRecipe> upgradeStationCategory;
	public static RecipeType<UpgradeStationRecipe> UPGRADING = RecipeType.create("dimensionalpocketsii", "upgrade_category", UpgradeStationRecipe.class);
	
	public DimensionalJEIPlugin() { }

	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "integration_jei");
	}
	
	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) { }

	@Override
	public void registerIngredients(IModIngredientRegistration registration) { }

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registration.addRecipeCategories(upgradeStationCategory = new CategoryUpgradeStation(guiHelper));
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) { }

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		DimensionalRecipes dimensionalRecipes = new DimensionalRecipes();
		
		registration.addRecipes(UPGRADING, dimensionalRecipes.getSmithingRecipes(upgradeStationCategory));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(ContainerModuleUpgradeStation.class, ModRegistrationManager.CONTAINER_TYPE_UPGRADE_STATION.get(), UPGRADING, 0, 9, 10, 36);
		
		registration.addRecipeTransferHandler(ContainerModuleCrafter.class, ModRegistrationManager.CONTAINER_TYPE_CRAFTER.get(), RecipeTypes.CRAFTING, 1, 9, 10, 36);
		registration.addRecipeTransferHandler(ContainerModuleSmithingTable.class, ModRegistrationManager.CONTAINER_TYPE_SMITHING_TABLE.get(), RecipeTypes.SMITHING, 0, 3, 3, 36);
		registration.addRecipeTransferHandler(ContainerModuleFurnace.class, ModRegistrationManager.CONTAINER_TYPE_FURNACE.get(), RecipeTypes.SMELTING, 0, 1, 3, 36);
		registration.addRecipeTransferHandler(ContainerModuleFurnace.class, ModRegistrationManager.CONTAINER_TYPE_FURNACE.get(), RecipeTypes.FUELING, 1, 1, 3, 36);

		registration.addRecipeTransferHandler(ContainerModuleBlastFurnace.class, ModRegistrationManager.CONTAINER_TYPE_BLAST_FURNACE.get(), RecipeTypes.BLASTING, 0, 1, 3, 36);
		registration.addRecipeTransferHandler(ContainerModuleBlastFurnace.class, ModRegistrationManager.CONTAINER_TYPE_BLAST_FURNACE.get(), RecipeTypes.FUELING, 1, 1, 3, 36);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) { 
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.MODULE_UPGRADE_STATION.get()), UPGRADING);
		
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.MODULE_CRAFTER.get()), RecipeTypes.CRAFTING);
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.MODULE_FURNACE.get()), RecipeTypes.SMELTING);
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.MODULE_BLAST_FURNACE.get()), RecipeTypes.BLASTING);
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.MODULE_SMITHING_TABLE.get()), RecipeTypes.SMITHING);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ScreenModuleUpgradeStation.class, 116, 38, 20, 24, UPGRADING);
		
		registration.addRecipeClickArea(ScreenModuleFurnace.class, 76, 35, 22, 15, RecipeTypes.SMELTING);
		registration.addRecipeClickArea(ScreenModuleBlastFurnace.class, 76, 35, 22, 15, RecipeTypes.BLASTING);
		registration.addRecipeClickArea(ScreenModuleCrafter.class, 94, 35, 22, 15, RecipeTypes.CRAFTING);
		registration.addRecipeClickArea(ScreenModuleSmithingTable.class, 72, 52, 22, 14, RecipeTypes.SMITHING);
		registration.addRecipeClickArea(ScreenModuleAnvil.class, 105, 56, 22, 15, RecipeTypes.ANVIL);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) { }

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) { }
}