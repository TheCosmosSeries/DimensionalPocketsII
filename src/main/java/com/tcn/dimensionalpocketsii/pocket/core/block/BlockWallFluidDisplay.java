package com.tcn.dimensionalpocketsii.pocket.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.dimensionalpocketsii.core.management.PocketsConfigManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleFluidDisplay;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockWallFluidDisplay extends BlockWallModule implements IBlankCreativeTab, EntityBlock {

	public BlockWallFluidDisplay(Block.Properties prop) {
		super(prop);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityModuleFluidDisplay(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, PocketsRegistrationManager.BLOCK_ENTITY_TYPE_FLUID_DISPLAY.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityModuleFluidDisplay> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityModuleFluidDisplay::tick);
	}
	
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos posIn, RandomSource randIn) {
		worldIn.setBlockAndUpdate(posIn, stateIn);
		worldIn.blockUpdated(posIn, this);
		worldIn.sendBlockUpdated(posIn, stateIn, stateIn.updateShape(Direction.UP, stateIn, worldIn, posIn, posIn.offset(Direction.UP.getNormal())), 3);
	}
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleFluidDisplay) {
			return ((BlockEntityModuleFluidDisplay) tileEntity).useItemOn(stackIn, state, worldIn, pos, playerIn, handIn, hit);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	
	@Override
	public void onPlace(BlockState stateIn, Level worldIn, BlockPos posIn, BlockState oldState, boolean isMoving) {
		worldIn.blockUpdated(posIn, this);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (PocketsConfigManager.getInstance().getCanDestroyWalls()) {
			return this.defaultBlockState();
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult result, LevelReader reader, BlockPos posIn, Player playerIn) {
       return new ItemStack(PocketsRegistrationManager.MODULE_FLUID_DISPLAY.get());
    }
}