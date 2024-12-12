package com.tcn.dimensionalpocketsii.pocket.network.packet;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketBlockSideState(CosmosChunkPos pos, int index) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketBlockSideState> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_block_side_state"));

	public static final StreamCodec<ByteBuf, PacketBlockSideState> STREAM_CODEC = StreamCodec.composite(
		CosmosChunkPos.STREAM_CODEC,
		PacketBlockSideState::pos,
		ByteBufCodecs.INT,
		PacketBlockSideState::index, 
		PacketBlockSideState::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}