package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketNetworkManager;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketChargerEnergyState;

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
public class ScreenModuleCharger extends CosmosScreenUIModeBE<ContainerModuleCharger> {
	
	private CosmosButtonWithType energyStateButton; private int[] MBI = new int[] { 83, 80 };
	
	public ScreenModuleCharger(ContainerModuleCharger containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(184, 206);

		this.setLight(RESOURCE.CHARGER[0]);
		this.setDark(RESOURCE.CHARGER[1]);

		this.setUIModeButtonIndex(167, 5);
		this.setUIHelpButtonIndex(167, 33);
		this.setUILockButtonIndex(167, 19);
		this.setUIHelpElementDeadzone(28, 13, 155, 104);
		
		
		this.setTitleLabelDims(53, 4);
		this.setInventoryLabelDims(8, 107);
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
		
		if (entity instanceof BlockEntityModuleCharger) {
			BlockEntityModuleCharger blockEntity = (BlockEntityModuleCharger) entity;
			
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
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.CHARGER_BASE);
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, GUI.RESOURCE.CHARGER_OVERLAY);

				CosmosUISystem.renderEnergyDisplay(this, poseStack, ComponentColour.RED, pocket, this.getScreenCoords(), 84, 23, 16, 48, false);
			}
		}
	}

	@Override
	public void renderStandardHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleCharger) {
			BlockEntityModuleCharger blockEntity = (BlockEntityModuleCharger) entity;
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				if (pocket != null) {
					if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 84 - 1, this.getScreenCoords()[0] + 84 + 16, this.getScreenCoords()[1] + 24 - 1, this.getScreenCoords()[1] + 24 + 48 - 1)) {
						DecimalFormat formatter = new DecimalFormat("#,###,###,###");
						String[] energyString = new String[] { formatter.format(pocket.getEnergyStored()), formatter.format(pocket.getMaxEnergyStored()) };
						
						Component[] comp = new Component[] {
							ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"),
							ComponentHelper.style2(ComponentColour.RED, energyString[0] + " / " + energyString[1], "dimensionalpocketsii.gui.energy_bar.suff")
						};
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					}
				}
				
				if (this.energyStateButton.isMouseOver(mouseX, mouseY)) {
					Component[] comp = new Component[] { 
						ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.charger.mode_info"), 
						ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.charger.mode_value").append(blockEntity.getEnergyState().getColouredComp())
					};
					
					this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
				} 
			}
		}
		
		super.renderStandardHoverEffect(poseStack, style, mouseX, mouseY);
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleCharger) {
			BlockEntityModuleCharger blockEntity = (BlockEntityModuleCharger) entity;
			
			this.energyStateButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.ENERGY, this.getScreenCoords()[0] + MBI[0], this.getScreenCoords()[1] + MBI[1], 18, true, true, blockEntity.getEnergyState().getIndex() + 16, ComponentHelper.empty(), (button) -> { this.pushButton(this.energyStateButton); }));
		}
	}

	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleCharger) {
			BlockEntityModuleCharger blockEntity = (BlockEntityModuleCharger) entity;
			
			if (button.equals(this.energyStateButton)) {
				PocketNetworkManager.sendToServer(new PacketChargerEnergyState(this.menu.getBlockPos()));
				blockEntity.cycleEnergyState();
			}
		}
	}

	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 82, 21, 20, 52, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.power_display"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.power_display_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.power_display_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 82, 79, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.charger.mode_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.mode_button_one"),
			ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.charger.mode_button_two_pre").append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.mode_button_two")),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.mode_button_two_"),
			ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.charger.mode_button_two__"),
			ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.charger.mode_button_three_pre").append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.mode_button_three")),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.mode_button_three_")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 19, 20, 56, ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.gui.help.charger.charge_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.charge_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.charge_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.charge_slot_three")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 106, 19, 20, 56, ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.gui.help.charger.charge_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.charge_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.charge_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.charge_slot_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 34, 79, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.helmet"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot.helmet")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 79, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.chestplate"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot.chestplate")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 106, 79, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.leggings"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot.leggings")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 130, 79, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.boots"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.armour_slot.boots")
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