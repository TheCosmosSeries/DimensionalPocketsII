package com.tcn.dimensionalpocketsii.pocket.core.block;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ItemBlockFocus extends BlockItem {

	public ItemBlockFocus(Block block, Item.Properties prop) {
		super(block, prop);
	}
	
	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable Level worldIn, List<Component> toolTipIn, TooltipFlag flagIn) {
		super.appendHoverText(stackIn, worldIn, toolTipIn, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			toolTipIn.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.block.focus"));
			
			if (ComponentHelper.displayShiftForDetail) {
				toolTipIn.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			toolTipIn.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.block.focus_shift"));
			toolTipIn.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.info.block.focus_shift_one"));
			toolTipIn.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.info.block.focus_shift_two"));
			toolTipIn.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.info.block.focus_shift_three"));
			toolTipIn.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.info.block.focus_shift_limit"));
			toolTipIn.add(ComponentHelper.shiftForLessDetails());
		}
	}

	@Override
	public boolean isDamageable(ItemStack stackIn) {
		return false;
	}
}