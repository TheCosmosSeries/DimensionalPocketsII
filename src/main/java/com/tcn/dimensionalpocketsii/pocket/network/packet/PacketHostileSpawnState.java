package com.tcn.dimensionalpocketsii.pocket.network.packet;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PacketHostileSpawnState implements PacketPocketNet {

	private CosmosChunkPos pos;
	private boolean value;
	
	public PacketHostileSpawnState(FriendlyByteBuf buf) {
		this.pos = CosmosChunkPos.convertTo(buf.readBlockPos());
		this.value = buf.readBoolean();
	}
	
	public PacketHostileSpawnState(CosmosChunkPos posIn, boolean valueIn) {
		this.pos = posIn;
		this.value = valueIn;
	}
	
	public static void encode(PacketHostileSpawnState packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(CosmosChunkPos.convertFrom(packet.pos));
		buf.writeBoolean(packet.value);
	}
	
	public static void handle(final PacketHostileSpawnState packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(packet.pos);
			
			if (pocket.exists()) {
				pocket.setHostileSpawnState(packet.value);
				
				pocket.forceUpdateInsidePocket();
				
				DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <hostilespawnstate> Hostile Spawn setting set to: { " + packet.value +  " } for Pocket: { " + pocket.getChunkPos() + " }");
			}
			
			PocketRegistryManager.saveData();
		});
		
		ctx.setPacketHandled(true);
	}
}