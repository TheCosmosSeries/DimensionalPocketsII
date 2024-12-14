package com.tcn.dimensionalpocketsii.client.screen;

import java.util.Arrays;
import java.util.UUID;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonUIMode;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.ModReferences.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateSettings;
import com.tcn.dimensionalpocketsii.client.screen.button.DimensionalButton;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateSettingsChange;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ScreenElytraplateSettings extends AbstractContainerScreen<ContainerElytraplateSettings> {
	private int[] screenCoords;
	
	private UUID playerUUID;
	private ItemStack stack;

	protected CosmosButtonUIMode uiModeButton;
	
	private DimensionalButton elytraFlyButton;   private int indexE[] = new int[] { 31,  19, 20 };
	private DimensionalButton teleToBlockButton; private int indexB[] = new int[] { 53,  19, 20 };
	private DimensionalButton visorButton; 		 private int indexV[] = new int[] { 75,  19, 20 };
	private DimensionalButton solarButton; 		 private int indexS[] = new int[] { 97,  19, 20 };
	private DimensionalButton chargerButton; 	 private int indexC[] = new int[] { 119, 19, 20 };
	private DimensionalButton fireworkButton; 	 private int indexF[] = new int[] { 141, 19, 20 };

	public ScreenElytraplateSettings(ContainerElytraplateSettings containerIn, Inventory inventoryIn, Component componentIn) {
		super(containerIn, inventoryIn, componentIn);
		
		this.playerUUID = this.getMenu().getPlayer().getUUID();
		this.stack = this.getMenu().getStack();
	
		this.setImageDims(192, 144);
		this.setTitleLabelDims(36, 5);
		this.setInventoryLabelDims(5, 55);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		this.setScreenCoords(CosmosUISystem.getScreenCoords(this, this.imageWidth, this.imageHeight));
		
		this.addButtons();
		super.init();
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(graphics, mouseX, mouseY, partialTicks);
		super.render(graphics, mouseX, mouseY, partialTicks);

		this.addButtons();
		this.renderComponentHoverEffect(graphics, Style.EMPTY, mouseX, mouseY);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		DimensionalElytraplate item = (DimensionalElytraplate) this.stack.getItem();
		
		CosmosUISystem.renderStaticElementWithUIMode(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_SETTINGS);
		CosmosUISystem.renderStaticElementWithUIMode(this, graphics, getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_SETTINGS_OVERLAY);
		
		CosmosUISystem.renderEnergyDisplay(graphics, ComponentColour.RED, item.getEnergy(this.stack) / 1000, item.getMaxEnergyStored(this.stack) / 1000, 116, this.getScreenCoords(), 38, 43, 116, 7, true);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec(), false);
		
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec(), false);
	}
	
	public void renderComponentHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {
		DimensionalElytraplate item = (DimensionalElytraplate) this.stack.getItem();
		
		if (this.uiModeButton.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.ui_mode.info"),
				ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.ui_mode.value").append(this.getUIMode().getColouredComp())
			};
			
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.elytraFlyButton.isMouseOver(mouseX, mouseY)) {
			ElytraSettings setting = ElytraSettings.ELYTRA_FLY;
			boolean value = DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			Component compV = setting.getValueComp(value);
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.fly_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.fly_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.teleToBlockButton.isMouseOver(mouseX, mouseY)) {
			ElytraSettings setting = ElytraSettings.TELEPORT_TO_BLOCK;
			boolean value = DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			Component compV = setting.getValueComp(value);
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.tele_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.tele_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.visorButton.isMouseOver(mouseX, mouseY)) {
			ElytraSettings setting = ElytraSettings.VISOR;
			boolean value = DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			Component compV = setting.getValueComp(value);
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.visor_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.visor_value").append(compV)//,
				//ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocktesii.gui.elytraplate.settings.visor_warn")
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.solarButton.isMouseOver(mouseX, mouseY)) {
			ElytraSettings setting = ElytraSettings.SOLAR;
			boolean value = DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			Component compV = setting.getValueComp(value);
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.solar_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.solar_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.chargerButton.isMouseOver(mouseX, mouseY)) {
			ElytraSettings setting = ElytraSettings.CHARGER;
			boolean value = DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			Component compV = setting.getValueComp(value);
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.charger_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.charger_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.fireworkButton.isMouseOver(mouseX, mouseY)) {
			ElytraSettings setting = ElytraSettings.FIREWORK;
			boolean value = DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			Component compV = setting.getValueComp(value);
			
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.elytraplate.settings.firework_info"), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.elytraplate.settings.firework_value").append(compV)
			};
				
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		}

		else if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 38, this.getScreenCoords()[0] + 153, this.getScreenCoords()[1] + 43, this.getScreenCoords()[1] + 49)) {
			DecimalFormat formatter = new DecimalFormat("#,###,###,###");
			String amount_string = formatter.format(item.getEnergy(this.stack));
			String capacity_string = formatter.format(item.getMaxEnergyStored(this.stack));
			
			Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"), ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.energy_bar.suff") };
			
			graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
		}
	}

	protected void addButtons() {
		this.clearWidgets();
		
		this.uiModeButton = this.addRenderableWidget(new CosmosButtonUIMode(this.getUIMode(), this.getScreenCoords()[0] + 159, this.getScreenCoords()[1] + 5, true, true, ComponentHelper.empty(), (button) -> { this.changeUIMode(); } ));
		
		this.elytraFlyButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexE[0], this.getScreenCoords()[1] + indexE[1], indexE[2], true, true, DimensionalElytraplate.getElytraSetting(this.stack, ElytraSettings.ELYTRA_FLY)[1] ? 16 : 17, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.teleToBlockButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexB[0], this.getScreenCoords()[1] + indexB[1], indexB[2], DimensionalElytraplate.hasModuleInstalled(this.stack, BaseElytraModule.SHIFTER), true, DimensionalElytraplate.getElytraSetting(this.stack, ElytraSettings.TELEPORT_TO_BLOCK)[1] ? 18 : 19, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.visorButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexV[0], this.getScreenCoords()[1] + indexV[1], indexV[2], DimensionalElytraplate.hasModuleInstalled(this.stack, BaseElytraModule.VISOR), true, DimensionalElytraplate.getElytraSetting(this.stack, ElytraSettings.VISOR)[1] ? 20 : 21, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.solarButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexS[0], this.getScreenCoords()[1] + indexS[1], indexS[2], DimensionalElytraplate.hasModuleInstalled(this.stack, BaseElytraModule.SOLAR), true, DimensionalElytraplate.getElytraSetting(this.stack, ElytraSettings.SOLAR)[1] ? 22 : 23, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.chargerButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexC[0], this.getScreenCoords()[1] + indexC[1], indexC[2], DimensionalElytraplate.hasModuleInstalled(this.stack, BaseElytraModule.BATTERY), true, DimensionalElytraplate.getElytraSetting(this.stack, ElytraSettings.CHARGER)[1] ? 24 : 25, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
		this.fireworkButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + indexF[0], this.getScreenCoords()[1] + indexF[1], indexF[2], DimensionalElytraplate.hasModuleInstalled(this.stack, BaseElytraModule.FIREWORK), true, DimensionalElytraplate.getElytraSetting(this.stack, ElytraSettings.FIREWORK)[1] ? 30 : 31, ComponentHelper.empty(), (button) -> { this.pushButton(button); }, (button) -> { return button.get(); }));
	}
	
	private void pushButton(Button button) {
		if (button.equals(this.elytraFlyButton)) {
			ElytraSettings setting = ElytraSettings.ELYTRA_FLY;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.playerUUID, 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.stack, setting, toSet);
		} else if (button.equals(this.teleToBlockButton)) {
			ElytraSettings setting = ElytraSettings.TELEPORT_TO_BLOCK;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.playerUUID, 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.stack, setting, toSet);
		} else if (button.equals(this.visorButton)) {
			ElytraSettings setting = ElytraSettings.VISOR;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.playerUUID, 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.stack, setting, toSet);
		} else if (button.equals(this.solarButton)) {
			ElytraSettings setting = ElytraSettings.SOLAR;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.playerUUID, 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.stack, setting, toSet);
		} else if (button.equals(this.chargerButton)) {
			ElytraSettings setting = ElytraSettings.CHARGER;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.playerUUID, 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.stack, setting, toSet);
		}
		 else if (button.equals(this.fireworkButton)) {
			ElytraSettings setting = ElytraSettings.FIREWORK;
			boolean toSet = !DimensionalElytraplate.getElytraSetting(stack, setting)[1];
			
			PacketDistributor.sendToServer(new PacketElytraplateSettingsChange(this.playerUUID, 2, setting, toSet));
			DimensionalElytraplate.addOrUpdateElytraSetting(this.stack, setting, toSet);
		}
	}

	protected void setImageDims(int widthIn, int heightIn) {
		this.imageWidth = widthIn;
		this.imageHeight = heightIn;
	}
	
	protected void setTitleLabelDims(int posX, int posY) {
		this.titleLabelX = posX;
		this.titleLabelY = posY;
	}
	
	protected void setInventoryLabelDims(int posX, int posY) {
		this.inventoryLabelX = posX;
		this.inventoryLabelY = posY;
	}
	
	protected void setScreenCoords(int[] coordsIn) {
		this.screenCoords = coordsIn;
	}
	
	protected int[] getScreenCoords() {
		return this.screenCoords;
	}

	public EnumUIMode getUIMode() {
		if (this.stack != null) {
			return DimensionalElytraplate.getUIMode(this.stack);
		}
		
		return EnumUIMode.DARK;
	}

	private void changeUIMode() {
		PacketDistributor.sendToServer(new PacketElytraplateUpdateUIMode(this.playerUUID, 2, this.getUIMode().getNextState()));
		DimensionalElytraplate.setUIMode(this.stack, this.getUIMode().getNextState());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double directionX, double directionY) {
		return super.mouseScrolled(mouseX, mouseY, directionX, directionY);
	}

	@Override
	public boolean keyPressed(int keyCode, int mouseX, int mouseY) {
		return super.keyPressed(keyCode, mouseX, mouseY);
	}

	@Override
	public boolean charTyped(char charIn, int p_98522_) {
		return super.charTyped(charIn, p_98522_);
	}
	
	@Override
	public void resize(Minecraft mc, int width, int height) {
		super.resize(mc, width, height);
	}
}