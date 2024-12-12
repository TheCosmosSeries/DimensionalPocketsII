package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketFocusTeleport(BlockPos fromPos, BlockPos toPos) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketFocusTeleport> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_focus_teleport"));

	public static final StreamCodec<ByteBuf, PacketFocusTeleport> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketFocusTeleport::fromPos,
		BlockPos.STREAM_CODEC,
		PacketFocusTeleport::toPos,
		PacketFocusTeleport::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}