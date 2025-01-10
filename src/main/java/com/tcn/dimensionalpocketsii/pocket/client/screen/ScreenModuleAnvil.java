package com.tcn.dimensionalpocketsii.pocket.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntityUI;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.ModReferences.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleAnvil;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleAnvil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleAnvil extends CosmosScreenBlockEntityUI<ContainerModuleAnvil> implements ContainerListener {
	
	private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");

	private EditBox textField; private int[] textFieldI = new int[] { 48, 28, 103, 12 };

	public ScreenModuleAnvil(ContainerModuleAnvil containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(184, 177);
		
		this.setLight(RESOURCE.ANVIL[0]);
		this.setDark(RESOURCE.ANVIL[1]);

		this.setUIModeButtonIndex(167, 5);
		this.setUIHelpButtonIndex(167, 33);
		this.setUILockButtonIndex(167, 19);
		this.setUIHelpElementDeadzone(23, 13, 160, 86);
		
		this.setTitleLabelDims(this.imageWidth / 2 - 38, 4);
		this.setInventoryLabelDims(8, 75);
		this.setHasEditBox();
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
	    //this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		//this.textField.tick();
	}

	@Override
	public void resize(Minecraft minecraftIn, int widthIn, int heightIn) {
		String beforeValue = this.textField.getValue();
		this.init(minecraftIn, widthIn, heightIn);
		this.textField.setValue(beforeValue);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.textField.render(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleAnvil) {
			BlockEntityModuleAnvil blockEntity = (BlockEntityModuleAnvil) entity;
			
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
				
				CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, RESOURCE.ANVIL_BASE);
			}
			
			CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, blockEntity, RESOURCE.ANVIL_OVERLAY);

			CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 45, 24, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16, blockEntity, RESOURCE.ANVIL_OVERLAY);
			
			if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
				CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 102, 49, this.imageWidth, 0, 28, 21, blockEntity, RESOURCE.ANVIL_OVERLAY);
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphicsIn, int mouseX, int mouseY) {
		RenderSystem.disableBlend();
		super.renderLabels(graphicsIn, mouseX, mouseY);
		int i = this.menu.getCost();

		if (i > 0) {
			int j = 8453920;
			Component component;
			if (i >= 40 && !this.minecraft.player.getAbilities().instabuild) {
				component = TOO_EXPENSIVE_TEXT;
				j = 16736352;
			} else if (!this.menu.getSlot(2).hasItem()) {
				component = null;
			} else {
				component = Component.translatable("container.repair.cost", i);
				if (!this.menu.getSlot(2).mayPickup(this.menu.getPlayer())) {
					j = 16736352;
				}
			}

			if (component != null) {
				int k = this.imageWidth - 9 - this.font.width(component) - 2;
				// int l = 69;
				graphicsIn.fill(k - 2, 74, this.imageWidth - 8, 83, 1325400064);
				graphicsIn.drawString(font, component.getString(), (float) k + 1, 75.0F, j, true);
			}
		}
	}

	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 28, 49, 20, 20, ComponentColour.LIGHT_BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.anvil.input_slot_a"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.anvil.input_slot_a_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.anvil.input_slot_a_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 77, 49, 20, 20, ComponentColour.LIGHT_BLUE, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.anvil.input_slot_b"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.anvil.input_slot_b_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.anvil.input_slot_b_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 136, 49, 20, 20, ComponentColour.RED, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.anvil.output_slot"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.anvil.output_slot_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.anvil.output_slot_two")
		);
	}
	
	@Override
	protected boolean isHovering(int positionX, int positionY, int width, int height, double mouseX, double mouseY) {
		return super.isHovering(positionX, positionY, width, height, mouseX, mouseY);
	}

	@Override
	public void initEditBox() {
		super.initEditBox();
		//this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.textField = new EditBox(this.font, this.getScreenCoords()[0] + this.textFieldI[0], this.getScreenCoords()[1] + this.textFieldI[1], this.textFieldI[2], this.textFieldI[3], ComponentHelper.comp("Allowed Player Entry"));
        this.textField.setMaxLength(50);
        this.textField.setResponder(this::onNameChanged);
		this.textField.setVisible(true);
		this.textField.setTextColor(CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
	    this.textField.setTextColorUneditable(-1);
		this.textField.setBordered(false);
		this.textField.setCanLoseFocus(true);
		this.textField.setEditable(true);
		this.textField.setValue("");
		
		this.addWidget(this.textField);
		this.setInitialFocus(this.textField);
	}
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return this.textField.mouseClicked(mouseX, mouseY, mouseButton) ? true : super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double directionX, double directionY) {
		return this.textField.mouseScrolled(mouseX, mouseY, directionX, directionY) ? true : super.mouseScrolled(mouseX, mouseY, directionX, directionY);
	}

	@Override
	public boolean keyPressed(int keyCode, int mouseX, int mouseY) {
		if (keyCode == 256) {
			if (this.textField.isFocused()) {
				this.textField.setFocused(false);
			} else {
				this.minecraft.player.closeContainer();
			}
		}
		
		return !this.textField.keyPressed(keyCode, mouseX, mouseY) && !this.textField.canConsumeInput() ? super.keyPressed(keyCode, mouseX, mouseY) : true;
	}

	@Override
	public boolean charTyped(char charIn, int p_98522_) {
		return !this.textField.charTyped(charIn, p_98522_) && !this.textField.canConsumeInput() ? super.charTyped(charIn, p_98522_) : true;
	}
	@Override
	protected void slotClicked(Slot slotIn, int mouseX, int mouseY, ClickType clickTypeIn) {
		super.slotClicked(slotIn, mouseX, mouseY, clickTypeIn);
	}

	@Override
	public void slotChanged(AbstractContainerMenu menuIn, int slotId, ItemStack stackIn) {
		if (slotId == 0) {
			this.textField.setValue(stackIn.isEmpty() ? "" : stackIn.getHoverName().getString());
			this.textField.setEditable(!stackIn.isEmpty());
			this.setFocused(this.textField);
		}
	}

	@Override
	public void dataChanged(AbstractContainerMenu menuIn, int slotId, int dataId) { }

	private void onNameChanged(String nameIn) {
		if (!nameIn.isEmpty()) {
			String s = nameIn;
			Slot slot = this.menu.getSlot(0);
			if (slot != null && slot.hasItem() && !slot.getItem().has(DataComponents.CUSTOM_NAME) && nameIn.equals(slot.getItem().getHoverName().getString())) {
				s = "";
			}

			this.menu.setItemName(s);
			this.minecraft.player.connection.send(new ServerboundRenameItemPacket(s));
		}
	}
}