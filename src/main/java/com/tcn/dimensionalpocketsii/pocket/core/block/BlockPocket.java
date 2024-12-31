package com.tcn.dimensionalpocketsii.pocket.core.block;

import javax.annotation.Nullable;

import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.AbstractBlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityPocket;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPocket extends AbstractBlockPocket {

	public BlockPocket(Properties prop) {
		super(prop, true);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityPocket(posIn, stateIn);
	}
	
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, PocketsRegistrationManager.BLOCK_ENTITY_TYPE_POCKET.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends AbstractBlockEntityPocket> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, AbstractBlockEntityPocket::tick);
	}
}