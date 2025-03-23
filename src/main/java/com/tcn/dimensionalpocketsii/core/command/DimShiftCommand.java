package com.tcn.dimensionalpocketsii.core.command;

import javax.annotation.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.shift.Shifter;
import com.tcn.dimensionalpocketsii.pocket.core.shift.ShifterCore;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DimShiftCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcherIn) {
		dispatcherIn.register(Commands.literal("dim").then(Commands.literal("shift").requires((commandSource) -> { 
			return commandSource.hasPermission(2);
		})).then(Commands.literal("shift").then(Commands.argument("target", EntityArgument.player()).then(Commands.argument("dimension", DimensionArgument.dimension()).executes((commandContext) -> { 
			return shiftToDimension(commandContext.getSource(), EntityArgument.getPlayer(commandContext, "target"), DimensionArgument.getDimension(commandContext, "dimension"), (Vec3) null); 
		}).then(Commands.argument("targetPos", Vec3Argument.vec3()).executes((commandContext) -> { 
			return shiftToDimension(commandContext.getSource(), EntityArgument.getPlayer(commandContext, "target"), DimensionArgument.getDimension(commandContext, "dimension"), Vec3Argument.getVec3(commandContext, "targetPos")); 
		}))))));
	}
	
	private static int shiftToDimension(CommandSourceStack commandSourceIn, ServerPlayer serverPlayer, ServerLevel serverWorldIn, @Nullable Vec3 locationIn) {
		if (locationIn != null) {
				BlockPos pos = new BlockPos(locationIn);
				ResourceKey<Level> dimension = serverWorldIn.dimension();
				
				Shifter shifterIn = Shifter.createTeleporter(dimension, EnumShiftDirection.GENERIC, pos, serverPlayer.getRotationVector().y, serverPlayer.getRotationVector().x, true, false, false);
				ShifterCore.shiftPlayerToDimension(serverPlayer, shifterIn);
				
				commandSourceIn.sendSuccess(ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.direction.generic.pre")
						.append(ComponentHelper.style(ComponentColour.ORANGE, "@" + serverPlayer.getDisplayName().getString()))
						.append(ComponentHelper.comp("dimensionalpocketsii.direction.generic.mid"))
						.append(ComponentHelper.style(ComponentColour.GREEN, dimension.location().toString())), true);
		} else {
			ResourceKey<Level> dimension = serverWorldIn.dimension();
				
				Shifter shifterIn = Shifter.createTeleporter(dimension, EnumShiftDirection.GENERIC, serverWorldIn.getSharedSpawnPos(), serverPlayer.getRotationVector().y, serverPlayer.getRotationVector().x, true, false, false);
				ShifterCore.shiftPlayerToDimension(serverPlayer, shifterIn);
				
				commandSourceIn.sendSuccess(ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.direction.generic.pre")
						.append(ComponentHelper.style(ComponentColour.ORANGE, "@" + serverPlayer.getDisplayName().getString()))
						.append(ComponentHelper.comp("dimensionalpocketsii.direction.generic.mid"))
						.append(ComponentHelper.style(ComponentColour.GREEN, dimension.location().toString())), true);
		}
		
		return 1;
	}
}
