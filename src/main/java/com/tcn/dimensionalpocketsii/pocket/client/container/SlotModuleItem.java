package com.tcn.dimensionalpocketsii.pocket.client.container;

import com.tcn.dimensionalpocketsii.core.item.armour.module.IModuleItem;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotModuleItem extends Slot {

	public SlotModuleItem(Container containerIn, int indexIn, int posX, int posY) {
		super(containerIn, indexIn, posX, posY);
	}

	@Override
	public boolean mayPlace(ItemStack stackIn) {
		if (stackIn != null) {
			return stackIn.getItem() instanceof IModuleItem;
		}
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}
