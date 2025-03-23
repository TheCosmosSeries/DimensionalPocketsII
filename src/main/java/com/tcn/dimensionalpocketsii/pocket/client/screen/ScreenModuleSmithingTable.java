package com.tcn.dimensionalpocketsii.pocket.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleSmithingTable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleSmithingTable extends CosmosScreenUIModeBE<ContainerModuleSmithingTable> implements ContainerListener {

	public ScreenModuleSmithingTable(ContainerModuleSmithingTable containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(184, 177);
		
		this.setLight(RESOURCE.SMITHING_TABLE[0]);
		this.setDark(RESOURCE.SMITHING_TABLE[1]);

		this.setUIModeButtonIndex(167, 5);
		this.setUIHelpButtonIndex(167, 33);
		this.setUILockButtonIndex(167, 19);
		this.setUIHelpElementDeadzone(23, 13, 160, 86);
		
		this.setTitleLabelDims(this.imageWidth / 2 - 38, 4);
		this.setInventoryLabelDims(8, 75);
	}

	@Override
	protected void init() {
		super.init();
	      this.menu.addSlotListener(this);
	}

	@Override
	public void removed() {
		super.removed();
		this.menu.removeSlotListener(this);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleSmithingTable) {
			BlockEntityModuleSmithingTable blockEntity = (BlockEntityModuleSmithingTable) entity;
			
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
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, RESOURCE.SMITHING_TABLE_BASE);
			}
			
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, RESOURCE.SMITHING_TABLE_OVERLAY);

			if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
				this.blit(poseStack, this.getScreenCoords()[0] + 102, this.getScreenCoords()[1] + 49, this.imageWidth, 0, 28, 21);
			}
		}
	}
	
	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 28, 49, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.smithing_table.input_slot_a"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_a_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_a_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 77, 49, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.smithing_table.input_slot_b"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_b_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_b_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 136, 49, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.smithing_table.output_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.output_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.output_slot_two")
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

	@Override
	public void slotChanged(AbstractContainerMenu p_39315_, int p_39316_, ItemStack p_39317_) { }

	@Override
	public void dataChanged(AbstractContainerMenu p_150524_, int p_150525_, int p_150526_) { }
}