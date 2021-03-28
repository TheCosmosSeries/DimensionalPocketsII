package com.tcn.dimensionalpocketsii.pocket.network.packet.connector;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.CoreDimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.tileentity.TileEntityConnector;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PacketSideState  {
	
	private BlockPos pos;
	
	public PacketSideState(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
	}
	
	public PacketSideState(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketSideState packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		
	}
	
	public static void handle(final PacketSideState packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerWorld world = ServerLifecycleHooks.getCurrentServer().getLevel(CoreDimensionManager.POCKET_WORLD);
			TileEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof TileEntityConnector) {
				TileEntityConnector tile_connector = (TileEntityConnector) tile;
			
				if (tile_connector.getPocket().exists()) {
					tile_connector.cycleSide(Direction.UP, true);
				}
			} else {
				DimensionalPockets.LOGGER.warn("[FAIL] TileEntity not instanceof!");
			}
			
		});
		
		ctx.setPacketHandled(true);
	}
}
