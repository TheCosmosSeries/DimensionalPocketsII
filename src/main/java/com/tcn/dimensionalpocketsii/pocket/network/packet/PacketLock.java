package com.tcn.dimensionalpocketsii.pocket.network.packet;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketLock(CosmosChunkPos pos, boolean doLock) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketLock> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_lock"));

	public static final StreamCodec<ByteBuf, PacketLock> STREAM_CODEC = StreamCodec.composite(
		CosmosChunkPos.STREAM_CODEC,
		PacketLock::pos,
		ByteBufCodecs.BOOL,
		PacketLock::doLock, 
		PacketLock::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}