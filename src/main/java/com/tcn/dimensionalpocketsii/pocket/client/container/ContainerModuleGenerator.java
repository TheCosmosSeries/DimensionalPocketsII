package com.tcn.dimensionalpocketsii.pocket.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotBucket;
import com.tcn.cosmoslibrary.client.container.slot.SlotBurnableItem;
import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
public class ContainerModuleGenerator extends CosmosContainerMenuBlockEntity {
	private final ContainerData data;
	
	final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{ EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

	public ContainerModuleGenerator(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(8), new SimpleContainerData(2), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleGenerator(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerData containerDataIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModRegistrationManager.CONTAINER_TYPE_GENERATOR.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		this.data = containerDataIn;
		
		this.addSlot(new SlotBurnableItem(contentsIn, 0, 73, 24));
		
		this.addSlot(new SlotBucket(contentsIn, 1, 126, 19));
		this.addSlot(new SlotRestrictedAccess(contentsIn, 2, 126, 40, false, true));
		
		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 12 + i1 * 18, 99 + k * 18));
			}
		}

		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 12 + l * 18, 157));
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
		return stillValid(this.access, playerIn, ModRegistrationManager.BLOCK_WALL_GENERATOR.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn < 3) {
				if (!this.moveItemStackTo(itemstack1, 4, this.slots.size() - 9, false)) {
					return ItemStack.EMPTY;
				}
				
			} else if (indexIn >= 3 && indexIn < this.slots.size()) {
				if (itemstack.getItem() instanceof BucketItem) {
					if (!this.moveItemStackTo(itemstack1, 1, indexIn, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (itemstack.getBurnTime(null) > 0) {
					if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (indexIn >= this.slots.size() - 9 && indexIn < this.slots.size()) {
					if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - 9, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (indexIn >= 3 && indexIn < this.slots.size() - 9) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
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