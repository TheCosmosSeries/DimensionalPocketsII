package com.tcn.dimensionalpocketsii.core.network.elytraplate;

import java.util.UUID;
import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.client.container.MenuProviderElytraplateSettings;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketElytraplateOpenSettings {
	
	private UUID playerUUID;
	private int index;
	
	public PacketElytraplateOpenSettings(FriendlyByteBuf buf) {
		this.playerUUID = buf.readUUID();
		this.index = buf.readInt();
	}
	
	public PacketElytraplateOpenSettings(UUID id, int index) {
		this.playerUUID = id;
		this.index = index;
	}
	
	public static void encode(PacketElytraplateOpenSettings packet, FriendlyByteBuf buf) {
		buf.writeUUID(packet.playerUUID);
		buf.writeInt(packet.index);
	}
	
	public static void handle(final PacketElytraplateOpenSettings packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID);
			ItemStack stack = player.getInventory().armor.get(packet.index);
			
			try {
				NetworkHooks.openGui(player, new MenuProviderElytraplateSettings(), (packetBuffer) -> {
					packetBuffer.writeItemStack(stack, false);
				});
			} catch (Exception e) {
				
			}
		});
		
		ctx.setPacketHandled(true);
	}
}