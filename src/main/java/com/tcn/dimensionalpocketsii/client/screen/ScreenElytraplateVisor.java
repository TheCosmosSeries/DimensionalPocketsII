package com.tcn.dimensionalpocketsii.client.screen;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.CosmosReference;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.dimensionalpocketsii.DimReference;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings({ "deprecation", "unused" })
@OnlyIn(Dist.CLIENT)
public class ScreenElytraplateVisor extends Gui {

	public ScreenElytraplateVisor() {
		super(Minecraft.getInstance());
	}

	@Override
	public void render(PoseStack poseStackIn, float pop) { }

	public void renderOverlay(PoseStack poseStackIn) {
		this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
		this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

		int powerOffset = 26;
		int powerWidth = 116;
		
		float scaleAll = Mth.clamp(((screenWidth / 1008.0F) + (screenHeight / 576.0F)) / 2, 0.0F, 1.0F);

		int scaledWidth = (int) (screenWidth / Mth.clamp(scaleAll, 0.0F, 1.0F));
		int scaledHeight = (int) (screenHeight / Mth.clamp(scaleAll, 0.0F, 1.0F));

		poseStackIn.pushPose();
		poseStackIn.scale(scaleAll, scaleAll, 1.0F);
		
		ItemStack chestStack = this.minecraft.player.getInventory().getArmor(2);
		DimensionalElytraplate plate = (DimensionalElytraplate) chestStack.getItem();
		
		if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.VISOR) && DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.VISOR)[1]) {
			CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, 1, scaledHeight - 43, 0, 0, 242, 42, DimReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
			CosmosUISystem.renderEnergyDisplay(this, poseStackIn, ComponentColour.RED, this.getSuitEnergy()[0] / 1000, this.getSuitEnergy()[1] / 1000, powerWidth, new int[] { 0, 0 }, 125, scaledHeight - powerOffset + 9, powerWidth, 7, true);
			CosmosUISystem.renderEnergyDisplay(this, poseStackIn, ComponentColour.GREEN, plate.getEnergy(chestStack) / 1000, plate.getMaxEnergyStored(chestStack) / 1000, powerWidth, new int[] { 0, 0 }, 125, scaledHeight - powerOffset + 19, powerWidth, 4, true);
			
			if (DimensionalElytraplate.getInstalledModules(chestStack).size() == 1) {
				this.getFont().draw(poseStackIn, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.elytraplate.visor.empty"), 80, scaledHeight - 36, ComponentColour.WHITE.dec());
			}
			
			boolean elytra = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.ELYTRA_FLY)[1];
			boolean telepo = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.TELEPORT_TO_BLOCK)[1];
			boolean solarp = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.SOLAR)[1];
			boolean charge = DimensionalElytraplate.getElytraSetting(chestStack, ElytraSettings.CHARGER)[1];
			
			//this.blit(poseStackIn, 02, scaledHeight - 42, 20, 42, 20, 20);
			
			CosmosUISystem.setTexture(poseStackIn, DimReference.GUI.RESOURCE.ELYTRAPLATE_VISOR);
			CosmosUISystem.renderStaticElementToggled(this, poseStackIn, new int[] { 0, 0 }, 02, scaledHeight - 42, 0, 42, 20, 20, elytra);
			CosmosUISystem.renderStaticElementToggled(this, poseStackIn, new int[] { 0, 0 }, 22, scaledHeight - 42, 0, 42, 20, 20, telepo);
			CosmosUISystem.renderStaticElementToggled(this, poseStackIn, new int[] { 0, 0 }, 42, scaledHeight - 42, 0, 42, 20, 20, solarp);
			CosmosUISystem.renderStaticElementToggled(this, poseStackIn, new int[] { 0, 0 }, 02, scaledHeight - 22, 0, 42, 20, 20, charge);
			
			CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, 04, scaledHeight - 39, 0,  90, 16, 14);
			CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, 25, scaledHeight - 39, 16, 90, 14, 14);
			CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, 45, scaledHeight - 39, 30, 90, 14, 14);
			CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, 05, scaledHeight - 19, 44, 90, 14, 14);
			
			float scl1 = 0.525F;
			
			poseStackIn.pushPose();
			poseStackIn.scale(scl1, scl1, 0.0F);
			CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, (int)(66 / scl1), (int) (scaledHeight / scl1) - (int) (39 / scl1), 56, 62, 28, 28);
			poseStackIn.popPose();
			
			if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SCREEN)) {
				poseStackIn.pushPose();
				poseStackIn.scale(scl1, scl1, 0.0F);
				CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, (int)(86 / scl1), (int) (scaledHeight / scl1) - (int) (39 / scl1), 28, 62, 28, 28);
				poseStackIn.popPose();
			}

			if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SHIFTER)) {
				poseStackIn.pushPose();
				poseStackIn.scale(scl1, scl1, 0.0F);
				CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, (int)(106 / scl1), (int) (scaledHeight / scl1) - (int) (39 / scl1), 0, 62, 28, 28);
				poseStackIn.popPose();
			}

			if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SOLAR)) {
				poseStackIn.pushPose();
				poseStackIn.scale(scl1, scl1, 0.0F);
				CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, (int)(66 / scl1), (int) (scaledHeight / scl1) - (int) (19 / scl1), 84, 62, 28, 28);
				poseStackIn.popPose();
			}
			
			if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.BATTERY)) {
				poseStackIn.pushPose();
				poseStackIn.scale(scl1, scl1, 0.0F);
				CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, (int)(86 / scl1), (int) (scaledHeight / scl1) - (int) (19 / scl1), 112, 62, 28, 28);
				poseStackIn.popPose();
			}

			if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.ENDER_CHEST)) {
				poseStackIn.pushPose();
				poseStackIn.scale(scl1, scl1, 0.0F);
				CosmosUISystem.renderStaticElement(this, poseStackIn, new int[] { 0, 0 }, (int)(106 / scl1), (int) (scaledHeight / scl1) - (int) (19 / scl1), 140, 62, 28, 28);
				poseStackIn.popPose();
			}

			if (DimensionalElytraplate.hasModuleInstalled(chestStack, BaseElytraModule.SHIFTER)) {
				if (chestStack.hasTag()) {
					CompoundTag stack_nbt = chestStack.getTag();

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

								if (this.getFont().width(comp) > 114) {
									sclA = 114.0F / this.getFont().width(comp);
								}

								poseStackIn.pushPose();
								poseStackIn.scale(sclA, sclA, sclA);
								this.getFont().draw(poseStackIn, comp, 126 / sclA, (int) (scaledHeight / sclA) - (40 / sclA), ComponentColour.WHITE.dec());
								poseStackIn.popPose();

								String position = "X: " + pos[0] + " | Y: " + pos[1] + " | Z: " + pos[2] + "";

								float sclB = 1.0F;

								if (this.getFont().width(position) > 114) {
									sclB = 114.0F / this.getFont().width(position);
								}
								
								poseStackIn.pushPose();
								poseStackIn.scale(sclB, sclB, sclB);
								this.getFont().draw(poseStackIn, ComponentHelper.style(ComponentColour.CYAN, position), 126 / sclB, (int) (scaledHeight / sclB) - (28 / sclB), ComponentColour.WHITE.dec());
								poseStackIn.popPose();
							}
						}
					}
				}
			}

			
		}
		
		poseStackIn.popPose();
		CosmosUISystem.setTexture(poseStackIn, GUI_ICONS_LOCATION);
	}

	public int[] getSuitEnergy() {
		Inventory playerInventory = this.minecraft.player.getInventory();

		int energy0 = 0;
		int maxEnergy0 = 0;

		int energy1 = 0;
		int maxEnergy1 = 0;

		int energy2 = 0;
		int maxEnergy2 = 0;

		int energy3 = 0;
		int maxEnergy3 = 0;

		if (playerInventory.getArmor(0).getItem() instanceof ICosmosEnergyItem) {
			ICosmosEnergyItem item = (ICosmosEnergyItem) playerInventory.getArmor(0).getItem();

			energy0 = item.getEnergy(playerInventory.getArmor(0));
			maxEnergy0 = item.getMaxEnergyStored(playerInventory.getArmor(0));
		}

		if (playerInventory.getArmor(1).getItem() instanceof ICosmosEnergyItem) {
			ICosmosEnergyItem item = (ICosmosEnergyItem) playerInventory.getArmor(1).getItem();

			energy1 = item.getEnergy(playerInventory.getArmor(1));
			maxEnergy1 = item.getMaxEnergyStored(playerInventory.getArmor(1));
		}

		if (playerInventory.getArmor(2).getItem() instanceof ICosmosEnergyItem) {
			ICosmosEnergyItem item = (ICosmosEnergyItem) playerInventory.getArmor(2).getItem();

			energy2 = item.getEnergy(playerInventory.getArmor(2));
			maxEnergy2 = item.getMaxEnergyStored(playerInventory.getArmor(2));
		}

		if (playerInventory.getArmor(3).getItem() instanceof ICosmosEnergyItem) {
			ICosmosEnergyItem item = (ICosmosEnergyItem) playerInventory.getArmor(3).getItem();

			energy3 = item.getEnergy(playerInventory.getArmor(3));
			maxEnergy3 = item.getMaxEnergyStored(playerInventory.getArmor(3));
		}

		return new int[] { energy0 + energy1 + energy2 + energy3, maxEnergy0 + maxEnergy1 + maxEnergy2 + maxEnergy3 };
	}
}