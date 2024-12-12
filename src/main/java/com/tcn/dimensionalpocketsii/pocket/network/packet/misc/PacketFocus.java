package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketFocus(BlockPos pos, boolean boolValue, boolean doJump) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketFocus> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_focus"));

	public static final StreamCodec<ByteBuf, PacketFocus> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketFocus::pos,
		ByteBufCodecs.BOOL,
		PacketFocus::boolValue,
		ByteBufCodecs.BOOL,
		PacketFocus::doJump,
		PacketFocus::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}