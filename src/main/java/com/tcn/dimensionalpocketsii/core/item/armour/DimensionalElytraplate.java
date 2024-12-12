package com.tcn.dimensionalpocketsii.core.item.armour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyArmourItemElytra;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.dimensionalpocketsii.client.renderer.ElytraplateBEWLR;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEnergyCell;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEnergyCellEnhanced;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.AbstractBlockEntityPocket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

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
				.append(ComponentHelper.style(ComponentColour.YELLOW, ModRegistrationManager.SUIT_SETTINGS.getKey().getName()))
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
						
						int x = pos_tag.getInt("x");
						int z = pos_tag.getInt("z");
						
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter.pocket").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_BLUE + x + Value.LIGHT_GRAY + ", " + Value.BRIGHT_BLUE + z + Value.LIGHT_GRAY + " ]")));
					}
				
					if (nbt_data.contains("player_pos")) {
						CompoundTag player_pos = nbt_data.getCompound("player_pos");
						
						int x = player_pos.getInt("x");
						int y = player_pos.getInt("y");
						int z = player_pos.getInt("z");
						
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter_player_pos").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + x + Value.LIGHT_GRAY + ", " + Value.CYAN + y + Value.LIGHT_GRAY + ", " + Value.CYAN + z + Value.LIGHT_GRAY + " ]")));
									
					}
					
					if (nbt_data.contains("dimension")) {
						CompoundTag dimension = nbt_data.getCompound("dimension");
						
						String namespace = dimension.getString("namespace");
						String path = dimension.getString("path");
						
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter_source_dimension").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_GREEN + namespace + Value.LIGHT_GRAY + ": " + Value.BRIGHT_GREEN + path + Value.LIGHT_GRAY + " ]")));
					}
					
					tooltip.add(ComponentHelper.ctrlForLessDetails());
				}
				
				if (!ComponentHelper.isAltKeyDown(Minecraft.getInstance())) {
					tooltip.add(ComponentHelper.altForMoreDetails(ComponentColour.POCKET_PURPLE_LIGHT));
				} else {
					
					if (tag.contains("settings_data")) {
						tooltip.add(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.item.info.elytraplate_settings"));
						
						for (int i = 0; i < ElytraSettings.LENGTH; i++) {
							ElytraSettings setting = ElytraSettings.getStateFromIndex(i);
							
							boolean value = getElytraSetting(stack, setting)[1];
							Component valueComp = setting.getValueComp(value);
							
							tooltip.add(setting.getColouredDisplayComp().append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ ").append(valueComp).append(ComponentHelper.comp(Value.LIGHT_GRAY + " ]"))));
						}
					}
					
					if (!(getInstalledModules(stack).isEmpty())) {
						ArrayList<BaseElytraModule> list = getInstalledModules(stack);
						tooltip.add(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.item.info.elytraplate_modules"));
						
						for (int i = 0; i < list.size(); i++) {
							BaseElytraModule module = list.get(i);
							
							tooltip.add(module.getColouredComp());
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
		if (!getElytraSetting(stackIn, ElytraSettings.ELYTRA_FLY)[0]) {
			addOrUpdateElytraSetting(stackIn, ElytraSettings.ELYTRA_FLY, true);
		}
	}

	@Override
	public void inventoryTick(ItemStack stackIn, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!levelIn.isClientSide) {
			if (entityIn instanceof Player) {
				if (itemSlot == 2) {
					if (this.hasEnergy(stackIn)) {
						if (hasModuleInstalled(stackIn, BaseElytraModule.BATTERY)) {
							if (getElytraSetting(stackIn, ElytraSettings.CHARGER)[1]) {
								if (entityIn instanceof ServerPlayer) {
									ServerPlayer player = (ServerPlayer) entityIn;
									Inventory inv = player.getInventory();
									
									for (int i = 0; i < inv.getContainerSize(); i++) {
										ItemStack testStack = inv.getItem(i);
										Item testItem = testStack.getItem();
										
										if (!(testItem instanceof DimensionalEnergyCell) && !(testItem instanceof DimensionalEnergyCellEnhanced) && !(testItem instanceof DimensionalElytraplate)) {
											if (testItem instanceof ICosmosEnergyItem energyItem) {
												if (energyItem.canReceiveEnergy(testStack)) {
													this.extractEnergy(stackIn, energyItem.receiveEnergy(testStack, Math.min(energyItem.getMaxReceive(testStack), this.getMaxExtract(stackIn)), false), false);
												}
											}
										}
									}
								}
							}
						}
					}
					
					if (this.getEnergy(stackIn) < this.getMaxEnergyStored(stackIn)) {
						if (hasModuleInstalled(stackIn, BaseElytraModule.SOLAR)) {
							if (getElytraSetting(stackIn, ElytraSettings.SOLAR)[1]) {
								if (levelIn.canSeeSky(new BlockPos(entityIn.blockPosition()))) {
									if (levelIn.isDay()) {
										//MATH BITCH :)
										float energy = ((Mth.clamp(Mth.sin((float) (((levelIn.dayTime() / 1000.0F) * 0.525F) + 4.6F)), 0.0F, 1.0F)) + 1.1F) * 200;
										int actual = (int) energy;
										
										if (this.receiveEnergy(stackIn, actual, true) > 0) {
											this.receiveEnergy(stackIn, actual, false);
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
		Item item = stackIn.getItem();
		
		int maxStored = 0;
		
		if (hasModuleInstalled(stackIn, BaseElytraModule.BATTERY)) {
			maxStored = ((DimensionalElytraplate)item).maxEnergyStored * 6;
		} else {
			maxStored = ((DimensionalElytraplate)item).maxEnergyStored;
		}
		
		return !(item instanceof DimensionalElytraplate) ? 0 : maxStored;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return ElytraplateBEWLR.INSTANCE;
			}
		});
	}

	@Override
	public boolean isFlyEnabled(ItemStack stackIn) {
		if (this.hasEnergy(stackIn)) {
			if (getElytraSetting(stackIn, ElytraSettings.ELYTRA_FLY)[1]) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return isFlyEnabled(stack);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return true;
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player playerIn = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		Level world = context.getLevel();
		BlockEntity entity = world.getBlockEntity(pos);
		
		if (entity != null) {
			if (entity instanceof AbstractBlockEntityPocket blockEntity) {
				Pocket pocket = blockEntity.getPocket();
				
				if (this.addOrUpdateShifterInformation(stack, pocket, world, playerIn)) {
					return InteractionResult.SUCCESS;
				}
			} else {
				return InteractionResult.PASS;
			}
		}
		
		return InteractionResult.FAIL;
	}
	
	public static boolean removeAllModules(ItemStack stackIn, boolean simulate) {
		if (stackIn.getItem() instanceof DimensionalElytraplate elytraplate) {
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
	
	public static ItemStack removeModule(ItemStack stackIn, BaseElytraModule moduleIn, boolean simulate) {
		if (stackIn.getItem() instanceof DimensionalElytraplate elytraplate) {
			if (hasModuleInstalled(stackIn, moduleIn)) {
				ArrayList<BaseElytraModule> list = getInstalledModules(stackIn);
				
				if (!simulate) {
					list.remove(moduleIn);
					
					saveModuleList(list, stackIn);
				}
				
				return new ItemStack(moduleIn.getModuleItem());
			}
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean addModule(ItemStack stackIn, BaseElytraModule moduleIn, boolean simulate) {
		ArrayList<BaseElytraModule> list = getInstalledModules(stackIn);
		
		if (stackIn.getItem() instanceof DimensionalElytraplate elytraplate) {
			if (!hasModuleInstalled(stackIn, moduleIn)) {
				if (!simulate) {
					list.add(moduleIn);
					addOrUpdateElytraSetting(stackIn, moduleIn.getElytraSetting(), true);
					saveModuleList(list, stackIn);
				}
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean hasModuleInstalled(ItemStack stackIn, BaseElytraModule moduleIn) {
		ArrayList<BaseElytraModule> list = getInstalledModules(stackIn);
		
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			if (!list.isEmpty()) {
				if (list.contains(moduleIn)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void saveModuleList(ArrayList<BaseElytraModule> listIn, ItemStack stackIn) {
		CompoundTag stackTag = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		CompoundTag newList = new CompoundTag();
		
		if (stackIn.getItem() instanceof DimensionalElytraplate) {
			for (int i = 0; i < listIn.size(); i ++) {
				BaseElytraModule module = listIn.get(i);
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
	
	public static ArrayList<BaseElytraModule> getInstalledModules(ItemStack stackIn) {
		ArrayList<BaseElytraModule> list = new ArrayList<BaseElytraModule>();
		
		CompoundTag compound = getModuleList(stackIn);
		
		if (stackIn.getItem() instanceof DimensionalElytraplate elytraplate) {
			if (compound != null) {
				int size = compound.getInt("size");
				
				for (int i = 0; i < size; i++) {
					int index = compound.getInt(Integer.toString(i));
					
					list.add(BaseElytraModule.getStateFromIndex(index));
				}
			}
		}
		return list;
	}
	
	public boolean addOrUpdateShifterInformation(ItemStack stackIn, Pocket pocketIn, Level levelIn, Player playerIn) {
		BlockPos player_pos = playerIn.blockPosition();
		
		if (pocketIn != null) {
			if (pocketIn.checkIfOwner(playerIn)) {
				CosmosChunkPos chunk_pos = pocketIn.getDominantChunkPos();

				int x = chunk_pos.getX();
				int z = chunk_pos.getZ();

				if (playerIn.isShiftKeyDown()) {
					CompoundTag stack_tag = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
					CompoundTag nbt_data = new CompoundTag();
					
					CompoundTag chunk_tag = new CompoundTag();
					chunk_tag.putInt("x", x);
					chunk_tag.putInt("z", z);
					nbt_data.put("chunk_pos", chunk_tag);
					
					nbt_data.putInt("colour", pocketIn.getDisplayColour());
					
					CompoundTag pos_tag = new CompoundTag();
					pos_tag.putInt("x", player_pos.getX());
					pos_tag.putInt("y", player_pos.getY());
					pos_tag.putInt("z", player_pos.getZ());
					pos_tag.putFloat("yaw", playerIn.getRotationVector().y);
					pos_tag.putFloat("pitch", playerIn.getRotationVector().x);
					nbt_data.put("player_pos", pos_tag);

					addOrUpdateElytraSetting(stackIn, ElytraSettings.TELEPORT_TO_BLOCK, true);
					
					CompoundTag dimension = new CompoundTag();
					dimension.putString("namespace", levelIn.dimension().location().getNamespace());
					dimension.putString("path", levelIn.dimension().location().getPath());
					nbt_data.put("dimension", dimension);
					
					stack_tag.put("nbt_data", nbt_data);
					
					stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
					CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.item.message.elytraplate.linked").append(ComponentHelper.comp(Value.LIGHT_GRAY + " {" + x + ", " + z + "}")));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean[] getElytraSetting(ItemStack stackIn, ElytraSettings settingIn) {
		CompoundTag compoundIn = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			
		if (compoundIn.contains("settings_data")) {
			CompoundTag settingsData = compoundIn.getCompound("settings_data");
			
			return new boolean[] { true, settingsData.getBoolean(settingIn.getName()) };
		}
		
		return new boolean[] { false, true };
	}

	public static void addOrUpdateElytraSetting(ItemStack stackIn, ElytraSettings settingIn, boolean valueIn) {
		CompoundTag compoundIn = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		
		if (settingIn != null) {
			if (compoundIn.contains("settings_data")) {
				CompoundTag settingsData = compoundIn.getCompound("settings_data");
				settingsData.putBoolean(settingIn.getName(), valueIn);
				compoundIn.put("settings_data", settingsData);
				stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundIn));
			} else {
				CompoundTag settingsData = new CompoundTag();
				settingsData.putBoolean(settingIn.getName(), valueIn);
				compoundIn.put("settings_data", settingsData);
				stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundIn));
			}
		}
	}
	
	public static void setUIMode(ItemStack stackIn, EnumUIMode mode) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

		compound.putInt("mode", mode.getIndex());
		stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
	}
	
	public static EnumUIMode getUIMode(ItemStack stackIn) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			return EnumUIMode.getStateFromIndex(compound.getInt("mode"));
		}
		
		return EnumUIMode.DARK;
	}

	public static EnumUIHelp getUIHelp(ItemStack stackIn) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			return EnumUIHelp.getStateFromIndex(compound.getInt("help"));
		}
		
		return EnumUIHelp.HIDDEN;
	}

	public static void setUIHelp(ItemStack stackIn, EnumUIHelp mode) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

		compound.putInt("help", mode.getIndex());
		stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
	}
}