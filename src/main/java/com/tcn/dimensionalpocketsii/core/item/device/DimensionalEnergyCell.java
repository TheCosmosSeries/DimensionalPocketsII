package com.tcn.dimensionalpocketsii.core.item.device;

import java.util.List;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyStorageItem;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class DimensionalEnergyCell extends CosmosEnergyStorageItem {

	public DimensionalEnergyCell(Item.Properties properties, CosmosEnergyItem.Properties energyProperties) {
		super(properties, energyProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.energy_cell_info"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			} 
			
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.energy_cell_shift_one"));
			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.info.energy_cell_shift_two"));
			tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.info.energy_cell_shift_three"));
			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.info.energy_cell_limitation"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
}