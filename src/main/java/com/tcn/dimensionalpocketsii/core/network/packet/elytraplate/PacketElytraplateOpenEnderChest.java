package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

import java.util.UUID;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketElytraplateOpenEnderChest(UUID playerUUID, int index) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateOpenEnderChest> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_open_enderchest"));

	public static final StreamCodec<ByteBuf, PacketElytraplateOpenEnderChest> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketElytraplateOpenEnderChest::playerUUID,
		ByteBufCodecs.INT,
		PacketElytraplateOpenEnderChest::index,
		PacketElytraplateOpenEnderChest::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}