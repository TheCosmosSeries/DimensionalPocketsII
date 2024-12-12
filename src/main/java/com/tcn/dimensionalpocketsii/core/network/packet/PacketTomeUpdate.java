package com.tcn.dimensionalpocketsii.core.network.packet;

import java.util.UUID;

import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketTomeUpdate(UUID playerUUID, int pageNumber, EnumUIMode mode) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketTomeUpdate> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_tome_update"));

	public static final StreamCodec<ByteBuf, PacketTomeUpdate> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketTomeUpdate::playerUUID,
		ByteBufCodecs.INT,
		PacketTomeUpdate::pageNumber,
		EnumUIMode.STREAM_CODEC,
		PacketTomeUpdate::mode,
		PacketTomeUpdate::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}