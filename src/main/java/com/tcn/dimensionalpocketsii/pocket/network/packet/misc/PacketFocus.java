package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityFocus;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketFocus implements PacketPocketNet {
	
	private BlockPos pos;
	private boolean value;
	private boolean jump;
	
	public PacketFocus(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.value = buf.readBoolean();
		this.jump = buf.readBoolean();
	}
	
	public PacketFocus(BlockPos pos, boolean valueIn, boolean jumpIn) {
		this.pos = pos;
		this.value = valueIn;
		this.jump = jumpIn;
	}
	
	public static void encode(PacketFocus packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeBoolean(packet.value);
		buf.writeBoolean(packet.jump);
	}
	
	public static void handle(final PacketFocus packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(DimensionManager.POCKET_WORLD);
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityFocus) {
				BlockEntityFocus blockEntity = (BlockEntityFocus) tile;
				
				if (packet.jump) {
					blockEntity.setJumpEnabled(packet.value);
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {focus} <jumpenabled> Jump Mode set to: { " + packet.value + " }");
				} else {
					blockEntity.setShiftEnabled(packet.value);
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {focus} <shiftenabled> Shift Mode set to: { " + packet.value + " }");
				}
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {generator} <generationmode> Block Entity not equal to expected.");
			}
		});
		
		ctx.setPacketHandled(true);
	}
}
