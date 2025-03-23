package com.tcn.dimensionalpocketsii.pocket.core.blockentity;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.interfaces.IBlockEntityClientUpdated;
import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumSideGuide;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.IFluidStorage;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntitySided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.DimReference;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerPocket;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockPocket;
import com.tcn.dimensionalpocketsii.pocket.core.chunkloading.PocketChunkLoadingManager;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkHooks;

public class BlockEntityPocket extends CosmosBlockEntityUpdateable implements IBlockNotifier, IBlockInteract, WorldlyContainer, IBlockEntitySided, MenuProvider, Nameable, Container, IFluidHandler, IFluidStorage, IBlockEntityClientUpdated.FluidTile, IBlockEntityUIMode {
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	private EnumSideGuide SIDE_GUIDE = EnumSideGuide.HIDDEN;
	
	private Pocket pocket;
	private int update = 0;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityPocket(BlockPos posIn, BlockState stateIn) {
		super(ModBusManager.BLOCK_ENTITY_TYPE_POCKET, posIn, stateIn);
	}
	
	public BlockEntityPocket(BlockPos posIn) {
		this(posIn, null);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return PocketRegistryManager.getOrCreatePocket(this.level, this.getBlockPos());
	}
	
	@Override
	public void sendUpdates(boolean forceUpdate) {
		//super.sendUpdates(forceUpdate);
		
		if (level != null) {
			this.setChanged();
			level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2 | 4);
			
			if (forceUpdate) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), ((BlockPocket) this.getBlockState().getBlock()).updateState(this.getBlockState(), this.getBlockPos(), level));
					
					if (this.getPocket() != null) {
						this.getPocket().updateBaseConnector(this.getLevel());
					}
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		if (this.getPocket().exists()) {
			this.getPocket().writeToNBT(compound);
		}
		
		compound.putInt("side_guide", this.getSideGuide().getIndex());

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
		
		this.setSideGuide(EnumSideGuide.getStateFromIndex(compound.getInt("side_guide")));
		
		this.inventoryItems = NonNullList.withSize(2, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}
	
	//Set the data once it has been received. [NBT > TE]
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	//Retrieve the data to be stored. [TE > NBT]
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag);
		return tag;
	}
	
	//Actually sends the data to the server. [NBT > SER]
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	//Method is called once packet has been received by the client. [SER > CLT]
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag_ = pkt.getTag();
		this.handleUpdateTag(tag_);
	}
	
	@Override
	public void onLoad() {
		if (!this.inventoryItems.get(0).getItem().equals(Items.BUCKET)) {
			this.inventoryItems.clear();
		}
		
		this.sendUpdates(true);
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityPocket entityIn) {
		if (entityIn.getPocket() != null) {
			entityIn.checkFluidSlots();
			entityIn.getPocket().setSurroundingStacks(levelIn, posIn);
		}
		
		Arrays.stream(Direction.values()).parallel().forEach((d) -> {
			BlockEntity tile = entityIn.getLevel().getBlockEntity(entityIn.getBlockPos().offset(d.getNormal()));

			if (!(tile instanceof BlockEntityModuleConnector) && !(tile instanceof BlockEntityModuleFurnace) 
					&& !(tile instanceof BlockEntityModuleArmourWorkbench) && !(tile instanceof BlockEntityModuleCrafter)
						&& !(tile instanceof BlockEntityModuleCharger)) {
				
				if (tile != null && !tile.isRemoved()) {
					if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_OUTPUT)) {
						if (tile.getCapability(CapabilityEnergy.ENERGY, d).resolve().isPresent()) {
							LazyOptional<?> consumer = tile.getCapability(CapabilityEnergy.ENERGY, d);

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
						} 
						
						if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, d).resolve().isPresent()) {
							LazyOptional<?> consumer = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, d);

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
						}
						
						if (tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, d).resolve().isPresent()) {
							LazyOptional<?> consumer = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, d);
							
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
							}
						}
					} else if (entityIn.getSide(d).equals(EnumSideState.INTERFACE_INPUT)) {
						if (tile.getCapability(CapabilityEnergy.ENERGY, d).resolve().isPresent()) {
							LazyOptional<?> consumer = tile.getCapability(CapabilityEnergy.ENERGY, d);

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
						
						if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, d).resolve().isPresent()) {
							LazyOptional<?> consumer = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, d);

							if (consumer.resolve().get() instanceof IItemHandler) {
								IItemHandler storage = (IItemHandler) consumer.resolve().get();
								
								for (int i = 40; i < 48; i++) {
									for (int j = 0; j < storage.getSlots(); j++) {
										ItemStack homeStack = entityIn.getPocket().getItem(i);
										
										if (homeStack.getItem().equals(storage.getStackInSlot(j).getItem()) || homeStack.isEmpty()) {
											if (!storage.extractItem(j, 1, true).isEmpty()) {
												//System.out.println(storage.getStackInSlot(j) + " | " + j);
												
												entityIn.getPocket().insertItem(i, storage.extractItem(j, 1, false), false);
												entityIn.sendUpdates(true);
												return;
											}
										}
									}
								}
							}
						}
						
						if (tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, d).resolve().isPresent()) {
							LazyOptional<?> consumer = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, d);
							
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
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player playerIn) { }

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		Pocket pocketIn = this.getPocket();
	
		if (pocketIn != null) {
			if (playerIn.isShiftKeyDown()) {
				if (CosmosUtil.handEmpty(playerIn)) {
					pocketIn.shift(playerIn, EnumShiftDirection.ENTER, pos, levelIn.dimension(), null);
					
					return InteractionResult.SUCCESS;
				} 
				
				else if (CosmosUtil.holdingWrench(playerIn)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						if (this.getLockState()) {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.remove_locked"));
							return InteractionResult.SUCCESS;
						} else {
							if (!levelIn.isClientSide) {
								CompatHelper.spawnStack(this.generateItemStackOnRemoval(pos), levelIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0);
								CosmosUtil.setToAir(levelIn, pos);
								
								PocketChunkLoadingManager.removeBlockFromChunkLoader(levelIn, pos);
								PocketChunkLoadingManager.removePocketFromChunkLoader(pocketIn);
							}
							
							return InteractionResult.SUCCESS;
						}
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access_remove"));
						return InteractionResult.FAIL;
					}
				}
			} else {
				if (CosmosUtil.holdingWrench(playerIn)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						if (!levelIn.isClientSide) {
							this.cycleSide(hit.getDirection(), true);
						}
						
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.pocket.status.cycle_side").append(this.getSide(hit.getDirection()).getColouredComp()));
						return InteractionResult.SUCCESS;
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return InteractionResult.FAIL;
					}
				}
				
				else if (CosmosUtil.getStackItem(playerIn) instanceof DyeItem || CosmosUtil.getStackItem(playerIn).equals(ModBusManager.DIMENSIONAL_SHARD)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						ItemStack stack = CosmosUtil.getStack(playerIn);
						DyeColor dyeColour = DyeColor.getColor(stack);
						
						ComponentColour colour = dyeColour != null ? ComponentColour.fromIndex(dyeColour.getId()) : ComponentColour.POCKET_PURPLE;
						
						if (pocketIn.getDisplayColour() != colour.dec()) {
							pocketIn.setDisplayColour(playerIn, levelIn, colour.dec());
							this.sendUpdates(true);
							
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.pocket.status.colour_update").append(colour.getColouredName()));
							return InteractionResult.SUCCESS;
						} else {
							return InteractionResult.FAIL;
						}
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return InteractionResult.FAIL;
					}
				}

				else if (!(CosmosUtil.getStackItem(playerIn) instanceof BlockItem)) {
					if (pocketIn.checkIfOwner(playerIn)) {
						if (playerIn instanceof ServerPlayer) {
							NetworkHooks.openGui((ServerPlayer) playerIn, this, (packetBuffer) -> { packetBuffer.writeBlockPos(this.getBlockPos()); });
						}
						
						return InteractionResult.SUCCESS;
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return InteractionResult.SUCCESS;
					}
				}
			}
		} else {
			CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
			return InteractionResult.SUCCESS;
		}
	
		levelIn.sendBlockUpdated(pos, state, state, 3);
		return InteractionResult.FAIL;
	}

	@Override
	public void setPlacedBy(Level levelIn, BlockPos posIn, BlockState state, LivingEntity placer, ItemStack stack) {
		boolean update = true;
		
		if (!levelIn.isClientSide) {
			if (stack.hasTag()) {
				CompoundTag compound = stack.getTag();
				
				if (compound.contains("nbt_data")) {
					CompoundTag nbt_tag = compound.getCompound("nbt_data");
					
					int X = nbt_tag.getCompound("chunk_set").getInt("X");
					int Z = nbt_tag.getCompound("chunk_set").getInt("Z");
	
					CosmosChunkPos chunkSet = new CosmosChunkPos(X, Z);
					boolean success = PocketRegistryManager.getPocketFromChunkPosition(chunkSet) != null;
	
					if (!success) {
						throw new RuntimeException("YOU DESERVED THIS!");
					}
					
					CosmosChunkPos pos = CosmosChunkPos.scaleToChunkPos(this.getBlockPos());
					ResourceKey<Level> dimension = levelIn.dimension();
					
					if (dimension.equals(DimensionManager.POCKET_WORLD)) {
						if (!pos.equals(chunkSet)) {
							PocketRegistryManager.updatePocket(chunkSet, levelIn.dimension(), posIn);
							
							CompoundTag side_tag = nbt_tag.getCompound("sides");
							
							for(Direction c : Direction.values()) {
								this.setSide(c, EnumSideState.getStateFromIndex(side_tag.getInt("index_" + c.get3DDataValue())), true);
							}
							
							if (placer instanceof Player) {
								ServerPlayer player = (ServerPlayer) placer;
								if (player.getItemInHand(InteractionHand.MAIN_HAND) == stack) {
									player.getItemInHand(InteractionHand.MAIN_HAND).setCount(0);
								}
							}
							
							Pocket pocket = this.getPocket();
							pocket.generatePocket((Player) placer);
							PocketChunkLoadingManager.addPocketToChunkLoader(pocket);
							PocketChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
							update = true;
						}
					} else {
						PocketRegistryManager.updatePocket(chunkSet, levelIn.dimension(), posIn);
						
						CompoundTag side_tag = nbt_tag.getCompound("sides");
						
						for(Direction c : Direction.values()) {
							this.setSide(c, EnumSideState.getStateFromIndex(side_tag.getInt("index_" + c.get3DDataValue())), true);
						}
						
						if (placer instanceof Player) {
							ServerPlayer player = (ServerPlayer) placer;
							if (player.getItemInHand(InteractionHand.MAIN_HAND) == stack) {
								player.getItemInHand(InteractionHand.MAIN_HAND).setCount(0);
							}
						}
						
						Pocket pocket = this.getPocket();
						pocket.generatePocket((Player) placer);
						PocketChunkLoadingManager.addPocketToChunkLoader(pocket);
						PocketChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
						update = true;
					}
				}
			} else {
				BlockPos pos = this.getBlockPos();
				Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(CosmosChunkPos.scaleToChunkPos(pos));
				
				if (pocket.exists()) {
					ResourceKey<Level> dimension = levelIn.dimension();
					
					if (dimension.equals(DimensionManager.POCKET_WORLD)) {
						if (pocket.doesBlockArrayContain(posIn, dimension.location())) {
							pocket.resetSourceBlock();
							DimensionalPockets.CONSOLE.info("[Pocket Generation Failure] <freshpocket> Pocket has been placed inside of iteself. This has been corrected.");
							
							CompatHelper.spawnStack(this.generateItemStackOnRemoval(pos), levelIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0);
							CosmosUtil.setToAir(levelIn, pos);
							
							update = false;
						} else {
							Pocket pocketA = this.getPocket();
							pocketA.generatePocket((Player) placer);
							PocketChunkLoadingManager.addPocketToChunkLoader(pocketA);
							PocketChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
							update = true;
						}
						
						/*
						if (pocket.getChunkPos().equals(CosmosChunkPos.scaleToChunkPos(pos))) {
							pocket.resetSourceBlock();
							DimensionalPockets.CONSOLE.info("OH NOOOOOOO");
							
							CompatHelper.spawnStack(this.generateItemStackOnRemoval(pos), levelIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0);
							CosmosUtil.setToAir(levelIn, pos);
							
							update = false;
						}*/
					} else {
						Pocket pocketA = this.getPocket();
						pocketA.generatePocket((Player) placer);
						PocketChunkLoadingManager.addPocketToChunkLoader(pocket);
						PocketChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
						update = true;
					}
				} else {
					Pocket pocketA = this.getPocket();
					pocketA.generatePocket((Player) placer);
					PocketChunkLoadingManager.addPocketToChunkLoader(pocket);
					PocketChunkLoadingManager.addBlockToChunkLoader(levelIn, posIn);
					update = true;
				}
			}
		}
		
		if (update) {
			this.sendUpdates(true);
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) { }

	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) { }

	@Override
	public void playerWillDestroy(Level levelIn, BlockPos pos, BlockState state, Player player) { }

	public ItemStack generateItemStackOnRemoval(BlockPos pos) {
		ItemStack itemStack = new ItemStack(ModBusManager.BLOCK_POCKET);

		if (!itemStack.hasTag()) {
			itemStack.setTag(new CompoundTag());
		}

		CosmosChunkPos chunkSet = this.getPocket().getChunkPos();
		
		CompoundTag compound = new CompoundTag();
		
		//Saves the chunk data to NBT
		CompoundTag chunk_tag = new CompoundTag();
		chunk_tag.putInt("X", chunkSet.getX());
		chunk_tag.putInt("Z", chunkSet.getZ());

		compound.put("chunk_set", chunk_tag);
		
		Pocket pocket = this.getPocket();
		
		//Saves the side data to NBT
		if (this instanceof IBlockEntitySided) {
			CompoundTag side_tag = new CompoundTag();
			
			for (Direction c : Direction.values()) {
				side_tag.putInt("index_" + c.get3DDataValue(), this.getSideArray()[c.get3DDataValue()].getIndex());
			}
			
			compound.put("sides", side_tag);
		}
		
		compound.putInt("colour", pocket.getDisplayColour());
		itemStack.getTag().put("nbt_data", compound);
		
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
	 * - TODO IFluid Start
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
								
								this.sendUpdates(true);
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
								
								this.sendUpdates(true);
							}
						}
					
					}
				}
				
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int indexIn, Inventory playerInventoryIn, Player playerIn) {
		return ContainerPocket.createContainerServerSide(indexIn, playerInventoryIn, this.getPocket(), this, this.getBlockPos());
	}
	
	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.pocket.header");
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.pocket.header");
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
		return DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH;
	}

	@Override
	public ItemStack getItem(int index) {
		if (index < DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			return this.getPocket().getItem(index);
		} else {
			return this.inventoryItems.get(index - DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH);
		}
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.setChanged();
		
		if (index < DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			return this.getPocket().removeItem(index, count);
		} else {
			return ContainerHelper.removeItem(inventoryItems, index - DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH, count);
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.setChanged();
		
		if (index < DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			return this.getPocket().removeItemNoUpdate(index);
		} else {
			return ContainerHelper.takeItem(this.inventoryItems, index - DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH);
		}
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
		if (index < DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH) {
			this.getPocket().setItem(index, stack);
		} else {
			this.inventoryItems.set(index - DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH, stack);
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
		
		if (state.equals(EnumSideState.INTERFACE_OUTPUT)) {
			return this.getPocket().canExtractEnergy();
		} else {
			return false;
		}
	}

	public boolean canReceive(Direction dir) {
		EnumSideState state = this.getSide(dir.getOpposite());
		
		if (state.equals(EnumSideState.INTERFACE_INPUT)) {
			return this.getPocket().canReceiveEnergy();
		} else {
			return false;
		}
	}

	private LazyOptional<IFluidHandler> createFluidProxy(@Nullable Direction directionIn) {
		return LazyOptional.of(() -> new IFluidHandler() {

			@Override
			public int getTanks() {
				return BlockEntityPocket.this.getPocket().getTanks();
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				return BlockEntityPocket.this.getPocket().getFluidInTank();
			}

			@Override
			public int getTankCapacity(int tank) {
				return BlockEntityPocket.this.getPocket().getFluidTankCapacity();
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (BlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return false;
				}
				
				return true;
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (BlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return 0;
				}
				
				return BlockEntityPocket.this.getPocket().fill(resource, action);
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (BlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return FluidStack.EMPTY;
				}
				
				BlockEntityPocket.this.sendUpdates(true);
				return BlockEntityPocket.this.getPocket().drain(resource, action);
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (BlockEntityPocket.this.getSide(directionIn.getOpposite()).equals(EnumSideState.DISABLED)) {
					return FluidStack.EMPTY;
				}
				
				BlockEntityPocket.this.sendUpdates(true);
				return BlockEntityPocket.this.getPocket().drain(maxDrain, action);
			}
			
		});
	}

	private LazyOptional<IEnergyStorage> createEnergyProxy(@Nullable Direction directionIn) {
        return LazyOptional.of(() -> new IEnergyStorage() {
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
            	BlockEntityPocket.this.sendUpdates(true);
                return BlockEntityPocket.this.getPocket().extractEnergy(maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityPocket.this.getPocket().getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityPocket.this.getPocket().getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
            	BlockEntityPocket.this.sendUpdates(true);
                return BlockEntityPocket.this.getPocket().receiveEnergy(maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityPocket.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityPocket.this.canExtract(directionIn);
            }
        });
    }

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction directionIn) {
		if (!this.remove && directionIn != null) {
			if (capability == CapabilityEnergy.ENERGY) {
				return this.createEnergyProxy(directionIn).cast();
			} else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return this.createFluidProxy(directionIn).cast();
			} else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		
		for (int x = 0; x < handlers.length; x++) {
			handlers[x].invalidate();
		}
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