package com.tcn.dimensionalpocketsii.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class SetSpawnCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcherIn) {
		dispatcherIn.register(Commands.literal("dim").requires((commandSource) -> { 
			return commandSource.hasPermission(0);
		}).then(Commands.literal("setspawn").executes((commandContext) -> {
			return setSpawn(commandContext.getSource());
		})));
	}
	
	private static int setSpawn(CommandSourceStack commandSourceIn) {
		Entity entity = commandSourceIn.getEntity();
		
		if (entity instanceof ServerPlayer) {
			ServerPlayer serverPlayer = (ServerPlayer) entity;
			
			if (serverPlayer.level().dimension().equals(ModDimensionManager.POCKET_WORLD)) {
				BlockPos spawnPos = BlockPos.ZERO;
				
				BlockPos playerPos = serverPlayer.blockPosition();
				CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(playerPos);
				
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, chunkPos);
				CosmosChunkPos domChunkPos = pocket.getDominantChunkPos();
				
				if (pocket.getChunkInfo().isSingleChunk()) {
					spawnPos = new BlockPos(playerPos.getX() & 15, playerPos.getY(), playerPos.getZ() & 15);
				} else {
					if (chunkPos.equals(domChunkPos)) {
						spawnPos = new BlockPos(playerPos.getX() & 15, playerPos.getY(), playerPos.getZ() & 15);
					} else {
						int chunkX = chunkPos.getX() - domChunkPos.getX();
						int chunkZ = chunkPos.getZ() - domChunkPos.getZ();
						
						int chunkOffsetX = chunkX * 16;
						int chunkOffsetZ = chunkZ * 16;
						
						int posXlocated = (playerPos.getX() & 15) + chunkOffsetX;
						int posZlocated = (playerPos.getZ() & 15) + chunkOffsetZ;
						
						spawnPos = new BlockPos(posXlocated, playerPos.getY(), posZlocated);
					}
				}
				
				if (pocket.exists()) {
					if (pocket.checkIfOwner(serverPlayer)) {
						pocket.setSpawnInPocket(spawnPos, serverPlayer.getRotationVector().y, serverPlayer.getRotationVector().x);
						
						final BlockPos finalPozzy = new BlockPos(spawnPos);
						
						commandSourceIn.sendSuccess(
							() -> ComponentHelper.style3(ComponentColour.GREEN, "", "dimensionalpocketsii.command.setspawn.success.pre", finalPozzy.getX() + ", " + finalPozzy.getY() + ", " + finalPozzy.getZ(), "dimensionalpocketsii.command.setspawn.success.suff")
							, true);
					} else {
						commandSourceIn.sendFailure(ComponentHelper.comp("dimensionalpocketsii.pocket.status.action.not_owner"));
					}
				} else {
					commandSourceIn.sendFailure(ComponentHelper.comp("dimensionalpocketsii.pocket.status.action.null"));
				}
			} else {
				commandSourceIn.sendFailure(ComponentHelper.comp("dimensionalpocketsii.command.setspawn.error.dimension"));
			}
		}
		
		return 1;
	}
}