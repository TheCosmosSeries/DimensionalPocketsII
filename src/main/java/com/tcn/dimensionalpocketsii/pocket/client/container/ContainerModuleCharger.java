package com.tcn.dimensionalpocketsii.pocket.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotArmourItem;
import com.tcn.cosmoslibrary.client.container.slot.SlotEnergyItem;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ContainerModuleCharger extends CosmosContainerMenuBlockEntity {
	
	public ContainerModuleCharger(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(6), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleCharger(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_CHARGER.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		this.addSlot(new SlotEnergyItem(contentsIn, 0, 60, 21));
		this.addSlot(new SlotEnergyItem(contentsIn, 1, 60, 39));
		this.addSlot(new SlotEnergyItem(contentsIn, 2, 60, 57));
		this.addSlot(new SlotEnergyItem(contentsIn, 3, 108, 21));
		this.addSlot(new SlotEnergyItem(contentsIn, 4, 108, 39));
		this.addSlot(new SlotEnergyItem(contentsIn, 5, 108, 57));
		
		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 12 + i1 * 18, 120 + k * 18));
			}
		}

		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 12 + l * 18, 178));
		}

		//Armour Slots
		this.addSlot(new SlotArmourItem(playerInventoryIn, 39, 36, 81, this.player, 0));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 38, 60, 81, this.player, 1));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 37, 108, 81, this.player, 2));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 36, 132, 81, this.player, 3));
		
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
		return stillValid(this.access, playerIn, PocketsRegistrationManager.BLOCK_WALL_CHARGER.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn < 6) {
				if (itemstack.getItem() instanceof ArmorItem) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
						if (!this.moveItemStackTo(itemstack1, 7, this.slots.size() - 13, false)) {
							return ItemStack.EMPTY;
						}
					} 
				} else {
					if (!this.moveItemStackTo(itemstack1, 7, this.slots.size() - 13, false)) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 13, this.slots.size() - 4, false)) {
							return ItemStack.EMPTY;
						}
					}
				}
			} else if (indexIn >= 6 && indexIn < this.slots.size()) {
				if (itemstack.getCapability(Capabilities.EnergyStorage.ITEM) instanceof IEnergyStorage) {
					if (!this.moveItemStackTo(itemstack, 0, 6, false)) {
						return ItemStack.EMPTY;
					}
				
					slot.set(ItemStack.EMPTY);
				} else if (itemstack.getItem() instanceof ArmorItem && indexIn < this.slots.size() - 4) {
					if (!this.moveItemStackTo(itemstack1, 42, 46, false)) {
						return ItemStack.EMPTY;
					}
				} else {
					if (indexIn >= this.slots.size() - 13 && indexIn < this.slots.size()) {
						if (!this.moveItemStackTo(itemstack1, 7, this.slots.size() - 13, false)) {
							return ItemStack.EMPTY;
						}
					}
					
					if (indexIn >= 6 && indexIn < this.slots.size() - 13) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 13, this.slots.size(), false)) {
							return ItemStack.EMPTY;
						}
					}
				}
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack != null ? itemstack : ItemStack.EMPTY;
	}

}