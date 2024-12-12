package com.tcn.dimensionalpocketsii.pocket.core.event;

import com.tcn.dimensionalpocketsii.pocket.core.Pocket;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class PocketEvent extends Event implements ICancellableEvent {

	private final Level level;
	private final Pocket pocket;
	
	public PocketEvent(Level levelIn, Pocket pocketIn) {
		this.level = levelIn;
		this.pocket = pocketIn;
	}
	
	public Level getLevel() {
		return this.level;
	}

	public Pocket getPocket() {
		return this.pocket;
	}

	public static class GeneratePocketEvent extends PocketEvent {

		public GeneratePocketEvent(Level levelIn, Pocket pocketIn) {
			super(levelIn, pocketIn);
		}
		
	}
}
