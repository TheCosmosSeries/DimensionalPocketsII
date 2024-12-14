package com.tcn.dimensionalpocketsii.core.item.device;

import java.util.List;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class DimensionalShifterEnhanced extends AbstractDimensionalShifterItem {

	public DimensionalShifterEnhanced(Item.Properties properties, CosmosEnergyItem.Properties energyProperties) {
		super(properties, energyProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.shifter_info"));

			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {

			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.shifter_shift_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.info.shifter_shift_two"));
			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.info.shifter_shift_three"));
			tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.info.shifter_enhanced"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
}