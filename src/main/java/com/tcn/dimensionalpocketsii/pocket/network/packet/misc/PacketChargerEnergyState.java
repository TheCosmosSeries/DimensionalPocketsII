package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketChargerEnergyState implements PacketPocketNet {
	
	private BlockPos pos;
	
	public PacketChargerEnergyState(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
	}
	
	public PacketChargerEnergyState(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketChargerEnergyState packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
	}
	
	public static void handle(final PacketChargerEnergyState packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(DimensionManager.POCKET_WORLD);
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityModuleCharger) {
				BlockEntityModuleCharger tileEntity = (BlockEntityModuleCharger) tile;
				
				tileEntity.cycleEnergyState();
				DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {charger} <chargemode> Charge Mode cycled.");
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {charger} <chargemode> Block Entity not equal to expected.");
			}
		});
		
		ctx.setPacketHandled(true);
	}
}
