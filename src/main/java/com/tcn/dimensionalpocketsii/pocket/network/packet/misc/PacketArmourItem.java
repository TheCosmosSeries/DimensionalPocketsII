package com.tcn.dimensionalpocketsii.pocket.network.packet.misc;

import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketPocketNet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketArmourItem implements PacketPocketNet {
	
	private BlockPos pos;
	private boolean apply;
	private boolean colour;
	private boolean module;
	
	public PacketArmourItem(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.apply = buf.readBoolean();
		this.colour = buf.readBoolean();
		this.module = buf.readBoolean();
	}
	
	public PacketArmourItem(BlockPos posIn, boolean applyIn, boolean colourIn, boolean moduleIn) {
		this.pos = posIn;
		this.apply = applyIn;
		this.colour = colourIn;
		this.module = moduleIn;
	}
	
	public static void encode(PacketArmourItem packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeBoolean(packet.apply);
		buf.writeBoolean(packet.colour);
		buf.writeBoolean(packet.module);
	}
	
	public static void handle(final PacketArmourItem packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(DimensionManager.POCKET_WORLD);
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityModuleArmourWorkbench) {
				BlockEntityModuleArmourWorkbench tileEntity = (BlockEntityModuleArmourWorkbench) tile;
			
				if (packet.apply) {
					tileEntity.applyToArmourItem(packet.colour, packet.module);
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {armourworkbench} <itemfunction> Values: { 'colour': " + packet.colour + ", 'module': " + packet.module + " } applied to Item.");
				} else {
					tileEntity.removeFromArmourItem();
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {armourworkbench} <itemfunction> All module values removed from Item.");
				}
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {armourworkbench} <itemfunction> Block Entity not equal to expected.");
			}
			
		});
		
		ctx.setPacketHandled(true);
	}
}
