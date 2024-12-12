package com.tcn.dimensionalpocketsii.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;

public class PocketTransferModeratorCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcherIn) {
		dispatcherIn.register(Commands.literal("dim").then(Commands.literal("pocket").then(Commands.literal("transfer").then(Commands.literal("moderator")).requires((commandSource) -> {
			return commandSource.hasPermission(3);
		}).then(Commands.literal("transfer").then(Commands.argument("chunkPos", Vec2Argument.vec2()).then(Commands.argument("newPlayer", EntityArgument.player()).executes((commandContext) -> {
			return transferPocket(commandContext.getSource(), Vec2Argument.getVec2(commandContext, "chunkPos"), EntityArgument.getPlayer(commandContext, "newPlayer"));
		})))))));
	}
	
	private static int transferPocket(CommandSourceStack commandSourceIn, Vec2 vector, ServerPlayer newPlayer) {
		CosmosChunkPos chunkPos = new CosmosChunkPos(vector.x, vector.y); 
		Pocket pocket = StorageManager.getPocketFromChunkPosition(null, chunkPos);
		
		if (pocket.exists()) {
			pocket.updateOwner(null, newPlayer);
		} else {
			commandSourceIn.sendFailure(ComponentHelper.comp("dimensionalpocketsii.command.recoverpocket.error.exists"));
		}
		
		return 1;
	}
}
