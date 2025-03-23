package com.tcn.dimensionalpocketsii.core.item.armour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.network.chat.BaseComponent;

public enum ElytraSettings {
	TELEPORT_TO_BLOCK(0, "tele_to_block", "dimensionalpocketsii.elytraplate.setting.teleport", "dimensionalpocketsii.item.info.elytraplate.setting.teleport", 
			"dimensionalpocketsii.item.info.elytraplate.setting.teleport.true", "dimensionalpocketsii.item.info.elytraplate.setting.teleport.false", ComponentColour.GREEN),
	
	ELYTRA_FLY(1, "elytra_fly", "dimensionalpocketsii.elytraplate.setting.elytra_fly", "dimensionalpocketsii.item.info.elytraplate.setting.elytra_fly", 
			"dimensionalpocketsii.item.info.elytraplate.setting.elytra_fly.enabled", "dimensionalpocketsii.item.info.elytraplate.setting.elytra_fly.disabled", ComponentColour.CYAN),
	
	VISOR(2, "visor", "dimensionalpocketsii.elytraplate.setting.visor", "dimensionalpocketsii.item.info.elytraplate.setting.visor",
			"dimensionalpocketsii.item.info.elytraplate.setting.visor.enabled", "dimensionalpocketsii.item.info.elytraplate.setting.visor.disabled", ComponentColour.LIGHT_BLUE),
	
	SOLAR(3, "solar", "dimensionalpocketsii.elytraplate.setting.solar", "dimensionalpocketsii.item.info.elytraplate.setting.solar",
			"dimensionalpocketsii.item.info.elytraplate.setting.solar.enabled", "dimensionalpocketsii.item.info.elytraplate.setting.solar.disabled", ComponentColour.BLUE),
	
	CHARGER(4, "charger", "dimensionalpocketsii.elytraplate.setting.charger", "dimensionalpocketsii.item.info.elytraplate.setting.charger",
			"dimensionalpocketsii.item.info.elytraplate.setting.charger.enabled", "dimensionalpocketsii.item.info.elytraplate.setting.charger.disabled", ComponentColour.RED);
	
	private int index;
	private String name;
	private final String localizedName;
	private final String displayName;
	
	private final String trueValue;
	private final String falseValue;
	
	private final ComponentColour displayColour;
	
	public static final int LENGTH = 5;
	
	ElytraSettings(int indexIn, String nameIn, String localizedNameIn, String displayNameIn, String trueValueIn, String falseValueIn, ComponentColour displayColourIn) {
		this.index = indexIn;
		this.name = nameIn;
		this.localizedName = localizedNameIn;
		this.displayName = displayNameIn;
		this.displayColour = displayColourIn;
		
		this.trueValue = trueValueIn;
		this.falseValue = falseValueIn;
	}
	
	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public BaseComponent getColouredComp() {
		return ComponentHelper.style(this.displayColour, this.localizedName);
	}

	public BaseComponent getColouredDisplayComp() {
		return ComponentHelper.style(ComponentColour.GRAY, this.displayName);
	}
	
	public BaseComponent getValueComp(boolean value) {
		if (value) {
			return ComponentHelper.style(ComponentColour.GREEN, "bold", this.trueValue);
		} else {
			return ComponentHelper.style(ComponentColour.RED, "bold", this.falseValue);
		}
	}

	public ElytraSettings getNextState() {
		switch(this) {
			case TELEPORT_TO_BLOCK:
				return ELYTRA_FLY;
			case ELYTRA_FLY:
				return VISOR;
			case VISOR:
				return SOLAR;
			case SOLAR:
				return CHARGER;
			case CHARGER:
				return TELEPORT_TO_BLOCK;
			default:
				throw new IllegalStateException("Unable to obtain state of [" + this + "]");
		}
	}

	public static ElytraSettings getNextState(ElytraSettings previous) {
		switch(previous) {
		case TELEPORT_TO_BLOCK:
			return ELYTRA_FLY;
		case ELYTRA_FLY:
			return VISOR;
		case VISOR:
			return SOLAR;
		case SOLAR:
			return CHARGER;
		case CHARGER:
			return TELEPORT_TO_BLOCK;
		default:
			throw new IllegalStateException("Unable to obtain state of [" + previous + "]");
		}
	}
	
	public static ElytraSettings getStateFromIndex(int index) {
		switch(index) {
			case 0:
				return TELEPORT_TO_BLOCK;
			case 1:
				return ELYTRA_FLY;
			case 2:
				return VISOR;
			case 3:
				return SOLAR;
			case 4:
				return CHARGER;
			default:
				throw new IllegalStateException("No state exists with index: [" + index + "]");
		}
	}
}