package com.tcn.dimensionalpocketsii.core.network;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.lib.MathHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.core.teleport.EnumSafeTeleport;
import com.tcn.dimensionalpocketsii.client.container.MenuProviderElytraplateConnector;
import com.tcn.dimensionalpocketsii.client.container.MenuProviderElytraplateEnderChest;
import com.tcn.dimensionalpocketsii.client.container.MenuProviderElytraplateSettings;
import com.tcn.dimensionalpocketsii.core.item.DimensionalTome;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.management.PocketsDimensionManager;
import com.tcn.dimensionalpocketsii.core.network.packet.PacketDimensionChange;
import com.tcn.dimensionalpocketsii.core.network.packet.PacketTomeUpdate;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenConnector;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenEnderChest;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenSettings;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateSettingsChange;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateShift;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateTagUpdate;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIHelp;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIMode;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUseEnergy;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUseFirework;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.shift.Shifter;
import com.tcn.dimensionalpocketsii.pocket.core.shift.ShifterCore;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ServerPacketHandler {

	public static void handleDataOnNetwork(final DimensionalPacket data, final IPayloadContext context) {
		HolderLookup.Provider provider = ServerLifecycleHooks.getCurrentServer().registryAccess();
		
		if (data instanceof PacketElytraplateShift packet) {
			context.enqueueWork(() -> {
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());
				
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.chunkPos());
				
				if (pocket.exists()) {
					if (packet.dimension().equals(PocketsDimensionManager.POCKET_WORLD)) {
						pocket.shift(player, EnumShiftDirection.LEAVE, null, null, null);
					} else {
						pocket.shift(player, EnumShiftDirection.ENTER, null, null, null);
					}
				}
			});
		}
		
		if (data instanceof PacketDimensionChange packet) {
			context.enqueueWork(() -> {
				ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, packet.toDimension()));
				
				EnumSafeTeleport teleport = EnumSafeTeleport.getValidTeleportLocation(level, packet.pos());
				boolean teleportSafe = EnumSafeTeleport.isSafeTeleportLocation(level, packet.pos());
				
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());
				
				Shifter shifter = Shifter.createTeleporter(packet.dimension(), packet.direction(), MathHelper.addBlockPos(packet.pos(), teleport.toBlockPos()), packet.yaw(), packet.pitch(), packet.playVanillaSound(), packet.sendMessage(), !teleportSafe);
				ShifterCore.shiftPlayerToDimension(player, shifter, !teleportSafe);
			});
		}
		
		if (data instanceof PacketElytraplateTagUpdate packet) {
			context.enqueueWork(() -> {
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());
				
				ItemStack stack = player.getInventory().getArmor(packet.index());
				stack.set(DataComponents.CUSTOM_DATA, CustomData.of(packet.stackTag()));
			});
		}
		
		if (data instanceof PacketElytraplateUseEnergy packet) {
			context.enqueueWork(() -> {
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());
				
				ItemStack itemStack = player.getInventory().getArmor(packet.index());
				Item item = itemStack.getItem();
				
				if (item instanceof DimensionalElytraplate elytraplate) {
					elytraplate.extractEnergy(itemStack, packet.energyUsed(), false);
				}
			});
		}
		
		if (data instanceof PacketTomeUpdate packet) {
			context.enqueueWork(() -> {
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());
				
				ItemStack stack = CosmosUtil.getStack(player);
				
				if (stack != null) {
					if (packet.pageNumber() > -1) {
						DimensionalTome.setPage(stack, packet.pageNumber());
					}
					
					if (packet.mode() != null) {
						DimensionalTome.setUIMode(stack, packet.mode());
					}
				}
			});
		}
		
		if (data instanceof PacketElytraplateSettingsChange packet) {
			context.enqueueWork(() -> {
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());
				
				ItemStack stack = player.getInventory().getArmor(packet.armourIndex());
				
				DimensionalElytraplate.addOrUpdateElytraSetting(stack, packet.setting(), packet.value());
			});
		}
		
		if (data instanceof PacketElytraplateUpdateUIMode packet) {
			context.enqueueWork(() -> {
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());

				ItemStack stack = player.getInventory().armor.get(packet.index());
				
				if (stack != null) {
					if (packet.mode() != null) {
						DimensionalElytraplate.setUIMode(stack, packet.mode());
					}
				}
			});
		}
		
		if (data instanceof PacketElytraplateUpdateUIHelp packet) {
			context.enqueueWork(() -> {
				ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID());

				ItemStack stack = player.getInventory().armor.get(packet.index());
				
				if (stack != null) {
					if (packet.mode() != null) {
						DimensionalElytraplate.setUIHelp(stack, packet.mode());
					}
				}
			});
		}
		
		if (data instanceof PacketElytraplateOpenConnector packet) {
			context.enqueueWork(() -> {
				Player player = context.player();
				
				if (player instanceof ServerPlayer serverPlayer) {
				ItemStack stack = player.getInventory().getArmor(packet.index());
					
					if (stack.has(DataComponents.CUSTOM_DATA)) {
						CompoundTag compound = stack.get(DataComponents.CUSTOM_DATA).copyTag();
						
						if (compound.contains("nbt_data")) {
							CompoundTag nbtData = compound.getCompound("nbt_data");
							
							if (nbtData.contains("chunk_pos")) {
								CompoundTag chunkPos = nbtData.getCompound("chunk_pos");
	
								int x = chunkPos.getInt("x");
								int z = chunkPos.getInt("z");
								CosmosChunkPos chunk = new CosmosChunkPos(x, z);
								
								Pocket pocket_ = StorageManager.getPocketFromChunkPosition(null, chunk);
								
								CompoundTag compoundA = new CompoundTag();
								pocket_.writeToNBT(compoundA, provider);
								
								if (pocket_ != null) {
									if (stack.getItem() instanceof DimensionalElytraplate item) {
										serverPlayer.openMenu(new MenuProviderElytraplateConnector(), (packetBuffer) -> {
											packetBuffer.writeNbt(compoundA);
											ItemStack.STREAM_CODEC.encode(packetBuffer, stack);
										});
									}
								}
							} else {
								CosmosChatUtil.sendServerPlayerMessage(player, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
							}
						} else {
							CosmosChatUtil.sendServerPlayerMessage(player, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
						}
					} else {
						CosmosChatUtil.sendServerPlayerMessage(player, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
					}
				}
			});
		}
		
		if (data instanceof PacketElytraplateOpenEnderChest packet) {
			context.enqueueWork(() -> {
				Player player = context.player();
				
				if (player instanceof ServerPlayer serverPlayer) {
					ItemStack stack = player.getInventory().getArmor(packet.index());
					
					serverPlayer.openMenu(new MenuProviderElytraplateEnderChest(), (packetBuffer) -> {
						ItemStack.STREAM_CODEC.encode(packetBuffer, stack);
					});
				}
			});
		}
		
		if (data instanceof PacketElytraplateOpenSettings packet) {
			context.enqueueWork(() -> {
				Player player = context.player();
				
				if (player instanceof ServerPlayer serverPlayer) {
					ItemStack stack = player.getInventory().getArmor(packet.index());
					serverPlayer.openMenu(new MenuProviderElytraplateSettings(), (packetBuffer) -> {
						ItemStack.STREAM_CODEC.encode(packetBuffer, stack);
					});
				}
			});
		}

		if (data instanceof PacketElytraplateUseFirework packet) {
			context.enqueueWork(() -> {
				Player player = context.player();
				
				if (player.isFallFlying()) {
					if (player instanceof ServerPlayer serverPlayer) {
						FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(serverPlayer.level(), new ItemStack(Items.FIREWORK_ROCKET), player);
		                serverPlayer.level().addFreshEntity(fireworkrocketentity);
					}
				}
			});
		}
	}
}
