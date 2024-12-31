package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.ModReferences.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleSmithingTable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleSmithingTable extends CosmosScreenUIModeBE<ContainerModuleSmithingTable> implements ContainerListener {

    private static final Vector3f ARMOR_STAND_TRANSLATION = new Vector3f();
    private static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);

    private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
    
    @Nullable
    private ArmorStand armorStandPreview;
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = ResourceLocation.withDefaultNamespace(
        "item/empty_slot_smithing_template_armor_trim"
    );
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = ResourceLocation.withDefaultNamespace(
        "item/empty_slot_smithing_template_netherite_upgrade"
    );
	private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES = List.of(
		EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE
	);
    
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

	protected void subInit() {
        this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0, 0.0, 0.0);
        this.armorStandPreview.setNoBasePlate(true);
        this.armorStandPreview.setShowArms(true);
        this.armorStandPreview.yBodyRot = 210.0F;
        this.armorStandPreview.setXRot(25.0F);
        this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
        this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
        this.updateArmorStandPreview(this.menu.getSlot(3).getItem());
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
	public void containerTick() {
		super.containerTick();
		Optional<SmithingTemplateItem> optional = this.getTemplateItem();
        this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.baseIcon.tick(optional.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
        this.additionalIcon.tick(optional.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleSmithingTable blockEntity) {
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
				
				CosmosUISystem.Render.renderStaticElementWithUIMode(guiGraphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, RESOURCE.SMITHING_TABLE_BASE);
			}
			
			CosmosUISystem.Render.renderStaticElementWithUIMode(guiGraphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, RESOURCE.SMITHING_TABLE_OVERLAY);

			if ((this.getMenu().getSlot(0).hasItem() || this.getMenu().getSlot(1).hasItem()) && !this.getMenu().getSlot(2).hasItem()) {
				guiGraphics.blit(blockEntity.getUIMode().equals(EnumUIMode.DARK) ? RESOURCE.SMITHING_TABLE_OVERLAY[1] : RESOURCE.SMITHING_TABLE_OVERLAY[0], this.getScreenCoords()[0] + 69, this.getScreenCoords()[1] + 49, this.imageWidth, 0, 28, 21);
			}
			
	        this.templateIcon.render(this.getMenu(), guiGraphics, partialTicks, this.leftPos, this.topPos);
	        this.baseIcon.render(this.getMenu(), guiGraphics, partialTicks, this.leftPos, this.topPos);
	        this.additionalIcon.render(this.getMenu(), guiGraphics, partialTicks, this.leftPos, this.topPos);

			int xPos = this.getScreenCoords()[0] + 128;	int yPos = this.getScreenCoords()[1] + 20;
			InventoryScreen.renderEntityInInventory(guiGraphics, (float)(xPos + xPos + 34) / 2.0F, (float)(yPos + yPos + 86) / 2.0F, 21.0F, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, null, this.armorStandPreview);
		}
	}
	
	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();

		this.addRenderableUIHelpElement(this.getScreenCoords(), 11, 50, 18, 18, ComponentColour.LIGHT_BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.smithing_table.input_slot_a"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_a_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_a_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 29, 50, 18, 18, ComponentColour.LIGHT_BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.smithing_table.input_slot_b"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_b_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_b_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 47, 50, 18, 18, ComponentColour.LIGHT_BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.smithing_table.input_slot_c"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_c_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.input_slot_c_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 101, 50, 18, 18, ComponentColour.RED, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.smithing_table.output_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.output_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.output_slot_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 128, 20, 33, 47, ComponentColour.ORANGE, ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.help.smithing_table.view"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.view_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.smithing_table.view_two")
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
		if (indexIn == 3) {
			this.updateArmorStandPreview(stackIn);
		}
	}

	@Override
	public void dataChanged(AbstractContainerMenu menuIn, int dataSlotIndex, int valueIn) { }

    private Optional<SmithingTemplateItem> getTemplateItem() {
        ItemStack itemstack = this.menu.getSlot(0).getItem();
        return !itemstack.isEmpty() && itemstack.getItem() instanceof SmithingTemplateItem smithingtemplateitem ? Optional.of(smithingtemplateitem) : Optional.empty();
    }

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