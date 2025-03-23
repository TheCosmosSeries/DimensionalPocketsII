package com.tcn.dimensionalpocketsii.pocket.core.blockentity;

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
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplateScreen;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplateShift;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.item.armour.module.IModuleItem;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class BlockEntityModuleArmourWorkbench extends CosmosBlockEntityUpdateable implements IBlockInteract, Container, IBlockEntityUIMode, IBlockEntityUpdateable {

	public NonNullList<ItemStack> inventoryItems = NonNullList.withSize(11, ItemStack.EMPTY);
	
	private Pocket pocket;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityModuleArmourWorkbench(BlockPos posIn, BlockState stateIn) {
		super(ModBusManager.BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH, posIn, stateIn);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return PocketRegistryManager.getPocketFromChunkPosition(CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}
	
	@Override
	public void sendUpdates(boolean forceUpdate) {
		super.sendUpdates(forceUpdate);
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		if (this.getPocket().exists()) {
			this.getPocket().writeToNBT(compound);
		}
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}

	public void saveToItemStack(ItemStack stackIn) {
		CompoundTag compound = stackIn.getOrCreateTag();
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		
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
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}

	public void loadFromItemStack(ItemStack stackIn) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems);
			
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
		this.sendUpdates(true);
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
	}
	
	@Override
	public void onLoad() { }
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityModuleArmourWorkbench entityIn) {
		entityIn.displayPreviewSlot();
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		worldIn.sendBlockUpdated(pos, state, state, 3);
		this.setChanged();
		this.sendUpdates(true);
		
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return InteractionResult.FAIL;
		}
		
		if (!playerIn.isShiftKeyDown()) {
			if (worldIn.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayer) {
					if (this.canPlayerAccess(playerIn)) {
						NetworkHooks.openGui((ServerPlayer)playerIn, state.getMenuProvider(worldIn, pos), (packetBuffer) -> { packetBuffer.writeBlockPos(pos); });
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
							ItemStack stack = new ItemStack(ModBusManager.MODULE_ARMOUR_WORKBENCH);
							this.saveToItemStack(stack);
							
							worldIn.setBlockAndUpdate(pos, ModBusManager.BLOCK_WALL.defaultBlockState());
							worldIn.removeBlockEntity(pos);
							
							CosmosUtil.addStack(worldIn, playerIn, stack);
							
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
		
		return InteractionResult.SUCCESS;
	}

	public void applyToArmourItem(boolean colourIn, boolean moduleIn) {
		ItemStack inputStack = this.getItem(0);
		
		if (moduleIn) {
			if (inputStack != ItemStack.EMPTY) {
				for (int i = 5; i < 11; i++) {
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
			for (int i = 5; i < 11; i++) {
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
		
		ItemStack mainColourStack = this.getItem(1);
		ItemStack wingColourStack = this.getItem(2);
		
		ItemStack resultStack = inputStack.copy();
		
		if (inputStack != ItemStack.EMPTY) {
			if (moduleIn) {
				for (int i = 5; i < 11; i++) {
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
				
				if (inputItem instanceof DimensionalElytraplateShift) {
					ItemStack stack = new ItemStack(ModBusManager.DIMENSIONAL_ELYTRAPLATE);
					
					if (inputStack.hasTag()) {
						CompoundTag compound = inputStack.getTag();
						
						stack.setTag(compound);
					}
					
					if (useColour) {
						DimensionalElytraplate.addModule(stack, BaseElytraModule.SHIFTER, false);
					}
					
					resultStack = CosmosUtil.setArmourColourInformation(stack, null, null);
				}
				
				if (inputItem instanceof DimensionalElytraplateScreen) {
					ItemStack stack = new ItemStack(ModBusManager.DIMENSIONAL_ELYTRAPLATE);
					
					if (inputStack.hasTag()) {
						CompoundTag compound = inputStack.getTag();
						
						stack.setTag(compound);
					}
					
					if (useColour) {
						DimensionalElytraplate.addModule(stack, BaseElytraModule.SCREEN, false);
					}
					
					resultStack = CosmosUtil.setArmourColourInformation(stack, null, null);
				}
			} 

			if (colourIn) {
				if (inputItem instanceof CosmosArmourItemColourable) {
					if (mainColourStack != ItemStack.EMPTY) {
						ComponentColour colour = CosmosUtil.getColourFromStack(mainColourStack, ComponentColour.POCKET_PURPLE_LIGHT);
						
						if (useColour) {
							resultStack = CosmosUtil.setArmourColourInformation(resultStack, colour, null);
						} else {
							resultStack = CosmosUtil.setArmourColourInformation(resultStack, ComponentColour.POCKET_PURPLE_LIGHT, null);
						}
					} else {
						resultStack = CosmosUtil.setArmourColourInformation(resultStack, ComponentColour.POCKET_PURPLE_LIGHT, null);
					}
				}
				
				if (inputItem instanceof CosmosArmourItemElytra) {
					if (wingColourStack != ItemStack.EMPTY) {
						if (wingColourStack.getItem().equals(ModBusManager.DIMENSIONAL_SHARD)) {
							if (!useColour) {
								resultStack = CosmosUtil.setArmourColourInformation(resultStack, null, ComponentColour.LIGHT_GRAY);
							} else {
								resultStack = CosmosUtil.setArmourColourInformation(resultStack, null, ComponentColour.POCKET_PURPLE_LIGHT);
							}
						} else {
							ComponentColour colour = CosmosUtil.getColourFromStack(wingColourStack, ComponentColour.LIGHT_GRAY);
						
							if (useColour) {
								resultStack = CosmosUtil.setArmourColourInformation(resultStack, null, colour);
							} else {
								resultStack = CosmosUtil.setArmourColourInformation(resultStack, null, ComponentColour.LIGHT_GRAY);
							}
						}
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
			if (this.getItem(3) != this.getResultStack(true, true, true)) {
				this.setItem(3, this.getResultStack(true, true, true));
			}
			
			if (this.getItem(4) != this.getResultStack(false, true, true)) {
				this.setItem(4, this.getResultStack(false, true, true));
			}
		} else {
			this.setItem(3, ItemStack.EMPTY);
			this.setItem(4, ItemStack.EMPTY);
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
}