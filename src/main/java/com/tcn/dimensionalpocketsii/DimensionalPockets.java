package com.tcn.dimensionalpocketsii;

import com.tcn.cosmoslibrary.runtime.common.CosmosConsoleManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsConfigManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRecipeManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsSoundManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsWorldgenManager;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(DimensionalPockets.MOD_ID)
public final class DimensionalPockets {

	//This must NEVER EVER EVER CHANGE!
	public static final String MOD_ID = "dimensionalpocketsii";
		
	public static CosmosConsoleManager CONSOLE = new CosmosConsoleManager(DimensionalPockets.MOD_ID, true, true);
	
	public DimensionalPockets(ModContainer container, IEventBus bus) {
		container.registerConfig(ModConfig.Type.COMMON, PocketsConfigManager.SPEC, "dimensionalpockets-common-rev-5.1.toml");
		
		PocketsRegistrationManager.register(bus);
		PocketsWorldgenManager.register(bus);
		PocketsRecipeManager.register(bus);
		PocketsSoundManager.register(bus);
		
		bus.addListener(this::onFMLCommonSetup);
		bus.addListener(this::onFMLClientSetup);
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CONSOLE = new CosmosConsoleManager(DimensionalPockets.MOD_ID, PocketsConfigManager.getInstance().getDebugMessage(), PocketsConfigManager.getInstance().getInfoMessage());
		
		CONSOLE.updateDebugEnabled(PocketsConfigManager.getInstance().getDebugMessage());
		CONSOLE.updateInfoEnabled(PocketsConfigManager.getInstance().getInfoMessage());
		
		CONSOLE.startup("DimensionalPocketsII Common Setup complete.");
	}
	
	public void onFMLClientSetup(final FMLClientSetupEvent event) {		
		PocketsRegistrationManager.onFMLClientSetup(event);
		
		CONSOLE.startup("Dimensional PocketsII Client Setup complete.");
	}
}