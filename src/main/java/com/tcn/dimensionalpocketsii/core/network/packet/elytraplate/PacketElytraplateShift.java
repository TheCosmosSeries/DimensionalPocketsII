package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

import java.util.UUID;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record PacketElytraplateShift(UUID playerUUID, ResourceKey<Level> dimension, CosmosChunkPos chunkPos) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateShift> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_shift"));

	public static final StreamCodec<ByteBuf, PacketElytraplateShift> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketElytraplateShift::playerUUID,
		ResourceKey.streamCodec(Registries.DIMENSION),
		PacketElytraplateShift::dimension,
		CosmosChunkPos.STREAM_CODEC,
		PacketElytraplateShift::chunkPos,
		PacketElytraplateShift::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}