package com.tcn.dimensionalpocketsii.pocket.core.item.module;

import java.util.List;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class ModuleZCreativeFluid extends Item {

	public ModuleZCreativeFluid(Item.Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
		return false;
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stackIn, LevelReader world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stackIn, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stackIn, context, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.module.creative_fluid"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.module.creative_fluid_shift_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.info.module.creative_fluid_shift_two"));
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
	}
	
	@Override
	public boolean isDamageable(ItemStack stackIn) {
		return false;
	}
}