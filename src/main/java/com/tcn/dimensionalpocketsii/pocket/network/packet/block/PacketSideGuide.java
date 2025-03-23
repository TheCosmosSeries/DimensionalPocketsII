package com.tcn.dimensionalpocketsii.pocket.network.packet.block;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketSideGuide implements PacketPocketNet {
	
	private BlockPos pos;
	private ResourceKey<Level> dimension;
	
	public PacketSideGuide(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		ResourceLocation location = buf.readResourceLocation();
		this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, location);
	}
	
	public PacketSideGuide(BlockPos pos, ResourceKey<Level> dimensionIn) {
		this.pos = pos;
		this.dimension = dimensionIn;
	}
	
	public static void encode(PacketSideGuide packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeResourceLocation(packet.dimension.location());
	}
	
	public static void handle(final PacketSideGuide packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			ServerLevel world = server.getLevel(packet.dimension);
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityPocket) {
				BlockEntityPocket tile_pocket = (BlockEntityPocket) tile;
			
				tile_pocket.toggleSideGuide();
				
				tile_pocket.sendUpdates(true);
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] <sideguide> Block Entity not equal to expected.");
			}
			
		});
		
		ctx.setPacketHandled(true);
	}
}
