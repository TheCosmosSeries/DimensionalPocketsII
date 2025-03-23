package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.common.enums.EnumGeneralEnableState;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.screen.button.DimensionalButton;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerFocus;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityFocus;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketNetworkManager;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketFocus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenFocus extends CosmosScreenUIModeBE<ContainerFocus> {
	
	private DimensionalButton jumpEnabledButton;
	private DimensionalButton shiftEnabledButton;
	
	public ScreenFocus(ContainerFocus containerIn, Inventory inventoryIn, Component componentIn) {
		super(containerIn, inventoryIn, componentIn);

		this.setImageDims(172, 144);
		
		this.setLight(RESOURCE.FOCUS[0]);
		this.setDark(RESOURCE.FOCUS[1]);
		this.setUIModeButtonIndex(134, 5);
		
		this.setTitleLabelDims(36, 5);
		this.setInventoryLabelDims(5, 55);
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
		
		if (entity instanceof BlockEntityFocus) {
			BlockEntityFocus blockEntity = (BlockEntityFocus) entity;
			
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, RESOURCE.FOCUS_SLOTS);
		}
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityFocus) {
			BlockEntityFocus blockEntity = (BlockEntityFocus) entity;
			
			if (this.jumpEnabledButton.isMouseOver(mouseX, mouseY)) {
				EnumGeneralEnableState state = blockEntity.getJump();
				
				Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.focus.jump_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.focus.jump_value").append(state.getColouredComp())
				};
					
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.shiftEnabledButton.isMouseOver(mouseX, mouseY)) {
				EnumGeneralEnableState state = blockEntity.getShift();
				
				Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.focus.shift_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.focus.shift_value").append(state.getColouredComp())
				};
					
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityFocus) {
			BlockEntityFocus blockEntity = (BlockEntityFocus) entity;
			
			this.jumpEnabledButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + 93, this.getScreenCoords()[1] + 19, 20, true, true, blockEntity.getJumpEnabled() ? 26 : 27, ComponentHelper.empty(), (button) -> { this.pushButton(button); }));
			this.shiftEnabledButton = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + 59, this.getScreenCoords()[1] + 19, 20, true, true, blockEntity.getShiftEnabled() ? 28 : 29, ComponentHelper.empty(), (button) -> { this.pushButton(button); }));
		}
	}
	
	@Override
	protected void pushButton(Button button) {
		super.pushButton(button);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityFocus) {
			BlockEntityFocus blockEntity = (BlockEntityFocus) entity;
			
			if (button.equals(this.jumpEnabledButton)) {
				EnumGeneralEnableState state = blockEntity.getJump();
				
				PocketNetworkManager.sendToServer(new PacketFocus(this.menu.getBlockPos(), !state.getValue(), true));
				blockEntity.setJumpEnabled(!state.getValue());
			} else if (button.equals(this.shiftEnabledButton)) {
				EnumGeneralEnableState state = blockEntity.getShift();
				
				PocketNetworkManager.sendToServer(new PacketFocus(this.menu.getBlockPos(), !state.getValue(), false));
				blockEntity.setJumpEnabled(!state.getValue());
			}
		}
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}