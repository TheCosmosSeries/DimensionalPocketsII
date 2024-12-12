package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PacketPocketNet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketChargerEnergyState(BlockPos pos) implements CustomPacketPayload, PacketPocketNet {

	public static final CustomPacketPayload.Type<PacketChargerEnergyState> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_charger_energy_state"));

	public static final StreamCodec<ByteBuf, PacketChargerEnergyState> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketChargerEnergyState::pos,
		PacketChargerEnergyState::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}