package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumEnergyState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUILockable;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.CosmosEnergyUtil;
import com.tcn.dimensionalpocketsii.core.management.PocketsDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallCharger;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BlockEntityModuleCharger extends BlockEntity implements IBlockInteract, WorldlyContainer, MenuProvider, Nameable, IBEUIMode, IBEUILockable {

	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);
	
	private Pocket pocket;
	private int update = 0;
	private EnumEnergyState energy_state = EnumEnergyState.FILL;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;
	
	private int chargeRate = 100000;

	public BlockEntityModuleCharger(BlockPos posIn, BlockState stateIn) {
		super(PocketsRegistrationManager.BLOCK_ENTITY_TYPE_CHARGER.get(), posIn, stateIn);
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
			BlockWallCharger block = (BlockWallCharger) state.getBlock();
			
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
		
		if (this.getPocket() != null) {
			this.getPocket().writeToNBT(compound, provider);
		}

		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("energy_state", this.energy_state.getIndex());
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
	}

	public void saveToItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);

		compound.putInt("energy_state", this.energy_state.getIndex());
		
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
		
		this.energy_state = EnumEnergyState.getStateFromIndex(compound.getInt("energy_state"));
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}

	public void loadFromItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
			
			this.energy_state = EnumEnergyState.getStateFromIndex(compound.getInt("energy_state"));
			
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityModuleCharger entityIn) {
		if (entityIn.energy_state.equals(EnumEnergyState.FILL)) {
			for (int i = 0; i < entityIn.inventoryItems.size(); i++) {
				entityIn.chargeItem(i);
			}
		} else {
			for (int i = 0; i < entityIn.inventoryItems.size(); i++) {
				entityIn.drainItem(i);
			}
		}

		boolean flag = entityIn.update > 0;

		if (flag) {
			entityIn.update--;
		} else {
			entityIn.update = 20;

			entityIn.sendUpdates(true);
		}
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player playerIn) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		this.setChanged();

		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		if (PocketUtil.isDimensionEqual(levelIn, PocketsDimensionManager.POCKET_WORLD)) {
			Pocket pocket = this.getPocket();
			
			if (pocket != null) {
				if (!playerIn.isShiftKeyDown()) {
					if (levelIn.isClientSide) {
						return ItemInteractionResult.SUCCESS;
					} else {
						if (playerIn instanceof ServerPlayer serverPlayer) {
							if (this.canPlayerAccess(playerIn)) {
								serverPlayer.openMenu(this, (packetBuffer)->{ packetBuffer.writeBlockPos(pos); });
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
									ItemStack stack = new ItemStack(PocketsRegistrationManager.MODULE_CHARGER.get());
									this.saveToItemStack(stack, levelIn.registryAccess());
									
									levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL.get().defaultBlockState());
									levelIn.removeBlockEntity(pos);
									
									CosmosUtil.addStack(levelIn, playerIn, stack);
									
									pocketIn.removeUpdateable(pos);
									
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
			} 
		}
		
		return ItemInteractionResult.FAIL;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}
	
	public void chargeItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			Object object = this.getItem(indexIn).getCapability(Capabilities.EnergyStorage.ITEM);
			
			if (object instanceof IEnergyStorage energyItem) {
				if (this.getPocket().hasEnergyStored()) {
					if (energyItem.canReceive()) {
						energyItem.receiveEnergy(this.getPocket().extractEnergy(Math.min(this.getPocket().getMaxReceive(), this.chargeRate), false), false);
					}
				}
			}
		}
	}
	
	public void drainItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			Object object = this.getItem(indexIn).getCapability(Capabilities.EnergyStorage.ITEM);
		
			if (object instanceof IEnergyStorage energyItem) {
				if (CosmosEnergyUtil.hasEnergy(energyItem)) {
					if (energyItem.canExtract()) {
						energyItem.extractEnergy(this.getPocket().receiveEnergy(Math.min(this.getPocket().getMaxReceive(), this.chargeRate), false), false);
					}
				}
			}
		}
	}

	@Override
	public void clearContent() { }

	@Override
	public boolean canPlaceItemThroughFace(int arg0, ItemStack arg1, Direction arg2) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int arg0, ItemStack arg1, Direction arg2) {
		return false;
	}

	@Override
	public int getContainerSize() {
		return 6;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.inventoryItems.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.setChanged();
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.setChanged();
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		
		this.setChanged();
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryItems) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction arg0) {
		return new int [] { 0, 0, 0, 0, 0, 0 };
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.charger");
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.charger");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerModuleCharger(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	public EnumEnergyState getEnergyState() {
		return this.energy_state;
	}

	public boolean getEnergyStateValue() {
		return this.energy_state.getValue();
	}
	
	public void setEnergyState(EnumEnergyState state) {
		this.energy_state = state;

		this.sendUpdates(true);
	}
	
	public void setEnergyState(boolean change) {
		this.energy_state = EnumEnergyState.getStateFromValue(change);

		this.sendUpdates(true);
	}
	
	public void cycleEnergyState() {
		this.setEnergyState(EnumEnergyState.getOpposite(this.energy_state));

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