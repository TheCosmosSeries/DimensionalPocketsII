package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SoundManager {
	
	@EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
	public static class GENERIC {

		public static final SoundEvent PORTAL = new SoundEvent(new ResourceLocation(DimensionalPockets.MOD_ID + ":" + "portal_in")).setRegistryName("portal_in");
		public static final SoundEvent WOOSH = new SoundEvent(new ResourceLocation(DimensionalPockets.MOD_ID + ":" + "woosh")).setRegistryName("woosh");

		public static final SoundEvent TINK = new SoundEvent(new ResourceLocation(DimensionalPockets.MOD_ID + ":" + "tink")).setRegistryName("tink");
		
		@SubscribeEvent
		public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
			event.getRegistry().registerAll(PORTAL, WOOSH, TINK);
			
			DimensionalPockets.CONSOLE.startup("Sound Registration complete.");
		}
	}	
}