package com.tcn.dimensionalpocketsii.core.item.tool;

import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyShield;
import com.tcn.dimensionalpocketsii.DimReference;

import net.minecraft.world.item.Item;

public class DimensionalShieldEnhanced extends CosmosEnergyShield {

	public DimensionalShieldEnhanced(Item.Properties properties, CosmosEnergyItem.Properties energyProperties) {
		super(properties, energyProperties, DimReference.RESOURCE.SHIELD_ENHANCED, DimReference.RESOURCE.SHIELD_ENHANCED_NO_PATTERN);
	}
}