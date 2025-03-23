package com.tcn.dimensionalpocketsii.pocket.network.packet.connector;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketSideState implements PacketPocketNet {
	
	private BlockPos pos;
	
	public PacketSideState(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
	}
	
	public PacketSideState(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketSideState packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
	}
	
	public static void handle(final PacketSideState packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(DimensionManager.POCKET_WORLD);
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityModuleConnector) {
				BlockEntityModuleConnector tile_connector = (BlockEntityModuleConnector) tile;
			
				if (tile_connector.getPocket().exists()) {
					tile_connector.cycleSide(Direction.UP, true);
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {connector} <sidestate> Side State cycled.");
				}
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {connector} <sidestate> Block Entity not equal to expected.");
			}
		});
		
		ctx.setPacketHandled(true);
	}
}
