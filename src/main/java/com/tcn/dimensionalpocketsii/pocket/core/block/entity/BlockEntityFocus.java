package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumGeneralEnableState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.ModConfigManager;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerFocus;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockFocus;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockEntityFocus extends BlockEntity implements IBlockInteract, MenuProvider, Nameable, IBlockEntityUIMode {
	
	private Pocket pocket;
	private EnumGeneralEnableState jumpEnabled = EnumGeneralEnableState.ENABLED;
	private EnumGeneralEnableState shiftEnabled = EnumGeneralEnableState.ENABLED;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityFocus(BlockPos posIn, BlockState stateIn) {
		super(ModRegistrationManager.BLOCK_ENTITY_TYPE_FOCUS.get(), posIn, stateIn);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return StorageManager.getPocketFromChunkPosition(this.getLevel(), CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockFocus block = (BlockFocus) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state,  block.updateState(state, this.getBlockPos(), level), 3);
			
			if (update) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), block.updateState(state, this.getBlockPos(), level));
					
					if (this.getPocket() != null) {
						this.getPocket().updateBaseConnectors(this.getLevel());
					}
				}
			} else {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), block.updateState(state, this.getBlockPos(), level));
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		if (this.getPocket() != null) {
			this.getPocket().writeToNBT(compound, provider);
		}

		compound.putInt("jumpEnabled", this.jumpEnabled.getIndex());
		compound.putInt("shiftEnabled", this.shiftEnabled.getIndex());
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}

	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);
		
		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound, provider);
		}
		
		this.jumpEnabled = EnumGeneralEnableState.getStateFromIndex(compound.getInt("jumpEnabled"));
		this.shiftEnabled = EnumGeneralEnableState.getStateFromIndex(compound.getInt("shiftEnabled"));
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
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
	
	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player playerIn) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		this.setChanged();

		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		if (PocketUtil.isDimensionEqual(levelIn, ModDimensionManager.POCKET_WORLD)) {
			Pocket pocket = this.getPocket();
			
			if (pocket != null) {
				if (!playerIn.isShiftKeyDown()) {
					if (levelIn.isClientSide) {
						return ItemInteractionResult.SUCCESS;
					} else {
						if (playerIn instanceof ServerPlayer serverPlayer) {
							if (this.canPlayerAccess(playerIn)) {
								serverPlayer.openMenu(this, (packetBuffer)->{ packetBuffer.writeBlockPos(pos); });
								return ItemInteractionResult.SUCCESS;
							}
						}
					}
				} else {
					if(!levelIn.isClientSide) {
						CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
						Pocket pocketIn = StorageManager.getPocketFromChunkPosition(levelIn, chunkPos);
						
						if (CosmosUtil.holdingWrench(playerIn)) {
							if (pocketIn.checkIfOwner(playerIn)) {
								if (this.getBlockPos().getY() == 1 || this.getBlockPos().getY() == this.getPocket().getInternalHeight() || this.getBlockPos().getY() == ModConfigManager.getInstance().getInternalHeight()) {
									ItemStack stack = new ItemStack(ModRegistrationManager.MODULE_FOCUS.get());
									
									levelIn.setBlockAndUpdate(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState());
									levelIn.removeBlockEntity(pos);
									
									CosmosUtil.addStack(levelIn, playerIn, stack);
									
									return ItemInteractionResult.SUCCESS;
								} else {
									ItemStack stack = new ItemStack(ModRegistrationManager.BLOCK_FOCUS.get());
									levelIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
									CosmosUtil.addStack(levelIn, playerIn, stack);
									
									return ItemInteractionResult.SUCCESS;
								}
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
								return ItemInteractionResult.FAIL;
							}
						}
						
						else if (pocketIn.exists()) {
							if (CosmosUtil.handEmpty(playerIn)) {
								if (this.getShiftEnabled()) {
									pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
									return ItemInteractionResult.SUCCESS;
								}
							}
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
							return ItemInteractionResult.FAIL;
						}
					}
				}
			} 
		}
		
		return ItemInteractionResult.FAIL;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}
	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.focus");
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.focus");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerFocus(idIn, playerInventoryIn, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	public EnumGeneralEnableState getJump() {
		return this.jumpEnabled;
	}
	
	public boolean getJumpEnabled() {
		return this.jumpEnabled.getValue();
	}
	
	public void setJumpEnabled(EnumGeneralEnableState state) {
		this.jumpEnabled = state;
		this.sendUpdates(true);
	}

	public void setJumpEnabled(Boolean state) {
		this.jumpEnabled = EnumGeneralEnableState.getStateFromValue(state);
		this.sendUpdates(true);
	}

	public EnumGeneralEnableState getShift() {
		return this.shiftEnabled;
	}
	
	public boolean getShiftEnabled() {
		return this.shiftEnabled.getValue();
	}

	public void setShiftEnabled(EnumGeneralEnableState state) {
		this.shiftEnabled = state;
		this.sendUpdates(true);
	}

	public void setShiftEnabled(Boolean state) {
		this.shiftEnabled = EnumGeneralEnableState.getStateFromValue(state);
		this.sendUpdates(true);
	}
	
	@Override
	public EnumUIMode getUIMode() {
		return this.uiMode;
	}

	@Override
	public void setUIMode(EnumUIMode modeIn) {
		this.uiMode = modeIn;
	}

	@Override
	public void cycleUIMode() {
		this.uiMode = EnumUIMode.getNextStateFromState(this.uiMode);
	}

	@Override
	public EnumUIHelp getUIHelp() {
		return this.uiHelp;
	}

	@Override
	public void setUIHelp(EnumUIHelp modeIn) {
		this.uiHelp = modeIn;
	}

	@Override
	public void cycleUIHelp() {
		this.uiHelp = EnumUIHelp.getNextStateFromState(this.uiHelp);
	}

	@Override
	public EnumUILock getUILock() {
		return this.uiLock;
	}

	@Override
	public void setUILock(EnumUILock modeIn) {
		this.uiLock = modeIn;
	}

	@Override
	public void cycleUILock() {
		this.uiLock = EnumUILock.getNextStateFromState(this.uiLock);
	}

	@Override
	public void setOwner(Player playerIn) { }

	@Override
	public boolean canPlayerAccess(Player playerIn) {
		if (this.getUILock().equals(EnumUILock.PUBLIC)) {
			return true;
		} else {
			if (this.getPocket().checkIfOwner(playerIn)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean checkIfOwner(Player playerIn) {
		if (this.getPocket().checkIfOwner(playerIn)) {
			return true;
		}
		return false;
	}

}