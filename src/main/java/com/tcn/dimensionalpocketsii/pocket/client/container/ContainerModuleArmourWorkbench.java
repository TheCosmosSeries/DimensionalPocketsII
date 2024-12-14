package com.tcn.dimensionalpocketsii.pocket.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotArmourItem;
import com.tcn.cosmoslibrary.client.container.slot.SlotColourableArmourItem;
import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.dimensionalpocketsii.core.item.armour.module.IModuleItem;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

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

public class ContainerModuleArmourWorkbench extends CosmosContainerMenuBlockEntity {
	
	public ContainerModuleArmourWorkbench(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(11), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleArmourWorkbench(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModRegistrationManager.CONTAINER_TYPE_ARMOUR_WORKBENCH.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		//Armour Slot
		this.addSlot(new SlotColourableArmourItem(contentsIn, 0, 57, 44, 1));
		
		//Preview Slot Applied
		this.addSlot(new SlotRestrictedAccess(contentsIn, 1, 101, 23, false, false) {
			@Override
			public void setChanged() {
				super.setChanged();
				ContainerModuleArmourWorkbench.this.slotsChanged(this.container);
				this.container.setChanged();
				contentsIn.setChanged();
			}
		});
		
		//Preview Slot Removed
		this.addSlot(new SlotRestrictedAccess(contentsIn, 2, 101, 45, false, false));

		//Module Slots
		this.addSlot(new SlotModuleItem(contentsIn, 3,  34, 21));
		this.addSlot(new SlotModuleItem(contentsIn, 4,  57, 21));
		this.addSlot(new SlotModuleItem(contentsIn, 5,  80, 21));
		this.addSlot(new SlotModuleItem(contentsIn, 6,  34, 44));
		this.addSlot(new SlotModuleItem(contentsIn, 7,  80, 44));
		this.addSlot(new SlotModuleItem(contentsIn, 8,  34, 67));
		this.addSlot(new SlotModuleItem(contentsIn, 9,  57, 67));
		this.addSlot(new SlotModuleItem(contentsIn, 10, 80, 67));

		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 12 + i1 * 18, 128 + k * 18));
			}
		}

		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 12 + l * 18, 186));
		}

		//Armour Slots
		this.addSlot(new SlotArmourItem(playerInventoryIn, 39, 13, 23, this.player, 0));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 38, 13, 45, this.player, 1));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 37, 13, 66, this.player, 2));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 36, 13, 88, this.player, 3));
	}

	@Override
	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

	@Override
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
		return stillValid(this.access, playerIn, ModRegistrationManager.BLOCK_WALL_ARMOUR_WORKBENCH.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn == 0) {
				if (itemstack.getItem() instanceof ArmorItem) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
						if (!this.moveItemStackTo(itemstack1, 11, this.slots.size() - 13, false)) {
							return ItemStack.EMPTY;
						}
					}
				} else {
					if (!this.moveItemStackTo(itemstack1, 11, this.slots.size() - 13, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn >= 1 && indexIn <= 10) {
				if (!this.moveItemStackTo(itemstack1, 11, this.slots.size() - 13, false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn >= 9 && indexIn < this.slots.size()) {
				if (itemstack.getItem() instanceof ArmorItem) {
					if (!this.moveItemStackTo(itemstack, 0, 1, false)) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
							if (!this.moveItemStackTo(itemstack1, 11, this.slots.size() - 13, false)) {
								return ItemStack.EMPTY;
							}
						}
					}
					slot.set(ItemStack.EMPTY);
				} else if (itemstack.getItem() instanceof IModuleItem) {
					if (!this.moveItemStackTo(itemstack, 3, 11, false)) {
						return ItemStack.EMPTY;
					}
				
					slot.set(ItemStack.EMPTY);
				} else if (itemstack.getItem() instanceof ArmorItem && indexIn < this.slots.size() - 4) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				} else {
					if (indexIn >= this.slots.size() - 13 && indexIn < this.slots.size()) {
						if (!this.moveItemStackTo(itemstack1, 11, this.slots.size() - 13, false)) {
							return ItemStack.EMPTY;
						}
					}
					
					if (indexIn >= 10 && indexIn < this.slots.size() - 13) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 13, this.slots.size(), false)) {
							return ItemStack.EMPTY;
						}
					}
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