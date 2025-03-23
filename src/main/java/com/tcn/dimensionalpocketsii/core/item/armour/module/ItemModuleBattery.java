package com.tcn.dimensionalpocketsii.core.item.armour.module;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemModuleBattery extends CosmosItem implements IModuleItem {

	public ItemModuleBattery(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.armour_module.battery"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.item.info.armour_module.battery_one"));
			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.item.info.armour_module.battery_two"));
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public BaseElytraModule getModule() {
		return BaseElytraModule.BATTERY;
	}

	@Override
	public boolean doesInformationCarry() {
		return false;
	}
	
	@Override
	public boolean transferInformation(ItemStack stackIn, ItemStack otherStack, boolean simulate) {
		return false;
	}
}