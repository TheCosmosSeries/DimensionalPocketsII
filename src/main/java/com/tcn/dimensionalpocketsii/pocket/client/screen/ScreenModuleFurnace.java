package com.tcn.dimensionalpocketsii.pocket.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeRecipeBook;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleFurnace;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleFurnace extends CosmosScreenUIModeRecipeBook<Container, ContainerModuleFurnace> {
	
	public ScreenModuleFurnace(ContainerModuleFurnace containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(184, 177);
		
		this.setLight(RESOURCE.FURNACE[0]);
		this.setDark(RESOURCE.FURNACE[1]);

		this.setUIModeButtonIndex(167, 5);
		this.setUIHelpButtonIndex(167, 33);
		this.setUILockButtonIndex(167, 19);
		this.setUIHelpElementDeadzone(48, 13, 135, 72);
		
		this.setTitleLabelDims(this.imageWidth / 2 - 40, 4);
		this.setInventoryLabelDims(8, 75);
	}

	public void init() {
		super.init();
	}
	
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleFurnace) {
			BlockEntityModuleFurnace blockEntity = (BlockEntityModuleFurnace) entity;
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				int decimal = pocket.getDisplayColour();
				ComponentColour colour = ComponentColour.col(decimal);
				float[] rgb = null;
				
				if (colour.equals(ComponentColour.POCKET_PURPLE)) {
					rgb = ComponentColour.rgbFloatArray(ComponentColour.POCKET_PURPLE_LIGHT.dec());
				} else {
					rgb = ComponentColour.rgbFloatArray(decimal);
				}
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.FURNACE_BASE);
			}
			
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, GUI.RESOURCE.FURNACE_OVERLAY);
		}

		
		if (this.menu.isLit()) {
			int k = this.menu.getLitProgress();
			this.blit(poseStack, this.getScreenCoords()[0] + 53, this.getScreenCoords()[1] + 36 + 13 - k, 184, 12 - k, 14, k + 1);
		}

		int l = this.menu.getBurnProgress();
		this.blit(poseStack, this.getScreenCoords()[0] + 75, this.getScreenCoords()[1] + 34, 184, 14, l + 1, 16);
	}

	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 50, 15, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.furnace.input_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.input_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.input_slot_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 50, 51, 20, 20, ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.help.furnace.fuel_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.fuel_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.fuel_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.fuel_slot_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 50, 36, 20, 14, ComponentHelper.style(ComponentColour.YELLOW, "dimensionalpocketsii.gui.help.furnace.burn_indicator"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.burn_indicator_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.burn_indicator_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.burn_indicator_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 73, 33, 28, 19, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.gui.help.furnace.progress_indicator"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.progress_indicator_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.progress_indicator_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.progress_indicator_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 106, 29, 28, 28, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.furnace.output_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.output_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.output_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.furnace.output_slot_three")
		);
	}
	
	@Override
	protected boolean isHovering(int positionX, int positionY, int width, int height, double mouseX, double mouseY) {
		return super.isHovering(positionX, positionY, width, height, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void slotClicked(Slot slotIn, int mouseX, int mouseY, ClickType clickTypeIn) {
		super.slotClicked(slotIn, mouseX, mouseY, clickTypeIn);
	}

}