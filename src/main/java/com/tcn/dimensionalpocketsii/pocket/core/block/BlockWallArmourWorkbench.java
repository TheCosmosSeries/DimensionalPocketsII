package com.tcn.dimensionalpocketsii.pocket.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.dimensionalpocketsii.core.management.ConfigurationManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleArmourWorkbench;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockWallArmourWorkbench extends BlockWallModule implements IBlankCreativeTab, EntityBlock {

	private static final Component CONTAINER_NAME = new TranslatableComponent("dimensionalpocketsii.gui.armour_workbench");
	
	public BlockWallArmourWorkbench(Block.Properties prop) {
		super(prop);	
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityModuleArmourWorkbench(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModBusManager.BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityModuleArmourWorkbench> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityModuleArmourWorkbench::tick);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleArmourWorkbench) {
			return ((BlockEntityModuleArmourWorkbench) tileEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleArmourWorkbench) {
			((BlockEntityModuleArmourWorkbench) tileEntity).attack(state, world, pos, player);
		}
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityModuleArmourWorkbench) {
			BlockEntityModuleArmourWorkbench tileFurnace = (BlockEntityModuleArmourWorkbench) tileEntity;
			return new SimpleMenuProvider((id, inventory, player) -> { return new ContainerModuleArmourWorkbench(id, inventory, tileFurnace, ContainerLevelAccess.create(worldIn, pos), pos); }, CONTAINER_NAME);
		}
		
		return null;
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
       return new ItemStack(ModBusManager.MODULE_ARMOUR_WORKBENCH);
    }
}