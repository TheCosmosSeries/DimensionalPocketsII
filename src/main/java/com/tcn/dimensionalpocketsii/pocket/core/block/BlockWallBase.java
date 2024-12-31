package com.tcn.dimensionalpocketsii.pocket.core.block;

import com.tcn.cosmoslibrary.common.block.CosmosBlockUnbreakable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.PocketsConfigManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleAnvil;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockWallBase extends CosmosBlockUnbreakable {
	
	public BlockWallBase(Block.Properties prop) {
		super(prop);
	}
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		if (PocketUtil.isDimensionEqual(levelIn, PocketsDimensionManager.POCKET_WORLD)) {
			Pocket pocket = StorageManager.getPocketFromChunkPosition(levelIn, CosmosChunkPos.scaleToChunkPos(pos));
			
			if (pocket.exists()) {
				if (!playerIn.isShiftKeyDown()) {
					return ItemInteractionResult.FAIL;
				} else {
					if (!playerIn.getItemInHand(handIn).isEmpty()) {
						if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_FOCUS.get())) {
							if (pos.getY() == 1) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_FOCUS.get().defaultBlockState());
										
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}
									
									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}
						}
						
						else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_GLASS.get())) {
							if (pos.getY() == pocket.getInternalHeight()) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_GLASS.get().defaultBlockState());
										
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}
						}

						else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_CRAFTER.get())) {
							if (pocket.checkIfOwner(playerIn)) {
								if (!levelIn.isClientSide) {
									levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_CRAFTER.get().defaultBlockState());
	
									if (!playerIn.isCreative()) {
										playerIn.getInventory().getSelected().shrink(1);
									}
								}

								return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
								return ItemInteractionResult.FAIL;
							}
						}
						
						else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_CONNECTOR.get())) {
							if (pocket.checkIfOwner(playerIn)) {
								if (!levelIn.isClientSide) {
									levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_CONNECTOR.get().defaultBlockState());
									
									if (!playerIn.isCreative()) {
										playerIn.getInventory().getSelected().shrink(1);
									}
								}
								
								if (!levelIn.isClientSide) {
									pocket.addUpdateable(pos);
								}

								return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
								return ItemInteractionResult.FAIL;
							}
						}
						
						else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_CREATIVE_ENERGY.get())) {
							if (pocket.checkIfOwner(playerIn)) {
								if (!levelIn.isClientSide) {
									levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_CREATIVE_ENERGY.get().defaultBlockState());
									
									if (!playerIn.isCreative()) {
										playerIn.getInventory().getSelected().shrink(1);
									}
								}

								return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
								return ItemInteractionResult.FAIL;
							}
						}

						else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_CREATIVE_FLUID.get())) {
							if (pocket.checkIfOwner(playerIn)) {
								if (!levelIn.isClientSide) {
									levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_CREATIVE_FLUID.get().defaultBlockState());
									
									if (!playerIn.isCreative()) {
										playerIn.getInventory().getSelected().shrink(1);
									}
								}

								return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
								return ItemInteractionResult.FAIL;
							}
						}
						
						if (pos.getY() != 1 && pos.getY() != pocket.getInternalHeight()) {
							if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_CHARGER.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_CHARGER.get().defaultBlockState());
	
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleCharger) {
											((BlockEntityModuleCharger) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn), levelIn.registryAccess());
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
										pocket.addUpdateable(pos);
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}

							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_SMITHING_TABLE.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_SMITHING_TABLE.get().defaultBlockState());
	
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleSmithingTable) {
											((BlockEntityModuleSmithingTable) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn));
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}

							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_UPGRADE_STATION.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_UPGRADE_STATION.get().defaultBlockState());
										
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleUpgradeStation) {
											((BlockEntityModuleUpgradeStation) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn), levelIn.registryAccess());
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}
							
							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_FURNACE.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_FURNACE.get().defaultBlockState());
										
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleFurnace) {
											((BlockEntityModuleFurnace) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn), levelIn.registryAccess());
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}
							
							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_BLAST_FURNACE.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_BLAST_FURNACE.get().defaultBlockState());
										
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleBlastFurnace) {
											((BlockEntityModuleBlastFurnace) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn), levelIn.registryAccess());
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}

							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_ENERGY_DISPLAY.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, ((BlockWallEnergyDisplay) PocketsRegistrationManager.BLOCK_WALL_ENERGY_DISPLAY.get()).updateState(state, pos, levelIn));
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
	
										pocket.addUpdateable(pos);
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}

							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_FLUID_DISPLAY.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_FLUID_DISPLAY.get().defaultBlockState());

										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
	
										pocket.addUpdateable(pos);
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}

							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_ARMOUR_WORKBENCH.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_ARMOUR_WORKBENCH.get().defaultBlockState());
	
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleArmourWorkbench) {
											((BlockEntityModuleArmourWorkbench) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn), levelIn.registryAccess());
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}

							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_GENERATOR.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_GENERATOR.get().defaultBlockState());
	
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleGenerator) {
											((BlockEntityModuleGenerator) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn), levelIn.registryAccess());
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}
							
							else if (CosmosUtil.handItem(playerIn, PocketsRegistrationManager.MODULE_ANVIL.get())) {
								if (pocket.checkIfOwner(playerIn)) {
									if (!levelIn.isClientSide) {
										levelIn.setBlockAndUpdate(pos, PocketsRegistrationManager.BLOCK_WALL_ANVIL.get().defaultBlockState());
	
										BlockEntity entity = levelIn.getBlockEntity(pos);
										
										if (entity instanceof BlockEntityModuleAnvil) {
											((BlockEntityModuleAnvil) levelIn.getBlockEntity(pos)).loadFromItemStack(CosmosUtil.getStack(playerIn));
										}
	
										if (!playerIn.isCreative()) {
											playerIn.getInventory().getSelected().shrink(1);
										}
									}

									return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.not_owner"));
									return ItemInteractionResult.FAIL;
								}
							}
						}
					} else {
						if (CosmosUtil.handEmpty(playerIn)) {
							pocket.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
							return ItemInteractionResult.SUCCESS;
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
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		//DebugPacketSender.func_218806_a(worldIn, pos);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (PocketsConfigManager.getInstance().getCanDestroyWalls()) {
			return this.defaultBlockState();
		}
		
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult result, LevelReader reader, BlockPos posIn, Player playerIn) {
		if (PocketsConfigManager.getInstance().getCanDestroyWalls()) {
			return new ItemStack(this);
		} else {
			return ItemStack.EMPTY;
		}
    }
}