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

public class ItemModuleEnderChest extends CosmosItem implements IModuleItem {

	public ItemModuleEnderChest(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.armour_module.ender_chest"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.item.info.armour_module.ender_chest_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.item.info.armour_module.ender_chest_two")
					.append(ComponentHelper.style(ComponentColour.YELLOW, PocketsRegistrationManager.SUIT_SCREEN_ENDER_CHEST.getKey().getName()))
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
		return BaseElytraModule.ENDER_CHEST;
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
