package com.tcn.dimensionalpocketsii.pocket.core.blockentity;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumEnergyState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallCharger;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class BlockEntityModuleCharger extends BlockEntity implements IBlockInteract, Container, WorldlyContainer, MenuProvider, Nameable, IBlockEntityUIMode {

	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);
	
	private Pocket pocket;
	private int update = 0;
	private EnumEnergyState energy_state = EnumEnergyState.FILL;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityModuleCharger(BlockPos posIn, BlockState stateIn) {
		super(ModBusManager.BLOCK_ENTITY_TYPE_CHARGER, posIn, stateIn);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return PocketRegistryManager.getPocketFromChunkPosition(CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
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
						this.getPocket().updateBaseConnector(this.getLevel());
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
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		if (this.getPocket() != null) {
			this.getPocket().writeToNBT(compound);
		}

		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		
		compound.putInt("energy_state", this.energy_state.getIndex());
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
	}

	public void saveToItemStack(ItemStack stackIn) {
		CompoundTag compound = stackIn.getOrCreateTag();
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems);

		compound.putInt("energy_state", this.energy_state.getIndex());
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound);
		}
		
		this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);
		
		this.energy_state = EnumEnergyState.getStateFromIndex(compound.getInt("energy_state"));
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}

	public void loadFromItemStack(ItemStack stackIn) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems);
			
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
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	/**
	 * Retrieve the data to be stored. [TE > NBT] (WRITE)
	 */
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag);
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
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag_ = pkt.getTag();
		this.handleUpdateTag(tag_);
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
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { }

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		this.setChanged();

		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return InteractionResult.FAIL;
		}
		
		if (PocketUtil.isDimensionEqual(worldIn, DimensionManager.POCKET_WORLD)) {
			Pocket pocket = this.getPocket();
			
			if (pocket != null) {
				if (!playerIn.isShiftKeyDown()) {
					if (worldIn.isClientSide) {
						return InteractionResult.SUCCESS;
					} else {
						if (playerIn instanceof ServerPlayer) {
							if (this.canPlayerAccess(playerIn)) {
								NetworkHooks.openGui((ServerPlayer)playerIn, this, (packetBuffer)->{ packetBuffer.writeBlockPos(pos); });
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
								return InteractionResult.FAIL;
							}
						}
						
						return InteractionResult.SUCCESS;
					}
				} else {
					if(!worldIn.isClientSide) {
						CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
						Pocket pocketIn = PocketRegistryManager.getPocketFromChunkPosition(chunkPos);
						
						if(pocketIn.exists()) {
							if (CosmosUtil.holdingWrench(playerIn)) {
								if (pocketIn.checkIfOwner(playerIn)) {
									ItemStack stack = new ItemStack(ModBusManager.MODULE_CHARGER);
									this.saveToItemStack(stack);
									
									worldIn.setBlockAndUpdate(pos, ModBusManager.BLOCK_WALL.defaultBlockState());
									worldIn.removeBlockEntity(pos);
									
									CosmosUtil.addStack(worldIn, playerIn, stack);
									
									pocketIn.removeUpdateable(pos);
									
									return InteractionResult.SUCCESS;
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
									return InteractionResult.FAIL;
								}
							} 
							
							else if (CosmosUtil.handEmpty(playerIn)) {
								pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
								return InteractionResult.SUCCESS;
							}
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
							return InteractionResult.FAIL;
						}
					}
				}
			} 
		}
		
		return InteractionResult.FAIL;
	}

	public void chargeItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			ItemStack stackIn = this.getItem(indexIn);
			Item item = stackIn.getItem();
		
			if (item instanceof ICosmosEnergyItem) {
				ICosmosEnergyItem energyItem = (ICosmosEnergyItem) item;
				
				if (this.getPocket().hasEnergyStored()) {
					if (energyItem.canReceiveEnergy(stackIn)) {
						energyItem.receiveEnergy(stackIn, this.getPocket().extractEnergy(Math.min(this.getPocket().getMaxExtract(), energyItem.getMaxReceive(stackIn)), false), false);
					}
				}
			}
		}
	}
	
	public void drainItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			ItemStack stackIn = this.getItem(indexIn);
			Item item = stackIn.getItem();
		
			if (item instanceof ICosmosEnergyItem) {
				ICosmosEnergyItem energyItem = (ICosmosEnergyItem) item;
				
				if (energyItem.hasEnergy(stackIn)) {
					if (energyItem.canExtractEnergy(stackIn)) {
						energyItem.extractEnergy(stackIn, this.getPocket().receiveEnergy(Math.min(this.getPocket().getMaxReceive(), energyItem.getMaxExtract(stackIn)), false), false);
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