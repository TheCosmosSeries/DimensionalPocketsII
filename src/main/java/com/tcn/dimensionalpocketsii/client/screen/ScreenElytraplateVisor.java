package com.tcn.dimensionalpocketsii.client.screen;

import org.apache.commons.lang3.text.WordUtils;

import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@SuppressWarnings({ "deprecation", "unused" })
@OnlyIn(Dist.CLIENT)
public class ScreenElytraplateVisor implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics graphicsIn, DeltaTracker tracker) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		Inventory playerInventory = player.getInventory();
		
		float screenWidth = minecraft.getWindow().getGuiScaledWidth();
		float screenHeight = minecraft.getWindow().getGuiScaledHeight();
		ItemStack chestStack = player.getInventory().getArmor(2);
		
		if (playerInventory.getArmor(2).getItem() instanceof DimensionalElytraplate plate) {
			int powerOffset = 26;
			int powerWidth = 116;
			
			float scaleAll = Mth.clamp(((screenWidth / 1008.0F) + (screenHeight / 576.0F)) / 2, 0.0F, 1.0F);
	
			int scaledWidth = (int) (screenWidth / Mth.clamp(scaleAll, 0.0F, 1.0F));
			int scaledHeight = (int) (screenHeight / Mth.clamp(scaleAll, 0.0F, 1.0F));
	
			graphicsIn.pose().pushPose();
			graphicsIn.pose().scale(scaleAll, scaleAll, 1.0F);
			
			if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.VISOR) && DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.VISOR)[1]) {
				CosmosUISystem.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 1, scaledHeight - 43, 0, 0, 242, 42, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR);
				CosmosUISystem.renderEnergyDisplay(graphicsIn, ComponentColour.RED, this.getSuitEnergy(minecraft)[0] / 1000, this.getSuitEnergy(minecraft)[1] / 1000, powerWidth, new int[] { 0, 0 }, 125, scaledHeight - powerOffset + 9, powerWidth, 7, true);
				CosmosUISystem.renderEnergyDisplay(graphicsIn, ComponentColour.GREEN, plate.getEnergy(chestStack) / 1000, plate.getMaxEnergyStored(chestStack) / 1000, powerWidth, new int[] { 0, 0 }, 125, scaledHeight - powerOffset + 19, powerWidth, 4, true);
				
				if (DimensionalElytraplate.getInstalledModules(chestStack).size() == 1) {
					graphicsIn.drawString(minecraft.font, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.elytraplate.visor.empty"), 80, scaledHeight - 36, ComponentColour.WHITE.dec());
				}
				
				boolean elytra = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.ELYTRA_FLY)[1];
				boolean telepo = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.TELEPORT_TO_BLOCK)[1];
				boolean solarp = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.SOLAR)[1];
				boolean charge = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.CHARGER)[1];
				
				//this.blit(graphicsIn, 02, scaledHeight - 42, 20, 42, 20, 20);
				
				CosmosUISystem.renderStaticElementToggled(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, 02, scaledHeight - 42, 0, 42, 20, 20, elytra);
				CosmosUISystem.renderStaticElementToggled(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, 22, scaledHeight - 42, 0, 42, 20, 20, telepo);
				CosmosUISystem.renderStaticElementToggled(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, 42, scaledHeight - 42, 0, 42, 20, 20, solarp);
				CosmosUISystem.renderStaticElementToggled(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, 02, scaledHeight - 22, 0, 42, 20, 20, charge);
				
				CosmosUISystem.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 04, scaledHeight - 39, 0,  90, 16, 14, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR);
				CosmosUISystem.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 25, scaledHeight - 39, 16, 90, 14, 14, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR);
				CosmosUISystem.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 45, scaledHeight - 39, 30, 90, 14, 14, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR);
				CosmosUISystem.renderStaticElement(graphicsIn, new int[] { 0, 0 }, 05, scaledHeight - 19, 44, 90, 14, 14, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR);
				
				float scl1 = 0.525F;
				
				graphicsIn.pose().pushPose();
				graphicsIn.pose().scale(scl1, scl1, 0.0F);
				CosmosUISystem.renderStaticElement(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, (int)(66 / scl1), (int) (scaledHeight / scl1) - (int) (39 / scl1), 56, 62, 28, 28);
				graphicsIn.pose().popPose();
				
				if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SCREEN)) {
					graphicsIn.pose().pushPose();
					graphicsIn.pose().scale(scl1, scl1, 0.0F);
					CosmosUISystem.renderStaticElement(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, (int)(86 / scl1), (int) (scaledHeight / scl1) - (int) (39 / scl1), 28, 62, 28, 28);
					graphicsIn.pose().popPose();
				}
	
				if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SHIFTER)) {
					graphicsIn.pose().pushPose();
					graphicsIn.pose().scale(scl1, scl1, 0.0F);
					CosmosUISystem.renderStaticElement(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, (int)(106 / scl1), (int) (scaledHeight / scl1) - (int) (39 / scl1), 0, 62, 28, 28);
					graphicsIn.pose().popPose();
				}
	
				if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SOLAR)) {
					graphicsIn.pose().pushPose();
					graphicsIn.pose().scale(scl1, scl1, 0.0F);
					CosmosUISystem.renderStaticElement(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, (int)(66 / scl1), (int) (scaledHeight / scl1) - (int) (19 / scl1), 84, 62, 28, 28);
					graphicsIn.pose().popPose();
				}
				
				if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.BATTERY)) {
					graphicsIn.pose().pushPose();
					graphicsIn.pose().scale(scl1, scl1, 0.0F);
					CosmosUISystem.renderStaticElement(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, (int)(86 / scl1), (int) (scaledHeight / scl1) - (int) (19 / scl1), 112, 62, 28, 28);
					graphicsIn.pose().popPose();
				}
	
				if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.ENDER_CHEST)) {
					graphicsIn.pose().pushPose();
					graphicsIn.pose().scale(scl1, scl1, 0.0F);
					CosmosUISystem.renderStaticElement(graphicsIn, ModReferences.GUI.RESOURCE.ELYTRAPLATE_VISOR, new int[] { 0, 0 }, (int)(106 / scl1), (int) (scaledHeight / scl1) - (int) (19 / scl1), 140, 62, 28, 28);
					graphicsIn.pose().popPose();
				}
	
				if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SHIFTER)) {
					if (chestStack.has(DataComponents.CUSTOM_DATA)) {
						CompoundTag stack_nbt = chestStack.get(DataComponents.CUSTOM_DATA).copyTag();
	
						if (stack_nbt.contains("nbt_data")) {
							CompoundTag nbt_data = stack_nbt.getCompound("nbt_data");
	
							if (nbt_data.contains("player_pos")) {
								CompoundTag player_pos = nbt_data.getCompound("player_pos");
	
								if (nbt_data.contains("dimension")) {
									CompoundTag dim = nbt_data.getCompound("dimension");
									String path = dim.getString("path");
	
									int[] pos = new int[] { player_pos.getInt("x"), player_pos.getInt("y"), player_pos.getInt("z") };
	
									float sclA = 1.0F;
									Component comp = ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.elytraplate.visor.dimension").append(ComponentHelper.style(ComponentColour.LIME, WordUtils.capitalizeFully(path.replace("_", " "))));
	
									if (minecraft.font.width(comp) > 114) {
										sclA = 114.0F / minecraft.font.width(comp);
									}
	
									graphicsIn.pose().pushPose();
									graphicsIn.pose().scale(sclA, sclA, sclA);
									graphicsIn.drawString(minecraft.font, comp, Math.round(126 / sclA), Math.round((scaledHeight / sclA) - (40 / sclA)), ComponentColour.WHITE.dec());
									graphicsIn.pose().popPose();
	
									String position = "X: " + pos[0] + " | Y: " + pos[1] + " | Z: " + pos[2] + "";
	
									float sclB = 1.0F;
	
									if (minecraft.font.width(position) > 114) {
										sclB = 114.0F / minecraft.font.width(position);
									}
									
									graphicsIn.pose().pushPose();
									graphicsIn.pose().scale(sclB, sclB, sclB);
									graphicsIn.drawString(minecraft.font, ComponentHelper.style(ComponentColour.CYAN, position), Math.round(126 / sclB), Math.round((scaledHeight / sclB) - (28 / sclB)), ComponentColour.WHITE.dec());
									graphicsIn.pose().popPose();
								}
							}
						}
					}
				}
			}
			
			graphicsIn.pose().popPose();
		}
	}

	public int[] getSuitEnergy(Minecraft minecraft) {
		Inventory playerInventory = minecraft.player.getInventory();

		int energy0 = 0;
		int maxEnergy0 = 0;

		int energy1 = 0;
		int maxEnergy1 = 0;

		int energy2 = 0;
		int maxEnergy2 = 0;

		int energy3 = 0;
		int maxEnergy3 = 0;

		if (playerInventory.getArmor(0).getItem() instanceof ICosmosEnergyItem energyItem) {
			energy0 = energyItem.getEnergy(playerInventory.getArmor(0));
			maxEnergy0 = energyItem.getMaxEnergyStored(playerInventory.getArmor(0));
		}

		if (playerInventory.getArmor(1).getItem() instanceof ICosmosEnergyItem energyItem) {
			energy1 = energyItem.getEnergy(playerInventory.getArmor(1));
			maxEnergy1 = energyItem.getMaxEnergyStored(playerInventory.getArmor(1));
		}

		if (playerInventory.getArmor(2).getItem() instanceof ICosmosEnergyItem energyItem) {
			energy2 = energyItem.getEnergy(playerInventory.getArmor(2));
			maxEnergy2 = energyItem.getMaxEnergyStored(playerInventory.getArmor(2));
		}

		if (playerInventory.getArmor(3).getItem() instanceof ICosmosEnergyItem energyItem) {
			energy3 = energyItem.getEnergy(playerInventory.getArmor(3));
			maxEnergy3 = energyItem.getMaxEnergyStored(playerInventory.getArmor(3));
		}

		return new int[] { energy0 + energy1 + energy2 + energy3, maxEnergy0 + maxEnergy1 + maxEnergy2 + maxEnergy3 };
	}
}