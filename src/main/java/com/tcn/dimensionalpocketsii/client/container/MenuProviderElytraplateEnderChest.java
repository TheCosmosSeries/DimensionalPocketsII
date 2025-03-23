package com.tcn.dimensionalpocketsii.client.container;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MenuProviderElytraplateEnderChest implements MenuProvider {

	@Override
	public AbstractContainerMenu createMenu(int indexIn, Inventory playerInventoryIn, Player playerIn) {
		return ContainerElytraplateEnderChest.createContainerServerSide(indexIn, playerInventoryIn, playerIn.getEnderChestInventory(), playerInventoryIn.getArmor(2));
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.elytraplate.ender_chest.title");
	}
}
