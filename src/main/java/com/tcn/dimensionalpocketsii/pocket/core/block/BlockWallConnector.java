package com.tcn.dimensionalpocketsii.pocket.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.dimensionalpocketsii.core.management.ModConfigManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleConnector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockWallConnector extends BlockWallModule implements IBlankCreativeTab, EntityBlock {
	
	public static final IntegerProperty MODE = IntegerProperty.create("mode", 0, 3);
	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 3);
	
	public BlockWallConnector(Block.Properties prop) {
		super(prop);
		
		this.registerDefaultState(this.defaultBlockState().setValue(MODE, 0).setValue(TYPE, 0));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityModuleConnector(posIn, stateIn);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModRegistrationManager.BLOCK_ENTITY_TYPE_CONNECTOR.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityModuleConnector> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityModuleConnector::tick);
	}
	
	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleConnector) {
			((BlockEntityModuleConnector) tileEntity).attack(state, world, pos, player);
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleConnector) {
			return ((BlockEntityModuleConnector) tileEntity).useItemOn(stackIn, state, worldIn, pos, playerIn, handIn, hit);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(MODE, TYPE);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {	
		BlockEntity tile_in = worldIn.getBlockEntity(currentPos);
		
		if (tile_in instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector tile = (BlockEntityModuleConnector) worldIn.getBlockEntity(currentPos);

			return stateIn.setValue(MODE, tile.getSide(Direction.UP).getIndex()).setValue(TYPE, tile.getConnectionType().getIndex());
		} else {
			return this.defaultBlockState();
		}
	}

	public BlockState updateState(BlockState state, BlockPos posIn, Level worldIn) {
		if (!worldIn.isClientSide) {
			BlockEntity entity = worldIn.getBlockEntity(posIn);
			
			if (entity instanceof BlockEntityModuleConnector) {
				BlockEntityModuleConnector connector = (BlockEntityModuleConnector) entity;
				
				return this.defaultBlockState().setValue(MODE, connector.getSide(Direction.UP).getIndex()).setValue(TYPE, connector.getConnectionType().getIndex());
			} else {
				return this.defaultBlockState();
			}
		} else {
			return this.defaultBlockState();
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (ModConfigManager.getInstance().getCanDestroyWalls()) {
			return this.defaultBlockState();
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult result, LevelReader reader, BlockPos posIn, Player playerIn) {
       return new ItemStack(ModRegistrationManager.MODULE_CONNECTOR.get());
    }
}
