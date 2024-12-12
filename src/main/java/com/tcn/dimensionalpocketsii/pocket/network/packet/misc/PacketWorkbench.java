package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketWorkbench(BlockPos pos, ComponentColour colour, boolean wingColour) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketWorkbench> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_workbench"));

	public static final StreamCodec<ByteBuf, PacketWorkbench> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketWorkbench::pos, 
		ComponentColour.STREAM_CODEC,
		PacketWorkbench::colour, 
		ByteBufCodecs.BOOL,
		PacketWorkbench::wingColour,
		PacketWorkbench::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}