package com.tcn.dimensionalpocketsii.integration.jei;

import javax.annotation.Nullable;

import com.tcn.dimensionalpocketsii.DimReference.INTEGRATION.JEI;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.crafting.UpgradeStationRecipe;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleUpgradeStation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
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
public class CoreJeiPlugin implements IModPlugin {
	
	@Nullable
	private IRecipeCategory<UpgradeStationRecipe> upgradeStationCategory;
	
	public CoreJeiPlugin() { }

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(DimensionalPockets.MOD_ID, "integration_jei");
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
		
		registration.addRecipes(dimensionalRecipes.getSmithingRecipes(upgradeStationCategory), JEI.UPGRADE_UID);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(ContainerModuleUpgradeStation.class, JEI.UPGRADE_UID, 0, 9, 10, 36);
		
		registration.addRecipeTransferHandler(ContainerModuleCrafter.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
		registration.addRecipeTransferHandler(ContainerModuleSmithingTable.class, VanillaRecipeCategoryUid.SMITHING, 0, 2, 3, 36);
		registration.addRecipeTransferHandler(ContainerModuleFurnace.class, VanillaRecipeCategoryUid.FURNACE, 0, 1, 3, 36);
		registration.addRecipeTransferHandler(ContainerModuleFurnace.class, VanillaRecipeCategoryUid.FUEL, 1, 1, 3, 36);

		registration.addRecipeTransferHandler(ContainerModuleBlastFurnace.class, VanillaRecipeCategoryUid.BLASTING, 0, 1, 3, 36);
		registration.addRecipeTransferHandler(ContainerModuleBlastFurnace.class, VanillaRecipeCategoryUid.FUEL, 1, 1, 3, 36);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) { 
		registration.addRecipeCatalyst(new ItemStack(ModBusManager.MODULE_UPGRADE_STATION), JEI.UPGRADE_UID);
		
		registration.addRecipeCatalyst(new ItemStack(ModBusManager.MODULE_CRAFTER), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeCatalyst(new ItemStack(ModBusManager.MODULE_FURNACE), VanillaRecipeCategoryUid.FURNACE);
		registration.addRecipeCatalyst(new ItemStack(ModBusManager.MODULE_BLAST_FURNACE), VanillaRecipeCategoryUid.BLASTING);
		registration.addRecipeCatalyst(new ItemStack(ModBusManager.MODULE_SMITHING_TABLE), VanillaRecipeCategoryUid.SMITHING);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ScreenModuleUpgradeStation.class, 103, 38, 20, 24, JEI.UPGRADE_UID);
		
		registration.addRecipeClickArea(ScreenModuleFurnace.class, 76, 35, 22, 15, VanillaRecipeCategoryUid.FURNACE);
		registration.addRecipeClickArea(ScreenModuleBlastFurnace.class, 76, 35, 22, 15, VanillaRecipeCategoryUid.BLASTING);
		registration.addRecipeClickArea(ScreenModuleCrafter.class, 94, 35, 22, 15, VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeClickArea(ScreenModuleSmithingTable.class, 105, 56, 22, 15, VanillaRecipeCategoryUid.SMITHING);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) { }

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) { }
}