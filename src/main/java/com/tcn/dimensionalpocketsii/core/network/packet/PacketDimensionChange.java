package com.tcn.dimensionalpocketsii.core.network.packet;

import java.util.UUID;

import com.tcn.cosmoslibrary.common.network.StreamCodecHelper;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record PacketDimensionChange(
		UUID playerUUID, 
		ResourceKey<Level> dimension, 
		ResourceLocation toDimension, 
		BlockPos pos, 
		EnumShiftDirection direction, 
		float yaw, 
		float pitch, 
		boolean playVanillaSound, 
		boolean sendMessage) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketDimensionChange> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_dimension_change"));
	
	public static final StreamCodec<ByteBuf, PacketDimensionChange> STREAM_CODEC = StreamCodecHelper.composite(
		UUIDUtil.STREAM_CODEC,
		PacketDimensionChange::playerUUID,
		ResourceKey.streamCodec(Registries.DIMENSION),
		PacketDimensionChange::dimension,
		ResourceLocation.STREAM_CODEC,
		PacketDimensionChange::toDimension,
		BlockPos.STREAM_CODEC,
		PacketDimensionChange::pos,
		EnumShiftDirection.STREAM_CODEC,
		PacketDimensionChange::direction,
		ByteBufCodecs.FLOAT,
		PacketDimensionChange::yaw,
		ByteBufCodecs.FLOAT,
		PacketDimensionChange::pitch,
		ByteBufCodecs.BOOL,
		PacketDimensionChange::playVanillaSound,
		ByteBufCodecs.BOOL,
		PacketDimensionChange::sendMessage,
		PacketDimensionChange::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}