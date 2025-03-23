package com.tcn.dimensionalpocketsii.pocket.network.packet;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PacketLock implements PacketPocketNet {

	private CosmosChunkPos pos;
	private boolean lock;
	
	public PacketLock(FriendlyByteBuf buf) {
		this.pos = CosmosChunkPos.convertTo(buf.readBlockPos());
		this.lock = buf.readBoolean();
	}
	
	public PacketLock(CosmosChunkPos pos, boolean lock) {
		this.pos = pos;
		this.lock = lock;
	}
	
	public static void encode(PacketLock packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(CosmosChunkPos.convertFrom(packet.pos));
		buf.writeBoolean(packet.lock);
	}
	
	public static void handle(final PacketLock packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			Pocket pocket = PocketRegistryManager.getPocketFromChunkPosition(packet.pos);
						
			if (pocket.exists()) {
				pocket.setLockState(packet.lock);
				
				pocket.forceUpdateInsidePocket();
				pocket.forceUpdateOutsidePocket();
				
				DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <lock> Lock setting set to: { " + packet.lock +  " } for Pocket: { " + pocket.getChunkPos() + " }");
			}
			
			PocketRegistryManager.saveData();
		});
		
		ctx.setPacketHandled(true);
	}
}