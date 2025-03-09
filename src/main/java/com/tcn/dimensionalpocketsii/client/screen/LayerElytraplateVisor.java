package com.tcn.dimensionalpocketsii.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.dimensionalpocketsii.PocketReference;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.EnumElytraSetting;
import com.tcn.dimensionalpocketsii.core.item.armour.module.EnumElytraModule;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class LayerElytraplateVisor implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics graphicsIn, DeltaTracker tracker) {
		PoseStack poseStack = graphicsIn.pose();
		Minecraft mc = Minecraft.getInstance();
		ItemStack chestStack = mc.player.getInventory().getArmor(2);
		
		int guiHeight = graphicsIn.guiHeight();
		int guiWidth = graphicsIn.guiWidth();
		
		if (!mc.getEntityRenderDispatcher().options.hideGui) {
			if (chestStack.getItem() instanceof DimensionalElytraplate plate) {
				int powerOffset = 26;
				
				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, 90.0F);
				
				if (DimensionalElytraplate.hasModuleInstalled(chestStack, EnumElytraModule.VISOR) && DimensionalElytraplate.getElytraSetting(chestStack, EnumElytraSetting.VISOR)) {
					CosmosUISystem.Render.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 1, guiHeight - 40, 0, 0, 102, 39, PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					
					CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.RED, this.getSuitEnergy(mc)[0] / 1000, this.getSuitEnergy(mc)[1] / 1000, 98, new int[] { 0, 0 }, 3, guiHeight - powerOffset + 9, 98, 7, true);
					CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.CYAN, plate.getEnergy(chestStack) / 1000, plate.getMaxEnergyStored(chestStack) / 1000, 98, new int[] { 0, 0 }, 3, guiHeight - powerOffset + 19, 98, 4, true);
										
					CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, new int[] { 0, 0 }, 02, guiHeight - 39, 0, 39, 20, 20, DimensionalElytraplate.getElytraSetting(chestStack, EnumElytraSetting.ELYTRA_FLY), PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, new int[] { 0, 0 }, 22, guiHeight - 39, 0, 39, 20, 20, DimensionalElytraplate.getElytraSetting(chestStack, EnumElytraSetting.TELEPORT_TO_BLOCK), PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, new int[] { 0, 0 }, 42, guiHeight - 39, 0, 39, 20, 20, DimensionalElytraplate.getElytraSetting(chestStack, EnumElytraSetting.SOLAR), PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, new int[] { 0, 0 }, 62, guiHeight - 39, 0, 39, 20, 20, DimensionalElytraplate.getElytraSetting(chestStack, EnumElytraSetting.CHARGER), PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, new int[] { 0, 0 }, 82, guiHeight - 39, 0, 39, 20, 20, DimensionalElytraplate.getElytraSetting(chestStack, EnumElytraSetting.FIREWORK), PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					
					CosmosUISystem.Render.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 04, guiHeight - 36, 20, 39, 16, 14, PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 25, guiHeight - 36, 36, 39, 14, 14, PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 45, guiHeight - 36, 50, 39, 14, 14, PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 65, guiHeight - 36, 64, 39, 14, 14, PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
					CosmosUISystem.Render.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 85, guiHeight - 36, 78, 39, 14, 14, PocketReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
				}
				
				poseStack.popPose();
			}
		}
	}

	public int[] getSuitEnergy(Minecraft mc) {
		Inventory playerInventory = mc.player.getInventory();
		
		int[] individualEnergies = new int[] { 0, 0, 0, 0 };
		int[] individualMaxEnergies = new int[] { 0, 0, 0, 0 };
		
		for (int i = 0; i < 4; i++) {
			if (playerInventory.getArmor(i).getItem() instanceof ICosmosEnergyItem energyItem) {
				individualEnergies[i] = energyItem.getEnergy(playerInventory.getArmor(i));
				individualMaxEnergies[i] = energyItem.getMaxEnergyStored(playerInventory.getArmor(i));
			}
		}
		
		return new int[] { 
			individualEnergies[0] + individualEnergies[1] + individualEnergies[2] + individualEnergies[3],
			individualMaxEnergies[0] + individualMaxEnergies[1] + individualMaxEnergies[2] + individualMaxEnergies[3]
		};
	}
}