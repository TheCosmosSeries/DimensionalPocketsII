package com.tcn.dimensionalpocketsii.client.colour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleConnector;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColourBlockWall implements BlockColor {

	@Override
	public int getColor(BlockState stateIn, BlockAndTintGetter displayReaderIn, BlockPos posIn, int tintIndexIn) {
		CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(posIn);
		
		if (chunkPos != null) {
			BlockPos scaledPos = CosmosChunkPos.scaleFromChunkPos(chunkPos);
			BlockEntity entity = displayReaderIn.getBlockEntity(scaledPos);
			
			if (entity instanceof BlockEntityModuleConnector blockEntity) {				
				if (blockEntity.getPocket() != null) {
					Pocket pocket = blockEntity.getPocket();
					int colour = pocket.getDisplayColour();
					
					return colour;
				}
			}
		}
		
		return ComponentColour.POCKET_PURPLE.dec();
	}
}