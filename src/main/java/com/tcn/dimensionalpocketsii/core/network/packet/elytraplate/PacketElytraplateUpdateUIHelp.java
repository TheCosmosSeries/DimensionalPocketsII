package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

import java.util.UUID;

import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketElytraplateUpdateUIHelp(UUID playerUUID, int index, EnumUIHelp mode) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateUpdateUIHelp> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_ui_help"));

	public static final StreamCodec<ByteBuf, PacketElytraplateUpdateUIHelp> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketElytraplateUpdateUIHelp::playerUUID,
		ByteBufCodecs.INT,
		PacketElytraplateUpdateUIHelp::index,
		EnumUIHelp.STREAM_CODEC,
		PacketElytraplateUpdateUIHelp::mode,
		PacketElytraplateUpdateUIHelp::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}