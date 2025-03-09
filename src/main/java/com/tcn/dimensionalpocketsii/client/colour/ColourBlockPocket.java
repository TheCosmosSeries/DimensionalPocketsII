package com.tcn.dimensionalpocketsii.client.colour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.AbstractBlockEntityPocket;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColourBlockPocket implements BlockColor {

	@Override
	public int getColor(BlockState stateIn, BlockAndTintGetter displayReaderIn, BlockPos posIn, int tintIndex) {
		if (stateIn.getBlock() != null) {
			if (displayReaderIn.getBlockEntity(posIn) instanceof AbstractBlockEntityPocket blockEntity) {
				Pocket pocket = blockEntity.getPocket();
				
				if (pocket != null) {
					return pocket.getDisplayColour();
				}
			}
		}
		return ComponentColour.POCKET_PURPLE.dec();
	}
}