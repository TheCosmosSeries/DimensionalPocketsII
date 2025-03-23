package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.ArrayList;
import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.common.enums.EnumGenerationMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketNetworkManager;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketGeneratorEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.misc.PacketGeneratorMode;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleGenerator extends CosmosScreenUIModeBE<ContainerModuleGenerator> {
	
	private CosmosButtonWithType modeChangeButton; private int[] MBI = new int[] { 41, 60,  18 };
	private CosmosButtonWithType buttonTankClear;  private int[] TBI = new int[] { 125, 60, 18 };
	
	public ScreenModuleGenerator(ContainerModuleGenerator containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(184, 185);
		
		this.setLight(RESOURCE.GENERATOR[0]);
		this.setDark(RESOURCE.GENERATOR[1]);

		this.setUIModeButtonIndex(167, 5);
		this.setUIHelpButtonIndex(167, 33);
		this.setUILockButtonIndex(167, 19);
		this.setUIHelpElementDeadzone(36, 13, 147, 82);
		
		this.setTitleLabelDims(36, 4);
		this.setInventoryLabelDims(8, 86);
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
		
		if (entity instanceof BlockEntityModuleGenerator) {
			BlockEntityModuleGenerator blockEntity = (BlockEntityModuleGenerator) entity;

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
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.GENERATOR_BASE);
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, GUI.RESOURCE.GENERATOR_OVERLAY);
				
				CosmosUISystem.renderScaledElementUpNestled(this, poseStack, this.getScreenCoords(), 72, 54, 184, 0, 18, 19, blockEntity.getBurnTimeScaled(19));

				CosmosUISystem.renderEnergyDisplay(this, poseStack, ComponentColour.RED, pocket, this.getScreenCoords(), 42, 19, 16, 37, false);
				CosmosUISystem.renderFluidTank(this, poseStack, this.getScreenCoords(), 105, 19, blockEntity.getFluidTank(), blockEntity.getFluidLevelScaled(58), 58);
			}
		}
	}

	@Override
	protected void renderStandardHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleGenerator) {
			BlockEntityModuleGenerator blockEntity = (BlockEntityModuleGenerator) entity;
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				if (pocket != null) {
					if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 42 - 1, this.getScreenCoords()[0] + 42 + 16, this.getScreenCoords()[1] + 20 - 1, this.getScreenCoords()[1] + 20 + 37 - 1)) {
						DecimalFormat formatter = new DecimalFormat("#,###,###,###");
						String amount_string = formatter.format(pocket.getEnergyStored());
						String capacity_string = formatter.format(pocket.getMaxEnergyStored());
						
						ArrayList<Component> comp = new ArrayList<Component>();
						
						comp.add(ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"));
						comp.add(ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.energy_bar.suff"));
						
						if (blockEntity.getGenerationMode().equals(EnumGenerationMode.BURNABLE_ITEM)) {
							if (blockEntity.dataAccess.get(0) > 0) {
								comp.add(ComponentHelper.style2(ComponentColour.ORANGE, "cosmoslibrary.gui.generation.pre", "" + blockEntity.dataAccess.get(2), "cosmoslibrary.gui.generation.suff"));
							}
						} else {
							if (blockEntity.dataAccess.get(3) > 0) {
								comp.add(ComponentHelper.style2(ComponentColour.ORANGE, "cosmoslibrary.gui.generation.pre", "" + blockEntity.dataAccess.get(2), "cosmoslibrary.gui.generation.suff"));
							}
						}
						
						this.renderComponentTooltip(poseStack, comp, mouseX, mouseY);
					}
				}
				
				if (this.modeChangeButton.isMouseOver(mouseX, mouseY)) {
					Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.generator.mode_info"),
							ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.generator.mode_value").append(blockEntity.getGenerationMode().getColouredComp())};
					
					this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
				}
				
				if (this.buttonTankClear != null) {
					if (this.buttonTankClear.isMouseOver(mouseX, mouseY)) {
						if (this.buttonTankClear.active) {
							if (!hasShiftDown()) {
								this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"), mouseX, mouseY);
							} else {
								Component[] comp = new Component[] { 
										ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"),
										ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.button.tank_clear_shift") };
								
								this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
							}
						}
					}
				}
				
				if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 105, this.getScreenCoords()[0] + 105 + 16, this.getScreenCoords()[1] + 19, this.getScreenCoords()[1] + 19 + 58 - 1)) {
					FluidTank tank = blockEntity.getFluidTank();
					
					DecimalFormat formatter = new DecimalFormat("#,###,###,###");
					String amount_string = formatter.format(tank.getFluidAmount());
					String capacity_string = formatter.format(tank.getCapacity());
					String fluid_name = tank.getFluid().getTranslationKey();
					
					Component[] comp = new Component[] { 
							ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.fluid_bar.pre").append(ComponentHelper.style3(ComponentColour.ORANGE, "bold", "[ ", fluid_name, " ]")), 
							ComponentHelper.style2(ComponentColour.CYAN, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.fluid_bar.suff") };
					
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
		
		if (entity instanceof BlockEntityModuleGenerator) {
			BlockEntityModuleGenerator blockEntity = (BlockEntityModuleGenerator) entity;
		
			this.modeChangeButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + MBI[0], this.getScreenCoords()[1] + MBI[1], MBI[2], true, true, blockEntity.getGenerationModeValue() ? 17 : 20, ComponentHelper.empty(), (button) -> { this.pushButton(this.modeChangeButton); }));
			this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBI[0], this.getScreenCoords()[1] + TBI[1], TBI[2], !blockEntity.getFluidTank().isEmpty(), true, blockEntity.getFluidTank().isEmpty() ? 15 : 16,  ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTankClear); }));
		}
	}

	@Override
	protected void pushButton(Button button) {
		super.pushButton(button);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleGenerator) {
			BlockEntityModuleGenerator blockEntity = (BlockEntityModuleGenerator) entity;
			
			if (button.equals(this.modeChangeButton)) {
				PocketNetworkManager.sendToServer(new PacketGeneratorMode(this.menu.getBlockPos()));
				blockEntity.cycleGenerationMode();
			} else if (button.equals(this.buttonTankClear)) {
				if (hasShiftDown()) {
					PocketNetworkManager.sendToServer(new PacketGeneratorEmptyTank(this.menu.getBlockPos()));
					blockEntity.getFluidTank().setFluid(FluidStack.EMPTY);
				}
			}
		}
	}
	
	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 40, 17, 20, 41, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.power_display"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.power_display_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.power_display_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 103, 17, 20, 62, ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.help.fluid_display"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.fluid_display_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.fluid_display_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.fluid_display_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 124, 59, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.fluid_clear_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.fluid_clear_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.fluid_clear_button_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 40, 59, 20, 20, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.gui.help.generator.mode_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.mode_button_one"),
			ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.help.generator.mode_button_two_pre").append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.mode_button_two")),
			ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.generator.mode_button_two__"),
			ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.help.generator.mode_button_three_pre").append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.charger.mode_button_three"))
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 70, 52, 22, 23, ComponentHelper.style(ComponentColour.YELLOW, "dimensionalpocketsii.gui.help.generator.burn_indicator"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.burn_indicator_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.burn_indicator_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.burn_indicator_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 70, 21, 22, 22, ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.help.generator.fuel_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.fuel_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.fuel_slot_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.generator.fuel_slot_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 124, 17, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.bucket_input"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_four")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 124, 38, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.slot.bucket_output"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_output_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_output_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_output_three")
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