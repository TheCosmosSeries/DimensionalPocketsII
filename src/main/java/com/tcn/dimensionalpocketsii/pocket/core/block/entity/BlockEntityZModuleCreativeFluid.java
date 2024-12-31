package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class BlockEntityZModuleCreativeFluid extends CosmosBlockEntityUpdateable implements IBlockInteract {
	
	private Pocket pocket;
	private int update = 0;
	
	public BlockEntityZModuleCreativeFluid(BlockPos posIn, BlockState stateIn) {
		super(PocketsRegistrationManager.BLOCK_ENTITY_TYPE_CREATIVE_FLUID.get(), posIn, stateIn);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return StorageManager.getPocketFromChunkPosition(this.getLevel(), CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}

	public void sendUpdates(boolean update) {
		super.sendUpdates(update);
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		if (this.getPocket() != null) {
			this.getPocket().writeToNBT(compound, provider);
		}
	}
	
	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);
		
		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound, provider);
		}
	}
	
	/**
	 * Set the data once it has been received. [NBT > TE] (READ)
	 */
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
	}
	
	/**
	 * Retrieve the data to be stored. [TE > NBT] (WRITE)
	 */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag, provider);
		return tag;
	}
	
	/**
	 * Actually sends the data to the server. [NBT > SER]
	 */
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	/**
	 * Method is called once packet has been received by the client. [SER > CLT]
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
		super.onDataPacket(net, pkt, provider);
		CompoundTag tag_ = pkt.getTag();
		this.handleUpdateTag(tag_, provider);
		this.sendUpdates(true);
	}
	
	@Override
	public void onLoad() { }

	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityZModuleCreativeFluid entityIn) {
		Pocket pocket = entityIn.getPocket();
		
		if (pocket != null) {
			FluidTank tank = pocket.getFluidTank();
			if (!tank.isEmpty()) {
				pocket.fill(new FluidStack(tank.getFluid().getFluid(), tank.getCapacity() - tank.getFluidAmount()), FluidAction.EXECUTE);
			}
		}
		
		boolean flag = entityIn.update > 0;

		if (flag) {
			entityIn.update--;
		} else {
			entityIn.update = 100;

			entityIn.sendUpdates(true);
		}
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (playerIn.isShiftKeyDown()) {
			CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
			Pocket pocketIn = StorageManager.getPocketFromChunkPosition(levelIn, chunkPos);
			
			if(pocketIn.exists()) {
				if (CosmosUtil.holdingWrench(playerIn)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						ItemStack stack = new ItemStack(PocketsRegistrationManager.MODULE_CREATIVE_FLUID.get());
						
						levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL.get().defaultBlockState());
						levelIn.removeBlockEntity(pos);
						
						CosmosUtil.addStack(levelIn, playerIn, stack);
						
						return ItemInteractionResult.SUCCESS;
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return ItemInteractionResult.FAIL;
					}
				} else if (CosmosUtil.handEmpty(playerIn)) {
					pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
					return ItemInteractionResult.SUCCESS;
				}
			} else {
				CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
				return ItemInteractionResult.FAIL;
			}
		}
		return ItemInteractionResult.FAIL;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}
}