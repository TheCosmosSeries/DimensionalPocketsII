package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketArmourItem(BlockPos pos, boolean doApply, boolean doColour, boolean doModule) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketArmourItem> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_armour_item"));

	public static final StreamCodec<ByteBuf, PacketArmourItem> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketArmourItem::pos,
		ByteBufCodecs.BOOL,
		PacketArmourItem::doApply,
		ByteBufCodecs.BOOL,
		PacketArmourItem::doColour,
		ByteBufCodecs.BOOL,
		PacketArmourItem::doModule,
		PacketArmourItem::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}