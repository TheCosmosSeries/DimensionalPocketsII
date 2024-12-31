package com.tcn.dimensionalpocketsii.pocket.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotBucket;
import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;

public class ContainerPocket extends CosmosContainerMenuBlockEntity {

	public static ContainerPocket createContainerServerSide(int windowID, Inventory playerInventory, Container pocketIn, Container contentsIn, BlockPos pos) {
		return new ContainerPocket(windowID, playerInventory, pocketIn, contentsIn, pos);
	}

	public static ContainerPocket createContainerClientSide(int windowID, Inventory playerInventory, FriendlyByteBuf extraData) {
		BlockPos pos = extraData.readBlockPos();
		
		return new ContainerPocket(windowID, playerInventory, new SimpleContainer(ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH), new SimpleContainer(ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH + 2), pos);
	}
	
	protected ContainerPocket(int id, Inventory playerInventoryIn, Container pocketIn, Container contentsIn, BlockPos posIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_POCKET.get(), id, playerInventoryIn, null, posIn);

		/** - Pocket Inventory - */
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 4; x++) {
				//0 - 39
				this.addSlot(new Slot(pocketIn, x + y * 4, 266 + x * 18, 17 + y * 18 ));
			}
		}
		
		/** - Pocket Interface Stacks - */
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 4; x++) {
				//40 - 47
				this.addSlot(new SlotRestrictedAccess(pocketIn, 40 + x + y * 4, 266 + x * 18, 210 + y * 18, true, true));
			}
		}
		
		/** - Surrounding Stacks - */
		this.addSlot(new SlotRestrictedAccess(pocketIn, 48, 37, 42, 1, false, false));
		this.addSlot(new SlotRestrictedAccess(pocketIn, 49, 16, 42, 1, false, false));
		this.addSlot(new SlotRestrictedAccess(pocketIn, 50, 58, 42, 1, false, false));
		this.addSlot(new SlotRestrictedAccess(pocketIn, 51, 16, 85, 1, false, false));
		this.addSlot(new SlotRestrictedAccess(pocketIn, 52, 37, 85, 1, false, false));
		this.addSlot(new SlotRestrictedAccess(pocketIn, 53, 58, 85, 1, false, false));
		
		/** - Bucket Slots -*/
		this.addSlot(new SlotBucket(contentsIn, 54, 60, 184));
		this.addSlot(new SlotRestrictedAccess(contentsIn, 55, 60, 205, 1, false, true));
		
		/** - Player Inventory - */
		for (int x = 0; x < 3; ++x) {
			for (int y = 0; y < 9; ++y) {
				this.addSlot(new Slot(playerInventoryIn, y + (x + 1) * 9, 92 + y * 18, 170 + x * 18));
			}
		}

		/** - Player Hotbar - */
		for (int x = 0; x < 9; ++x) {
			this.addSlot(new Slot(playerInventoryIn, x, 92 + x * 18, 228));
		}
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (itemstack.getItem() instanceof BucketItem) {
				//if (FluidUtil.getFluidContained(itemstack).isPresent()) {
					if (index > 40 && index != 55 && index != 54) {
						if (!this.moveItemStackTo(itemstack1, 54, index, false)) {
							return ItemStack.EMPTY;
						}
					} else if (index == 54) {
						if (!this.moveItemStackTo(itemstack1, ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH - 2, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				//}
			}
			
			if (index == 55) {
				if (!this.moveItemStackTo(itemstack1, 55, this.slots.size() - 9, false)) {
					return ItemStack.EMPTY;
				}
			} 

			if (index >= 0 && index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE) {
				if (!this.moveItemStackTo(itemstack1, ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH, this.slots.size(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH && index < this.slots.size() - 9) {
				if (!this.moveItemStackTo(itemstack1, 0, ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= this.slots.size() - 9 && index < this.slots.size()) {
				if (!this.moveItemStackTo(itemstack1, ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE, this.slots.size() - 9, false)) {
					if (!this.moveItemStackTo(itemstack1, 0, ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		
		return itemstack != null ? itemstack : ItemStack.EMPTY;
	}
}