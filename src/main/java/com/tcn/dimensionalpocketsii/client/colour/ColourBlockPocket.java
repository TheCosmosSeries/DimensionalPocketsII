package com.tcn.dimensionalpocketsii.client.colour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityPocketEnhanced;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColourBlockPocket implements BlockColor {

	@Override
	public int getColor(BlockState stateIn, BlockAndTintGetter displayReaderIn, BlockPos posIn, int colourIn) {
		Block block = stateIn.getBlock();
		BlockEntity tile = displayReaderIn.getBlockEntity(posIn);
		
		if (block != null) {
			if (tile != null) {
				if (tile instanceof BlockEntityPocket pocketTile) {					
					Pocket pocket = pocketTile.getPocket();
					
					if (pocket != null) {
						return pocket.getDisplayColour();
					}
				}
				
				if (tile instanceof BlockEntityPocketEnhanced pocketTile) {					
					Pocket pocket = pocketTile.getPocket();
					
					if (pocket != null) {
						return pocket.getDisplayColour();
					}
				}
			}
		}
		
		return ComponentColour.POCKET_PURPLE.dec();
	}
}