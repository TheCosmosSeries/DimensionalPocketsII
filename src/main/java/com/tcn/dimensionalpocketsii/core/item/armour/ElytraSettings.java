package com.tcn.dimensionalpocketsii.core.item.armour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

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
			"dimensionalpocketsii.item.info.elytraplate.setting.charger.enabled", "dimensionalpocketsii.item.info.elytraplate.setting.charger.disabled", ComponentColour.RED),

	FIREWORK(5, "firework", "dimensionalpocketsii.elytraplate.setting.firework", "dimensionalpocketsii.item.info.elytraplate.setting.firework",
			"dimensionalpocketsii.item.info.elytraplate.setting.firework.enabled", "dimensionalpocketsii.item.info.elytraplate.setting.firework.disabled", ComponentColour.YELLOW);
	
	private int index;
	private String name;
	private final String localizedName;
	private final String displayName;
	
	private final String trueValue;
	private final String falseValue;
	
	private final ComponentColour displayColour;
	
	public static final int LENGTH = 5;

	public static final StreamCodec<ByteBuf, ElytraSettings> STREAM_CODEC = new StreamCodec<ByteBuf, ElytraSettings>() {
		@Override
        public ElytraSettings decode(ByteBuf bufIn) {
            return ElytraSettings.getStateFromIndex(bufIn.readInt());
        }

		@Override
        public void encode(ByteBuf bufIn, ElytraSettings modeIn) {
        	bufIn.writeInt(modeIn.getIndex());
        }
    };
    
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

	public MutableComponent getColouredComp() {
		return ComponentHelper.style(this.displayColour, this.localizedName);
	}

	public MutableComponent getColouredDisplayComp() {
		return ComponentHelper.style(ComponentColour.GRAY, this.displayName);
	}
	
	public MutableComponent getValueComp(boolean value) {
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
				return FIREWORK;
			case FIREWORK:
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
			return FIREWORK;
		case FIREWORK:
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
			case 5:
				return FIREWORK;
			default:
				throw new IllegalStateException("No state exists with index: [" + index + "]");
		}
	}
}