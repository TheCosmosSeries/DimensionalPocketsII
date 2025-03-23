package com.tcn.dimensionalpocketsii.client.colour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityPocket;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColourBlockPocket implements BlockColor {

	@Override
	public int getColor(BlockState stateIn, BlockAndTintGetter displayReaderIn, BlockPos posIn, int colourIn) {
		Block block = stateIn.getBlock();
		BlockEntity tile = displayReaderIn.getBlockEntity(posIn);
		
		if (block != null) {
			if (tile != null) {
				if (tile instanceof BlockEntityPocket) {
					BlockEntityPocket pocket_tile = (BlockEntityPocket) tile;
					
					Pocket pocket = pocket_tile.getPocket();
					
					if (pocket != null) {
						return pocket.getDisplayColour();
					}
				}
			}
		}
		
		return ComponentColour.POCKET_PURPLE.dec();
	}
}