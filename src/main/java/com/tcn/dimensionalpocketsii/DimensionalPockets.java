package com.tcn.dimensionalpocketsii;

import com.tcn.cosmoslibrary.common.runtime.CosmosConsoleManager;
import com.tcn.dimensionalpocketsii.core.management.ModConfigManager;
import com.tcn.dimensionalpocketsii.core.management.ModRecipeManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.core.management.ModSoundManager;
import com.tcn.dimensionalpocketsii.core.management.ModWorldgenManager;

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
		container.registerConfig(ModConfig.Type.COMMON, ModConfigManager.SPEC, "dimensionalpockets-common-rev-5.1.toml");
		
		ModRegistrationManager.register(bus);
		ModWorldgenManager.register(bus);
		ModRecipeManager.register(bus);
		ModSoundManager.register(bus);
		
		bus.addListener(this::onFMLCommonSetup);
		bus.addListener(this::onFMLClientSetup);
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CONSOLE = new CosmosConsoleManager(DimensionalPockets.MOD_ID, ModConfigManager.getInstance().getDebugMessage(), ModConfigManager.getInstance().getInfoMessage());
		
		CONSOLE.updateDebugEnabled(ModConfigManager.getInstance().getDebugMessage());
		CONSOLE.updateInfoEnabled(ModConfigManager.getInstance().getInfoMessage());
		
		CONSOLE.startup("DimensionalPocketsII Common Setup complete.");
	}
	
	public void onFMLClientSetup(final FMLClientSetupEvent event) {		
		ModRegistrationManager.onFMLClientSetup(event);
		
		CONSOLE.startup("Dimensional PocketsII Client Setup complete.");
	}
}