package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

import java.util.UUID;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketElytraplateSettingsChange(UUID playerUUID, int index, ElytraSettings setting, boolean value) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateSettingsChange> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_settings_change"));

	public static final StreamCodec<ByteBuf, PacketElytraplateSettingsChange> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketElytraplateSettingsChange::playerUUID,
		ByteBufCodecs.INT,
		PacketElytraplateSettingsChange::index,
		ElytraSettings.STREAM_CODEC,
		PacketElytraplateSettingsChange::setting,
		ByteBufCodecs.BOOL,
		PacketElytraplateSettingsChange::value,
		PacketElytraplateSettingsChange::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}