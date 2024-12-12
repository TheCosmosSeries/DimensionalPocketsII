package com.tcn.dimensionalpocketsii.core.item.armour.module;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

public enum BaseElytraModule {

	SHIFTER(0, "shifter", "dimensionalpocketsii.armour_module.shifter", ComponentColour.GREEN, ModRegistrationManager.ARMOUR_MODULE_SHIFTER.get(), ElytraSettings.TELEPORT_TO_BLOCK),
	SCREEN(1, "screen", "dimensionalpocketsii.armour_module.screen", ComponentColour.CYAN, ModRegistrationManager.ARMOUR_MODULE_SCREEN.get(), null),
	VISOR(2, "visor", "dimensionalpocketsii.armour_module.visor", ComponentColour.LIGHT_BLUE, ModRegistrationManager.ARMOUR_MODULE_VISOR.get(), ElytraSettings.VISOR),
	SOLAR(3, "solar", "dimensionalpocketsii.armour_module.solar", ComponentColour.TURQUOISE, ModRegistrationManager.ARMOUR_MODULE_SOLAR.get(), ElytraSettings.SOLAR),
	BATTERY(4, "battery", "dimensionalpocketsii.armour_module.battery", ComponentColour.RED, ModRegistrationManager.ARMOUR_MODULE_BATTERY.get(), ElytraSettings.CHARGER),
	ENDER_CHEST(5, "screen_ender_chest", "dimensionalpocketsii.armour_module.ender_chest", ComponentColour.DARK_CYAN, ModRegistrationManager.ARMOUR_MODULE_ENDER_CHEST.get(), null);
	
	private int index;
	private String name;
	private final String localizedName;
	private final ComponentColour displayColour;
	private Item moduleItem;
	private ElytraSettings setting;
	
	public static final int LENGTH = 6;
	
	BaseElytraModule(int indexIn, String nameIn, String localizedName, ComponentColour displayColour, Item moduleItemIn, ElytraSettings settingIn) {
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
	
	public Item getModuleItem() {
		return this.moduleItem;
	}
	
	public ElytraSettings getElytraSetting() {
		return this.setting;
	}

	public BaseElytraModule getNextState() {
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
				return SHIFTER;
			default:
				throw new IllegalStateException("Unable to obtain state of [" + this + "]");
		}
	}

	public static BaseElytraModule getNextState(BaseElytraModule previous) {
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
			return SHIFTER;
		default:
			throw new IllegalStateException("Unable to obtain state of [" + previous + "]");
		}
	}
	
	public static BaseElytraModule getStateFromIndex(int index) {
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
			default:
				throw new IllegalStateException("No state exists with index: [" + index + "]");
		}
	}
}