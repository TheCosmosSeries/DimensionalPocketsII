package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketGeneratorMode(BlockPos pos) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketGeneratorMode> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_generator_mode"));

	public static final StreamCodec<ByteBuf, PacketGeneratorMode> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketGeneratorMode::pos,
		PacketGeneratorMode::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}