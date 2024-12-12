package com.tcn.dimensionalpocketsii.pocket.network.packet.block;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record PacketSideGuide(BlockPos pos, ResourceKey<Level> dimension) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketSideGuide> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_side_guide"));

	public static final StreamCodec<ByteBuf, PacketSideGuide> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketSideGuide::pos,
		ResourceKey.streamCodec(Registries.DIMENSION),
		PacketSideGuide::dimension,
		PacketSideGuide::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}