package com.tcn.dimensionalpocketsii;


import com.tcn.cosmoslibrary.runtime.common.CosmosRuntime;
import com.tcn.dimensionalpocketsii.client.renderer.DimensionalTridentBEWLR;
import com.tcn.dimensionalpocketsii.client.renderer.ElytraplateBEWLR;
import com.tcn.dimensionalpocketsii.client.screen.ScreenConfiguration;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@Mod(value = DimensionalPockets.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DimensionalPocketsClient {

	@OnlyIn(Dist.CLIENT)
	public DimensionalPocketsClient(ModContainer container) {
		CosmosRuntime.Client.regiserConfigScreen(container, ScreenConfiguration::new);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemRenderers(RegisterClientExtensionsEvent event) {
		CosmosRuntime.Client.registerBEWLRToItem(event, ElytraplateBEWLR.INSTANCE, PocketsRegistrationManager.DIMENSIONAL_ELYTRAPLATE.get());
		
		CosmosRuntime.Client.registerBEWLRToItems(event, DimensionalTridentBEWLR.INSTANCE, 
			PocketsRegistrationManager.DIMENSIONAL_TRIDENT.get(),
			PocketsRegistrationManager.DIMENSIONAL_TRIDENT_ENHANCED.get()
		);
	}
}