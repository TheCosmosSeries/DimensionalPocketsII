package com.tcn.dimensionalpocketsii.pocket.network.packet;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketLockToAllowedPlayers(CosmosChunkPos pos, boolean doLock) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketLockToAllowedPlayers> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_lock_to_allowed_players"));

	public static final StreamCodec<ByteBuf, PacketLockToAllowedPlayers> STREAM_CODEC = StreamCodec.composite(
		CosmosChunkPos.STREAM_CODEC,
		PacketLockToAllowedPlayers::pos,
		ByteBufCodecs.BOOL,
		PacketLockToAllowedPlayers::doLock, 
		PacketLockToAllowedPlayers::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}