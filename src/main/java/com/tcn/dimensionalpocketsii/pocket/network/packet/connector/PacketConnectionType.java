package com.tcn.dimensionalpocketsii.pocket.network.packet.connector;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketConnectionType(BlockPos pos) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketConnectionType> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_connection_type"));

	public static final StreamCodec<ByteBuf, PacketConnectionType> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketConnectionType::pos,
		PacketConnectionType::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}