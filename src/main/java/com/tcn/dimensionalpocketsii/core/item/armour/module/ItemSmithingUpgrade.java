package com.tcn.dimensionalpocketsii.core.item.armour.module;

import java.util.List;

import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemSmithingUpgrade extends CosmosItem implements IModuleItem {
	private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    
	private static final Component APPLIES_TO_TITLE = Component.translatable(
			Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.applies_to")))
			.withStyle(TITLE_FORMAT);
	private static final Component INGREDIENTS_TITLE = Component.translatable(
			Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.ingredients")))
			.withStyle(TITLE_FORMAT);
	private static final Component APPLIES_TO = Component
			.translatable(Util.makeDescriptionId("item",
					ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.applies_to")))
			.withStyle(DESCRIPTION_FORMAT);
	private static final Component INGREDIENTS = Component
			.translatable(Util.makeDescriptionId("item",
					ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.ingredients")))
			.withStyle(DESCRIPTION_FORMAT);

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
