package com.tcn.dimensionalpocketsii.pocket.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.network.PocketServerPacketHandler;
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
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketWorkbench;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketFocus;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketFocusTeleport;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketGeneratorEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketGeneratorMode;
import com.tcn.dimensionalpocketsii.pocket.network.packet.system.PacketSystem;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PocketPacketManager {
	
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		
		registrar.playToServer(PacketSystem.TYPE, PacketSystem.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		
		registrar.playToServer(PacketLock.TYPE, PacketLock.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketAllowedPlayer.TYPE, PacketAllowedPlayer.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketBlockSideState.TYPE, PacketBlockSideState.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketEmptyTank.TYPE, PacketEmptyTank.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketTrapPlayers.TYPE, PacketTrapPlayers.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketHostileSpawnState.TYPE, PacketHostileSpawnState.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketLockToAllowedPlayers.TYPE, PacketLockToAllowedPlayers.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		
		registrar.playToServer(PacketConnectionType.TYPE, PacketConnectionType.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketSideState.TYPE, PacketSideState.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketSideGuide.TYPE, PacketSideGuide.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);

		registrar.playToServer(PacketArmourItem.TYPE, PacketArmourItem.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketChargerEnergyState.TYPE, PacketChargerEnergyState.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketGeneratorMode.TYPE, PacketGeneratorMode.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketGeneratorEmptyTank.TYPE, PacketGeneratorEmptyTank.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		
		registrar.playToServer(PacketWorkbench.TYPE, PacketWorkbench.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		
		//registrar.playToServer(PacketUpgradeStationCraft.TYPE, PacketUpgradeStationCraft.STREAM_CODEC, null);
		
		registrar.playToServer(PacketFocus.TYPE, PacketFocus.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketFocusTeleport.TYPE, PacketFocusTeleport.STREAM_CODEC, PocketServerPacketHandler::handleDataOnNetwork);
		
		DimensionalPockets.CONSOLE.startup("Pocket Network Setup complete.");
	}
}