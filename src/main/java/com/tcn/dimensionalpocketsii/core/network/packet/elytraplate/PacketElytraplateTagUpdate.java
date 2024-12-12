package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

import java.util.UUID;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketElytraplateTagUpdate(UUID playerUUID, int index, CompoundTag stackTag) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateTagUpdate> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_tag_update"));

	public static final StreamCodec<ByteBuf, PacketElytraplateTagUpdate> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketElytraplateTagUpdate::playerUUID,
		ByteBufCodecs.INT,
		PacketElytraplateTagUpdate::index,
		ByteBufCodecs.COMPOUND_TAG,
		PacketElytraplateTagUpdate::stackTag,
		PacketElytraplateTagUpdate::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}	