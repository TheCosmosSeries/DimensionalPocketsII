package com.tcn.dimensionalpocketsii.pocket.network;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModSoundManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.AbstractBlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityFocus;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketFocusManager;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketAllowedPlayer;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketBlockSideState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketHostileSpawnState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLock;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLockToAllowedPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketTrapPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.block.PacketSideGuide;
import com.tcn.dimensionalpocketsii.pocket.network.packet.connector.PacketConnectionType;
import com.tcn.dimensionalpocketsii.pocket.network.packet.connector.PacketSideState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketArmourItem;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketChargerEnergyState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketFocus;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketFocusTeleport;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketGeneratorEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketGeneratorMode;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketWorkbench;
import com.tcn.dimensionalpocketsii.pocket.network.packet.system.PacketSystem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PocketServerPacketHandler {

	public static void handleDataOnNetwork(final PacketPocketNet data, final IPayloadContext context) {
		HolderLookup.Provider provider = ServerLifecycleHooks.getCurrentServer().registryAccess();
		
		if (data instanceof PacketSystem packet) {
			context.enqueueWork(() -> {
				if (packet.doSave()) {
					StorageManager.saveRegistry(provider);
				} else {
					StorageManager.loadRegistry(provider);
				}
			});
		}
		
		if (data instanceof PacketLock packet) {
			context.enqueueWork(() -> {
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.pos());
							
				if (pocket.exists()) {
					pocket.setLockState(packet.doLock());
					
					pocket.forceUpdateInsidePocket();
					pocket.forceUpdateOutsidePocket();
					
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <lock> Lock setting set to: { " + packet.doLock() +  " } for Pocket: { " + pocket.getDominantChunkPos() + " }");
				}
				
				StorageManager.saveRegistry(provider);
			});
		}

		if (data instanceof PacketAllowedPlayer packet) {
			context.enqueueWork(() -> {
				String player_name = packet.playerName();
				boolean add = packet.doAdd();
				
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.pos());
				
				if (!player_name.isEmpty()) {
					if (pocket.exists()) {
						if (add) {
							pocket.addAllowedPlayerNBT(player_name);
							
							sendConsoleMessage(false, true, pocket, player_name);
						} else {
							pocket.removeAllowedPlayerNBT(player_name);
	
							sendConsoleMessage(true, true, pocket, player_name);
						}
						
						pocket.forceUpdateInsidePocket();
						pocket.forceUpdateOutsidePocket();
					}
				} 
				
				StorageManager.saveRegistry(provider);
			});
		}

		if (data instanceof PacketBlockSideState packet) {
			context.enqueueWork(() -> {
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.pos());
				
				if (pocket.exists()) {
					pocket.cycleSide(Direction.from3DDataValue(packet.index()), true);
					
					pocket.forceUpdateInsidePocket();
					pocket.forceUpdateOutsidePocket();
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <pocketsidestate> Pocket Side State cycled for Pocket: { " + pocket.getDominantChunkPos() + " }");
				}
				
				StorageManager.saveRegistry(provider);
			});
		}
		
		if (data instanceof PacketEmptyTank packet) {
			context.enqueueWork(() -> {
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.pos());
				
				if (pocket.exists()) {
					pocket.emptyFluidTank();
					
					pocket.forceUpdateInsidePocket();
					pocket.forceUpdateOutsidePocket();
					
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <emptytank> Fluid Tank Emptied for Pocket: { " + pocket.getDominantChunkPos() + " }");
				}
				
				StorageManager.saveRegistry(provider);
			});
		}
		
		if (data instanceof PacketTrapPlayers packet) {
			context.enqueueWork(() -> {
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.pos());
							
				if (pocket.exists()) {
					pocket.setTrapState(packet.doLock());

					pocket.forceUpdateInsidePocket();
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <trapplayers> Trap Players setting set to: { " + packet.doLock() +  " } for Pocket: { " + pocket.getDominantChunkPos() + " }");
				}
					
				StorageManager.saveRegistry(provider);
			});
		}
		
		if (data instanceof PacketHostileSpawnState packet) {
			context.enqueueWork(() -> {
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.pos());
				
				if (pocket.exists()) {
					pocket.setHostileSpawnState(packet.state());
					
					pocket.forceUpdateInsidePocket();
					
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <hosentityspawnstate> Hosentity Spawn setting set to: { " + packet.state() +  " } for Pocket: { " + pocket.getDominantChunkPos() + " }");
				}
				
				StorageManager.saveRegistry(provider);
			});
		}
		
		if (data instanceof PacketLockToAllowedPlayers packet) {
			context.enqueueWork(() -> {
				Pocket pocket = StorageManager.getPocketFromChunkPosition(null, packet.pos());
							
				if (pocket.exists()) {
					pocket.setAllowedPlayerState(packet.doLock());

					pocket.forceUpdateInsidePocket();
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <locktoallowedplayers> Lock to Allowed Players setting set to: { " + packet.doLock() +  " } for Pocket: { " + pocket.getDominantChunkPos() + " }");
				}
					
				StorageManager.saveRegistry(provider);
			});
		}
		
		if (data instanceof PacketConnectionType packet) {
			context.enqueueWork(() -> {
				ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD);
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityModuleConnector blockEntity) {
					if (blockEntity.getPocket().exists()) {
						blockEntity.cycleConnectionType(true);
						DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {connector} <connectiontype> Connection Type cycled.");
					}
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {connector} <connectiontype> Block Entity not equal to expected.");
				}
			});
		}
		
		if (data instanceof PacketSideState packet) {
			context.enqueueWork(() -> {
				ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD);
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityModuleConnector blockEntity) {
					if (blockEntity.getPocket().exists()) {
						blockEntity.cycleSide(Direction.UP, true);
						DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {connector} <sidestate> Side State cycled.");
					}
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {connector} <sidestate> Block Entity not equal to expected.");
				}
			});
		}
		
		if (data instanceof PacketSideGuide packet) {
			context.enqueueWork(() -> {
				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				ServerLevel world = server.getLevel(packet.dimension());
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof AbstractBlockEntityPocket blockEntity) {
					blockEntity.toggleSideGuide();
					blockEntity.sendUpdates(true);
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] <sideguide> Block Entity not equal to expected.");
				}
				
			});
		}
		
		if (data instanceof PacketArmourItem packet) {
			context.enqueueWork(() -> {
				ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD);
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
					if (packet.doApply()) {
						blockEntity.applyToArmourItem(packet.doColour(), packet.doModule());
						DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {armourworkbench} <itemfunction> Values: { 'colour': " + packet.doColour() + ", 'module': " + packet.doModule() + " } applied to Item.");
					} else {
						blockEntity.removeFromArmourItem();
						DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {armourworkbench} <itemfunction> All module values removed from Item.");
					}
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {armourworkbench} <itemfunction> Block Entity not equal to expected.");
				}
			});
		}
		
		if (data instanceof PacketChargerEnergyState packet) {
			context.enqueueWork(() -> {
				ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD);
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityModuleCharger blockEntity) {
					blockEntity.cycleEnergyState();
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {charger} <chargemode> Charge Mode cycled.");
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {charger} <chargemode> Block Entity not equal to expected.");
				}
			});
		}
		
		if (data instanceof PacketGeneratorMode packet) {
			context.enqueueWork(() -> {
				ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD);
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityModuleGenerator blockEntity) {
					blockEntity.cycleGenerationMode();
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {generator} <generationmode> Generation Mode cycled.");
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {generator} <generationmode> Block Entity not equal to expected.");
				}
			});
		}
		
		if (data instanceof PacketGeneratorEmptyTank packet) {
			context.enqueueWork(() -> {
				ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD);
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityModuleGenerator blockEntity) {
					blockEntity.getFluidTank().setFluid(FluidStack.EMPTY);
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {generator} <emptytank> Fluid Tank Emptied.");
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {generator} <emptytank> Block Entity not equal to expected.");
				}
			});
		}
		
		if (data instanceof PacketFocus packet) {
			context.enqueueWork(() -> {
				ServerLevel world = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD);
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityFocus blockEntity) {
					boolean value = packet.boolValue();
					
					if (packet.doJump()) {
						blockEntity.setJumpEnabled(value);
						DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {focus} <jumpenabled> Jump Mode set to: { " + value + " }");
					} else {
						blockEntity.setShiftEnabled(value);
						DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {focus} <shiftenabled> Shift Mode set to: { " + value + " }");
					}
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {generator} <generationmode> Block Entity not equal to expected.");
				}
			});
		}
		
		if (data instanceof PacketFocusTeleport packet) {
			context.enqueueWork(() -> {
				Player player = context.player();
				
				if (player instanceof ServerPlayer serverPlayer) {
					if (PocketFocusManager.isBadTeleportPacket(packet, serverPlayer)) {
						return;
					}
	
					ServerLevel world = serverPlayer.serverLevel();
					BlockPos toPos = packet.toPos();
					BlockState toState = world.getBlockState(packet.toPos());
					
					final float yaw, pitch;
					yaw = player.getYRot();
					pitch = player.getXRot();
					
					final double toX, toZ;
					toX = toPos.getX() + .5D;
					toZ = toPos.getZ() + .5D;
					
					double blockYOffset = toState.getBlockSupportShape(world, toPos).max(Direction.Axis.Y);
					serverPlayer.teleportTo(world, toX, toPos.getY() + blockYOffset, toZ, yaw, pitch);
					serverPlayer.setDeltaMovement(serverPlayer.getDeltaMovement().multiply(new Vec3(1D, 0D, 1D)));
					world.playSound(null, toPos, ModSoundManager.WOOSH.get(), SoundSource.BLOCKS, 0.6F, 1F);
				}
			});
		}
		
		if (data instanceof PacketWorkbench packet) {
			context.enqueueWork(() -> {
				BlockEntity entity = ServerLifecycleHooks.getCurrentServer().getLevel(ModDimensionManager.POCKET_WORLD).getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
					blockEntity.updateColour(packet.colour(), packet.wingColour());
					DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] {workbench} <updateColour> Colour updated to: { " + packet.colour().getName() + ", " + (packet.wingColour() ? " wing" : " armour") + " }");
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Packet Delivery Failure] {workbench} <updateColour> Block Entity not equal to expected.");
				}
			});
		}
	}

	public static void sendConsoleMessage(boolean removed, boolean connector, Pocket pocket, String added) {
		String noun = removed ? "removed" : "added";
		DimensionalPockets.CONSOLE.debug("[Packet Delivery Success] <allowedplayer:" + noun + "> Allowed Player " + noun + " for Pocket: { " + pocket.getDominantChunkPos() + " } [ Owner: { " + pocket.getOwnerName() + " } Player: { " + added + " } ]");
	}
}
