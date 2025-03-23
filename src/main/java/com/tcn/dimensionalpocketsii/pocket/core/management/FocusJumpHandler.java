package com.tcn.dimensionalpocketsii.pocket.core.management;

import java.util.Arrays;

import com.tcn.dimensionalpocketsii.pocket.core.block.BlockFocus;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketFocusTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * 
 * Code for Elevators
 * @author VsnGamer
 * 
 * #OPEN
 * This means that any code contained in this class can be copied as per the MIT Licence.
 *
 */
public class FocusJumpHandler {

    public static boolean isBlocked(BlockGetter world, BlockPos target) {
        return validateTargets(world.getBlockState(target.above()), world.getBlockState(target.above(2)));
    }

    private static boolean validTarget(BlockState blockState) {
        return !blockState.getMaterial().isSolid();
    }

    private static boolean validateTargets(BlockState... states) {
        return Arrays.stream(states).allMatch(FocusJumpHandler::validTarget);
    }

    public static BlockFocus getElevator(BlockState blockState) {
        if (blockState.getBlock() instanceof BlockFocus elevator) {
            return elevator;
        }
        return null;
    }

    public static boolean isBadTeleportPacket(PacketFocusTeleport msg, ServerPlayer player) {
        if (player == null || !player.isAlive()) {
            return true;
        }

        ServerLevel world = player.getLevel();
        BlockPos fromPos = msg.getFrom();
        BlockPos toPos = msg.getTo();

        if (!world.isLoaded(fromPos) || !world.isLoaded(toPos))
            return true;

        // This ensures the player is still standing on the "from" elevator
        final double distanceSq = player.distanceToSqr(Vec3.atCenterOf(fromPos.north()));
        if (distanceSq > 4D) {
            return true;
        }

        if (fromPos.getX() != toPos.getX() || fromPos.getZ() != toPos.getZ()) {
            return true;
        }

        BlockFocus fromElevator = getElevator(world.getBlockState(fromPos));
        BlockFocus toElevator = getElevator(world.getBlockState(toPos));

        if (fromElevator == null || toElevator == null) {
            return true;
        }

        if (!isBlocked(world, toPos)) {
            return true;
        }

        return false;
    }

}
