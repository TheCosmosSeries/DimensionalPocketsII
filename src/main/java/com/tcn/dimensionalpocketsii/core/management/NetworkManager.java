package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.network.PacketDimensionChange;
import com.tcn.dimensionalpocketsii.core.network.PacketTomeUpdate;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraItemStackTagUpdate;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraSettingsChange;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraShift;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraUseEnergy;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateOpenConnector;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateOpenEnderChest;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateOpenSettings;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateUpdateUIMode;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkManager {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(DimensionalPockets.MOD_ID, "network"), 
			() -> PROTOCOL_VERSION, 
			PROTOCOL_VERSION::equals, 
			PROTOCOL_VERSION::equals);
	
	public static void register() {
		INSTANCE.registerMessage(0, PacketElytraShift.class, PacketElytraShift::encode, PacketElytraShift::new, PacketElytraShift::handle);
		INSTANCE.registerMessage(1, PacketDimensionChange.class, PacketDimensionChange::encode, PacketDimensionChange::new, PacketDimensionChange::handle);
		INSTANCE.registerMessage(2, PacketElytraItemStackTagUpdate.class, PacketElytraItemStackTagUpdate::encode, PacketElytraItemStackTagUpdate::new, PacketElytraItemStackTagUpdate::handle);
		INSTANCE.registerMessage(3, PacketElytraUseEnergy.class, PacketElytraUseEnergy::encode, PacketElytraUseEnergy::new, PacketElytraUseEnergy::handle);
		
		INSTANCE.registerMessage(4, PacketTomeUpdate.class, PacketTomeUpdate::encode, PacketTomeUpdate::new, PacketTomeUpdate::handle);
		
		INSTANCE.registerMessage(5, PacketElytraSettingsChange.class, PacketElytraSettingsChange::encode, PacketElytraSettingsChange::new, PacketElytraSettingsChange::handle);
		INSTANCE.registerMessage(6, PacketElytraplateUpdateUIMode.class, PacketElytraplateUpdateUIMode::encode, PacketElytraplateUpdateUIMode::new, PacketElytraplateUpdateUIMode::handle);
		
		INSTANCE.registerMessage(7, PacketElytraplateOpenConnector.class, PacketElytraplateOpenConnector::encode, PacketElytraplateOpenConnector::new, PacketElytraplateOpenConnector::handle);
		INSTANCE.registerMessage(8, PacketElytraplateOpenSettings.class, PacketElytraplateOpenSettings::encode, PacketElytraplateOpenSettings::new, PacketElytraplateOpenSettings::handle);
		INSTANCE.registerMessage(9, PacketElytraplateOpenEnderChest.class, PacketElytraplateOpenEnderChest::encode, PacketElytraplateOpenEnderChest::new, PacketElytraplateOpenEnderChest::handle);

		DimensionalPockets.CONSOLE.startup("Dimensional Network Setup complete.");
	}

	public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}