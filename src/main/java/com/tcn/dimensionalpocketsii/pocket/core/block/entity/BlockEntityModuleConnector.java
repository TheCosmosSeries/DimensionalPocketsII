package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import java.util.Arrays;
import java.util.Optional;

import com.tcn.cosmoslibrary.client.interfaces.IBlockEntityClientUpdated;
import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumConnectionType;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.IFluidStorage;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityConnectionType;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntitySided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallConnector;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class BlockEntityModuleConnector extends CosmosBlockEntityUpdateable implements IBlockInteract, Container, MenuProvider, Nameable, WorldlyContainer, IBlockEntitySided, IFluidHandler, IFluidStorage, IBlockEntityClientUpdated.FluidTile, IBlockEntityConnectionType, IBlockEntityUIMode {

	private EnumSideState[] SIDE_STATE_ARRAY = EnumSideState.getStandardArray();
	private EnumConnectionType TYPE = EnumConnectionType.getStandardValue();
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	
	private Pocket pocket;
	private int update = 0;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityModuleConnector(BlockPos posIn, BlockState stateIn) {
		super(ModRegistrationManager.BLOCK_ENTITY_TYPE_CONNECTOR.get(), posIn, stateIn);
	}
	
	public BlockEntityModuleConnector(BlockPos posIn) {
		this(posIn, null);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return StorageManager.getPocketFromChunkPosition(this.getLevel(), CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}

	@Override
	public void sendUpdates(boolean forceUpdate) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockWallConnector block = (BlockWallConnector) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (forceUpdate) {
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

		compound.putInt("side", this.getSide(Direction.UP).getIndex());
		compound.putInt("type", this.getConnectionType().getIndex());
		
		if (this.getPocket().exists()) {
			this.getPocket().writeToNBT(compound, provider);
		}
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);

		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}
	
	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);

		int side = compound.getInt("side");
		int type = compound.getInt("type");
		
		this.setSide(Direction.UP, EnumSideState.getStateFromIndex(side), false);
		this.setConnectionType(EnumConnectionType.getStateFromIndex(type), false);

		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound, provider);
		}

		this.inventoryItems = NonNullList.withSize(8, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
		
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
	public void onLoad() { 
		if (!this.inventoryItems.get(0).getItem().equals(Items.BUCKET)) {
			this.inventoryItems.clear();
		}
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityModuleConnector entityIn) {
		if (entityIn.getPocket() != null) {
			//entityIn.setSurroundingStacks();
			//entityIn.syncWithPocketInventory();
			entityIn.checkFluidSlots();
		}
		
		Arrays.stream(Direction.values()).parallel().forEach((d) -> {
			BlockEntity tile = entityIn.getLevel().getBlockEntity(entityIn.getBlockPos().offset(d.getNormal()));

			if (!(tile instanceof BlockEntityModuleConnector) && !(tile instanceof BlockEntityModuleFurnace) 
					&& !(tile instanceof BlockEntityModuleArmourWorkbench) && !(tile instanceof BlockEntityModuleCrafter)
						&& !(tile instanceof BlockEntityModuleCharger) && !(tile instanceof BlockEntityModuleGenerator)) {
				
				if (tile != null && !tile.isRemoved()) {
					if (entityIn.getConnectionType().equals(EnumConnectionType.ENERGY)) {
						if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_OUTPUT)) {
							/*if (tile.getCapability(ForgeCapabilities.ENERGY, d).resolve().isPresent()) {
								LazyOptional<?> consumer = tile.getCapability(ForgeCapabilities.ENERGY, d);

								if (consumer.resolve().get() instanceof IEnergyStorage) {
									IEnergyStorage storage = (IEnergyStorage) consumer.resolve().get();

									if (storage.canReceive() && entityIn.canExtract(d)) {
										int extract = entityIn.getPocket().extractEnergy(entityIn.getPocket().getMaxExtract(), true);
										int actualExtract = storage.receiveEnergy(extract, true);
										
										if (actualExtract > 0) {
											entityIn.getPocket().extractEnergy(storage.receiveEnergy(actualExtract, false), false);
											entityIn.sendUpdates(true);
										}
									}
								}
							}*/
						} else if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_INPUT)) {
							/*if (tile.getCapability(ForgeCapabilities.ENERGY, d).resolve().isPresent()) {
							LazyOptional<?> consumer = tile.getCapability(ForgeCapabilities.ENERGY, d);

								if (consumer.resolve().get() instanceof IEnergyStorage) {
									IEnergyStorage storage = (IEnergyStorage) consumer.resolve().get();
	
									if (storage.canExtract() && entityIn.canReceive(d)) {
										int extract = storage.extractEnergy(entityIn.getPocket().getMaxReceive(), true);
										int actualExtract = entityIn.getPocket().receiveEnergy(extract, true);
										
										if (actualExtract > 0) {
											entityIn.getPocket().receiveEnergy(storage.extractEnergy(actualExtract, false), false);
											entityIn.sendUpdates(true);
										}
									}
								}
							}
						}*/
					} else if (entityIn.getConnectionType().equals(EnumConnectionType.ITEM)) {
						if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_OUTPUT)) {
							/*if (tile.getCapability(ForgeCapabilities.ITEM_HANDLER, d).resolve().isPresent()) {
								LazyOptional<?> consumer = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, d);
	
								if (consumer.resolve().get() instanceof IItemHandler) {
									IItemHandler storage = (IItemHandler) consumer.resolve().get();
	
									for (int i = 40; i < 48; i++) {
										if (!(entityIn.getPocket().getItem(i).isEmpty())) {
											for (int j = 0; j < storage.getSlots(); j++) {
												if (storage.isItemValid(j, entityIn.getPocket().getItem(i))) {
													ItemStack stack = storage.getStackInSlot(j);
													
													if (stack.getItem().equals(entityIn.getPocket().getItem(i).getItem()) || stack.isEmpty()) {
														storage.insertItem(j, entityIn.getPocket().removeItem(i, 1), false);
														entityIn.sendUpdates(true);
														break;
													}
												}
											}
										}
									}
								}
							}*/
						} else if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_INPUT)) {
							/*if (tile.getCapability(ForgeCapabilities.ITEM_HANDLER, d).resolve().isPresent()) {
								LazyOptional<?> consumer = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, d);
	
								if (consumer.resolve().get() instanceof IItemHandler) {
									IItemHandler storage = (IItemHandler) consumer.resolve().get();
									
									for (int i = 40; i < 48; i++) {
										for (int j = 0; j < storage.getSlots(); j++) {
											ItemStack homeStack = entityIn.getPocket().getItem(i);
											
											if (homeStack.getItem().equals(storage.getStackInSlot(j).getItem()) || homeStack.isEmpty()) {
												if (!storage.extractItem(j, 1, true).isEmpty()) {
													entityIn.getPocket().insertItem(i, storage.extractItem(j, 1, false), false);
													entityIn.sendUpdates(true);
													return;
												}
											}
										}
									}
								}
							}*/
						}
					} else if (entityIn.getConnectionType().equals(EnumConnectionType.FLUID)) {
						if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_OUTPUT)) {
							/*if (tile.getCapability(ForgeCapabilities.FLUID_HANDLER, d).resolve().isPresent()) {
								LazyOptional<?> consumer = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, d);
								
								if (consumer.resolve().get() instanceof IFluidHandler) {
									IFluidHandler storage = (IFluidHandler) consumer.resolve().get();
									
									if (storage.isFluidValid(capacity, entityIn.getFluidInTank(0))) {
										FluidStack stack = entityIn.drain(1000, FluidAction.SIMULATE);
										int lost = storage.fill(stack, FluidAction.SIMULATE);
										
										if (!stack.isEmpty()) {
											if (lost > 0) {
												storage.fill(stack, FluidAction.EXECUTE);
												entityIn.drain(lost, FluidAction.EXECUTE);
											}
										}
									}
								}*/
							}
						} else if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_INPUT)) {
							/*if (tile.getCapability(ForgeCapabilities.FLUID_HANDLER, d).resolve().isPresent()) {
								LazyOptional<?> consumer = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, d);
								
								if (consumer.resolve().get() instanceof IFluidHandler) {
									IFluidHandler storage = (IFluidHandler) consumer.resolve().get();
									
									FluidStack stack = storage.drain(1000, FluidAction.SIMULATE);
									int lost = entityIn.fill(stack, FluidAction.SIMULATE);
									
									if (!stack.isEmpty()) {
										if (lost > 0) {
											entityIn.fill(stack, FluidAction.EXECUTE);
											storage.drain(lost, FluidAction.EXECUTE);
										}
									}
									
								}
							}*/
						}
					}
				}
			}
		});
		
		if (entityIn.update > 0) {
			entityIn.update--;
		} else {
			entityIn.update = 40;
			entityIn.sendUpdates(true);
		}
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		if (PocketUtil.isDimensionEqual(worldIn, ModDimensionManager.POCKET_WORLD)) {
			Pocket pocketIn = this.getPocket();
			
			if (pocketIn != null) {
				if (playerIn.isShiftKeyDown()) {
					if (CosmosUtil.holdingWrench(playerIn)) {
						if (pocketIn.checkIfOwner(playerIn)) {
							if (!worldIn.isClientSide) {
								worldIn.setBlockAndUpdate(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState());
								worldIn.removeBlockEntity(pos);
								
								CosmosUtil.addItem(worldIn, playerIn, ModRegistrationManager.MODULE_CONNECTOR.get(), 1);
								pocketIn.removeUpdateable(pos);
							}
							
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
					if (CosmosUtil.holdingWrench(playerIn)) {
						if (!worldIn.isClientSide) {
							this.cycleSide(Direction.UP, true);
						}
						
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.pocket.status.cycle_side").append(this.getSide(Direction.UP).getColouredComp()));
						return ItemInteractionResult.SUCCESS;
					}
					
					else if (CosmosUtil.getStackItem(playerIn) instanceof DyeItem || CosmosUtil.getStackItem(playerIn).equals(ModRegistrationManager.DIMENSIONAL_SHARD.get())) {
						if (pocketIn.checkIfOwner(playerIn)) {
							ItemStack stack = CosmosUtil.getStack(playerIn);
							DyeColor dyeColour = DyeColor.getColor(stack);
							
							ComponentColour colour = dyeColour != null ? ComponentColour.fromIndex(dyeColour.getId()) : ComponentColour.POCKET_PURPLE;
							
							if (pocketIn.getDisplayColour() != colour.dec()) {
								pocketIn.setDisplayColour(playerIn, worldIn, colour.dec());
								this.sendUpdates(true);
								
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.pocket.status.colour_update").append(colour.getColouredName()));
								return ItemInteractionResult.SUCCESS;
							} else {
								return ItemInteractionResult.FAIL;
							}
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
							return ItemInteractionResult.FAIL;
						}
					}
					if (worldIn.isClientSide) {
						return ItemInteractionResult.SUCCESS;
					} else {
						if (playerIn instanceof ServerPlayer serverPlayer) {
							if (pocketIn.checkIfOwner(playerIn)) {
								serverPlayer.openMenu(this, (packetBuffer)->{ packetBuffer.writeBlockPos(this.getBlockPos()); });
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
								return ItemInteractionResult.FAIL;
							}
						}
						
						return ItemInteractionResult.SUCCESS;
					}
				}
			} else {
				CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
				return ItemInteractionResult.FAIL;
			}
		}

		worldIn.sendBlockUpdated(pos, state, state, 3);
		return ItemInteractionResult.FAIL;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}
	@Override
	public EnumConnectionType getConnectionType() {
		return this.TYPE;
	}

	@Override
	public void setConnectionType(EnumConnectionType type, boolean update) {
		this.TYPE = type;
		
		this.sendUpdates(update);
	}
	
	@Override
	public void cycleConnectionType(boolean update) {
		EnumConnectionType next_state = TYPE.getNextState();
		this.TYPE = next_state;
		
		this.sendUpdates(update);
	}
	
	@Override 
	public EnumSideState getSide(Direction facing) {
		return SIDE_STATE_ARRAY[facing.get3DDataValue()];
	}
	
	@Override
	public void setSide(Direction facing, EnumSideState side_state, boolean update) {
		for (int i = 0; i < this.SIDE_STATE_ARRAY.length; i++) {
			SIDE_STATE_ARRAY[i] = side_state;
		}
		this.sendUpdates(update);
	}
	
	@Override
	public EnumSideState[] getSideArray() {
		return this.SIDE_STATE_ARRAY;
	}

	@Override
	public void setSideArray(EnumSideState[] new_array, boolean update) {
		SIDE_STATE_ARRAY = new_array;
		
		this.sendUpdates(update);
	}

	@Override
	public void cycleSide(Direction facing, boolean update) {
		EnumSideState state = SIDE_STATE_ARRAY[facing.get3DDataValue()];
		
		EnumSideState state2 = state.getNextState();
		
		for (int i = 0; i < this.SIDE_STATE_ARRAY.length; i++) {
			SIDE_STATE_ARRAY[i] = state2;
		}
		
		this.sendUpdates(update);
	}

	@Override
	public boolean canConnect(Direction facing) {
		EnumSideState state = SIDE_STATE_ARRAY[facing.get3DDataValue()];
		
		if (state.equals(EnumSideState.DISABLED)) {
			return false;
		}
		return true;
	}

	public boolean getLockState() {
		return this.getPocket().getLockStateValue();
	}
	
	public void setLockState(boolean change, boolean update) {
		this.getPocket().setLockState(change);
		this.sendUpdates(update);
	}
	
	/**
	 * - IFluid Start
	 */

	@Override
	public int getFluidLevelScaled(int one) {
		return this.getPocket().getFluidLevelScaled(one);
	}
	
	@Override
	public Fluid getCurrentStoredFluid() {
		this.setChanged();
		return this.getPocket().getCurrentStoredFluid();
	}

	@Override
	public boolean isFluidEmpty() {
		return this.getPocket().isFluidTankEmpty();
	}

	@Override
	public int getCurrentFluidAmount() {
		this.setChanged();
		return this.getPocket().getCurrentFluidAmount();
	}
	
	public String getCurrentStoredFluidName() {
		return this.getPocket().getCurrentStoredFluidName();
	}

	public int fill(FluidStack resource, FluidAction doFill) {
		this.setChanged();
		return this.getPocket().fill(resource, doFill);
	}

	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		this.setChanged();
		return this.getPocket().drain(resource.getAmount(), doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		this.setChanged();
		return this.getPocket().drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(Direction from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(Direction from, Fluid fluid) {
		return true;
	}
	
	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.getPocket().getFluidTank().getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.getPocket().getFluidTankCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return true;
	}

	@Override
	public FluidTank getTank() {
		return this.getPocket().getFluidTank();
	}

	@Override
	public int getFluidCapacity() {
		return this.getPocket().getFluidTankCapacity();
	}
	
	public void checkFluidSlots() {
		if (!this.level.isClientSide) {
			if (!this.getItem(54).isEmpty()) {
				if (this.getItem(54).getItem() instanceof BucketItem) {
					Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(this.getItem(54));
					
					if (fluidStack.isPresent()) {
						FluidStack fluid = fluidStack.get();
						
						if (fluid != null) {
							int amount = this.fill(fluid, FluidAction.SIMULATE);
							if (amount == fluid.getAmount()) {
								if (this.getItem(55).getItem().equals(Items.BUCKET) && this.getItem(55).getCount() < 17) {
									this.fill(fluid, FluidAction.EXECUTE);
									this.getItem(54).shrink(1);
									this.getItem(55).grow(1);
								}
								
								if (this.getItem(55).isEmpty()) {
									this.fill(fluid, FluidAction.EXECUTE);
									this.getItem(54).shrink(1);
									this.setItem(55, new ItemStack(Items.BUCKET));
								}
							}
						}
					} else {
						if (this.getCurrentFluidAmount() > 0) {
							if (this.getItem(55).isEmpty()) {
								ItemStack fillStack = FluidUtil.tryFillContainer(this.getItem(54), this.getTank(), 1000, null, true).result;
								
								if (fillStack.getItem() instanceof BucketItem) {
									this.getItem(54).shrink(1);
									this.setItem(55, fillStack);
								} else {
									this.getItem(54).shrink(1);
									this.setItem(55, new ItemStack(Items.BUCKET));
								}
							}
						}
					
					}
				}
				
				this.sendUpdates(true);
			}
		}
	}
	
	@Override
	public AbstractContainerMenu createMenu(int indexIn, Inventory playerInventoryIn, Player playerIn) {
		return ContainerModuleConnector.createContainerServerSide(indexIn, playerInventoryIn, this.getPocket(), this, this.getBlockPos());
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.connector.header");
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.connector.header");
	}

	@Override
	public boolean isEmpty() {
		if (this.level != null) {
			return this.getPocket().isEmpty();
		}
		return false;
	}
	
	@Override
	public int getContainerSize() {
		if (this.level != null) {
			return this.getPocket().getContainerSize();
		}
		return ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH;
	}

	@Override
	public ItemStack getItem(int index) {
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			return this.getPocket().getItem(index);
		} else {
			return this.inventoryItems.get(index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH);
		}
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.setChanged();
		
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			return this.getPocket().removeItem(index, count);
		} else {
			return ContainerHelper.removeItem(inventoryItems, index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH, count);
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.setChanged();
		
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			return this.getPocket().removeItemNoUpdate(index);
		} else {
			return ContainerHelper.takeItem(this.inventoryItems, index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH);
		}
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			this.getPocket().setItem(index, stack);
		} else {
			this.inventoryItems.set(index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH, stack);
		}
		
		this.setChanged();
	}
	
	@Override
	public boolean stillValid(Player player) {
		if (this.level != null) {
			return this.getPocket().stillValid(player);
		}
		
		return true;
	}

	@Override
	public void clearContent() {
		if (this.level != null) {
			this.getPocket().clearContent();
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int indexIn, ItemStack itemStackIn, Direction directionIn) {
		if (indexIn > 39 && indexIn < 48) {
			if (this.getConnectionType().equals(EnumConnectionType.ITEM)) {
				if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_INPUT) || this.getSide(directionIn).equals(EnumSideState.INTERFACE_NORMAL)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int indexIn, ItemStack itemStackIn, Direction directionIn) {
		if (indexIn > 39 && indexIn < 48) {
			if (this.getConnectionType().equals(EnumConnectionType.ITEM)) {
				if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_OUTPUT)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction arg0) {
		return new int[] { 40, 41, 42, 43, 44, 45, 46, 47 };
	}
	
	public boolean canExtract(Direction directionIn) {
		EnumConnectionType type = this.getConnectionType();
		
		if (type.equals(EnumConnectionType.ENERGY)) {
			EnumSideState state = this.getSide(directionIn.getOpposite());
			
			if (state.equals(EnumSideState.INTERFACE_OUTPUT) || state.equals(EnumSideState.INTERFACE_NORMAL)) {
				return this.getPocket().canExtractEnergy();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean canReceive(Direction directionIn) {
		EnumConnectionType type = this.getConnectionType();
		
		if (type.equals(EnumConnectionType.ENERGY)) {
			EnumSideState state = this.getSide(directionIn.getOpposite());
			
			if (state.equals(EnumSideState.INTERFACE_INPUT) || this.getSide(directionIn).equals(EnumSideState.INTERFACE_NORMAL)) {
				return this.getPocket().canReceiveEnergy();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
/*
	private LazyOptional<IFluidHandler> createFluidProxy(@Nullable Direction directionIn) {
		return LazyOptional.of(() -> new IFluidHandler() {

			@Override
			public int getTanks() {
				if (BlockEntityModuleConnector.this.getConnectionType().equals(EnumConnectionType.FLUID)) {
					return BlockEntityModuleConnector.this.getPocket().getTanks();
				}
				
				return 0;
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				if (BlockEntityModuleConnector.this.getConnectionType().equals(EnumConnectionType.FLUID)) {
					return BlockEntityModuleConnector.this.getPocket().getFluidInTank();
				}
				
				return FluidStack.EMPTY;
			}

			@Override
			public int getTankCapacity(int tank) {
				if (BlockEntityModuleConnector.this.getConnectionType().equals(EnumConnectionType.FLUID)) {
					return BlockEntityModuleConnector.this.getPocket().getFluidTankCapacity();
				}
				
				return 0;
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (BlockEntityModuleConnector.this.getConnectionType().equals(EnumConnectionType.FLUID)) {
					if (!BlockEntityModuleConnector.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
						return true;
					}
				}
				
				return false;
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (BlockEntityModuleConnector.this.getConnectionType().equals(EnumConnectionType.FLUID)) {
					if (!BlockEntityModuleConnector.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
						return BlockEntityModuleConnector.this.getPocket().fill(resource, action);
					}
				}
				
				return 0;
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (BlockEntityModuleConnector.this.getConnectionType().equals(EnumConnectionType.FLUID)) {
					if (!BlockEntityModuleConnector.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
						BlockEntityModuleConnector.this.sendUpdates(true);
						return BlockEntityModuleConnector.this.getPocket().drain(resource, action);
					}
				}
				
				return FluidStack.EMPTY;
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (BlockEntityModuleConnector.this.getConnectionType().equals(EnumConnectionType.FLUID)) {
					if (!BlockEntityModuleConnector.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
						BlockEntityModuleConnector.this.sendUpdates(true);
						return BlockEntityModuleConnector.this.getPocket().drain(maxDrain, action);
					}
				}
				
				return FluidStack.EMPTY;
			}
			
		});
	}

	private LazyOptional<IEnergyStorage> createEnergyProxy(@Nullable Direction directionIn) {
        return LazyOptional.of(() -> new IEnergyStorage() {
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
            	BlockEntityModuleConnector.this.sendUpdates(true);
                return BlockEntityModuleConnector.this.getPocket().extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityModuleConnector.this.getPocket().getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityModuleConnector.this.getPocket().getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
            	BlockEntityModuleConnector.this.sendUpdates(true);
                return BlockEntityModuleConnector.this.getPocket().receiveEnergy(maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityModuleConnector.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityModuleConnector.this.canExtract(directionIn);
            }
        });
    }

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction directionIn) {
		if (!this.remove && directionIn != null) {
			if (capability == ForgeCapabilities.ENERGY) {
				return this.createEnergyProxy(directionIn).cast();
			} else if (capability == ForgeCapabilities.FLUID_HANDLER) {
				return this.createFluidProxy(directionIn).cast();
			} else if (capability == ForgeCapabilities.ITEM_HANDLER) {
				if (directionIn == Direction.DOWN) {
					return handlers[0].cast();
				} else if (directionIn == Direction.UP) {
					return handlers[1].cast();
				} else if (directionIn == Direction.EAST) {
					return handlers[2].cast();
				} else if (directionIn == Direction.WEST) {
					return handlers[3].cast();
				} else if (directionIn == Direction.NORTH) {
					return handlers[4].cast();
				} else if (directionIn == Direction.SOUTH) {
					return handlers[5].cast();
				}
			}
		}
		
		return super.getCapability(capability, directionIn);
	}
	*/
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