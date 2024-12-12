package com.tcn.dimensionalpocketsii.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.tcn.dimensionalpocketsii.pocket.core.registry.BackupManager.BackupType;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class CreateBackupCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcherIn) {
		dispatcherIn.register(Commands.literal("dim").requires((commandSource) -> { 
			return commandSource.hasPermission(0);
		}).then(Commands.literal("backup").executes((commandContext) -> {
			return createBackup(commandContext.getSource());
		})));
	}
	
	private static int createBackup(CommandSourceStack commandSourceIn) {
		Entity entity = commandSourceIn.getEntity();
		
		if (entity instanceof ServerPlayer) {
			StorageManager.createBackup(BackupType.USER, commandSourceIn.registryAccess());
		}
		return 1;
	}
}