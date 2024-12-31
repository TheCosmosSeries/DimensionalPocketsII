package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyArmourItemColourable;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.network.packet.PacketDimensionChange;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenConnector;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenEnderChest;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenSettings;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateShift;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateTagUpdate;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUseEnergy;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUseFirework;
import com.tcn.dimensionalpocketsii.pocket.core.registry.ChunkLoadingManager;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PocketsGameEventsManager {

	@SubscribeEvent
	public static void onLivingEquipmentChangeEvent(final LivingEquipmentChangeEvent event) {
		EquipmentSlot slot = event.getSlot();
		
		if (!(slot.equals(EquipmentSlot.MAINHAND)) && !(slot.equals(EquipmentSlot.OFFHAND))) {
			LivingEntity entity = event.getEntity();
	
			if (!(entity instanceof Player)) {
				ItemStack stackTo = event.getTo();
				
				if (stackTo.getItem() instanceof CosmosEnergyArmourItemColourable item) {
					item.setDamage(stackTo, 0);
					
					if (!item.hasEnergy(stackTo)) {
						item.setEnergy(stackTo, 1000);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onKeyPressEvent(Key event) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer playerIn = mc.player;
		
		if (playerIn != null) {
			Level world = playerIn.level();
			if (PocketsRegistrationManager.SUIT_SCREEN.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					Item armour = armourStack.getItem();
					
					if (armour instanceof DimensionalElytraplate elytraplate) {						
						if (DimensionalElytraplate.hasModuleInstalled(armourStack, BaseElytraModule.SCREEN)) {
							if (elytraplate.hasEnergy(armourStack)) {
								PacketDistributor.sendToServer(new PacketElytraplateOpenConnector(playerIn.getUUID(), 2));
								PacketDistributor.sendToServer(new PacketElytraplateUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
							} else {
								CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
							}
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_screen"));
						}
					}
				}
			} 
			
			else if (PocketsRegistrationManager.SUIT_SCREEN_ENDER_CHEST.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					Item armour = armourStack.getItem();
					
					if (armour instanceof DimensionalElytraplate elytraplate) {						
						if (DimensionalElytraplate.hasModuleInstalled(armourStack, BaseElytraModule.ENDER_CHEST)) {
							if (elytraplate.hasEnergy(armourStack)) {
								PacketDistributor.sendToServer(new PacketElytraplateOpenEnderChest(playerIn.getUUID(), 2));
								PacketDistributor.sendToServer(new PacketElytraplateUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
							} else {
								CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
							}
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_ender_chest"));
						}
					}
				}
			}  
			
			else if (PocketsRegistrationManager.SUIT_SETTINGS.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					
					if (armourStack.getItem() instanceof DimensionalElytraplate elytraplate) {
						if (elytraplate.hasEnergy(armourStack)) {
							PacketDistributor.sendToServer(new PacketElytraplateOpenSettings(playerIn.getUUID(), 2));
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
						}
					}
				}
			}
		
			else if (PocketsRegistrationManager.SUIT_SHIFT.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					Item armour = armourStack.getItem();
					BlockPos player_pos_actual = playerIn.blockPosition();
					
					if (armour instanceof DimensionalElytraplate elytraplate) {
						if (DimensionalElytraplate.hasModuleInstalled(armourStack, BaseElytraModule.SHIFTER)) {
							if (armourStack.has(DataComponents.CUSTOM_DATA)) {
								CompoundTag stack_nbt = armourStack.get(DataComponents.CUSTOM_DATA).copyTag();
								
								if (stack_nbt.contains("nbt_data")) {
									CompoundTag nbt_data = stack_nbt.getCompound("nbt_data");
									
									if (nbt_data.contains("chunk_pos")) {
										CompoundTag chunk_pos = nbt_data.getCompound("chunk_pos");
										
										if (nbt_data.contains("player_pos")) {
											CompoundTag player_pos = nbt_data.getCompound("player_pos");
											
											int player_x = player_pos.getInt("x");
											int player_y = player_pos.getInt("y");
											int player_z = player_pos.getInt("z");
											float player_pitch = player_pos.getFloat("pitch");
											float player_yaw = player_pos.getFloat("yaw");
											
											boolean tele_to_block = DimensionalElytraplate.getElytraSetting(armourStack, ElytraSettings.TELEPORT_TO_BLOCK)[1];
											
											CompoundTag dim = nbt_data.getCompound("dimension");
											String namespace = dim.getString("namespace");
											String path = dim.getString("path");
											ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(namespace, path);
											
											ResourceKey<Level> source_dimension = ResourceKey.create(Registries.DIMENSION, loc);
											//ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, loc));
											
											BlockPos teleport_pos = new BlockPos(player_x, player_y, player_z);
											
											//EnumSafeTeleport teleport = EnumSafeTeleport.getValidTeleportLocation(level, teleport_pos);
											//boolean teleportSafe = EnumSafeTeleport.isSafeTeleportLocation(level, teleport_pos);
											
											int x = chunk_pos.getInt("x");
											int z = chunk_pos.getInt("z");
											
											CosmosChunkPos chunk = new CosmosChunkPos(x, z);
											
											if (elytraplate.hasEnergy(armourStack)) {
												if (PocketUtil.isDimensionEqual(world, PocketsDimensionManager.POCKET_WORLD)) {
													if (tele_to_block) {
														PacketDistributor.sendToServer(new PacketElytraplateUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
														PacketDistributor.sendToServer(new PacketElytraplateShift(playerIn.getUUID(), world.dimension(), chunk));
													} else {
														PacketDistributor.sendToServer(new PacketElytraplateUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
														PacketDistributor.sendToServer(new PacketDimensionChange(playerIn.getUUID(), source_dimension, loc, teleport_pos, EnumShiftDirection.LEAVE, player_yaw, player_pitch, false, true));
													}
												} else {
													CompoundTag pos_tag = new CompoundTag();
													pos_tag.putInt("x", player_pos_actual.getX());
													pos_tag.putInt("y", player_pos_actual.getY());
													pos_tag.putInt("z", player_pos_actual.getZ());
													pos_tag.putFloat("yaw", playerIn.getRotationVector().y);
													pos_tag.putFloat("pitch", playerIn.getRotationVector().x);
													//pos_tag.putBoolean("tele_to_block", tele_to_block);
													
													CompoundTag dimension = new CompoundTag();
													dimension.putString("namespace", world.dimension().location().getNamespace());
													dimension.putString("path", world.dimension().location().getPath());
													
													nbt_data.put("player_pos", pos_tag);
													nbt_data.put("dimension", dimension);
													stack_nbt.put("nbt_data", nbt_data);
													armourStack.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_nbt));
													
													PacketDistributor.sendToServer(new PacketElytraplateTagUpdate(playerIn.getUUID(), 2, stack_nbt));
													PacketDistributor.sendToServer(new PacketElytraplateUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
													PacketDistributor.sendToServer(new PacketElytraplateShift(playerIn.getUUID(), world.dimension(), chunk));
												}
											} else {
												CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
											}
										}
									}
								} else {
									CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
								}
							} else {
								CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
							}
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_shifter"));
						}
					}
				}
			} else if (PocketsRegistrationManager.SUIT_FIREWORK.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					Item armour = armourStack.getItem();
					
					if (armour instanceof DimensionalElytraplate elytraplate) {						
						if (DimensionalElytraplate.hasModuleInstalled(armourStack, BaseElytraModule.FIREWORK)) {
							if (DimensionalElytraplate.getElytraSetting(armourStack, ElytraSettings.FIREWORK)[1]) {
								if (elytraplate.hasEnergy(armourStack)) {
									PacketDistributor.sendToServer(new PacketElytraplateUseFirework(2));
									PacketDistributor.sendToServer(new PacketElytraplateUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
									return;
								} else {
									CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
								}
							} else {
								CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.firework_disabled"));
							}
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_firework"));
						}
					}
				}
			} 
			
		}
	}
	
	@SubscribeEvent
	public static void onServerSaveEvent(final LevelEvent.Save event) {
		LevelAccessor level = event.getLevel();
		DimensionType type = level.dimensionType();
		
		if (!level.isClientSide()) {
			if (type.effectsLocation() == BuiltinDimensionTypes.OVERWORLD_EFFECTS) {
				
				StorageManager.saveRegistry(level.registryAccess());
				ChunkLoadingManager.saveToStorage();
			}
		}
	}

	@SubscribeEvent
	public static void onServerUnloadEvent(final LevelEvent.Unload event) {
		LevelAccessor level = event.getLevel();
		
		if (!level.isClientSide()) {
			StorageManager.saveRegistry(level.registryAccess());
			ChunkLoadingManager.saveToStorage();
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedOutEvent(final PlayerEvent.PlayerLoggedOutEvent event) { }
	
	@SubscribeEvent
	public static void onPlayerLoggedInEvent(final PlayerEvent.PlayerLoggedInEvent event) { }
	
}