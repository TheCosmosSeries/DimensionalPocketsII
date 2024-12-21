package com.tcn.dimensionalpocketsii.core.item.armour.module;

import java.util.List;

import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemSmithingUpgrade extends CosmosItem implements IModuleItem {
	public ItemSmithingUpgrade(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);

        tooltip.add(CommonComponents.EMPTY);
		tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.smithing_template.applies_to"));
		tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.smithing_template.dimensional.applies_to"));

		tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.smithing_template.ingredients"));
		tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.smithing_template.dimensional.ingredients"));
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public BaseElytraModule getModule() {
		return BaseElytraModule.VISOR;
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
