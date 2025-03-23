package com.tcn.dimensionalpocketsii.core.network.elytraplate;

import java.util.UUID;
import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.client.container.MenuProviderElytraplateConnector;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketElytraplateOpenConnector {
	
	private UUID playerUUID;
	private int index;
	
	public PacketElytraplateOpenConnector(FriendlyByteBuf buf) {
		this.playerUUID = buf.readUUID();
		this.index = buf.readInt();
	}
	
	public PacketElytraplateOpenConnector(UUID id, int index) {
		this.playerUUID = id;
		this.index = index;
	}
	
	public static void encode(PacketElytraplateOpenConnector packet, FriendlyByteBuf buf) {
		buf.writeUUID(packet.playerUUID);
		buf.writeInt(packet.index);
	}
	
	public static void handle(final PacketElytraplateOpenConnector packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID);
			ItemStack stack = player.getInventory().armor.get(packet.index);
			
			if (stack.hasTag()) {
				CompoundTag compound = stack.getTag();
				
				if (compound.contains("nbt_data")) {
					CompoundTag nbtData = compound.getCompound("nbt_data");
					
					if (nbtData.contains("chunk_pos")) {
						CompoundTag chunkPos = nbtData.getCompound("chunk_pos");

						int x = chunkPos.getInt("x");
						int z = chunkPos.getInt("z");
						CosmosChunkPos chunk = new CosmosChunkPos(x, z);
						
						Pocket pocket_ = PocketRegistryManager.getPocketFromChunkPosition(chunk);
						
						CompoundTag compoundA = new CompoundTag();
						pocket_.writeToNBT(compoundA);
						
						if (pocket_ != null) {
							if (stack.getItem() instanceof DimensionalElytraplate) {
								try {
									NetworkHooks.openGui(player, new MenuProviderElytraplateConnector(), (packetBuffer) -> {
										packetBuffer.writeNbt(compoundA);
										packetBuffer.writeItemStack(stack, false);
									});
								} catch (Exception e) {
									
								}
							}
						}
					} else {
						CosmosChatUtil.sendServerPlayerMessage(player, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
					}
				} else {
					CosmosChatUtil.sendServerPlayerMessage(player, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
				}
			} else {
				CosmosChatUtil.sendServerPlayerMessage(player, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
			}
		});
		
		ctx.setPacketHandled(true);
	}
}