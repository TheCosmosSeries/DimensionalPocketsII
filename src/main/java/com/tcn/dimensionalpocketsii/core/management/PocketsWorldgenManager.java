package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.worldgen.DimensionalOreFeature;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PocketsWorldgenManager {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, DimensionalPockets.MOD_ID);
	
	public static final DeferredHolder<Feature<?>, Feature<?>> DIMENSIONAL_ORE_END = FEATURES.register("dimensional_ore_end", DimensionalOreFeature::new);
	public static final DeferredHolder<Feature<?>, Feature<?>> DIMENSIONAL_ORE_NETHER = FEATURES.register("dimensional_ore_nether", DimensionalOreFeature::new);
	public static final DeferredHolder<Feature<?>, Feature<?>> DIMENSIONAL_ORE_OVERWORLD = FEATURES.register("dimensional_ore_overworld", DimensionalOreFeature::new);
	
	public static void register(IEventBus bus) {
		FEATURES.register(bus);
	}
}