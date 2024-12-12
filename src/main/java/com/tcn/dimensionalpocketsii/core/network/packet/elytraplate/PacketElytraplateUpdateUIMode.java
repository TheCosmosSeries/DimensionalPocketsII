package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

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

public record PacketElytraplateUpdateUIMode(UUID playerUUID, int index, EnumUIMode mode) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateUpdateUIMode> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_ui_mode"));

	public static final StreamCodec<ByteBuf, PacketElytraplateUpdateUIMode> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketElytraplateUpdateUIMode::playerUUID,
		ByteBufCodecs.INT,
		PacketElytraplateUpdateUIMode::index,
		EnumUIMode.STREAM_CODEC,
		PacketElytraplateUpdateUIMode::mode,
		PacketElytraplateUpdateUIMode::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}