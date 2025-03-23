package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.core.management.SoundManager;
import com.tcn.dimensionalpocketsii.pocket.core.management.FocusJumpHandler;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class PacketFocusTeleport implements PacketPocketNet {
	private final BlockPos from, to;

	public PacketFocusTeleport(FriendlyByteBuf buf) {
		this.from = buf.readBlockPos();
		this.to = buf.readBlockPos();
	}

	public PacketFocusTeleport(BlockPos fromIn, BlockPos toIn) {
		this.from = fromIn;
		this.to = toIn;
	}

	public static void encode(PacketFocusTeleport msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.from);
		buf.writeBlockPos(msg.to);
	}

	public BlockPos getFrom() {
		return this.from;
	}

	public BlockPos getTo() {
		return this.to;
	}

	public static void handle(final PacketFocusTeleport message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (FocusJumpHandler.isBadTeleportPacket(message, player)) {
				return;
			}

			ServerLevel world = player.getLevel();
			BlockPos toPos = message.getTo();
			BlockState toState = world.getBlockState(message.getTo());

			// Apply yaw and pitch
			final float yaw, pitch;
			yaw = player.getYRot();
			pitch = player.getXRot();

			// Apply X & Z
			final double toX, toZ;
			//if (ModConfig.GENERAL.precisionTarget.get()) {
				toX = toPos.getX() + .5D;
				toZ = toPos.getZ() + .5D;
			/*} else {
				toX = player.getX();
				toZ = player.getZ();
			}*/

			double blockYOffset = toState.getBlockSupportShape(world, toPos).max(Direction.Axis.Y);
			player.teleportTo(world, toX, toPos.getY() + blockYOffset, toZ, yaw, pitch);
			player.setDeltaMovement(player.getDeltaMovement().multiply(new Vec3(1D, 0D, 1D)));
			world.playSound(null, toPos, SoundManager.GENERIC.WOOSH, SoundSource.BLOCKS, 0.6F, 1F);
		});

		ctx.get().setPacketHandled(true);
	}
}
