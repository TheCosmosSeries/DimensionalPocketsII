package com.tcn.dimensionalpocketsii.pocket.network.packet;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketEmptyTank(CosmosChunkPos pos) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketEmptyTank> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_empty_tank"));

	public static final StreamCodec<ByteBuf, PacketEmptyTank> STREAM_CODEC = StreamCodec.composite(
		CosmosChunkPos.STREAM_CODEC,
		PacketEmptyTank::pos,
		PacketEmptyTank::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}