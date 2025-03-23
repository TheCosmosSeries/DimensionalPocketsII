package com.tcn.dimensionalpocketsii.pocket.core.shift;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.management.SoundManager;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum EnumShiftDirection {
	
	ENTER(0, "enter", "Enter", "dimensionalpocketsii.pocket.direction.enter", ComponentColour.GREEN, SoundManager.GENERIC.PORTAL),
	LEAVE(1, "leave", "Leave", "dimensionalpocketsii.pocket.direction.leave", ComponentColour.ORANGE, SoundManager.GENERIC.PORTAL),
	GENERIC(2, "generic", "Generic", "dimensionalpocketsii.pocket.direcion.generic", ComponentColour.CYAN, SoundEvents.PORTAL_TRAVEL),
	UNKNOWN(-1, "unknown", "Unknown", "dimensionalpocketsii.pocket.direction.unknown", ComponentColour.RED, null);
	
	private final int index;
	private final String name;
	private final String display_name;
	private final String localized_name;
	private final ComponentColour display_colour;
	private final SoundEvent sound;
	
	private static final EnumShiftDirection[] VALUES = values();
	
	EnumShiftDirection(int indexIn, String nameIn, String displayNameIn, String localizedNameIn, ComponentColour displayColourIn, @Nullable SoundEvent soundIn) {
		this.index = indexIn;
		this.name = nameIn;
		this.display_name = displayNameIn;
		this.localized_name = localizedNameIn;
		this.display_colour = displayColourIn;
		this.sound = soundIn;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.display_name;
	}

	public Component getUseName() {
		return ComponentHelper.style(display_colour, "bold", this.localized_name);
	}

	public String getLocalizedName() {
		return this.localized_name;
	}
	
	public static EnumShiftDirection getDirectionFromIndex(int index) {
		switch (index) {
			case 0:
				return ENTER;
			case 1:
				return LEAVE;
			default:
				return UNKNOWN;
		}
	}

	public ComponentColour getDisplayColour() {
		return display_colour;
	}

	public static EnumShiftDirection[] getValues() {
		return VALUES;
	}
	
	public Component getChatComponentForDirection() {
		return ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.pocket.direction.pre").append(getUseName()).append(ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.pocket.direction.suff"));
	}

	public SoundEvent getSound() {
		return this.sound;
	}
}
