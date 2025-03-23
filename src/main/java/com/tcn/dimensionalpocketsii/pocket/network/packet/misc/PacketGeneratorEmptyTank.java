package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketGeneratorEmptyTank implements PacketPocketNet {
	
	private BlockPos pos;
	
	public PacketGeneratorEmptyTank(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
	}
	
	public PacketGeneratorEmptyTank(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketGeneratorEmptyTank packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
	}
	
	public static void handle(final PacketGeneratorEmptyTank packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(DimensionManager.POCKET_WORLD);
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityModuleGenerator) {
				BlockEntityModuleGenerator blockEntity = (BlockEntityModuleGenerator) tile;

				blockEntity.getFluidTank().setFluid(FluidStack.EMPTY);
				DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {generator} <emptytank> Fluid Tank Emptied.");
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {generator} <emptytank> Block Entity not equal to expected.");
			}
		});
		
		ctx.setPacketHandled(true);
	}
}