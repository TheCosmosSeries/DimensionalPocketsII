package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.recipe.UpgradeStationRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeManager {
	
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, DimensionalPockets.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, DimensionalPockets.MOD_ID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UpgradeStationRecipe>> RECIPE_SERIALIZER_UPGRADE_STATION = RECIPE_SERIALIZERS.register("upgrading", () -> new UpgradeStationRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<UpgradeStationRecipe>> RECIPE_TYPE_UPGRADE_STATION = RECIPE_TYPES.register("upgrading", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "upgrading")));
	
	public static void register(IEventBus bus) {
		RECIPE_TYPES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}
}