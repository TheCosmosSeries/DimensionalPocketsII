package com.tcn.dimensionalpocketsii.client.screen;

import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenItemStackUI;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.dimensionalpocketsii.PocketReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateEnderChest;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIMode;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ScreenElytraplateEnderChest extends CosmosScreenItemStackUI<ContainerElytraplateEnderChest> {
	
	public ScreenElytraplateEnderChest(ContainerElytraplateEnderChest containerIn, Inventory inventoryIn, Component componentIn) {
		super(containerIn, inventoryIn, componentIn);

		this.setImageDims(202, 164);
		
		this.setUIModeButtonIndex(185, 5);
		this.setTitleLabelDims(20, 5);
		this.setInventoryLabelDims(10, 70);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_ENDER_CHEST);
		CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, this.getUIMode(), RESOURCE.ELYTRAPLATE_ENDER_CHEST_OVERLAY);
	}

	@Override
	protected EnumUIMode getUIMode() {
		if (this.getStack() != null) {
			return DimensionalElytraplate.getUIMode(this.getStack());
		}
		
		return EnumUIMode.DARK;
	}

	@Override
	protected void changeUIMode() {
		PacketDistributor.sendToServer(new PacketElytraplateUpdateUIMode(this.getPlayerUUID(), 2, this.getUIMode().getNextState()));
		DimensionalElytraplate.setUIMode(this.getStack(), this.getUIMode().getNextState());
	}
}