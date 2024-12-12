package com.tcn.dimensionalpocketsii.pocket.network.packet.system;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketSystem(boolean doSave) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketSystem> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_save"));

	public static final StreamCodec<ByteBuf, PacketSystem> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL,
		PacketSystem::doSave, 
		PacketSystem::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}