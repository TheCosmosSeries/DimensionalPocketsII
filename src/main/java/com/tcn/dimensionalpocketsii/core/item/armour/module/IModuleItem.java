package com.tcn.dimensionalpocketsii.core.item.armour.module;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IModuleItem {
	
	public EnumElytraModule getModule();
	
	public boolean doesInformationCarry();

	public boolean transferInformation(ItemStack fromStack, ItemStack toStack, boolean simulate);

	public Item asActualItem();
}