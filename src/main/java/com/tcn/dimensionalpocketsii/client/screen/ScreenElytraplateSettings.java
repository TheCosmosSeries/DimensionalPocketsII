package com.tcn.dimensionalpocketsii.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenItemStackUI;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.PocketReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateSettings;
import com.tcn.dimensionalpocketsii.client.screen.button.DimensionalButton;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.EnumElytraSetting;
import com.tcn.dimensionalpocketsii.core.item.armour.module.EnumElytraModule;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateSettingsChange;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIMode;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ScreenElytraplateSettings extends CosmosScreenItemStackUI<ContainerElytraplateSettings> {
	
	private DimensionalButton elytraFlyButton;   private int indexE[] = new int[] { 31,  19, 20 };
	private DimensionalButton teleToBlockButton; private int indexB[] = new int[] { 53,  19, 20 };
	private DimensionalButton visorButton; 		 private int indexV[] = new int[] { 75,  19, 20 };
	private DimensionalButton solarButton; 		 private int indexS[] = new int[] { 97,  19, 20 };
	private DimensionalButton chargerButton; 	 private int indexC[] = new int[] { 119, 19, 20 };
	private DimensionalButton fireworkButton; 	 private int indexF[] = new int[] { 141, 19, 20 };

	public ScreenElytraplateSettings(ContainerElytraplateSettings containerIn, Inventory inventoryIn, Component componentIn) {
		super(containerIn, inventoryIn, componentIn);
	
		this.setImageDims(192, 144);
		this.setUIModeButtonIndex(159, 5);
		this.setTitleLabelDims(38, 5);
		this.setInventoryLabelDims(5, 55);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		DimensionalElytraplate item = (DimensionalElytraplate) this.getStack().getItem();
		
		CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_SETTINGS);
		CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_SETTINGS_OVERLAY);
		
		CosmosUISystem.Render.renderEnergyDisplay(graphics, ComponentColour.RED, item.getEnergy(this.getStack()) / 1000, item.getMaxEnergyStored(this.getStack()) / 1000, this.getScreenCoords(), 38, 43, 116, 7, true);
	}

	@Override
	public void renderComponentHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {
		DimensionalElytraplate item = (DimensionalElytraplate) this.getStack().getItem();
		
		if (this.uiModeButton.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.ui_mode.info"),
				ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.ui_mode.value").append(this.getUIMode().getColouredComp())
			};
			
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.elytraFlyButton.isMouseOver(mouseX, mouseY)) {
			EnumElytraSetting setting = EnumElytraSetting.ELYTRA_FLY;
			Component compV = setting.getValueComp(DimensionalElytraplate.getElytraSetting(this.getStack(), setting));
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.fly_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.fly_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.teleToBlockButton.isMouseOver(mouseX, mouseY)) {
			EnumElytraSetting setting = EnumElytraSetting.TELEPORT_TO_BLOCK;
			Component compV = setting.getValueComp(DimensionalElytraplate.getElytraSetting(this.getStack(), setting));
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.tele_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.tele_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.visorButton.isMouseOver(mouseX, mouseY)) {
			EnumElytraSetting setting = EnumElytraSetting.VISOR;
			Component compV = setting.getValueComp(DimensionalElytraplate.getElytraSetting(this.getStack(), setting));
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.visor_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.visor_value").append(compV)//,
				//ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocktesii.gui.elytraplate.settings.visor_warn")
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.solarButton.isMouseOver(mouseX, mouseY)) {
			EnumElytraSetting setting = EnumElytraSetting.SOLAR;
			Component compV = setting.getValueComp(DimensionalElytraplate.getElytraSetting(this.getStack(), setting));
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.solar_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.solar_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.chargerButton.isMouseOver(mouseX, mouseY)) {
			EnumElytraSetting setting = EnumElytraSetting.CHARGER;
			Component compV = setting.getValueComp(DimensionalElytraplate.getElytraSetting(this.getStack(), setting));
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.charger_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.charger_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.fireworkButton.isMouseOver(mouseX, mouseY)) {
			EnumElytraSetting setting = EnumElytraSetting.FIREWORK;
			Component compV = setting.getValueComp(DimensionalElytraplate.getElytraSetting(this.getStack(), setting));
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.firework_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.firework_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		}

		else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 38, this.getScreenCoords()[0] + 153, this.getScreenCoords()[1] + 43, this.getScreenCoords()[1] + 49)) {
			DecimalFormat formatter = new DecimalFormat("#,###,###,###");
			String amount_string = formatter.format(item.getEnergy(this.getStack()));
			String capacity_string = formatter.format(item.getMaxEnergyStored(this.getStack()));
			
			Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"), ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.energy_bar.suff") };
			
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		}
	}

	@Override
	protected void addButtons() {
		super.addButtons();
		
		this.elytraFlyButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexE[0], this.getScreenCoords()[1] + indexE[1], indexE[2], true, true, DimensionalElytraplate.getElytraSetting(this.getStack(), EnumElytraSetting.ELYTRA_FLY) ? 16 : 17, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.teleToBlockButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexB[0], this.getScreenCoords()[1] + indexB[1], indexB[2], DimensionalElytraplate.hasModuleInstalled(this.getStack(), EnumElytraModule.SHIFTER), true, DimensionalElytraplate.getElytraSetting(this.getStack(), EnumElytraSetting.TELEPORT_TO_BLOCK) ? 18 : 19, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.visorButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexV[0], this.getScreenCoords()[1] + indexV[1], indexV[2], DimensionalElytraplate.hasModuleInstalled(this.getStack(), EnumElytraModule.VISOR), true, DimensionalElytraplate.getElytraSetting(this.getStack(), EnumElytraSetting.VISOR) ? 20 : 21, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.solarButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexS[0], this.getScreenCoords()[1] + indexS[1], indexS[2], DimensionalElytraplate.hasModuleInstalled(this.getStack(), EnumElytraModule.SOLAR), true, DimensionalElytraplate.getElytraSetting(this.getStack(), EnumElytraSetting.SOLAR) ? 22 : 23, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.chargerButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexC[0], this.getScreenCoords()[1] + indexC[1], indexC[2], DimensionalElytraplate.hasModuleInstalled(this.getStack(), EnumElytraModule.BATTERY), true, DimensionalElytraplate.getElytraSetting(this.getStack(), EnumElytraSetting.CHARGER) ? 24 : 25, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.fireworkButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexF[0], this.getScreenCoords()[1] + indexF[1], indexF[2], DimensionalElytraplate.hasModuleInstalled(this.getStack(), EnumElytraModule.FIREWORK), true, DimensionalElytraplate.getElytraSetting(this.getStack(), EnumElytraSetting.FIREWORK) ? 30 : 31, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
	}
	
	@Override
	protected void pushButton(Button button) {
		if (button.equals(this.elytraFlyButton)) {
			EnumElytraSetting setting = EnumElytraSetting.ELYTRA_FLY;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(this.getStack(), setting);
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.getPlayerUUID(), 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.getStack(), setting, toSet);
		} else if (button.equals(this.teleToBlockButton)) {
			EnumElytraSetting setting = EnumElytraSetting.TELEPORT_TO_BLOCK;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(this.getStack(), setting);
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.getPlayerUUID(), 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.getStack(), setting, toSet);
		} else if (button.equals(this.visorButton)) {
			EnumElytraSetting setting = EnumElytraSetting.VISOR;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(this.getStack(), setting);
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.getPlayerUUID(), 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.getStack(), setting, toSet);
		} else if (button.equals(this.solarButton)) {
			EnumElytraSetting setting = EnumElytraSetting.SOLAR;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(this.getStack(), setting);
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.getPlayerUUID(), 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.getStack(), setting, toSet);
		} else if (button.equals(this.chargerButton)) {
			EnumElytraSetting setting = EnumElytraSetting.CHARGER;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(this.getStack(), setting);
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.getPlayerUUID(), 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.getStack(), setting, toSet);
		}
		 else if (button.equals(this.fireworkButton)) {
			EnumElytraSetting setting = EnumElytraSetting.FIREWORK;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(this.getStack(), setting);
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.getPlayerUUID(), 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.getStack(), setting, toSet);
		}
	}

	@Override
	public EnumUIMode getUIMode() {
		if (this.getStack() != null) {
			return DimensionalElytraplate.getUIMode(this.getStack());
		}
		return EnumUIMode.DARK;
	}

	@Override
	protected void changeUIMode() {
		PacketDistributor.sendToServer(new PacketElytraplateUpdateUIMode(this.getPlayerUUID(), 2, this.getUIMode().getNextState()));
		DimensionalElytraplate.setUIMode(this.getStack(), this.getUIMode().getNextState());
	}
}