package com.tcn.dimensionalpocketsii.pocket.core.block;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.ModConfigManager;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockWallEnergyDisplay extends BlockWallModule implements IBlankCreativeTab {
	public static final IntegerProperty ENERGY = IntegerProperty.create("energy", 0, 24);
	
	public BlockWallEnergyDisplay(Block.Properties prop) {
		super(prop);
		
		this.registerDefaultState(this.defaultBlockState().setValue(ENERGY, 0));
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos posIn, RandomSource randIn) {
		worldIn.setBlockAndUpdate(posIn, this.updateState(stateIn, posIn, worldIn));
		worldIn.blockUpdated(posIn, this);
		worldIn.sendBlockUpdated(posIn, stateIn, this.updateState(stateIn, posIn, worldIn), 3);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
		Pocket pocketIn = StorageManager.getPocketFromChunkPosition(worldIn, chunkPos);
		
		if (playerIn.isShiftKeyDown()) {
			if(pocketIn.exists()) {
				if (CosmosUtil.holdingWrench(playerIn)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						if(!worldIn.isClientSide) {
							worldIn.setBlockAndUpdate(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState());
							
							CosmosUtil.addItem(worldIn, playerIn, ModRegistrationManager.MODULE_ENERGY_DISPLAY.get(), 1);
							pocketIn.removeUpdateable(pos);
						}
						
						return ItemInteractionResult.SUCCESS;
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return ItemInteractionResult.FAIL;
					}
				} 
				
				else if (CosmosUtil.handEmpty(playerIn)) {
					pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
					return ItemInteractionResult.SUCCESS;
				}
			}
		}
		return ItemInteractionResult.FAIL;
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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ENERGY);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {	
		CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(currentPos);
		
		if (chunkPos != null) {
			BlockPos scaled = CosmosChunkPos.scaleFromChunkPos(chunkPos);
			
			BlockEntity entity = worldIn.getBlockEntity(scaled);
			
			if (entity != null) {
				if (entity instanceof BlockEntityModuleConnector) {
					BlockEntityModuleConnector connector = (BlockEntityModuleConnector) entity;
					
					if (connector.getPocket() != null) {
						Pocket pocket = connector.getPocket();
						
						int scaled_energy = pocket.getEnergyStoredScaled(24);
						
						return this.defaultBlockState().setValue(ENERGY, scaled_energy);
					}
				}
			}
		}
		return this.defaultBlockState();
	}

	public BlockState updateState(BlockState state, BlockPos posIn, Level worldIn) {
		CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(posIn);
		
		if (PocketUtil.isDimensionEqual(worldIn, ModDimensionManager.POCKET_WORLD)) {
			if (chunkPos != null) {
				BlockPos scaled = CosmosChunkPos.scaleFromChunkPos(chunkPos);
				
				BlockEntity entity = worldIn.getBlockEntity(scaled);
				
				if (entity != null) {
					if (entity instanceof BlockEntityModuleConnector) {
						BlockEntityModuleConnector connector = (BlockEntityModuleConnector) entity;
						
						if (connector.getPocket() != null) {
							Pocket pocket = connector.getPocket();
							
							int scaled_energy = pocket.getEnergyStoredScaled(24);
							
							return this.defaultBlockState().setValue(ENERGY, scaled_energy);
						}
					}
				}
			}
		}
		return this.defaultBlockState();
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
       return new ItemStack(ModRegistrationManager.MODULE_ENERGY_DISPLAY.get());
    }
}