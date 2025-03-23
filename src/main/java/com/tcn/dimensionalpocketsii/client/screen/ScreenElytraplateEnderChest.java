package com.tcn.dimensionalpocketsii.client.screen;

import java.util.Arrays;
import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonUIMode;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateEnderChest;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.management.NetworkManager;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateUpdateUIMode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenElytraplateEnderChest extends AbstractContainerScreen<ContainerElytraplateEnderChest> {
	private int[] screenCoords;
	
	private UUID playerUUID;
	private ItemStack stack;

	protected CosmosButtonUIMode uiModeButton;
	
	public ScreenElytraplateEnderChest(ContainerElytraplateEnderChest containerIn, Inventory inventoryIn, Component componentIn) {
		super(containerIn, inventoryIn, componentIn);
		
		this.playerUUID = this.getMenu().getPlayer().getUUID();
		this.stack = this.getMenu().getStack();
			
		this.setImageDims(202, 164);
		this.setTitleLabelDims(20, 5);
		this.setInventoryLabelDims(10, 70);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		this.setScreenCoords(CosmosUISystem.getScreenCoords(this, this.imageWidth, this.imageHeight));
		super.init();
		this.addButtons();
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);

		this.addButtons();
		
		this.renderComponentHoverEffect(poseStack, Style.EMPTY, mouseX, mouseY);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_ENDER_CHEST);
		CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_ENDER_CHEST_OVERLAY);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec());
		
		this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec());
	}

	@Override
	public void renderComponentHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		if (this.uiModeButton.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.ui_mode.info"),
				ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.ui_mode.value").append(this.getUIMode().getColouredComp())
			};
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		}
	}
	
	protected void addButtons() {
		this.clearWidgets();
		
		this.uiModeButton = this.addRenderableWidget(new CosmosButtonUIMode(this.getUIMode(), this.getScreenCoords()[0] + 185, this.getScreenCoords()[1] + 5, true, true, ComponentHelper.empty(), (button) -> { this.changeUIMode(); } ));
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
		NetworkManager.sendToServer(new PacketElytraplateUpdateUIMode(this.playerUUID, 2, this.getUIMode().getNextState()));
		DimensionalElytraplate.setUIMode(this.stack, this.getUIMode().getNextState());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
		return super.mouseScrolled(mouseX, mouseY, direction);
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