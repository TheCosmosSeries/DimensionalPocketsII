package com.tcn.dimensionalpocketsii.core.management;

import com.tcn.dimensionalpocketsii.DimensionalPockets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PocketsSoundManager {
	
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DimensionalPockets.MOD_ID);

	public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_IN = SOUND_EVENTS.register("sound_portal_in", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "sound_portal_in")));
	public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_OUT = SOUND_EVENTS.register("sound_portal_out", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "sound_portal_in")));
	
	public static final DeferredHolder<SoundEvent, SoundEvent> WOOSH = SOUND_EVENTS.register("sound_woosh", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "sound_woosh")));

	public static final DeferredHolder<SoundEvent, SoundEvent> TINK = SOUND_EVENTS.register("sound_tink", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "sound_tink")));
	
	public static void register(IEventBus bus) {
		SOUND_EVENTS.register(bus);
	}
}