package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntityUI;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleArmourWorkbench extends CosmosScreenBlockEntityUI<ContainerModuleArmourWorkbench> implements ContainerListener {

    private static final Vector3f ARMOR_STAND_TRANSLATION = new Vector3f();
    private static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);

    @Nullable
    private ArmorStand armorStandPreview;
    
	private CosmosButtonWithType applyColourButton;  private int[] ACI = new int[] { 79, 89, 18 };
	
	private CosmosButtonWithType applyModuleButton;  private int[] ABI = new int[] { 33, 89, 18 };
	private CosmosButtonWithType removeModuleButton; private int[] RBI = new int[] { 56, 89, 18 };

	private CosmosColourButton colourButtonArmour; private int[] indexC2 = new int[] { 100, 65, 18 };
	private CosmosColourButton colourButtonWings; private int[] indexC1 = new int[] { 100, 87, 18 };

	public ScreenModuleArmourWorkbench(ContainerModuleArmourWorkbench containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(184, 215);
		
		this.setLight(RESOURCE.ARMOUR_WORKBENCH[0]);
		this.setDark(RESOURCE.ARMOUR_WORKBENCH[1]);

		this.setUIModeButtonIndex(186, 5);
		this.setUIHelpButtonIndex(186, 33);
		this.setUILockButtonIndex(186, 19);
		this.setUIHelpElementDeadzone(12, 20, 171, 104);
		
		this.setTitleLabelDims(28, 5);
		this.setInventoryLabelDims(8, 114);
	}

	protected void subInit() {
        this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0, 0.0, 0.0);
        this.armorStandPreview.setNoBasePlate(true);
        this.armorStandPreview.setShowArms(false);
        this.armorStandPreview.yBodyRot = 210.0F;
        this.armorStandPreview.setXRot(25.0F);
        this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
        this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
        this.updateArmorStandPreview(this.getMenu().getSlot(1).getItem());
	}
	
	@Override
	protected void init() {
		super.init();
		this.subInit();
		this.menu.addSlotListener(this);
	}

	@Override
	public void removed() {
		super.removed();
		this.menu.removeSlotListener(this);
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				int decimal = pocket.getDisplayColour();
				ComponentColour colour = ComponentColour.col(decimal);
				float[] rgb = colour.equals(ComponentColour.POCKET_PURPLE) ? ComponentColour.rgbFloatArray(ComponentColour.POCKET_PURPLE_LIGHT.dec()) : ComponentColour.rgbFloatArray(decimal);
				
				CosmosUISystem.Render.renderStaticElementWithUIMode(guiGraphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.ARMOUR_WORKBENCH_BASE);
			}
			
			CosmosUISystem.Render.renderStaticElementWithUIMode(guiGraphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, GUI.RESOURCE.ARMOUR_WORKBENCH_OVERLAY);
		}
		
		int xPos = this.getScreenCoords()[0] + 122; int yPos = this.getScreenCoords()[1] + 56;
		InventoryScreen.renderEntityInInventory(guiGraphics, (float)(xPos + xPos + 45) / 2.0F, (float)(yPos + yPos + 71) / 2.0F, 30.0F, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, null, this.armorStandPreview);
	}

	@Override
	public void renderStandardHoverEffect(GuiGraphics guiGraphics, Style style, int mouseX, int mouseY) {

		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleArmourWorkbench blockEntity) {
			if (this.applyColourButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.armour_workbench.colour_apply_info") };
				
				guiGraphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} 
			
			else if (this.applyModuleButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.armour_workbench.apply_info") };
				
				guiGraphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} 
			
			else if (this.removeModuleButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.armour_workbench.remove_info") };
				
				guiGraphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} 
			
			if (this.colourButtonArmour.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info").append(ComponentHelper.style(ComponentColour.LIME, "bold", " [Armour]")), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getCustomColour(false).getColouredName())
				};
				
				guiGraphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.colourButtonWings.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info").append(ComponentHelper.style(ComponentColour.LIME, "bold", " [Wings]")), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getCustomColour(true).getColouredName())
				};
				
				guiGraphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			
		}
		
		super.renderStandardHoverEffect(guiGraphics, style, mouseX, mouseY);
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
					ComponentColour colour = hasShiftDown() ? ComponentColour.ELYTRA : blockEntity.getCustomColour(true).getNextVanillaColourElytra();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, true));
			        this.updateArmorStandPreview(this.getMenu().getSlot(1).getItem());
				} else if (button.equals(this.colourButtonArmour)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.POCKET_PURPLE : blockEntity.getCustomColour(false).getNextVanillaColourPocket();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, false));
			        this.updateArmorStandPreview(this.getMenu().getSlot(1).getItem());
				}
			} else {
				if (button.equals(this.colourButtonWings)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.ELYTRA : blockEntity.getCustomColour(true).getNextVanillaColourReverseElytra();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, true));
			        this.updateArmorStandPreview(this.getMenu().getSlot(1).getItem());
				} else if (button.equals(this.colourButtonArmour)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.POCKET_PURPLE : blockEntity.getCustomColour(false).getNextVanillaColourReversePocket();
					PacketDistributor.sendToServer(new PacketWorkbench(this.menu.getBlockPos(), colour, false));
			        this.updateArmorStandPreview(this.getMenu().getSlot(1).getItem());
				}
			}
		}
	}

	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 78, 88, 20, 20, ComponentColour.YELLOW, ComponentHelper.style(ComponentColour.YELLOW, "dimensionalpocketsii.gui.help.armour_workbench.colour_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_button_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 32, 88, 20, 20, ComponentColour.LIME, ComponentHelper.style(ComponentColour.LIME, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_add_button_three")

		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 55, 88, 20, 20, ComponentColour.LIGHT_RED, ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_remove_button_three")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 53, 40, 24, 24, ComponentColour.MAGENTA, ComponentHelper.style(ComponentColour.MAGENTA, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.focused_slot_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 32, 42, 20, 20, ComponentColour.ORANGE, ComponentHelper.style(ComponentColour.ORANGE, "Module Slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_three"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_four"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_five"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_six")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 78, 42, 20, 20, ComponentColour.ORANGE, ComponentHelper.style(ComponentColour.ORANGE, "Module Slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_three"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_four"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_five"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_six")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 32, 19, 66, 20, ComponentColour.ORANGE, ComponentHelper.style(ComponentColour.ORANGE, "Module Slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_three"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_four"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_five"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_six")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 32, 65, 66, 20, ComponentColour.ORANGE, ComponentHelper.style(ComponentColour.ORANGE, "Module Slots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_three"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_four"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_five"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.module_slots_six")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 99, 64, 20, 20, ComponentColour.PINK, ComponentHelper.style(ComponentColour.PINK, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.armour_colour_slot_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 99, 86, 20, 20, ComponentColour.PINK, ComponentHelper.style(ComponentColour.PINK, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.wings_colour_slot_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.colour_slot_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 99, 21, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_two"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.applied_preview_slot_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 99, 43, 20, 20, ComponentColour.RED, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.removed_preview_slot_two")
		);
		
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 11, 21, 20, 20, ComponentColour.BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.helmet"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.helmet_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 11, 43, 20, 20, ComponentColour.BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.chestplate"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.chestplate_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 11, 65, 20, 20, ComponentColour.BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.leggings"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.leggings_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 11, 86, 20, 20, ComponentColour.BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.boots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.armour"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.boots_one")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 121, 27, 51, 73, ComponentColour.WHITE, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.armour_workbench.view"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.view_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_workbench.view_two")
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
	public void slotChanged(AbstractContainerMenu menuIn, int indexIn, ItemStack stackIn) {
		if (indexIn == 1) {
			this.updateArmorStandPreview(stackIn);
		}
	}

	@Override
	public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) { }
	
	private void updateArmorStandPreview(ItemStack stack) {
        if (this.armorStandPreview != null) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                this.armorStandPreview.setItemSlot(equipmentslot, ItemStack.EMPTY);
            }

            if (!stack.isEmpty()) {
                ItemStack itemstack = stack.copy();
                if (stack.getItem() instanceof ArmorItem armoritem) {
                    this.armorStandPreview.setItemSlot(armoritem.getEquipmentSlot(), itemstack);
                } else {
                    this.armorStandPreview.setItemSlot(EquipmentSlot.OFFHAND, itemstack);
                }
            }
        }
    }
}