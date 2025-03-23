package com.tcn.dimensionalpocketsii.pocket.core.block;

import javax.annotation.Nonnull;

import com.tcn.cosmoslibrary.common.block.CosmosBlockConnectedUnbreakable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.ConfigurationManager;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockWallEdge extends CosmosBlockConnectedUnbreakable {

	public BlockWallEdge(Block.Properties prop) {
		super(prop);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (PocketUtil.isDimensionEqual(worldIn, DimensionManager.POCKET_WORLD)) {
			Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(CosmosChunkPos.scaleToChunkPos(pos));
			
			if (pocket.exists()) {
				if (playerIn.isShiftKeyDown()) {
					if (CosmosUtil.handEmpty(playerIn)) {
						pocket.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
						return InteractionResult.SUCCESS;
					} 
				}
			} else {
				CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
				return InteractionResult.FAIL;
			}
		}
		return InteractionResult.FAIL;
	}
	
	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
		return false;
    }
	
	@Override
	protected boolean canConnect(@Nonnull BlockState orig, @Nonnull BlockState conn) {
		if (ConfigurationManager.getInstance().getConnectedTexturesInsidePocket()) {
			if (conn.getBlock().equals(Blocks.AIR)) {
				return false;
			} else if (orig.getBlock().equals(conn.getBlock())) {
				return true;
			} else if (conn.getBlock().equals(ModBusManager.BLOCK_WALL)) {
				return true;
			} else if (conn.getBlock() instanceof BlockWallModule) {
				return true;
			} else if (conn.getBlock() instanceof BlockWallDoor) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		DebugPackets.sendNeighborsUpdatePacket(worldIn, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (ConfigurationManager.getInstance().getCanDestroyWalls()) {
			return this.defaultBlockState();
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter blockReader, BlockPos posIn, BlockState stateIn) {
		if (ConfigurationManager.getInstance().getCanDestroyWalls()) {
			return new ItemStack(this);
		} else {
			return ItemStack.EMPTY;
		}
	}
}