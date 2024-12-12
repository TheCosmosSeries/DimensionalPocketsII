package com.tcn.dimensionalpocketsii.core.network.packet.elytraplate;

import java.util.UUID;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.DimensionalPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketElytraplateUseEnergy(UUID playerUUID, int index, int energyUsed) implements CustomPacketPayload, DimensionalPacket {

	public static final CustomPacketPayload.Type<PacketElytraplateUseEnergy> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "packet_elytraplate_use_energy"));
	
	public static final StreamCodec<ByteBuf, PacketElytraplateUseEnergy> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC,
		PacketElytraplateUseEnergy::playerUUID,
		ByteBufCodecs.INT,
		PacketElytraplateUseEnergy::index,
		ByteBufCodecs.INT,
		PacketElytraplateUseEnergy::energyUsed,
		PacketElytraplateUseEnergy::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}