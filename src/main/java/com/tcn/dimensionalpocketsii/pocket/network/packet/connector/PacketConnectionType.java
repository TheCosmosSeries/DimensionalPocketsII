package com.tcn.dimensionalpocketsii.pocket.network.packet.connector;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketConnectionType implements PacketPocketNet {
	
	private BlockPos pos;
	
	public PacketConnectionType(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
	}
	
	public PacketConnectionType(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketConnectionType packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
	}
	
	public static void handle(final PacketConnectionType packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(DimensionManager.POCKET_WORLD);
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityModuleConnector) {
				BlockEntityModuleConnector tile_connector = (BlockEntityModuleConnector) tile;
			
				if (tile_connector.getPocket().exists()) {
					tile_connector.cycleConnectionType(true);
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {connector} <connectiontype> Connection Type cycled.");
				}
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {connector} <connectiontype> Block Entity not equal to expected.");
			}
		});
		
		ctx.setPacketHandled(true);
	}
}
