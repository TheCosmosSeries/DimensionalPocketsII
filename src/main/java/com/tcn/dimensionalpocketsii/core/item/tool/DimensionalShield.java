package com.tcn.dimensionalpocketsii.core.item.tool;

import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyShield;
import com.tcn.dimensionalpocketsii.DimReference;

import net.minecraft.world.item.Item;

public class DimensionalShield extends CosmosEnergyShield {

	public DimensionalShield(Item.Properties properties, CosmosEnergyItem.Properties energyProperties) {
		super(properties, energyProperties, DimReference.RESOURCE.SHIELD, DimReference.RESOURCE.SHIELD_NO_PATTERN);
	}
}