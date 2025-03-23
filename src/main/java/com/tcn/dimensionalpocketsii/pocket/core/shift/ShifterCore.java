package com.tcn.dimensionalpocketsii.pocket.core.shift;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.MathHelper;
import com.tcn.cosmoslibrary.core.teleport.EnumSafeTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ShifterCore {
	
	public static void sendPlayerToBed(Player playerIn, @Nullable EnumShiftDirection directionIn) {
		if (playerIn instanceof ServerPlayer) {
			ServerPlayer server_player = (ServerPlayer) playerIn;
			shiftPlayerToDimension(server_player, Shifter.createTeleporter(server_player.getRespawnDimension(), directionIn, server_player.getRespawnPosition(), playerIn.getRotationVector().y, playerIn.getRotationVector().x, false, false, true));
		}
	}
	
	public static void sendPlayerToBedWithMessage(Player playerIn, @Nullable EnumShiftDirection directionIn, String messageIn) {
		if (playerIn instanceof ServerPlayer) {
			ServerPlayer server_player = (ServerPlayer) playerIn;
			CosmosChatUtil.sendServerPlayerMessage(server_player, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.target_unknown_custom").append(ComponentHelper.style(ComponentColour.ORANGE, "bold", messageIn)));
			
			BlockPos pos = new BlockPos(0, 0, 0);
			if (((ServerPlayer) playerIn).getRespawnPosition() != null) {
				pos = ((ServerPlayer) playerIn).getRespawnPosition();
			} else {
				pos = ServerLifecycleHooks.getCurrentServer().getLevel(((ServerPlayer) playerIn).getRespawnDimension()).getLevel().getSharedSpawnPos();
			}
			
			shiftPlayerToDimension(server_player, Shifter.createTeleporter(server_player.getRespawnDimension(), directionIn, pos, playerIn.getRotationVector().y, playerIn.getRotationVector().x, false, false, true));
		}
	}
	
	public static void sendPlayerToDimensionSpawn(Player playerIn, ResourceKey<Level> dimensionIn, @Nullable BaseComponent componentIn) {
		if (playerIn instanceof ServerPlayer) {
			ServerPlayer serverPlayer = (ServerPlayer) playerIn;
			
			if (dimensionIn != null) {
				if (componentIn != null) {
					CosmosChatUtil.sendServerPlayerMessage(serverPlayer, componentIn);
				}
				
				ServerLevel targetWorld = ServerLifecycleHooks.getCurrentServer().getLevel(dimensionIn);
				BlockPos spawnPos = targetWorld.getSharedSpawnPos();
				
				EnumSafeTeleport location = EnumSafeTeleport.getValidTeleportLocation(targetWorld, spawnPos);
				
				if (location != EnumSafeTeleport.UNKNOWN) {
					BlockPos locationPos = location.toBlockPos();
					BlockPos targetPos = MathHelper.addBlockPos(spawnPos, locationPos);

					shiftPlayerToDimension(serverPlayer, Shifter.createTeleporter(dimensionIn, EnumShiftDirection.GENERIC, targetPos, 0.0F, 0.0F, false, false, false));
				} else {
					CosmosChatUtil.sendPlayerMessageServer(serverPlayer, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.direction_unknown"));
				}
			} else {
				CosmosChatUtil.sendPlayerMessageServer(serverPlayer, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.dimension_null"));
			}
		}
	}

	@SuppressWarnings("unused")
	public static void shiftPlayerToDimension(Player playerIn, Shifter shifterIn) {
		if (playerIn instanceof ServerPlayer) {
			ServerPlayer server_player = (ServerPlayer) playerIn;
			Level player_world = playerIn.level;
			
			ResourceKey<Level> dimension_key = shifterIn.getDimensionKey();
			EnumShiftDirection direction = shifterIn.getDirection();
			
			if (dimension_key != null) {
				if (direction != EnumShiftDirection.UNKNOWN) {
					MinecraftServer Mserver = ServerLifecycleHooks.getCurrentServer();
					ServerLevel server_world = Mserver.getLevel(dimension_key);
					BlockPos target_pos = shifterIn.getTargetPos();
					
					if (server_world != null) {
						if (target_pos != null) {
							double[] position = shifterIn.getTargetPosA();
							
							if (shifterIn.getSendMessage()) {
								CosmosChatUtil.sendPlayerMessageServer(server_player, direction.getChatComponentForDirection());
							}
							
							server_player.changeDimension(server_world, shifterIn);
							
							if (!shifterIn.playVanillaSound()) {
								server_player.connection.send(new ClientboundSoundPacket(direction.getSound(), SoundSource.AMBIENT, position[0], position[1], position[2], 0.4F, 1));
							}
						} else {
							server_player.changeDimension(server_world, shifterIn);
							CosmosChatUtil.sendPlayerMessageServer(server_player, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.target_unknown"));
						}
					} else {
						CosmosChatUtil.sendPlayerMessageServer(server_player, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.server_world_null"));
					}
				} else {
					CosmosChatUtil.sendPlayerMessageServer(server_player, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.direction_unknown"));
				}
			} else {
				CosmosChatUtil.sendPlayerMessageServer(server_player, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.dimension_null"));
			}
		}
	}
}