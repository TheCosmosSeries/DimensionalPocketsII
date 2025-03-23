package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketNetworkManager;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketArmourItem;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ScreenModuleArmourWorkbench extends CosmosScreenUIModeBE<ContainerModuleArmourWorkbench> {

	private CosmosButtonWithType applyColourButton;  private int[] ACI = new int[] { 104, 62, 18 };
	
	private CosmosButtonWithType applyModuleButton;  private int[] ABI = new int[] { 125, 62, 18 };
	private CosmosButtonWithType removeModuleButton; private int[] RBI = new int[] { 146, 62, 18 };

	public ScreenModuleArmourWorkbench(ContainerModuleArmourWorkbench containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(184, 189);
		
		this.setLight(RESOURCE.ARMOUR_WORKBENCH[0]);
		this.setDark(RESOURCE.ARMOUR_WORKBENCH[1]);

		this.setUIModeButtonIndex(167, 5);
		this.setUIHelpButtonIndex(167, 33);
		this.setUILockButtonIndex(167, 19);
		this.setUIHelpElementDeadzone(13, 13, 170, 86);
		
		this.setTitleLabelDims(28, 4);
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
		
		if (entity instanceof BlockEntityModuleArmourWorkbench) {
			BlockEntityModuleArmourWorkbench blockEntity = (BlockEntityModuleArmourWorkbench) entity;
			
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
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.ARMOUR_WORKBENCH_BASE);
			}
			
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, GUI.RESOURCE.ARMOUR_WORKBENCH_OVERLAY);
		}
	}

	@Override
	public void renderStandardHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		if (this.applyColourButton.isMouseOver(mouseX, mouseY)) {
			BaseComponent[] comp = new BaseComponent[] { ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.armour_workbench.colour_apply_info") };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} 
		
		else if (this.applyModuleButton.isMouseOver(mouseX, mouseY)) {
			BaseComponent[] comp = new BaseComponent[] { ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.armour_workbench.apply_info") };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} 
		
		else if (this.removeModuleButton.isMouseOver(mouseX, mouseY)) {
			BaseComponent[] comp = new BaseComponent[] { ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.armour_workbench.remove_info") };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} 
		
		super.renderStandardHoverEffect(poseStack, style, mouseX, mouseY);
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleArmourWorkbench) {
			BlockEntityModuleArmourWorkbench blockEntity = (BlockEntityModuleArmourWorkbench) entity;
			this.applyColourButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + ACI[0], this.getScreenCoords()[1] + ACI[1], ACI[2], true, true, 32, ComponentHelper.empty(), (button) -> { this.pushButton(this.applyColourButton); }));
			
			this.applyModuleButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + ABI[0], this.getScreenCoords()[1] + ABI[1], ABI[2], true, true, 1, ComponentHelper.empty(), (button) -> { this.pushButton(this.applyModuleButton); }));
			this.removeModuleButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + RBI[0], this.getScreenCoords()[1] + RBI[1], RBI[2], true, true, 2, ComponentHelper.empty(), (button) -> { this.pushButton(this.removeModuleButton); }));
		}
	}
	
	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
		
		if (button.equals(this.applyColourButton)) {
			PocketNetworkManager.sendToServer(new PacketArmourItem(this.menu.getBlockPos(), true, true, false));
		} else if (button.equals(this.applyModuleButton)) {
			PocketNetworkManager.sendToServer(new PacketArmourItem(this.menu.getBlockPos(), true, false, true));
		} else if (button.equals(this.removeModuleButton)) {
			PocketNetworkManager.sendToServer(new PacketArmourItem(this.menu.getBlockPos(), false, false, true));
		}
	}

	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 103, 61, 20, 20, ComponentHelper.style(ComponentColour.YELLOW, "dimensionalpocketsii.gui.help.armour_workbench.colour_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 124, 61, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_three")

		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 145, 61, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_three")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 112, 28, 24, 24, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 27, 19, 62, 41, ComponentHelper.style(ComponentColour.WHITE, "Module Slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_three"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_four"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_five"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_six")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 91, 19, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 91, 40, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 137, 19, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 137, 40, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot_two")
		);
		
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 19, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.helmet"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.helmet_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 40, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.chestplate"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.chestplate_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 61, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.leggings"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.leggings_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 82, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.boots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.boots_one")
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