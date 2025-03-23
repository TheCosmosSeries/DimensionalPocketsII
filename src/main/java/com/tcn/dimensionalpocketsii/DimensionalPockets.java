package com.tcn.dimensionalpocketsii;

import com.tcn.cosmoslibrary.common.runtime.CosmosConsoleManager;
import com.tcn.dimensionalpocketsii.core.advancement.CoreTriggers;
import com.tcn.dimensionalpocketsii.core.management.ConfigurationManager;
import com.tcn.dimensionalpocketsii.core.management.ForgeEventManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.core.management.NetworkManager;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketNetworkManager;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DimensionalPockets.MOD_ID)
public final class DimensionalPockets {

	//This must NEVER EVER CHANGE!
	public static final String MOD_ID = "dimensionalpocketsii";
		
	public static final CosmosConsoleManager CONSOLE = new CosmosConsoleManager(DimensionalPockets.MOD_ID, ConfigurationManager.getInstance().getDebugMessage(), ConfigurationManager.getInstance().getInfoMessage());
	
	public DimensionalPockets() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLCommonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLClientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigurationManager.spec, "dimensionalpockets-common-rev-5.1.toml");
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CriteriaTriggers.register(CoreTriggers.USE_SHIFTER_TRIGGER);
		ForgeEventManager.registerOresForGeneration();
		
		NetworkManager.register();
		PocketNetworkManager.register();
		
		CONSOLE.updateDebugEnabled(ConfigurationManager.getInstance().getDebugMessage());
		CONSOLE.updateInfoEnabled(ConfigurationManager.getInstance().getInfoMessage());
		
		CONSOLE.startup("DimensionalPocketsII Common Setup complete.");
	}

	public void onFMLClientSetup(final FMLClientSetupEvent event) {
		final ModLoadingContext context = ModLoadingContext.get();
		
		ModBusManager.registerClient(context);
		ModBusManager.onFMLClientSetup(event);
		
		CONSOLE.startup("Dimensional PocketsII Client Setup complete.");
	}
}