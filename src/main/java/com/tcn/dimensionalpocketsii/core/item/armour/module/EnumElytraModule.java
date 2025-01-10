package com.tcn.dimensionalpocketsii.core.item.armour.module;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.network.chat.MutableComponent;

public enum EnumElytraModule {

	SHIFTER(0, "shifter", "dimensionalpocketsii.armour_module.shifter", ComponentColour.GREEN, PocketsRegistrationManager.ARMOUR_MODULE_SHIFTER.get(), ElytraSettings.TELEPORT_TO_BLOCK),
	SCREEN(1, "screen", "dimensionalpocketsii.armour_module.screen", ComponentColour.CYAN, PocketsRegistrationManager.ARMOUR_MODULE_SCREEN.get(), null),
	VISOR(2, "visor", "dimensionalpocketsii.armour_module.visor", ComponentColour.LIGHT_BLUE, PocketsRegistrationManager.ARMOUR_MODULE_VISOR.get(), ElytraSettings.VISOR),
	SOLAR(3, "solar", "dimensionalpocketsii.armour_module.solar", ComponentColour.TURQUOISE, PocketsRegistrationManager.ARMOUR_MODULE_SOLAR.get(), ElytraSettings.SOLAR),
	BATTERY(4, "battery", "dimensionalpocketsii.armour_module.battery", ComponentColour.RED, PocketsRegistrationManager.ARMOUR_MODULE_BATTERY.get(), ElytraSettings.CHARGER),
	ENDER_CHEST(5, "screen_ender_chest", "dimensionalpocketsii.armour_module.ender_chest", ComponentColour.DARK_CYAN, PocketsRegistrationManager.ARMOUR_MODULE_ENDER_CHEST.get(), null),
	FIREWORK(6, "firework", "dimensionalpocketsii.armour_module.firework", ComponentColour.YELLOW, PocketsRegistrationManager.ARMOUR_MODULE_FIREWORK.get(), ElytraSettings.FIREWORK);
	
	private int index;
	private String name;
	private final String localizedName;
	private final ComponentColour displayColour;
	private IModuleItem moduleItem;
	private ElytraSettings setting;
	
	public static final int LENGTH = 7;
	
	EnumElytraModule(int indexIn, String nameIn, String localizedName, ComponentColour displayColour, IModuleItem moduleItemIn, ElytraSettings settingIn) {
		this.index = indexIn;
		this.name = nameIn;
		this.localizedName = localizedName;
		this.displayColour = displayColour;
		this.moduleItem = moduleItemIn;
		this.setting = settingIn;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}
	
	public MutableComponent getColouredComp() {
		return ComponentHelper.style(this.displayColour, this.localizedName);
	}
	
	public IModuleItem getModuleItem() {
		return this.moduleItem;
	}
	
	public ElytraSettings getSetting() {
		return this.setting;
	}

	public EnumElytraModule getNextState() {
		switch(this) {
			case SHIFTER:
				return SCREEN;
			case SCREEN:
				return VISOR;
			case VISOR:
				return SOLAR;
			case SOLAR:
				return BATTERY;
			case BATTERY:
				return ENDER_CHEST;
			case ENDER_CHEST:
				return FIREWORK;
			case FIREWORK:
				return SHIFTER;
			default:
				throw new IllegalStateException("Unable to obtain state of [" + this + "]");
		}
	}

	public static EnumElytraModule getNextState(EnumElytraModule previous) {
		switch(previous) {
		case SHIFTER:
			return SCREEN;
		case SCREEN:
			return VISOR;
		case VISOR:
			return SOLAR;
		case SOLAR:
			return BATTERY;
		case BATTERY:
			return ENDER_CHEST;
		case ENDER_CHEST:
			return FIREWORK;
		case FIREWORK:
			return SHIFTER;
		default:
			throw new IllegalStateException("Unable to obtain state of [" + previous + "]");
		}
	}
	
	public static EnumElytraModule getStateFromIndex(int index) {
		switch(index) {
			case 0:
				return SHIFTER;
			case 1:
				return SCREEN;
			case 2:
				return VISOR;
			case 3:
				return SOLAR;
			case 4:
				return BATTERY;
			case 5:
				return ENDER_CHEST;
			case 6:
				return FIREWORK;
			default:
				throw new IllegalStateException("No state exists with index: [" + index + "]");
		}
	}
}