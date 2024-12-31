package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.ServerPacketHandler;
import com.tcn.dimensionalpocketsii.core.network.packet.PacketDimensionChange;
import com.tcn.dimensionalpocketsii.core.network.packet.PacketTomeUpdate;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenConnector;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenEnderChest;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateOpenSettings;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateSettingsChange;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateShift;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateTagUpdate;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIHelp;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIMode;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUseEnergy;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUseFirework;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PocketsPacketManager {
	
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		
		registrar.playToServer(PacketElytraplateShift.TYPE, PacketElytraplateShift.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateTagUpdate.TYPE, PacketElytraplateTagUpdate.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateUseEnergy.TYPE, PacketElytraplateUseEnergy.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateSettingsChange.TYPE, PacketElytraplateSettingsChange.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateUpdateUIMode.TYPE, PacketElytraplateUpdateUIMode.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateUpdateUIHelp.TYPE, PacketElytraplateUpdateUIHelp.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);

		registrar.playToServer(PacketElytraplateOpenConnector.TYPE, PacketElytraplateOpenConnector.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateOpenSettings.TYPE, PacketElytraplateOpenSettings.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateOpenEnderChest.TYPE, PacketElytraplateOpenEnderChest.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketElytraplateUseFirework.TYPE, PacketElytraplateUseFirework.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		
		registrar.playToServer(PacketDimensionChange.TYPE, PacketDimensionChange.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		registrar.playToServer(PacketTomeUpdate.TYPE, PacketTomeUpdate.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
		
		DimensionalPockets.CONSOLE.startup("Pocket Network Setup complete.");
	}
}