package com.tcn.dimensionalpocketsii.pocket.core.block;

import com.tcn.cosmoslibrary.common.block.CosmosBlockDoor;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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

public class BlockWallDoor extends CosmosBlockDoor implements IBlankCreativeTab {

	public BlockWallDoor(Properties propertiesIn) {
		super(propertiesIn);
	}

	@Override
	public InteractionResult use(BlockState stateIn, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult resultIn) {
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return InteractionResult.FAIL;
		}
		
		if (playerIn.isShiftKeyDown()) {
			if (PocketUtil.isDimensionEqual(levelIn, DimensionManager.POCKET_WORLD)) {
				Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(CosmosChunkPos.scaleToChunkPos(posIn));
				
				if (pocket.exists()) {
					if (CosmosUtil.handEmpty(playerIn)) {
						pocket.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
						return InteractionResult.SUCCESS;
					}
				} else {
					CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
					return InteractionResult.FAIL;
				}
			}
		} else {
			if (playerIn.getDisplayName().getString().equals("TheCosmicNebula_")) {
				return super.use(stateIn, levelIn, posIn, playerIn, handIn, resultIn);
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	public void neighborChanged(BlockState stateIn, Level levelIn, BlockPos posIn, Block blockIn, BlockPos otherPosIn, boolean valueIn) { }

	@Override
	public boolean canSurvive(BlockState stateIn, LevelReader levelIn, BlockPos posIn) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        return false;
    }
	
	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
		return false;
	}
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (ConfigurationManager.getInstance().getCanDestroyWalls()) {
			return super.getStateForPlacement(context);
		}
		
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter blockReader, BlockPos posIn, BlockState stateIn) {
		if (ConfigurationManager.getInstance().getCanDestroyWalls()) {
			return new ItemStack(ModBusManager.BLOCK_WALL);
		} else {
			return ItemStack.EMPTY;
		}
    }
}