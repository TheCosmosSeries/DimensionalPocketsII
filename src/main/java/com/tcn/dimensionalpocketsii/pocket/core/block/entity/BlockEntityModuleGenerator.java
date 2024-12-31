package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import java.util.Optional;

import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumGenerationMode;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.IFluidStorage;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUILockable;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;
import com.tcn.dimensionalpocketsii.core.management.PocketsDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("removal")
public class BlockEntityModuleGenerator extends CosmosBlockEntityUpdateable implements IBlockInteract, WorldlyContainer, MenuProvider, Nameable, IFluidHandler, IFluidStorage, IBEUIMode, IBEUILockable {
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
	
	private Pocket pocket;
	private int update = 0;
	
	private int burnTime;
	private int lastBurnTime = 1;
	private int cookTime;

	private int fluidBurnTime;
	private int fluidLastBurnTime = 1;
	private int fluidCookTime;
	
	private int generationRate = 0;
	
	private EnumGenerationMode generationMode = EnumGenerationMode.BURNABLE_ITEM;
	private ObjectFluidTankCustom internalTank = new ObjectFluidTankCustom(new FluidTank(64000), 0);
	
	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	private final int BUCKET_IN_SLOT = 1;
	private final int BUCKET_OUT_SLOT = 2;

	public final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int indexIn) {
			switch (indexIn) {
				case 0:
					return BlockEntityModuleGenerator.this.burnTime;
				case 1:
					return BlockEntityModuleGenerator.this.cookTime;
				case 2:
					return BlockEntityModuleGenerator.this.generationRate;
				case 3:
					return BlockEntityModuleGenerator.this.fluidBurnTime;
				case 4:
					return BlockEntityModuleGenerator.this.fluidCookTime;
				default:
					return 0;
			}
		}
		
		@Override
		public void set(int indexIn, int valueIn) {
			switch (indexIn) {
				case 0:
					BlockEntityModuleGenerator.this.burnTime = valueIn;
					break;
				case 1:
					BlockEntityModuleGenerator.this.cookTime = valueIn;
					break;
				case 2:
					BlockEntityModuleGenerator.this.generationRate = valueIn;
					break;
				case 3:
					BlockEntityModuleGenerator.this.fluidBurnTime = valueIn;
					break;
				case 4:
					BlockEntityModuleGenerator.this.fluidCookTime = valueIn;
				default:
					return;
			}
		}

		@Override
		public int getCount() {
			return 5;
		}
	};
	
	public BlockEntityModuleGenerator(BlockPos posIn, BlockState stateIn) {
		super(PocketsRegistrationManager.BLOCK_ENTITY_TYPE_GENERATOR.get(), posIn, stateIn);
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

		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("generation_mode", this.generationMode.getIndex());
		this.internalTank.writeToNBT(compound);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		
		compound.putInt("cookTime", this.cookTime);
		compound.putInt("burnTime", this.burnTime);
		compound.putInt("lastBurnTime", this.lastBurnTime);
		compound.putInt("generationRate", this.generationRate);

		compound.putInt("fluidCookTime", this.fluidCookTime);
		compound.putInt("fluidBurnTime", this.fluidBurnTime);
		compound.putInt("fluidLastBurnTime", this.fluidLastBurnTime);
	}
	
	public void saveToItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("generation_mode", this.generationMode.getIndex());
		this.internalTank.writeToNBT(compound);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		
		compound.putInt("cookTime", this.cookTime);
		compound.putInt("burnTime", this.burnTime);
		compound.putInt("lastBurnTime", this.lastBurnTime);
		compound.putInt("generationRate", this.generationRate);

		compound.putInt("fluidCookTime", this.fluidCookTime);
		compound.putInt("fluidBurnTime", this.fluidBurnTime);
		compound.putInt("fluidLastBurnTime", this.fluidLastBurnTime);
		
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
		
		this.generationMode = EnumGenerationMode.getStateFromIndex(compound.getInt("generation_mode"));
		this.internalTank = ObjectFluidTankCustom.readFromNBT(compound);
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
		
		this.cookTime = compound.getInt("cookTime");
		this.burnTime = compound.getInt("burnTime");
		this.lastBurnTime = compound.getInt("lastBurnTime");
		this.generationRate = compound.getInt("generationRate");
		
		this.fluidCookTime = compound.getInt("fluidCookTime");
		this.fluidBurnTime = compound.getInt("fluidBurnTime");
		this.fluidLastBurnTime = compound.getInt("fluidLastBurnTime");
	}

	public void loadFromItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
			
			this.generationMode = EnumGenerationMode.getStateFromIndex(compound.getInt("generation_mode"));
			this.internalTank = ObjectFluidTankCustom.readFromNBT(compound);
			
			this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
			this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
			this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
			
			this.cookTime = compound.getInt("cookTime");
			this.burnTime = compound.getInt("burnTime");
			this.lastBurnTime = compound.getInt("lastBurnTime");
			
			this.fluidCookTime = compound.getInt("fluidCookTime");
			this.fluidBurnTime = compound.getInt("fluidBurnTime");
			this.fluidLastBurnTime = compound.getInt("fluidLastBurnTime");
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

	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityModuleGenerator entityIn) {
		entityIn.checkFluidSlots();

		if (entityIn.burnTime > 0) {
			entityIn.burnTime--;
		}

		if (entityIn.fluidBurnTime > 0) {
			entityIn.fluidBurnTime--;
		}
		
		if (entityIn.getGenerationMode().equals(EnumGenerationMode.BURNABLE_ITEM)) {
			entityIn.generationRate = 500;
			
			ItemStack burnStack = entityIn.getItem(0);
			
			int burnTime = 0;
			
			if (!entityIn.getItem(0).isEmpty()) {
				burnTime = burnStack.getBurnTime(null);
			}

			if (!(entityIn.burnTime > 0) && entityIn.getPocket().getEnergyStored() < entityIn.getPocket().getMaxEnergyStored()) {
				entityIn.burnTime = burnTime;
				
				if (burnTime > 0) {
					entityIn.lastBurnTime = burnTime;
				}
			}
			
			if (entityIn.burnTime > 0) {
				entityIn.cookTime++;
				
				if (entityIn.getPocket().getEnergyStored() < entityIn.getPocket().getMaxEnergyStored()) {
					if (entityIn.getPocket().receiveEnergy(entityIn.generationRate, true) > 0) {
						entityIn.getPocket().receiveEnergy(entityIn.generationRate, false);
					}
					
					if (entityIn.cookTime == 1) {
						entityIn.getItem(0).shrink(1);
						entityIn.sendUpdates(true);
					}
				}
				
				if (entityIn.cookTime == burnTime) {
					entityIn.sendUpdates(true);
					entityIn.cookTime = 0;
				}
			} else {
				entityIn.cookTime = 0;
			}
		} else if (entityIn.getGenerationMode().equals(EnumGenerationMode.BURNABLE_FLUID)) {
			entityIn.generationRate = 1000;
			int burnTime = 0;
			
			if (entityIn.getCurrentFluidAmount() > 0) {
				burnTime = 20000;
			}
			
			if (!(entityIn.fluidBurnTime > 0) && entityIn.getPocket().getEnergyStored() < entityIn.getPocket().getMaxEnergyStored()) {
				entityIn.fluidBurnTime = burnTime;
				
				if (burnTime > 0) {
					entityIn.fluidLastBurnTime = burnTime;
				}
			}
			
			if (entityIn.fluidBurnTime > 0) {
				entityIn.fluidCookTime++;
				
				if (entityIn.getPocket().getEnergyStored() < entityIn.getPocket().getMaxEnergyStored()) {
					if (entityIn.getPocket().receiveEnergy(entityIn.generationRate, true) > 0) {
						entityIn.getPocket().receiveEnergy(entityIn.generationRate, false);
					}
					
					if (entityIn.fluidCookTime == 1) {
						if (!entityIn.drain(1000, FluidAction.SIMULATE).isEmpty()) {
							entityIn.drain(1000, FluidAction.EXECUTE);
							entityIn.sendUpdates(true);
						}
					}
				}
				
				if (entityIn.fluidCookTime == 20000) {
					entityIn.sendUpdates(true);
					entityIn.fluidCookTime = 0;
				}
			} else {
				entityIn.fluidCookTime = 0;
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
			Pocket pocketIn = this.getPocket();
			
			if (pocketIn != null) {
				if (!playerIn.isShiftKeyDown()) {
					if (levelIn.isClientSide) {
						return ItemInteractionResult.SUCCESS;
					} else {
						if (playerIn instanceof ServerPlayer serverPlayer) {
							if (this.canPlayerAccess(playerIn)) {
								serverPlayer.openMenu(this, (packetBuffer)->{ packetBuffer.writeBlockPos(pos); });
								return ItemInteractionResult.SUCCESS;
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
								return ItemInteractionResult.FAIL;
							}
						}
						
						return ItemInteractionResult.SUCCESS;
					}
				} else {
					if (CosmosUtil.handEmpty(playerIn)) {
						pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
						
						return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
					} 
					
					else if (CosmosUtil.holdingWrench(playerIn)) {
						if (pocketIn.checkIfOwner(playerIn)) {
							ItemStack stack = new ItemStack(PocketsRegistrationManager.MODULE_GENERATOR.get());
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
		return 64;
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
		if (stack.getCount() > this.getContainerSize()) {
			stack.setCount(this.getContainerSize());
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
		return ComponentHelper.title("dimensionalpocketsii.gui.generator");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerModuleGenerator(idIn, playerInventoryIn, this, this.dataAccess, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	public EnumGenerationMode getGenerationMode() {
		return this.generationMode;
	}

	public boolean getGenerationModeValue() {
		return this.generationMode.getValue();
	}
	
	public void setGenerationMode(EnumGenerationMode state) {
		this.generationMode = state;

		this.sendUpdates(true);
	}
	
	public void setGenerationMode(boolean change) {
		this.generationMode = EnumGenerationMode.getStateFromValue(change);

		this.sendUpdates(true);
	}
	
	public void cycleGenerationMode() {
		this.setGenerationMode(EnumGenerationMode.getOpposite(this.generationMode));

		this.sendUpdates(true);
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.generator");
	}
	
	public int getFluidLevelScaled(int one) {
		int cap = this.getFluidTankCapacity() > 0 ? this.getFluidTankCapacity() : 1;
		
		
		float scaled = this.getCurrentFluidAmount() * one / cap;
		
		if (scaled == 0 && this.getCurrentFluidAmount() > 0) {
			return 1;
		} else {
			return (int) scaled;
		}
	}

	public int getCurrentFluidAmount() {
		return this.internalTank.getFluidTank().getFluidAmount();
	}
	
	public Fluid getCurrentStoredFluid() {
		this.updateFluidFillLevel();
		
		if (!this.isFluidTankEmpty()) {
			return this.internalTank.getFluidTank().getFluid().getFluid();
		}
		return null;
	}
	
	public String getCurrentStoredFluidName() {
		if (this.isFluidTankEmpty()) {
			return "Empty";
		}
		return this.internalTank.getFluidTank().getFluid().getTranslationKey();
	}

	public boolean isFluidTankEmpty() {
		return this.internalTank.getFluidTank().getFluidAmount() == 0;
	}

	public int getFluidTankCapacity() {
		return this.internalTank.getFluidTank().getCapacity();
	}
	
	public int getFluidFillLevel() {
		return this.internalTank.getFillLevel();
	}

	public void setFluidFillLevel(int set) {
		this.internalTank.setFillLevel(set);
	}
	
	public void updateFluidFillLevel() {
		if (!this.isFluidTankEmpty()) {
			if (this.getFluidLevelScaled(16) == 0) {
				this.internalTank.setFillLevel(1);
			} else {
				this.internalTank.setFillLevel(this.getFluidLevelScaled(16));
			}
		} else {
			this.internalTank.setFillLevel(0);
		}
	}
	
	public int fill(FluidStack resource, FluidAction doFill) {
		if (this.isFluidValid(0, resource)) {
			this.updateFluidFillLevel();
			return this.internalTank.getFluidTank().fill(resource, doFill);
		}
		
		return 0;
	}

	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		this.updateFluidFillLevel();
		
		return this.internalTank.getFluidTank().drain(resource.getAmount(), doDrain);
	}

	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		this.updateFluidFillLevel();
		return this.internalTank.getFluidTank().drain(maxDrain, doDrain);
	}
	
	public boolean canFill(Direction from, Fluid fluid) {
		return true;
	}
	
	public boolean canDrain(Direction from, Fluid fluid) {
		return true;
	}
	
	public FluidTank getFluidTank() {
		return this.internalTank.getFluidTank();
	}
	
	public void setFluidTank(FluidTank tank) {
		this.internalTank.setFluidTank(tank);
	}
	
	public void emptyFluidTank() {
		this.internalTank.getFluidTank().setFluid(FluidStack.EMPTY);
	}
	
	public void checkFluidSlots() {
		if (!this.getLevel().isClientSide()) {
			if (!this.getItem(BUCKET_IN_SLOT).isEmpty()) {
				Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(this.getItem(BUCKET_IN_SLOT));
				
				if (fluidStack.isPresent()) {
					FluidStack fluid = fluidStack.get();
					
					if (fluid != null) {
						if (this.isFluidValid(0, fluid)) {
							if (fluid.getAmount() > 0) {
								int amount = this.fill(fluid, FluidAction.SIMULATE);
								if (amount == fluid.getAmount()) {
									if (this.getItem(BUCKET_OUT_SLOT).getItem().equals(FluidUtil.tryEmptyContainer(this.getItem(BUCKET_IN_SLOT), this.getTank(), amount, null, false).result.getItem()) && this.getItem(BUCKET_OUT_SLOT).getCount() < this.getItem(BUCKET_OUT_SLOT).getMaxStackSize()) {
										this.fill(fluid, FluidAction.EXECUTE);
										this.getItem(BUCKET_IN_SLOT).shrink(1);
										this.getItem(BUCKET_OUT_SLOT).grow(1);
									}
									
									if (this.getItem(BUCKET_OUT_SLOT).isEmpty()) {
										this.setItem(BUCKET_OUT_SLOT, FluidUtil.tryEmptyContainer(this.getItem(BUCKET_IN_SLOT), this.getTank(), amount, null, true).result);
										this.getItem(BUCKET_IN_SLOT).shrink(1);
									}
								}
							}
						}
					}
				} else {
					if (this.getCurrentFluidAmount() > 0) {
						if (this.getItem(BUCKET_OUT_SLOT).isEmpty()) {
							ItemStack fillStack = FluidUtil.tryFillContainer(this.getItem(BUCKET_IN_SLOT), this.getTank(), this.getCurrentFluidAmount(), null, true).result;
							
							if (!fillStack.isEmpty()) {
								this.setItem(BUCKET_OUT_SLOT, fillStack);
								this.getItem(BUCKET_IN_SLOT).shrink(1);
								this.getPocket().updateFluidFillLevel();
							}
						}
					}
				
				}
				this.sendUpdates(true);
			}
		}
	}

	@Override
	public int getFluidCapacity() {
		return this.internalTank.getFluidTank().getCapacity();
	}

	@Override
	public FluidTank getTank() {
		return this.internalTank.getFluidTank();
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.internalTank.getFluidTank().getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.internalTank.getFluidTank().getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return stack.getFluid().getFluidType().getTemperature() >= 1000;
	}
	
	public int getBurnTimeScaled(int scale) {
		if (this.getGenerationMode().equals(EnumGenerationMode.BURNABLE_ITEM)) {
			return (this.burnTime * scale) / (this.lastBurnTime + 1);
		}
		return (this.fluidBurnTime * scale) / (this.fluidLastBurnTime + 1);
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