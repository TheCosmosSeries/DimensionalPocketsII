package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.capability.IEnergyCapBE;
import com.tcn.cosmoslibrary.common.capability.IFluidCapBE;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumSideGuide;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.IFluidStorage;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBESided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUILockable;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUpdates;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.core.management.PocketsDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.AbstractBlockPocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketEventFactory;
import com.tcn.dimensionalpocketsii.pocket.core.registry.ChunkLoadingManager;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public abstract class AbstractBlockEntityPocket extends CosmosBlockEntityUpdateable implements IBlockNotifier, IBlockInteract, WorldlyContainer, IBESided, MenuProvider, Nameable, IFluidHandler, IFluidStorage, IBEUpdates.FluidBE, IBEUIMode, IBEUILockable, IEnergyCapBE, IFluidCapBE {
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	private EnumSideGuide SIDE_GUIDE = EnumSideGuide.HIDDEN;
	
	private Pocket pocket;
	private boolean isSingleChunk;
	private int update = 0;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;
	
	private final int BUCKET_IN_SLOT = 54;
	private final int BUCKET_OUT_SLOT = 55;

	public AbstractBlockEntityPocket(BlockEntityType<?> typeIn, BlockPos posIn, BlockState stateIn, boolean isSingleChunkIn) {
		super(typeIn, posIn, stateIn);
		
		this.isSingleChunk = isSingleChunkIn;
	}
	
	public AbstractBlockEntityPocket(BlockEntityType<?> typeIn, BlockPos posIn, boolean isSingleChunkIn) {
		this(typeIn, posIn, null, isSingleChunkIn);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return StorageManager.getOrCreatePocket(this.level, this.getBlockPos(), this.isSingleChunk);
	}
	
	@Override
	public void sendUpdates(boolean forceUpdate) {
		//super.sendUpdates(forceUpdate);
		
		if (this.getLevel() != null) {
			this.setChanged();
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2 | 4);
			
			if (forceUpdate) {
				if (!this.getLevel().isClientSide()) {
					this.getLevel().setBlockAndUpdate(this.getBlockPos(), ((AbstractBlockPocket) this.getBlockState().getBlock()).updateState(this.getBlockState(), this.getBlockPos(), level));
					
					if (this.getPocket() != null) {
						this.getPocket().updateBaseConnectors(this.getLevel());
					}
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
		
		compound.putInt("side_guide", this.getSideGuide().getIndex());

		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);

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
		
		this.setSideGuide(EnumSideGuide.getStateFromIndex(compound.getInt("side_guide")));
		
		this.inventoryItems = NonNullList.withSize(2, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}
	
	//Set the data once it has been received. [NBT > TE]
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
	}
	
	//Retrieve the data to be stored. [TE > NBT]
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag, provider);
		return tag;
	}
	
	//Actually sends the data to the server. [NBT > SER]
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	//Method is called once packet has been received by the client. [SER > CLT]
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
		
		this.sendUpdates(true);
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, AbstractBlockEntityPocket entityIn) {		
		if (entityIn.getPocket() != null) {
			entityIn.checkFluidSlots();
			entityIn.getPocket().setSurroundingStacks(levelIn, posIn);
		}
		
		if (!levelIn.isClientSide()) {
			entityIn.pushEnergy(Direction.DOWN);
			entityIn.pushEnergy(Direction.UP);
			entityIn.pushEnergy(Direction.NORTH);
			entityIn.pushEnergy(Direction.SOUTH);
			entityIn.pushEnergy(Direction.EAST);
			entityIn.pushEnergy(Direction.WEST);
			
			entityIn.pullEnergy(Direction.DOWN);
			entityIn.pullEnergy(Direction.UP);
			entityIn.pullEnergy(Direction.NORTH);
			entityIn.pullEnergy(Direction.SOUTH);
			entityIn.pullEnergy(Direction.EAST);
			entityIn.pullEnergy(Direction.WEST);

			entityIn.pushFluid(Direction.DOWN);
			entityIn.pushFluid(Direction.UP);
			entityIn.pushFluid(Direction.NORTH);
			entityIn.pushFluid(Direction.SOUTH);
			entityIn.pushFluid(Direction.EAST);
			entityIn.pushFluid(Direction.WEST);
			
			entityIn.pullFluid(Direction.DOWN);
			entityIn.pullFluid(Direction.UP);
			entityIn.pullFluid(Direction.NORTH);
			entityIn.pullFluid(Direction.SOUTH);
			entityIn.pullFluid(Direction.EAST);
			entityIn.pullFluid(Direction.WEST);
		}
		
//		Arrays.stream(Direction.values()).parallel().forEach((d) -> {
//			BlockPos otherPos = entityIn.getBlockPos().offset(d.getNormal());
//			BlockEntity tile = entityIn.getLevel().getBlockEntity(otherPos);
//
//			if (!(tile instanceof BlockEntityModuleConnector) && !(tile instanceof BlockEntityModuleFurnace) 
//					&& !(tile instanceof BlockEntityModuleArmourWorkbench) && !(tile instanceof BlockEntityModuleCrafter)
//						&& !(tile instanceof BlockEntityModuleCharger)) {
//				
//				if (tile != null && !tile.isRemoved()) {
//					if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_OUTPUT)) {
//						Object object1 = entityIn.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, otherPos, d);
//						
//						if (object1 != null) {
//							if (object1 instanceof IItemHandler storage) {
//								for (int i = 40; i < 48; i++) {
//									if (!(entityIn.getPocket().getItem(i).isEmpty())) {
//										for (int j = 0; j < storage.getSlots(); j++) {
//											if (storage.isItemValid(j, entityIn.getPocket().getItem(i))) {
//												ItemStack stack = storage.getStackInSlot(j);
//												
//												if (stack.getItem().equals(entityIn.getPocket().getItem(i).getItem()) || stack.isEmpty()) {
//													storage.insertItem(j, entityIn.getPocket().removeItem(i, 1), false);
//													entityIn.sendUpdates(true);
//													break;
//												}
//											}
//										}
//									}
//								}
//							}
//						}
//					} else if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_INPUT)) {
//						Object object1 = entityIn.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, otherPos, d);
//						
//						if (object1 != null) {
//							if (object1 instanceof IItemHandler storage) {
//								for (int i = 40; i < 48; i++) {
//									for (int j = 0; j < storage.getSlots(); j++) {
//										ItemStack homeStack = entityIn.getPocket().getItem(i);
//										
//										if (homeStack.getItem().equals(storage.getStackInSlot(j).getItem()) || homeStack.isEmpty()) {
//											if (!storage.extractItem(j, 1, true).isEmpty()) {
//												//System.out.println(storage.getStackInSlot(j) + " | " + j);
//												
//												entityIn.getPocket().insertItem(i, storage.extractItem(j, 1, false), false);
//												entityIn.sendUpdates(true);
//												return;
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		});

		if (entityIn.update > 0) {
			entityIn.update--;
		} else {
			entityIn.update = 40;
			entityIn.sendUpdates(true);
		}
	}

	public void pushEnergy(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);

		if (entity != null && !entity.isRemoved()) {
			if (!(entity instanceof BlockEntityModuleConnector) && !(entity instanceof BlockEntityModuleFurnace) && !(entity instanceof BlockEntityModuleArmourWorkbench) && !(entity instanceof BlockEntityModuleCrafter) && !(entity instanceof BlockEntityModuleCharger)) {
				if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_OUTPUT)) {
					Object object = this.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, otherPos, directionIn.getOpposite());
					
					if (object != null) {
						if (object instanceof IEnergyStorage storage) {
							if (storage.canReceive() && this.canExtract(directionIn)) {
								int extract = this.getPocket().extractEnergy(this.getPocket().getMaxExtract(), true);
								int actualExtract = storage.receiveEnergy(extract, true);
								
								if (actualExtract > 0) {
									this.getPocket().extractEnergy(storage.receiveEnergy(actualExtract, false), false);
									this.sendUpdates(true);
								}
							}
						}
					}
				} else {
					return;
				}
			} else {
				return;
			}
		}
	}

	public void pullEnergy(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);
		
		if (entity != null && !entity.isRemoved()) {
			if (!(entity instanceof BlockEntityModuleConnector) && !(entity instanceof BlockEntityModuleFurnace) && !(entity instanceof BlockEntityModuleArmourWorkbench) && !(entity instanceof BlockEntityModuleCrafter) && !(entity instanceof BlockEntityModuleCharger)) {
				if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_INPUT)) {
					if (this.getPocket().hasEnergyStored() && this.canExtract(directionIn)) {
						Object object = this.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, otherPos, directionIn.getOpposite());
						
						if (object != null) {
							if (object instanceof IEnergyStorage storage) {
		
		
								if (storage.canExtract() && this.canReceive(directionIn)) {
									int extract = storage.extractEnergy(this.getPocket().getMaxReceive(), true);
									int actualExtract = this.getPocket().receiveEnergy(extract, true);
									
									if (actualExtract > 0) {
										this.getPocket().receiveEnergy(storage.extractEnergy(actualExtract, false), false);
										this.sendUpdates(true);
									}
								}
							}
						}
					}
				} else {
					return;
				}
			} else {
				return;
			}
		}
	}
	
	public void pushFluid(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);

		if (entity != null && !entity.isRemoved()) {
			if (!(entity instanceof BlockEntityModuleConnector) && !(entity instanceof BlockEntityModuleFurnace) && !(entity instanceof BlockEntityModuleArmourWorkbench) && !(entity instanceof BlockEntityModuleCrafter) && !(entity instanceof BlockEntityModuleCharger)) {
				if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_OUTPUT)) {
					Object object = this.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, otherPos, directionIn);
				
					if (object != null) {
						if (object instanceof IFluidHandler storage) {
							if (storage.isFluidValid(0, this.getFluidInTank(0))) {
								FluidStack stack = this.drain(1000, FluidAction.SIMULATE);
								int lost = storage.fill(stack, FluidAction.SIMULATE);
								
								if (!stack.isEmpty()) {
									if (lost > 0) {
										storage.fill(stack, FluidAction.EXECUTE);
										this.drain(lost, FluidAction.EXECUTE);
									}
								}
							}
						}
					}
				} else {
					return;
				}
			} else {
				return;
			}
		}
	}

	public void pullFluid(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);
		
		if (entity != null && !entity.isRemoved()) {
			if (!(entity instanceof BlockEntityModuleConnector) && !(entity instanceof BlockEntityModuleFurnace) && !(entity instanceof BlockEntityModuleArmourWorkbench) && !(entity instanceof BlockEntityModuleCrafter) && !(entity instanceof BlockEntityModuleCharger)) {
				if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_INPUT)) {
					Object object = this.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, otherPos, directionIn);
					
					if (object != null) {
						if (object instanceof IFluidHandler storage) {
							FluidStack stack = storage.drain(1000, FluidAction.SIMULATE);
							int lost = this.fill(stack, FluidAction.SIMULATE);
							
							if (!stack.isEmpty()) {
								if (lost > 0) {
									this.fill(stack, FluidAction.EXECUTE);
									storage.drain(lost, FluidAction.EXECUTE);
								}
							}
						}
					}
				}
			} else {
				return;
			}
		} else {
			return;
		}
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player playerIn) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		Pocket pocketIn = this.getPocket();
	
		if (pocketIn != null) {
			if (playerIn.isShiftKeyDown()) {
				if (CosmosUtil.handEmpty(playerIn)) {
					pocketIn.shift(playerIn, EnumShiftDirection.ENTER, pos, levelIn.dimension(), null);
					
					return ItemInteractionResult.SUCCESS;
				} 
				
				else if (CosmosUtil.holdingWrench(playerIn)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						if (this.getLockState()) {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.remove_locked"));
							return ItemInteractionResult.SUCCESS;
						} else {
							if (!levelIn.isClientSide) {
								CompatHelper.spawnStack(this.generateItemStackOnRemoval(pos), levelIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0);
								CosmosUtil.setToAir(levelIn, pos);
								
								ChunkLoadingManager.removeBlockFromChunkLoader(levelIn, pos);
								ChunkLoadingManager.removePocketFromChunkLoader(pocketIn);
							}
							
							return ItemInteractionResult.SUCCESS;
						}
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access_remove"));
						return ItemInteractionResult.FAIL;
					}
				}
			} else {
				if (CosmosUtil.holdingWrench(playerIn)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						if (!levelIn.isClientSide) {
							this.cycleSide(hit.getDirection(), true);
						}
						
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.pocket.status.cycle_side").append(this.getSide(hit.getDirection()).getColouredComp()));
						return ItemInteractionResult.SUCCESS;
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return ItemInteractionResult.FAIL;
					}
				}
				
				else if (CosmosUtil.getStackItem(playerIn) instanceof DyeItem || CosmosUtil.getStackItem(playerIn).equals(PocketsRegistrationManager.DIMENSIONAL_SHARD.get())) {
					if (pocketIn.checkIfOwner(playerIn)) {
						ItemStack stack = CosmosUtil.getStack(playerIn);
						DyeColor dyeColour = DyeColor.getColor(stack);
						
						ComponentColour colour = dyeColour != null ? ComponentColour.fromIndex(dyeColour.getId()) : ComponentColour.POCKET_PURPLE;
						
						if (pocketIn.getDisplayColour() != colour.dec()) {
							pocketIn.setDisplayColour(playerIn, levelIn, colour.dec());
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

				else if (!(CosmosUtil.getStackItem(playerIn) instanceof BlockItem)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						if (playerIn instanceof ServerPlayer serverPlayer) {
							serverPlayer.openMenu(this, (packetBuffer) -> { packetBuffer.writeBlockPos(this.getBlockPos()); });
						}
						
						return ItemInteractionResult.SUCCESS;
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return ItemInteractionResult.SUCCESS;
					}
				}
			}
		} else {
			CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
			return ItemInteractionResult.SUCCESS;
		}
	
		levelIn.sendBlockUpdated(pos, state, state, 3);
		return ItemInteractionResult.FAIL;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}

	@Override
	public void setPlacedBy(Level levelIn, BlockPos posIn, BlockState state, LivingEntity placer, ItemStack stack) {
		boolean update = true;

		//DimensionalPockets.CONSOLE.debug("[Pocket Generation] <> Pocket has been placed");
		
		if (PocketEventFactory.onPocketBlockPlaced(levelIn, placer, this, posIn)) {
			if (!levelIn.isClientSide) {
				if (stack.has(DataComponents.CUSTOM_DATA)) {
					CompoundTag compound = stack.get(DataComponents.CUSTOM_DATA).copyTag();
					
					if (compound.contains("nbt_data")) {
						CompoundTag nbt_tag = compound.getCompound("nbt_data");
						
						int X = nbt_tag.getCompound("chunk_set").getInt("X");
						int Z = nbt_tag.getCompound("chunk_set").getInt("Z");
		
						CosmosChunkPos chunkSet = new CosmosChunkPos(X, Z);
						boolean success = StorageManager.getPocketFromChunkPosition(levelIn, chunkSet) != null;
		
						if (!success) {
							throw new RuntimeException("YOU DESERVED THIS!");
						}
						
						CosmosChunkPos pos = CosmosChunkPos.scaleToChunkPos(this.getBlockPos());
						ResourceKey<Level> dimension = levelIn.dimension();
						
						if (dimension.equals(PocketsDimensionManager.POCKET_WORLD)) {
							if (!pos.equals(chunkSet)) {
								StorageManager.updatePocket(chunkSet, levelIn, posIn);
								
								CompoundTag side_tag = nbt_tag.getCompound("sides");
								
								for(Direction c : Direction.values()) {
									this.setSide(c, EnumSideState.getStateFromIndex(side_tag.getInt("index_" + c.get3DDataValue())), true);
								}
								
								if (placer instanceof Player player) {
									if (player.getItemInHand(InteractionHand.MAIN_HAND) == stack) {
										player.getItemInHand(InteractionHand.MAIN_HAND).setCount(0);
									}
								}
								
								Pocket pocket = this.getPocket();
								pocket.generatePocket((Player) placer, levelIn);
								ChunkLoadingManager.addPocketToChunkLoader(pocket);
								ChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
								update = true;
								
								DimensionalPockets.CONSOLE.debug("[Pocket Generation] <loadedpocket> Pocket has been placed in the Pocket Dimension");
							}
						} else {
							StorageManager.updatePocket(chunkSet, levelIn, posIn);
							
							CompoundTag side_tag = nbt_tag.getCompound("sides");
							
							for(Direction c : Direction.values()) {
								this.setSide(c, EnumSideState.getStateFromIndex(side_tag.getInt("index_" + c.get3DDataValue())), true);
							}
							
							if (placer instanceof Player player) {
								if (player.getItemInHand(InteractionHand.MAIN_HAND) == stack) {
									player.getItemInHand(InteractionHand.MAIN_HAND).setCount(0);
								}
							}
							
							Pocket pocket = this.getPocket();
							pocket.generatePocket((Player) placer, levelIn);
							ChunkLoadingManager.addPocketToChunkLoader(pocket);
							ChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
							update = true;

							DimensionalPockets.CONSOLE.debug("[Pocket Generation] <loadedpocket> Pocket has been placed outside the Pocket Dimension");
						}
					}
				} else {
					BlockPos pos = this.getBlockPos();
					ResourceKey<Level> dimension = levelIn.dimension();
					
					if (dimension.equals(PocketsDimensionManager.POCKET_WORLD)) {
						Pocket pocket = StorageManager.getPocketFromChunkPosition(levelIn, CosmosChunkPos.scaleToChunkPos(pos));
						
						if (pocket.exists()) {
							if (pocket.doesBlockArrayContain(posIn, dimension.location())) {
								pocket.resetSourceBlock();
								DimensionalPockets.CONSOLE.debug("[Pocket Generation Failure] <freshpocket> Pocket has been placed inside of iteself. This has been corrected.");
								
								CompatHelper.spawnStack(this.generateItemStackOnRemoval(pos), levelIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0);
								CosmosUtil.setToAir(levelIn, pos);
								
								update = false;
							} else {
								Pocket pocketA = this.getPocket();
								pocketA.generatePocket((Player) placer, levelIn);
								ChunkLoadingManager.addPocketToChunkLoader(pocketA);
								ChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
								update = true;
	
								DimensionalPockets.CONSOLE.debug("[Pocket Generation] <freshpocket> New Pocket has been generated: " + pocketA.getChunkInfo() + " | FOUND |  Inside the Pocket Dimension.");
							}
						} 
					} else {
						Pocket pocketA = this.getPocket();
						pocketA.generatePocket((Player) placer, levelIn);
						ChunkLoadingManager.addPocketToChunkLoader(pocketA);
						ChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
						update = true;
						
						DimensionalPockets.CONSOLE.debug("[Pocket Generation] <freshpocket> New Pocket has been generated: " + pocketA.getChunkInfo() + " | UNKNOWN |  Outside the Pocket Dimension.");
					}
				}
			}
			
			if (update) {
				this.sendUpdates(true);
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) { }

	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) { }

	@Override
	public BlockState playerWillDestroy(Level levelIn, BlockPos pos, BlockState state, Player player) {
		return state;
	}

	public ItemStack generateItemStackOnRemoval(BlockPos pos) {
		ItemStack itemStack = new ItemStack(this.getType().equals(PocketsRegistrationManager.BLOCK_ENTITY_TYPE_POCKET.get()) ? PocketsRegistrationManager.BLOCK_POCKET.get() : PocketsRegistrationManager.BLOCK_POCKET_ENHANCED.get());

		if (!itemStack.has(DataComponents.CUSTOM_DATA)) {
			itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag()));
		}

		CosmosChunkPos chunkSet = this.getPocket().getDominantChunkPos();
		
		CompoundTag stackTag = new CompoundTag();
		CompoundTag compound = new CompoundTag();
		
		//Saves the chunk data to NBT
		CompoundTag chunk_tag = new CompoundTag();
		chunk_tag.putInt("X", chunkSet.getX());
		chunk_tag.putInt("Z", chunkSet.getZ());

		compound.put("chunk_set", chunk_tag);
		
		Pocket pocket = this.getPocket();
		
		//Saves the side data to NBT
		if (this instanceof IBESided) {
			CompoundTag side_tag = new CompoundTag();
			
			for (Direction c : Direction.values()) {
				side_tag.putInt("index_" + c.get3DDataValue(), this.getSideArray()[c.get3DDataValue()].getIndex());
			}
			
			compound.put("sides", side_tag);
		}
		
		compound.putInt("colour", pocket.getDisplayColour());
		
		stackTag.put("nbt_data", compound);
		itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
		
		return itemStack;
	}

	@Override 
	public EnumSideState getSide(Direction facing) {
		Pocket pocket = this.getPocket();
		
		if (pocket != null) {
			return pocket.getSide(facing);
		}
		
		return EnumSideState.INTERFACE_NORMAL;
	}
	
	@Override
	public void setSide(Direction facing, EnumSideState side_state, boolean update) {
		this.getPocket().setSide(facing, side_state, update);
		
		this.sendUpdates(update);
	}
	
	@Override
	public EnumSideState[] getSideArray() {
		return this.getPocket().getSideArray();
	}

	@Override
	public void setSideArray(EnumSideState[] new_array, boolean update) {
		this.getPocket().setSideArray(new_array, update);
		this.sendUpdates(update);
	}

	@Override
	public void cycleSide(Direction facing, boolean update) {
		this.getPocket().cycleSide(facing, update);	
		this.sendUpdates(update);
	}

	@Override
	public boolean canConnect(Direction direction) {
		return this.getPocket().canConnect(direction);
	}
	
	public EnumSideGuide getSideGuide() {
		return this.SIDE_GUIDE;
	}
	
	public void setSideGuide(EnumSideGuide value) {
		this.SIDE_GUIDE = value;
	}
	
	public void toggleSideGuide() {
		this.SIDE_GUIDE = EnumSideGuide.getOpposite(this.SIDE_GUIDE);
	}
	
	public boolean getSideGuideValue() {
		return this.SIDE_GUIDE.getValue();
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
		int amount = this.getPocket().fill(resource, doFill);
		
		this.setChanged();
		return amount;
	}

	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		FluidStack amount = this.getPocket().drain(resource.getAmount(), doDrain);
		this.setChanged();
		return amount;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		FluidStack amount = this.getPocket().drain(maxDrain, doDrain);
		this.setChanged();
		return amount;
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

	@Override
	public int getFluidFillLevel() {
		return this.getPocket().getFluidFillLevel();
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
			if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_INPUT) || this.getSide(directionIn).equals(EnumSideState.INTERFACE_NORMAL)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int indexIn, ItemStack itemStackIn, Direction directionIn) {
		if (indexIn > 39 && indexIn < 48) {
			if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_OUTPUT)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction arg0) {
		return new int[] { 40, 41, 42, 43, 44, 45, 46, 47 };
	}
	
	public boolean canExtract(Direction dir) {
		EnumSideState state = this.getSide(dir);
		
		if (state.equals(EnumSideState.INTERFACE_OUTPUT) || state.equals(EnumSideState.INTERFACE_NORMAL)) {
			return this.getPocket().canExtractEnergy();
		} else {
			return false;
		}
	}

	public boolean canReceive(Direction dir) {
		EnumSideState state = this.getSide(dir.getOpposite());
		
		if (state.equals(EnumSideState.INTERFACE_INPUT) || state.equals(EnumSideState.INTERFACE_NORMAL)) {
			return this.getPocket().canReceiveEnergy();
		} else {
			return false;
		}
	}

	@Override
	public IFluidHandler getFluidCapability(@Nullable Direction directionIn) {
		return new IFluidHandler() {

			@Override
			public int getTanks() {
				return AbstractBlockEntityPocket.this.getPocket().getTanks();
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				return AbstractBlockEntityPocket.this.getPocket().getFluidInTank();
			}

			@Override
			public int getTankCapacity(int tank) {
				return AbstractBlockEntityPocket.this.getPocket().getFluidTankCapacity();
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (AbstractBlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return false;
				}
				
				return true;
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (AbstractBlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return 0;
				}
				
				return AbstractBlockEntityPocket.this.getPocket().fill(resource, action);
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (AbstractBlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return FluidStack.EMPTY;
				}
				
				AbstractBlockEntityPocket.this.sendUpdates(true);
				return AbstractBlockEntityPocket.this.getPocket().drain(resource, action);
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (AbstractBlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return FluidStack.EMPTY;
				}
				
				AbstractBlockEntityPocket.this.sendUpdates(true);
				return AbstractBlockEntityPocket.this.getPocket().drain(maxDrain, action);
			}
			
		};
	}
	
	public IEnergyStorage getEnergyCapability(@Nullable Direction directionIn) {
        return new IEnergyStorage() {
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
            	AbstractBlockEntityPocket.this.sendUpdates(true);
                return AbstractBlockEntityPocket.this.getPocket().extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return AbstractBlockEntityPocket.this.getPocket().getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return AbstractBlockEntityPocket.this.getPocket().getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
            	AbstractBlockEntityPocket.this.sendUpdates(true);
                return AbstractBlockEntityPocket.this.getPocket().receiveEnergy(maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return AbstractBlockEntityPocket.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return AbstractBlockEntityPocket.this.canExtract(directionIn);
            }
        };
    }
/*
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
					return handlers[3].cast()
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
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
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
	public Component getDisplayName() {
		return null;
	}
	
	public boolean getIsSingleChunk() {
		return this.isSingleChunk;
	}
}