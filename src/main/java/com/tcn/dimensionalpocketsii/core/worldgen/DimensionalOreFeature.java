package com.tcn.dimensionalpocketsii.core.worldgen;

import java.util.Set;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class DimensionalOreFeature extends OreFeature {
	private final Set<ResourceKey<Level>> dimensions = Set.of(Level.OVERWORLD);
	
	
	public DimensionalOreFeature() {
		super(OreConfiguration.CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<OreConfiguration> context) {
		WorldGenLevel world = context.level();
		
		if (!this.dimensions.contains(world.getLevel().dimension())) {
			return false;
		}
		
		return super.place(context);
	}
}
