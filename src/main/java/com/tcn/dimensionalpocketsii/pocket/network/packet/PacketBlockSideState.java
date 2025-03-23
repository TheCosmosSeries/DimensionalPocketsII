package com.tcn.dimensionalpocketsii.pocket.network.packet;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PacketBlockSideState implements PacketPocketNet {

	private CosmosChunkPos pos;
	private int index;
	
	public PacketBlockSideState(FriendlyByteBuf buf) {
		this.pos = CosmosChunkPos.convertTo(buf.readBlockPos());
		this.index = buf.readInt();
	}
	
	public PacketBlockSideState(CosmosChunkPos posIn, Direction indexIn) {
		this.pos = posIn;
		this.index = indexIn.get3DDataValue();
	}
	
	public static void encode(PacketBlockSideState packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(CosmosChunkPos.convertFrom(packet.pos));
		buf.writeInt(packet.index);
	}
	
	public static void handle(final PacketBlockSideState packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(packet.pos);
			
			if (pocket.exists()) {
				pocket.cycleSide(Direction.from3DDataValue(packet.index), true);
				
				pocket.forceUpdateInsidePocket();
				pocket.forceUpdateOutsidePocket();
				DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <pocketsidestate> Pocket Side State cycled for Pocket: { " + pocket.getChunkPos() + " }");
			}
			
			PocketRegistryManager.saveData();
		});
		
		ctx.setPacketHandled(true);
	}
}