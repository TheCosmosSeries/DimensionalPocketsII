package com.tcn.dimensionalpocketsii.core.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class PocketCommands {

	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcherIn) {
		PocketRecoverCommand.register(dispatcherIn);
		PocketRecoverModeratorCommand.register(dispatcherIn);
		PocketTransferCommand.register(dispatcherIn);
		PocketTransferModeratorCommand.register(dispatcherIn);
		DimShiftCommand.register(dispatcherIn);
		SetSpawnCommand.register(dispatcherIn);
		CreateBackupCommand.register(dispatcherIn);
	}
}