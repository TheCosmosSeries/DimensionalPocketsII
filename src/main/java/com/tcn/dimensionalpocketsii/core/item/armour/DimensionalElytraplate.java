package com.tcn.dimensionalpocketsii.core.item.armour;

import java.util.ArrayList;
import java.util.List;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.energy.CosmosEnergyUtil;
import com.tcn.cosmoslibrary.energy.interfaces.IEnergyStorageBulk;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyArmourItemElytra;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyStorageItem;
import com.tcn.dimensionalpocketsii.core.item.armour.module.EnumElytraModule;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.AbstractBlockEntityPocket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class DimensionalElytraplate extends CosmosEnergyArmourItemElytra {

	public DimensionalElytraplate(Holder<ArmorMaterial> materialIn, Type typeIn, Item.Properties builderIn, boolean damageableIn, CosmosEnergyItem.Properties energyProperties) {
		super(materialIn, typeIn, builderIn, damageableIn, energyProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.elytraplate"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.item.info.elytraplate_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.item.info.elytraplate_two"));

			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.item.info.elytraplate_three")
				.append(ComponentHelper.style(ComponentColour.YELLOW, PocketsRegistrationManager.SUIT_SETTINGS.getKey().getName()))
				.append(ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.item.info.elytraplate_key"))
			);
			tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.item.info.elytraplate_usage"));
			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.item.info.elytraplate_limitation"));
			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.item.info.elytraplate_limitation_combat"));
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		
		if (stack.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			
			if (tag.contains("nbt_data") || tag.contains("moduleList")) {
				CompoundTag nbt_data = tag.getCompound("nbt_data");
				
				if (!ComponentHelper.isControlKeyDown(Minecraft.getInstance())) {
					tooltip.add(ComponentHelper.ctrlForMoreDetails());
				} else {
					if (nbt_data.contains("chunk_pos")) {
						CompoundTag pos_tag = nbt_data.getCompound("chunk_pos");
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter.pocket").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_BLUE + pos_tag.getInt("x") + Value.LIGHT_GRAY + ", " + Value.BRIGHT_BLUE + pos_tag.getInt("z") + Value.LIGHT_GRAY + " ]")));
					}
				
					if (nbt_data.contains("player_pos")) {
						CompoundTag player_pos = nbt_data.getCompound("player_pos");
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter_player_pos").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + player_pos.getInt("x") + Value.LIGHT_GRAY + ", " + Value.CYAN + player_pos.getInt("y") + Value.LIGHT_GRAY + ", " + Value.CYAN + player_pos.getInt("z") + Value.LIGHT_GRAY + " ]")));
									
					}
					
					if (nbt_data.contains("dimension")) {
						CompoundTag dimension = nbt_data.getCompound("dimension");
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter_source_dimension").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_GREEN + dimension.getString("namespace") + Value.LIGHT_GRAY + ": " + Value.BRIGHT_GREEN + dimension.getString("path") + Value.LIGHT_GRAY + " ]")));
					}
					
					tooltip.add(ComponentHelper.ctrlForLessDetails());
				}
				
				if (!ComponentHelper.isAltKeyDown(Minecraft.getInstance())) {
					tooltip.add(ComponentHelper.altForMoreDetails(ComponentColour.POCKET_PURPLE_LIGHT));
				} else {
					if (tag.contains("settings_data")) {
						tooltip.add(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.item.info.elytraplate_settings"));
						
						for (int i = 0; i < EnumElytraSetting.LENGTH; i++) {
							EnumElytraSetting setting = EnumElytraSetting.getStateFromIndex(i);
							tooltip.add(ComponentHelper.comp("  ").append(setting.getColouredDisplayComp().append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ ").append(setting.getValueComp(DimensionalElytraplate.getElytraSetting(stack, setting))).append(ComponentHelper.comp(Value.LIGHT_GRAY + " ]")))));
						}
					}
					
					if (!(DimensionalElytraplate.getInstalledModules(stack).isEmpty())) {
						ArrayList<EnumElytraModule> list = DimensionalElytraplate.getInstalledModules(stack);
						tooltip.add(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.item.info.elytraplate_modules"));
						
						for (int i = 0; i < list.size(); i++) {
							tooltip.add(ComponentHelper.comp("  ").append(list.get(i).getColouredComp()));
						}
					}
					
					tooltip.add(ComponentHelper.altForLessDetails(ComponentColour.POCKET_PURPLE_LIGHT));
				}
			}
		}
		
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
	
	@Override
	public void onCraftedBy(ItemStack stackIn, Level levelIn, Player playerIn) {
		if (!DimensionalElytraplate.getElytraSetting(stackIn, EnumElytraSetting.ELYTRA_FLY)) {
			DimensionalElytraplate.addOrUpdateElytraSetting(stackIn, EnumElytraSetting.ELYTRA_FLY, true);
		}
	}

	@Override
	public void inventoryTick(ItemStack stackIn, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!levelIn.isClientSide()) {
			if (entityIn instanceof ServerPlayer serverPlayer) {
				if (itemSlot == 38) {
					if (this.hasEnergy(stackIn)) {
						if (DimensionalElytraplate.hasModuleInstalled(stackIn, EnumElytraModule.BATTERY)) {
							if (DimensionalElytraplate.getElytraSetting(stackIn, EnumElytraSetting.CHARGER)) {
								Inventory inv = serverPlayer.getInventory();
								
								for (int i = 0; i < inv.getContainerSize(); i++) {
									ItemStack testStack = inv.getItem(i);
									Item testItem = testStack.getItem();
									
									if (!(testItem instanceof CosmosEnergyStorageItem) && !(testItem instanceof DimensionalElytraplate)) {
										Object object = testStack.getCapability(Capabilities.EnergyStorage.ITEM);

										if (!(object instanceof IEnergyStorageBulk)) {
											if (object instanceof IEnergyStorage energyItem) {
												if (energyItem.canReceive()) {
													this.extractEnergy(stackIn, energyItem.receiveEnergy(this.getMaxExtract(stackIn), false), false);
												}
											}
										}
									}
								}
							}
						}
					}
					
					if (this.getEnergy(stackIn) < this.getMaxEnergyStored(stackIn)) {
						if (DimensionalElytraplate.hasModuleInstalled(stackIn, EnumElytraModule.SOLAR)) {
							if (DimensionalElytraplate.getElytraSetting(stackIn, EnumElytraSetting.SOLAR)) {
								if (levelIn.canSeeSky(new BlockPos(serverPlayer.blockPosition()))) {
									if (levelIn.isDay()) {
										//MATH BITCH :)
										float energy = ((Mth.clamp(Mth.sin((float) (((levelIn.dayTime() / 1000.0F) * 0.525F) + 4.6F)), 0.0F, 1.0F)) + 1.1F) * 200;
										
										if (this.receiveEnergy(stackIn, (int) energy, true) > 0) {
											this.receiveEnergy(stackIn, (int) energy, false);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int getMaxEnergyStored(ItemStack stackIn) {
		return !(stackIn.getItem() instanceof DimensionalElytraplate elytraItem) ? 0 : DimensionalElytraplate.hasModuleInstalled(stackIn, EnumElytraModule.BATTERY) ? elytraItem.maxEnergyStored * 6 : elytraItem.maxEnergyStored;
	}

	@Override
	public IEnergyStorageBulk getEnergyCapability(ItemStack stackIn) {
		return CosmosEnergyUtil.getDefaultBulk(stackIn, this);
	}
	
	@Override
	public boolean isFlyEnabled(ItemStack stackIn) {
		return !(this.hasEnergy(stackIn)) ? false : DimensionalElytraplate.getElytraSetting(stackIn, EnumElytraSetting.ELYTRA_FLY);
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return this.isFlyEnabled(stack);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return true;
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Level level = context.getLevel();
		BlockEntity entity = level.getBlockEntity(context.getClickedPos());
		
		if (entity != null) {
			if (entity instanceof AbstractBlockEntityPocket blockEntity) {
				Pocket pocket = blockEntity.getPocket();
				
				if (this.addOrUpdateShifterInformation(stack, pocket, level, context.getPlayer())) {
					return InteractionResult.sidedSuccess(level.isClientSide());
				}
			} else {
				return InteractionResult.PASS;
			}
		}
		return InteractionResult.FAIL;
	}
	
	public static boolean addModule(ItemStack stackIn, EnumElytraModule moduleIn, boolean simulate) {
		ArrayList<EnumElytraModule> list = DimensionalElytraplate.getInstalledModules(stackIn);
		
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			if (!DimensionalElytraplate.hasModuleInstalled(stackIn, moduleIn)) {
				if (!simulate) {
					list.add(moduleIn);
					DimensionalElytraplate.addOrUpdateElytraSetting(stackIn, moduleIn.getSetting(), true);
					DimensionalElytraplate.saveModuleList(list, stackIn);
				}
				return true;
			}
		}
		
		return false;
	}
	
	public static ItemStack removeModule(ItemStack stackIn, EnumElytraModule moduleIn, boolean simulate) {
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			if (DimensionalElytraplate.hasModuleInstalled(stackIn, moduleIn)) {
				ArrayList<EnumElytraModule> list = DimensionalElytraplate.getInstalledModules(stackIn);
				
				if (!simulate) {
					list.remove(moduleIn);
					DimensionalElytraplate.saveModuleList(list, stackIn);
				}
				
				return new ItemStack(moduleIn.getModuleItem().asActualItem());
			}
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean hasModuleInstalled(ItemStack stackIn, EnumElytraModule moduleIn) {
		ArrayList<EnumElytraModule> list = DimensionalElytraplate.getInstalledModules(stackIn);
		
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			if (!list.isEmpty()) {
				if (list.contains(moduleIn)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean removeAllModules(ItemStack stackIn, boolean simulate) {
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			if (stackIn.has(DataComponents.CUSTOM_DATA)) {
				CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
				
				if (compound.contains("moduleList")) {
					if (!simulate) {
						compound.remove("moduleList");
						stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
					}
					return true;
				}
			}
		}
		return false;
	}

	public static void saveModuleList(ArrayList<EnumElytraModule> listIn, ItemStack stackIn) {
		CompoundTag stackTag = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		CompoundTag newList = new CompoundTag();
		
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			for (int i = 0; i < listIn.size(); i ++) {
				EnumElytraModule module = listIn.get(i);
				newList.putInt(Integer.toString(i), module.getIndex());
			}
		}
		
		newList.putInt("size", listIn.size());
		stackTag.put("moduleList", newList);
		stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
	}
	
	public static CompoundTag getModuleList(ItemStack stackIn) {
		if (stackIn.getItem() instanceof DimensionalElytraplate elytraplate) {
			CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			if (compound.contains("moduleList")) {
				return compound.getCompound("moduleList");
			}
		}
		return null;
	}
	
	public static ArrayList<EnumElytraModule> getInstalledModules(ItemStack stackIn) {
		ArrayList<EnumElytraModule> list = new ArrayList<EnumElytraModule>();
		CompoundTag compound = DimensionalElytraplate.getModuleList(stackIn);
		
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			if (compound != null) {
				for (int i = 0; i < compound.getInt("size"); i++) {
					list.add(EnumElytraModule.getStateFromIndex(compound.getInt(Integer.toString(i))));
				}
			}
		}
		return list;
	}
	
	public boolean addOrUpdateShifterInformation(ItemStack stackIn, Pocket pocketIn, Level levelIn, Player playerIn) {
		if (pocketIn != null) {
			if (pocketIn.checkIfOwner(playerIn)) {
				CosmosChunkPos chunkPos = pocketIn.getDominantChunkPos();

				int x = chunkPos.getX();
				int z = chunkPos.getZ();

				if (playerIn.isShiftKeyDown()) {
					CompoundTag stackTag = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
					CompoundTag nbtTag = stackTag.contains("nbt_data") ? stackTag.getCompound("nbt_data") : new CompoundTag();
					
					CompoundTag chunkTag = new CompoundTag();
					chunkTag.putInt("x", x);
					chunkTag.putInt("z", z);
					nbtTag.put("chunk_pos", chunkTag);
					
					nbtTag.putInt("colour", pocketIn.getDisplayColour());

					CompoundTag posTag = new CompoundTag();
					BlockPos playerPos = playerIn.blockPosition();
					posTag.putInt("x", playerPos.getX());
					posTag.putInt("y", playerPos.getY());
					posTag.putInt("z", playerPos.getZ());
					posTag.putFloat("yaw", playerIn.getRotationVector().y);
					posTag.putFloat("pitch", playerIn.getRotationVector().x);
					nbtTag.put("player_pos", posTag);

					addOrUpdateElytraSetting(stackIn, EnumElytraSetting.TELEPORT_TO_BLOCK, true);
					
					CompoundTag dimension = new CompoundTag();
					dimension.putString("namespace", levelIn.dimension().location().getNamespace());
					dimension.putString("path", levelIn.dimension().location().getPath());
					nbtTag.put("dimension", dimension);
					
					stackTag.put("nbt_data", nbtTag);
					
					stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
					CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.item.message.elytraplate.linked").append(ComponentHelper.comp(Value.LIGHT_GRAY + " {" + x + ", " + z + "}")));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static Boolean getElytraSetting(ItemStack stackIn, EnumElytraSetting settingIn) {
		CompoundTag compoundIn = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			
		if (compoundIn.contains("settings_data")) {
			CompoundTag settingsData = compoundIn.getCompound("settings_data");
			
			return settingsData.getBoolean(settingIn.getName());
		}
		
		return false;
	}

	public static void addOrUpdateElytraSetting(ItemStack stackIn, EnumElytraSetting settingIn, boolean valueIn) {
		CompoundTag compoundIn = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		
		if (settingIn != null) {
			CompoundTag settingsData = compoundIn.contains("settings_data") ? compoundIn.getCompound("settings_data") : new CompoundTag();
			settingsData.putBoolean(settingIn.getName(), valueIn);
			compoundIn.put("settings_data", settingsData);
			stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundIn));
		}
	}

	public static EnumUIMode getUIMode(ItemStack stackIn) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			return EnumUIMode.getStateFromIndex(stackIn.get(DataComponents.CUSTOM_DATA).copyTag().getInt("mode"));
		}
		return EnumUIMode.DARK;
	}

	public static void setUIMode(ItemStack stackIn, EnumUIMode mode) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		compound.putInt("mode", mode.getIndex());
		stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
	}
	
	public static EnumUIHelp getUIHelp(ItemStack stackIn) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			return EnumUIHelp.getStateFromIndex(stackIn.get(DataComponents.CUSTOM_DATA).copyTag().getInt("help"));
		}
		return EnumUIHelp.HIDDEN;
	}

	public static void setUIHelp(ItemStack stackIn, EnumUIHelp mode) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		compound.putInt("help", mode.getIndex());
		stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
	}
}