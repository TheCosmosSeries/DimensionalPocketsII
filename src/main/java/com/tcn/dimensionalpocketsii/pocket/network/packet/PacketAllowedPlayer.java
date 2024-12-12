package com.tcn.dimensionalpocketsii.pocket.network.packet;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketAllowedPlayer(CosmosChunkPos pos, String playerName, boolean doAdd) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketAllowedPlayer> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_allowed_player"));

	public static final StreamCodec<ByteBuf, PacketAllowedPlayer> STREAM_CODEC = StreamCodec.composite(
		CosmosChunkPos.STREAM_CODEC,
		PacketAllowedPlayer::pos,
		ByteBufCodecs.STRING_UTF8,
		PacketAllowedPlayer::playerName,
		ByteBufCodecs.BOOL,
		PacketAllowedPlayer::doAdd, 
		PacketAllowedPlayer::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}