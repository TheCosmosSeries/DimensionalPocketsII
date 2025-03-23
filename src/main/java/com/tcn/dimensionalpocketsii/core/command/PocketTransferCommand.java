package com.tcn.dimensionalpocketsii.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;

public class PocketTransferCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcherIn) {
		dispatcherIn.register(Commands.literal("dim").then(Commands.literal("pocket").then(Commands.literal("transfer")).requires((commandSource) -> {
			return commandSource.hasPermission(2);
		}).then(Commands.literal("transfer").then(Commands.argument("chunkPos", Vec2Argument.vec2()).then(Commands.argument("newPlayer", EntityArgument.player()).executes((commandContext) -> {
			return transferPocket(commandContext.getSource(), Vec2Argument.getVec2(commandContext, "chunkPos"), commandContext.getSource().getEntity(), EntityArgument.getPlayer(commandContext, "newPlayer"));
		}))))));
	}
	
	private static int transferPocket(CommandSourceStack commandSourceIn, Vec2 vector, Entity senderEntity, ServerPlayer newPlayer) {
		if (senderEntity instanceof ServerPlayer) {
			ServerPlayer oldPlayer = (ServerPlayer) senderEntity;
		
			if (!(oldPlayer.equals(newPlayer))) {
				CosmosChunkPos chunkPos = new CosmosChunkPos(vector.x, vector.y); 
				Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(chunkPos);
				
				if (pocket.exists()) {
					if (pocket.checkIfOwner(oldPlayer)) {
						pocket.updateOwner(oldPlayer, newPlayer);
					} else {
						commandSourceIn.sendFailure(ComponentHelper.comp("dimensionalpocketsii.command.transfer.error.not_owner"));
					}
				} else {
					commandSourceIn.sendFailure(ComponentHelper.comp("dimensionalpocketsii.command.recoverpocket.error.exists"));
				}
			} else {
				commandSourceIn.sendFailure(ComponentHelper.comp("dimensionalpocketsii.command.transfer.error.owner_same"));
			}
		}
		
		return 1;
	}
}
