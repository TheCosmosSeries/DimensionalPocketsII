package com.tcn.dimensionalpocketsii.pocket.core.block;

import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityFocus;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockFocus extends CosmosBlock implements EntityBlock {

	public static final BooleanProperty JUMP_ENABLED = BooleanProperty.create("jump_enabled");
	public static final BooleanProperty SHIFT_ENABLED = BooleanProperty.create("shift_enabled");
	
	public BlockFocus(Block.Properties prop) {
		super(prop);
		
		this.registerDefaultState(this.defaultBlockState()
			.setValue(JUMP_ENABLED, true).setValue(SHIFT_ENABLED, true)
		);
	}
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityFocus) {
			return ((BlockEntityFocus) tileEntity).useItemOn(stackIn, state, worldIn, pos, playerIn, handIn, hit);
		}
		return ItemInteractionResult.FAIL;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityFocus(posIn, stateIn);
	}

	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
		return false;
    }

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(JUMP_ENABLED, SHIFT_ENABLED);
	}

	public BlockState updateState(BlockState state, BlockPos posIn, Level worldIn) {
		if (!worldIn.isClientSide) {
			BlockEntity entity = worldIn.getBlockEntity(posIn);
			
			if (entity instanceof BlockEntityFocus) {
				BlockEntityFocus blockEntity = (BlockEntityFocus) entity;
				
				return this.defaultBlockState().setValue(JUMP_ENABLED, blockEntity.getJumpEnabled()).setValue(SHIFT_ENABLED, blockEntity.getShiftEnabled());
			} else {
				return this.defaultBlockState();
			}
		} else {
			return this.defaultBlockState();
		}
	}
	

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		BlockEntity entity = worldIn.getBlockEntity(currentPos);
		
		if (entity instanceof BlockEntityFocus) {
			BlockEntityFocus blockEntity = (BlockEntityFocus) entity;
			
			return this.defaultBlockState().setValue(JUMP_ENABLED, blockEntity.getJumpEnabled()).setValue(SHIFT_ENABLED, blockEntity.getShiftEnabled());
		} else {
			return this.defaultBlockState();
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (PocketUtil.isDimensionEqual(context.getLevel(), ModDimensionManager.POCKET_WORLD)) {
			return this.defaultBlockState();
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		return false;
    }
}