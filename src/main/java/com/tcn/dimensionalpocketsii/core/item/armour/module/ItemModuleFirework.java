package com.tcn.dimensionalpocketsii.core.item.armour.module;

import java.util.List;

import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemModuleFirework extends CosmosItem implements IModuleItem {

	public ItemModuleFirework(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.armour_module.firework"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.item.info.armour_module.firework_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.item.info.armour_module.firework_two")
					.append(ComponentHelper.style(ComponentColour.YELLOW, PocketsRegistrationManager.SUIT_FIREWORK.getKey().getName()))
					.append(ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.item.info.elytraplate_key"))
				);
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public BaseElytraModule getModule() {
		return BaseElytraModule.FIREWORK;
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
