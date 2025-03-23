package com.tcn.dimensionalpocketsii.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

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
			
			if (serverPlayer.level.dimension().equals(DimensionManager.POCKET_WORLD)) {
				BlockPos pos = serverPlayer.blockPosition();
				CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
				Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(chunkPos);
				BlockPos spawnPos = new BlockPos(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
				
				if (pocket.exists()) {
					if (pocket.checkIfOwner(serverPlayer)) {
						pocket.setSpawnInPocket(spawnPos, serverPlayer.getRotationVector().y, serverPlayer.getRotationVector().x);
						
						commandSourceIn.sendSuccess(ComponentHelper.style2(ComponentColour.GREEN, "dimensionalpocketsii.command.setspawn.success.pre", pos.getX() + ", " + pos.getY() + ", " + pos.getZ(), "dimensionalpocketsii.command.setspawn.success.suff"), true);
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