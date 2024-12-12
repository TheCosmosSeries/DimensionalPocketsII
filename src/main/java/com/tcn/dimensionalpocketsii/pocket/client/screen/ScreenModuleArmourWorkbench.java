package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.Arrays;

import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosColourButton;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.ModReferences.GUI;
import com.tcn.dimensionalpocketsii.ModReferences.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketArmourItem;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketWorkbench;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleArmourWorkbench extends CosmosScreenUIModeBE<ContainerModuleArmourWorkbench> {

	private CosmosButtonWithType applyColourButton;  private int[] ACI = new int[] { 138, 62, 18 };
	
	private CosmosButtonWithType applyModuleButton;  private int[] ABI = new int[] { 159, 62, 18 };
	private CosmosButtonWithType removeModuleButton; private int[] RBI = new int[] { 180, 62, 18 };

	private CosmosColourButton colourButtonWings; private int[] indexC1 = new int[] { 126, 41, 18 };
	private CosmosColourButton colourButtonArmour; private int[] indexC2 = new int[] { 126, 20, 18 };

	public ScreenModuleArmourWorkbench(ContainerModuleArmourWorkbench containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(246, 189);
		
		this.setLight(RESOURCE.ARMOUR_WORKBENCH[0]);
		this.setDark(RESOURCE.ARMOUR_WORKBENCH[1]);

		this.setUIModeButtonIndex(229, 5);
		this.setUIHelpButtonIndex(229, 33);
		this.setUILockButtonIndex(229, 19);
		this.setUIHelpElementDeadzone(13, 13, 170, 86);
		
		this.setTitleLabelDims(62, 4);
		this.setInventoryLabelDims(8, 89);
	}

	@Override
	protected void init() {
		super.init();
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
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
				
				CosmosUISystem.renderStaticElementWithUIMode(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.ARMOUR_WORKBENCH_BASE);
			}
			
			CosmosUISystem.renderStaticElementWithUIMode(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, GUI.RESOURCE.ARMOUR_WORKBENCH_OVERLAY);
		}
		
		int xPos = 185;
		int yPos = 104;
		
	    InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.getScreenCoords()[0] + xPos, this.getScreenCoords()[1] + yPos, this.getScreenCoords()[0] + xPos + 49, this.getScreenCoords()[1] + yPos + 70, 30, 0.0625F, (float)mouseX, (float)mouseY, this.minecraft.player);
	}

	@Override
	public void renderStandardHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {

		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
			if (this.applyColourButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.armour_workbench.colour_apply_info") };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} 
			
			else if (this.applyModuleButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.armour_workbench.apply_info") };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} 
			
			else if (this.removeModuleButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.armour_workbench.remove_info") };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} 
			
			if (this.colourButtonArmour.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info").append(ComponentHelper.style(ComponentColour.GREEN, "bold", " [Armour]")), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getCustomColour(false).getColouredName())
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.colourButtonWings.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info").append(ComponentHelper.style(ComponentColour.GREEN, "bold", " [Wings]")), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getCustomColour(true).getColouredName())
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			
		}
		
		super.renderStandardHoverEffect(graphics, style, mouseX, mouseY);
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
			this.applyColourButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + ACI[0], this.getScreenCoords()[1] + ACI[1], ACI[2], true, true, 32, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.applyColourButton, isLeftClick); }));
			
			this.applyModuleButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + ABI[0], this.getScreenCoords()[1] + ABI[1], ABI[2], true, true, 1, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.applyModuleButton, isLeftClick); }));
			this.removeModuleButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + RBI[0], this.getScreenCoords()[1] + RBI[1], RBI[2], true, true, 2, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.removeModuleButton, isLeftClick); }));

			this.colourButtonWings = this.addRenderableWidget(new CosmosColourButton(blockEntity.getCustomColour(true), this.getScreenCoords()[0] + indexC1[0], this.getScreenCoords()[1] + indexC1[1], indexC1[2],  true, true, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.colourButtonWings, isLeftClick); }));
			this.colourButtonArmour = this.addRenderableWidget(new CosmosColourButton(blockEntity.getCustomColour(false), this.getScreenCoords()[0] + indexC2[0], this.getScreenCoords()[1] + indexC2[1], indexC2[2], true, true, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.colourButtonArmour, isLeftClick); }));
		}
	}
	
	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);

		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
			if (isLeftClick) {
				if (button.equals(this.applyColourButton)) {
					PacketDistributor.sendToServer(new PacketArmourItem(this.menu.getBlockPos(), true, true, false));
				} else if (button.equals(this.applyModuleButton)) {
					PacketDistributor.sendToServer(new PacketArmourItem(this.menu.getBlockPos(), true, false, true));
				} else if (button.equals(this.removeModuleButton)) {
					PacketDistributor.sendToServer(new PacketArmourItem(this.menu.getBlockPos(), false, false, true));
				}
				
				else if (button.equals(this.colourButtonWings)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour(true).getNextVanillaColourPocket();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, true));
					//blockEntity.updateColour(colour, true);
				} else if (button.equals(this.colourButtonArmour)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour(false).getNextVanillaColourPocket();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, false));
					//blockEntity.updateColour(colour, false);
				}
			} else {
				if (button.equals(this.colourButtonWings)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour(true).getNextVanillaColourReversePocket();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, true));
					//blockEntity.updateColour(colour, true);
				} else if (button.equals(this.colourButtonArmour)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour(false).getNextVanillaColourReversePocket();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, false));
					//blockEntity.updateColour(colour, false);
				}
			}
		}
	}

	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 137, 61, 20, 20, ComponentHelper.style(ComponentColour.YELLOW, "dimensionalpocketsii.gui.help.armour_workbench.colour_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 158, 61, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_three")

		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 179, 61, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_three")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 146, 28, 24, 24, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 61, 19, 62, 41, ComponentHelper.style(ComponentColour.ORANGE, "Module Slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_three"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_four"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_five"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_six")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 125, 19, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 125, 40, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 171, 19, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 171, 40, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot_two")
		);
		
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 53, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.helmet"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.helmet_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 74, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.chestplate"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.chestplate_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 95, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.leggings"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.leggings_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 116, 61, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.boots"), 
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