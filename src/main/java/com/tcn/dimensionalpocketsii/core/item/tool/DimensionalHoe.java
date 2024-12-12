package com.tcn.dimensionalpocketsii.core.item.tool;

import java.util.List;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyHoeItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

public class DimensionalHoe extends CosmosEnergyHoeItem {
	
	public boolean enhanced;
	
	public DimensionalHoe(Tier itemTier, boolean enhanced, Properties builderIn, CosmosEnergyItem.Properties energyProperties) {
		super(itemTier, builderIn, energyProperties);
		
		this.enhanced = enhanced;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			if (this.enhanced) {
				tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.hoe_enhanced"));
			} else {
				tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.hoe"));
			}
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			} 
			
		} else {
			
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.item.info.tool_charge"));
			
			if (this.enhanced) {
				tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.item.info.tool_usage_enhanced"));
				tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.item.info.tool_energy_enhanced"));
			} else {
				tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.item.info.tool_usage"));
				tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.item.info.tool_energy"));
			}
			
			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.item.info.tool_limitation"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
}
