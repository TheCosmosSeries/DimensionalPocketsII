package com.tcn.dimensionalpocketsii.core.item;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ModuleEnergyDisplay extends Item {

	public ModuleEnergyDisplay(Item.Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean canAttackBlock(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		return false;
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if (!CosmosCompHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(CosmosCompHelper.getTooltipInfo("dimensionalpocketsii.info.module.energy_display"));
			
			if (CosmosCompHelper.displayShiftForDetail) {
				tooltip.add(CosmosCompHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(CosmosCompHelper.getTooltipOne("dimensionalpocketsii.info.module.energy_display_shift_one"));
			tooltip.add(CosmosCompHelper.getTooltipTwo("dimensionalpocketsii.info.module.energy_display_shift_two"));
			tooltip.add(CosmosCompHelper.shiftForLessDetails());
		}
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}
}