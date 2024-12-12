package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.item.CosmosArmourItemColourable;
import com.tcn.cosmoslibrary.common.item.CosmosArmourItemElytra;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.item.armour.module.IModuleItem;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockEntityModuleArmourWorkbench extends CosmosBlockEntityUpdateable implements IBlockInteract, Container, IBlockEntityUIMode, IBlockEntityUpdateable {

	public NonNullList<ItemStack> inventoryItems = NonNullList.withSize(9, ItemStack.EMPTY);
	
	private Pocket pocket;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public ComponentColour customColourWings = ComponentColour.POCKET_PURPLE;
	public ComponentColour customColourArmour = ComponentColour.POCKET_PURPLE;
	
	public BlockEntityModuleArmourWorkbench(BlockPos posIn, BlockState stateIn) {
		super(ModRegistrationManager.BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH.get(), posIn, stateIn);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return StorageManager.getPocketFromChunkPosition(this.getLevel(), CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}
	
	@Override
	public void sendUpdates(boolean forceUpdate) {
		super.sendUpdates(forceUpdate);
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

		compound.putInt("wingColour", customColourWings.getIndex());
		compound.putInt("armourColour", customColourArmour.getIndex());
	}

	public void saveToItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());

		compound.putInt("wingColour", customColourWings.getIndex());
		compound.putInt("armourColour", customColourArmour.getIndex());
		
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
		
		this.customColourWings = ComponentColour.fromIndex(compound.getInt("wingColour"));
		this.customColourArmour = ComponentColour.fromIndex(compound.getInt("armourColour"));
	}

	public void loadFromItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
			
			this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
			this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
			this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));

			this.customColourWings = ComponentColour.fromIndex(compound.getInt("wingColour"));
			this.customColourArmour = ComponentColour.fromIndex(compound.getInt("armourColour"));
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityModuleArmourWorkbench entityIn) {
		entityIn.displayPreviewSlot();
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }
	
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
						serverPlayer.openMenu(state.getMenuProvider(levelIn, pos), (packetBuffer) -> { packetBuffer.writeBlockPos(pos); });
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
							ItemStack stack = new ItemStack(ModRegistrationManager.MODULE_ARMOUR_WORKBENCH.get());
							this.saveToItemStack(stack, levelIn.registryAccess());
							
							levelIn.setBlockAndUpdate(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState());
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
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn,
			BlockHitResult hit) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void applyToArmourItem(boolean colourIn, boolean moduleIn) {
		ItemStack inputStack = this.getItem(0);
		
		if (moduleIn) {
			if (inputStack != ItemStack.EMPTY) {
				for (int i = 3; i < 9; i++) {
					ItemStack stackIn = this.getItem(i);
					
					if (!stackIn.isEmpty()) {
						if (stackIn.getItem() instanceof IModuleItem) {
							IModuleItem item = (IModuleItem) stackIn.getItem();
							BaseElytraModule module = item.getModule();
							
							if (DimensionalElytraplate.addModule(inputStack, module, true)) {
								DimensionalElytraplate.addModule(inputStack, module, false);
								
								if (item.doesInformationCarry()) {
									if (item.transferInformation(stackIn, inputStack, true)) {
										item.transferInformation(stackIn, inputStack, false);
									}
								}
								
								this.getItem(i).shrink(1);
							}
						}
					}
				}
			}
		}
		
		if (inputStack != this.getResultStack(true, colourIn, moduleIn)) {
			this.setItem(0, this.getResultStack(true, colourIn, moduleIn));
		}
		
		this.setChanged();
	}
	
	public void removeFromArmourItem() {
		ItemStack inputStack = this.getItem(0);
		
		if (inputStack != ItemStack.EMPTY) {
			for (int i = 3; i < 9; i++) {
				ItemStack stackIn = this.getItem(i);
				
				if (stackIn.isEmpty()) {
					for (int j = 0; j < BaseElytraModule.LENGTH; j++) {
						BaseElytraModule module = BaseElytraModule.getStateFromIndex(j);
						
						if (!DimensionalElytraplate.removeModule(inputStack, module, true).isEmpty()) {
							ItemStack moduleStack = DimensionalElytraplate.removeModule(inputStack, module, false);

							this.setItem(i, moduleStack);
							break;
						}
					}
				}
			}
		}
	}
	
	public ItemStack getResultStack(boolean useColour, boolean colourIn, boolean moduleIn) {
		ItemStack inputStack = this.getItem(0);
		Item inputItem = inputStack.getItem();
		
		ItemStack resultStack = inputStack.copy();
		
		if (inputStack != ItemStack.EMPTY) {
			if (moduleIn) {
				for (int i = 3; i < 9; i++) {
					ItemStack stackIn = this.getItem(i);
					
					if (!stackIn.isEmpty()) {
						if (stackIn.getItem() instanceof IModuleItem) {
							IModuleItem item = (IModuleItem) stackIn.getItem();
							BaseElytraModule module = item.getModule();
							
							if (useColour) {
								if (DimensionalElytraplate.addModule(resultStack, module, true)) {
									DimensionalElytraplate.addModule(resultStack, module, false);
									
									if (item.doesInformationCarry()) {
										if (item.transferInformation(stackIn, resultStack, true)) {
											item.transferInformation(stackIn, resultStack, false);
										}
									}
								}
							}
						}
					} else {
						if (!useColour) {
							DimensionalElytraplate.removeAllModules(resultStack, false);
						}
					}
				}
			} 

			if (colourIn) {
				if (inputItem instanceof CosmosArmourItemColourable) {
					if (useColour) {
						resultStack = CosmosUtil.setArmourColourInformation(resultStack, this.customColourArmour, null);
					} else {
						resultStack = CosmosUtil.setArmourColourInformation(resultStack, ComponentColour.POCKET_PURPLE_LIGHT, null);
					}
				}
				
				if (inputItem instanceof CosmosArmourItemElytra) {
					if (useColour) {
						resultStack = CosmosUtil.setArmourColourInformation(resultStack, null, this.customColourWings);
					} else {
						resultStack = CosmosUtil.setArmourColourInformation(resultStack, null, ComponentColour.LIGHT_GRAY);
					}
				}
			}
		}
		
		return resultStack;
	}
	
	public void displayPreviewSlot() {
		if (!(this.getItem(0).isEmpty())) {
			if (this.getItem(1) != this.getResultStack(true, true, true)) {
				this.setItem(1, this.getResultStack(true, true, true));
			}
			
			if (this.getItem(2) != this.getResultStack(false, true, true)) {
				this.setItem(2, this.getResultStack(false, true, true));
			}
		} else {
			this.setItem(1, ItemStack.EMPTY);
			this.setItem(2, ItemStack.EMPTY);
		}
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

	public void updateColour(ComponentColour colourIn, boolean wingColour) {		
		if (colourIn.isEmpty()) {
			if(wingColour) {
				this.customColourWings = ComponentColour.POCKET_PURPLE;
			} else {
				 this.customColourArmour = ComponentColour.POCKET_PURPLE;
			}
		} else {
			if(wingColour) {
				this.customColourWings = colourIn;
			} else {
				 this.customColourArmour = colourIn;
			}
		}
		this.sendUpdates(true);
	}

	public ComponentColour getCustomColour(boolean wingColour) {
		return wingColour ? this.customColourWings : this.customColourArmour;
	}
	
}