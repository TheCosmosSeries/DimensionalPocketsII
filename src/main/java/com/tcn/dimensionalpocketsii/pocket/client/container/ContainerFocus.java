package com.tcn.dimensionalpocketsii.pocket.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ContainerFocus extends CosmosContainerMenuBlockEntity {

	public ContainerFocus(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraDataIn) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraDataIn.readBlockPos());
	}
	
	public ContainerFocus(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_FOCUS.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 6 + i1 * 18, 65 + k * 18));
			}
		}
		
		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 6 + l * 18, 122));
		}
	}
	
	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void removeSlotListener(ContainerListener listenerIn) {
		super.removeSlotListener(listenerIn);
	}
	
	@Override
	public void slotsChanged(Container inventoryIn) {
		super.slotsChanged(inventoryIn);
		this.broadcastChanges();
	}
	
	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
	}
	
	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn > 0 && indexIn < this.slots.size() - 9) {
				if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn > this.slots.size() - 9 && indexIn < this.slots.size()) {
				if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - 9, false)) {
					return ItemStack.EMPTY;
				}
			}
			
			slot.onTake(playerIn, itemstack1);
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return itemstack;
	}
}