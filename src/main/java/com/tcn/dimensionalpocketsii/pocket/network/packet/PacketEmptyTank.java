package com.tcn.dimensionalpocketsii.pocket.network.packet;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PacketEmptyTank implements PacketPocketNet {

	private CosmosChunkPos pos;
	
	public PacketEmptyTank(FriendlyByteBuf buf) {
		this.pos = CosmosChunkPos.convertTo(buf.readBlockPos());
	}
	
	public PacketEmptyTank(CosmosChunkPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketEmptyTank packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(CosmosChunkPos.convertFrom(packet.pos));
	}
	
	public static void handle(final PacketEmptyTank packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(packet.pos);
			
			if (pocket.exists()) {
				pocket.emptyFluidTank();
				
				pocket.forceUpdateInsidePocket();
				pocket.forceUpdateOutsidePocket();
				
				DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <emptytank> Fluid Tank Emptied for Pocket: { " + pocket.getChunkPos() + " }");
			}
			
			PocketRegistryManager.saveData();
		});
		
		ctx.setPacketHandled(true);
	}
}