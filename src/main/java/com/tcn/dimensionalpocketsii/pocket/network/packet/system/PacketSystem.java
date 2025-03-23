package com.tcn.dimensionalpocketsii.pocket.network.packet.system;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PacketSystem  {
	
	private boolean save;
	
	public PacketSystem(FriendlyByteBuf buf) {
		this.save = buf.readBoolean();
	}
	
	public PacketSystem(boolean save) {
		this.save = save;
	}
	
	public static void encode(PacketSystem packet, FriendlyByteBuf buf) {
		buf.writeBoolean(packet.save);
	}
	
	public static void handle(final PacketSystem packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			if (packet.save) {
				PocketRegistryManager.saveData();
			} else {
				PocketRegistryManager.loadData();
			}
		});
		
		ctx.setPacketHandled(true);
	}
}