package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.cosmoslibrary.CosmosLibrary;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.crafting.UpgradeStationRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CraftingManager {

	public static final RecipeSerializer<UpgradeStationRecipe> RECIPE_SERIALIZER_UPGRADE_STATION = new UpgradeStationRecipe.Serializer();
	public static final RecipeType<UpgradeStationRecipe> RECIPE_TYPE_UPGRADE_STATION = RecipeType.register(DimensionalPockets.MOD_ID + ":upgrading");

	@SubscribeEvent
	public static void onRecipeSerializerRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event) {
		event.getRegistry().registerAll(
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "upgrading", RECIPE_SERIALIZER_UPGRADE_STATION)
		);

		CosmosLibrary.CONSOLE.startup("RecipeSerializer<?> Registration complete.");
	}
}
