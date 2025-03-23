package com.tcn.dimensionalpocketsii.pocket.core.block;

import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.dimensionalpocketsii.core.management.ConfigurationManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleUpgradeStation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockWallUpgradeStation extends BlockWallModule implements IBlankCreativeTab, EntityBlock {

	public BlockWallUpgradeStation(Block.Properties prop) {
		super(prop);	
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityModuleUpgradeStation(posIn, stateIn);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleUpgradeStation) {
			return ((BlockEntityModuleUpgradeStation) tileEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleUpgradeStation) {
			((BlockEntityModuleUpgradeStation) tileEntity).attack(state, world, pos, player);
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
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
       return new ItemStack(ModBusManager.MODULE_UPGRADE_STATION);
    }
}