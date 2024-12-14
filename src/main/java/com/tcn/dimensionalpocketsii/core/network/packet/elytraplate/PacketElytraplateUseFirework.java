package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketElytraplateUseFirework(int index) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateUseFirework> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_use_firework"));

	public static final StreamCodec<ByteBuf, PacketElytraplateUseFirework> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT,
		PacketElytraplateUseFirework::index,
		PacketElytraplateUseFirework::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}