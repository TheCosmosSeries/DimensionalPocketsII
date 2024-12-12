package com.tcn.dimensionalpocketsii.pocket.network.packet;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketHostileSpawnState(CosmosChunkPos pos, boolean state) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketHostileSpawnState> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_hostile_spawn_state"));

	public static final StreamCodec<ByteBuf, PacketHostileSpawnState> STREAM_CODEC = StreamCodec.composite(
		CosmosChunkPos.STREAM_CODEC,
		PacketHostileSpawnState::pos,
		ByteBufCodecs.BOOL,
		PacketHostileSpawnState::state, 
		PacketHostileSpawnState::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}