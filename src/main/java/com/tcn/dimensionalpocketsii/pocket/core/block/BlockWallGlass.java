package com.tcn.dimensionalpocketsii.pocket.core.block;

import javax.annotation.Nonnull;

import com.tcn.cosmoslibrary.common.block.CosmosBlockConnectedUnbreakable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.ModConfigManager;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockWallGlass extends CosmosBlockConnectedUnbreakable {
	
	public BlockWallGlass(Block.Properties prop) {
		super(prop.randomTicks());
	}
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		if (PocketUtil.isDimensionEqual(levelIn, ModDimensionManager.POCKET_WORLD)) {
			Pocket pocket = StorageManager.getPocketFromChunkPosition(levelIn, CosmosChunkPos.scaleToChunkPos(pos));
			
			if (pocket.exists()) {
				if (!playerIn.isShiftKeyDown()) {
					return ItemInteractionResult.FAIL;
				} else {
					if (CosmosUtil.handEmpty(playerIn)) {
						pocket.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
						return ItemInteractionResult.SUCCESS;
					}
					if(!levelIn.isClientSide) {
						CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
						Pocket pocketIn = StorageManager.getPocketFromChunkPosition(levelIn, chunkPos);
						
						if(pocketIn.exists()) {
							if (CosmosUtil.holdingWrench(playerIn)) {
								if (pocketIn.checkIfOwner(playerIn)) {
									ItemStack stack = new ItemStack(ModRegistrationManager.MODULE_GLASS.get());
									
									levelIn.setBlockAndUpdate(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState());
									
									CosmosUtil.addStack(levelIn, playerIn, stack);
									
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
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
							return ItemInteractionResult.FAIL;
						}
					}
				}
			} else {
				CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
				return ItemInteractionResult.FAIL;
			}
		}
	
		return ItemInteractionResult.FAIL;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		//DebugPacketSender.func_218806_a(worldIn, pos);
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
		if (ModConfigManager.getInstance().getCanDestroyWalls()) {
			return new ItemStack(this);
		} else {
			return ItemStack.EMPTY;
		}
    }

	@Override
	public VoxelShape getVisualShape(BlockState blockStateIn, BlockGetter levelIn, BlockPos posIn, CollisionContext collisionContext) {
		return Shapes.empty();
	}

	@Override
	public float getShadeBrightness(BlockState blockStateIn, BlockGetter levelIn, BlockPos posIn) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockStateIn, BlockGetter levelIn, BlockPos posIn) {
		return true;
	}

	@Override
	protected boolean canConnect(@Nonnull BlockState orig, @Nonnull BlockState conn) {
		if (ModConfigManager.getInstance().getConnectedTexturesInsidePocket()) {
			if (conn.getBlock().equals(Blocks.AIR)) {
				return false;
			} else if (orig.getBlock().equals(conn.getBlock())) {
				return true;
			} else {
				return false;
			}
		}
		
		return false;
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
		return adjacentState.is(this) ? true : super.skipRendering(state, adjacentState, direction);
	}
}