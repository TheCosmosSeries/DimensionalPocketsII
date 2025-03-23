package com.tcn.dimensionalpocketsii.pocket.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleUpgradeStation;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleUpgradeStation extends CosmosScreenUIModeBE<ContainerModuleUpgradeStation> {

	public ScreenModuleUpgradeStation(ContainerModuleUpgradeStation containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(184, 189);
		
		this.setLight(RESOURCE.UPGRADE_STATION[0]);
		this.setDark(RESOURCE.UPGRADE_STATION[1]);

		this.setUIModeButtonIndex(167, 5);
		this.setUIHelpButtonIndex(167, 33);
		this.setUILockButtonIndex(167, 19);
		this.setUIHelpElementDeadzone(23, 13, 160, 86);
		
		this.setTitleLabelDims(34, 4);
		this.setInventoryLabelDims(8, 89);
	}

	@Override
	protected void init() {
		super.init();
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleUpgradeStation) {
			BlockEntityModuleUpgradeStation blockEntity = (BlockEntityModuleUpgradeStation) entity;
			
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
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.UPGRADE_STATION_BASE);
			}
			
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, GUI.RESOURCE.UPGRADE_STATION_OVERLAY);
		}
	}

	@Override
	public void renderStandardHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		super.renderStandardHoverEffect(poseStack, style, mouseX, mouseY);
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
	}
		
	
	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
	}

	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 124, 40, 20, 20, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.gui.help.upgrade_station.result_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.result_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.result_slot_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 61, 40, 20, 20, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.gui.help.upgrade_station.focused_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.focused_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.focused_slot_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 40, 19, 62, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.upgrade_station.input_slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 40, 40, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.upgrade_station.input_slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 40, 61, 62, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.upgrade_station.input_slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 82, 40, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.upgrade_station.input_slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.upgrade_station.input_slots_two")
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