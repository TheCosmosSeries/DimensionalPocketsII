package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

@SuppressWarnings("unused")
public class ModDimensionManager {
	
	public static final ResourceLocation POCKET_ID = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "pocket");

	public static final ResourceKey<Level> POCKET_WORLD = ResourceKey.create(Registries.DIMENSION, POCKET_ID);
	public static final ResourceKey<DimensionType> POCKET_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, POCKET_ID);
	private static final ResourceKey<LevelStem> POCKET_DIMENSION = ResourceKey.create(Registries.LEVEL_STEM, POCKET_ID);
	private static final ResourceKey<Biome> POCKET_BIOME = ResourceKey.create(Registries.BIOME, POCKET_ID);	  
}