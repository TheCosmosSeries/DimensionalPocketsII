package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUILockable;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockEntityModuleUpgradeStation extends BlockEntity implements IBlockInteract, Container, MenuProvider, IBEUIMode, IBEUILockable {
	public NonNullList<ItemStack> inventoryItems = NonNullList.withSize(9, ItemStack.EMPTY);
	
	private Pocket pocket;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;
	
	public BlockEntityModuleUpgradeStation(BlockPos posIn, BlockState stateIn) {
		super(PocketsRegistrationManager.BLOCK_ENTITY_TYPE_UPGRADE_STATION.get(), posIn, stateIn);
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
			BlockWallUpgradeStation block = (BlockWallUpgradeStation) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (update) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), block.defaultBlockState());
					
					if (this.getPocket() != null) {
						this.getPocket().updateBaseConnectors(this.getLevel());
					}
				}
			} else {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), block.defaultBlockState());
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		if (this.getPocket().exists()) {
			this.getPocket().writeToNBT(compound, provider);
		}
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}

	public void saveToItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		
		stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
	}
	
	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);

		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound, provider);
		}
		
		this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}

	public void loadFromItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
			
			this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
			this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
			this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
		}
	}
	
	/**
	 * Set the data once it has been received. [NBT > TE] (READ)
	 */
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
		this.sendUpdates(true);
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
	}
	
	@Override
	public void onLoad() { }
	
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		levelIn.sendBlockUpdated(pos, state, state, 3);
		this.setChanged();
		this.sendUpdates(true);
		
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		if (!playerIn.isShiftKeyDown()) {
			if (levelIn.isClientSide) {
				return ItemInteractionResult.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayer serverPlayer) {
					if (this.canPlayerAccess(playerIn)) {
						serverPlayer.openMenu(this, (packetBuffer) -> { packetBuffer.writeBlockPos(pos); });
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return ItemInteractionResult.FAIL;
					}
				}
				
				return ItemInteractionResult.SUCCESS;
			}
		} else {
			if(!levelIn.isClientSide) {
				CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
				Pocket pocketIn = StorageManager.getPocketFromChunkPosition(levelIn, chunkPos);
				
				if(pocketIn.exists()) {
					if (CosmosUtil.holdingWrench(playerIn)) {
						if (pocketIn.checkIfOwner(playerIn)) {
							ItemStack stack = new ItemStack(PocketsRegistrationManager.MODULE_UPGRADE_STATION.get());
							this.saveToItemStack(stack, levelIn.registryAccess());
							
							levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL.get().defaultBlockState());
							levelIn.removeBlockEntity(pos);
							
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
		
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}
	@Override
	public int getContainerSize() {
		return this.inventoryItems.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack inventoryItemstack : this.inventoryItems) {
			if (!inventoryItemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int indexIn) {
		return this.inventoryItems.get(indexIn);
	}

	@Override
	public ItemStack removeItem(int indexIn, int countIn) {
		this.setChanged();
		return ContainerHelper.removeItem(this.inventoryItems, indexIn, countIn);
	}

	@Override
	public ItemStack removeItemNoUpdate(int indexIn) {
		return ContainerHelper.takeItem(this.inventoryItems, indexIn);
	}

	@Override
	public void setItem(int indexIn, ItemStack stackIn) {
		this.inventoryItems.set(indexIn, stackIn);
		
		if (stackIn.getCount() > this.getMaxStackSize()) {
			stackIn.setCount(this.getMaxStackSize());
		}
		
		this.setChanged();
	}

	@Override
	public boolean stillValid(Player playerIn) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return playerIn.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public boolean canPlaceItem(int indexIn, ItemStack stackIn) {
		return true;
	}

	@Override
	public void clearContent() {
		this.inventoryItems.clear();
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

	@Override
	public AbstractContainerMenu createMenu(int indexIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerModuleUpgradeStation(indexIn, playerInventoryIn, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.upgrade_station");
	}

}