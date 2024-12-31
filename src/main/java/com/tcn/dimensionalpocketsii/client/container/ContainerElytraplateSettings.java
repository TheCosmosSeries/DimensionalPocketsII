package com.tcn.dimensionalpocketsii.client.container;

import com.tcn.cosmoslibrary.client.container.slot.SlotArmourItem;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ContainerElytraplateSettings extends AbstractContainerMenu {

	private ItemStack stack;
	protected final Player player;
	private final Level world;
	
	public static ContainerElytraplateSettings createContainerServerSide(int windowID, Inventory playerInventory, ItemStack stackIn) {
		return new ContainerElytraplateSettings(windowID, playerInventory, stackIn);
	}

	public static ContainerElytraplateSettings createContainerClientSide(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
		return new ContainerElytraplateSettings(windowID, playerInventory, ItemStack.STREAM_CODEC.decode(extraData));
	}
	
	protected ContainerElytraplateSettings(int id, Inventory playerInventoryIn, ItemStack stackIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_ELYTRAPLATE_SETTINGS.get(), id);
		
		this.stack = stackIn;
		this.player = playerInventoryIn.player;
		this.world = playerInventoryIn.player.level();

		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 26 + i1 * 18, 65 + k * 18));
			}
		}

		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 26 + l * 18, 122));
		}
		
		//Armour Slots
		this.addSlot(new SlotArmourItem(playerInventoryIn, 39, 6, 65, this.player, 0));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 38, 6, 84, this.player, 1));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 37, 6, 103, this.player, 2));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 36, 6, 122, this.player, 3));
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= this.slots.size() - 4 && indexIn < this.slots.size()) {
				if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - 4, false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn >= this.slots.size() - 13 && indexIn < this.slots.size() - 4) {
				if (itemstack.getItem() instanceof ArmorItem) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
						if (!this.moveItemStackTo(itemstack1, 11, this.slots.size() - 13, false)) {
							return ItemStack.EMPTY;
						}
					}
				} else if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - 13, false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn >= 0 && indexIn < this.slots.size() - 13) {
				if (itemstack.getItem() instanceof ArmorItem) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
						if (!this.moveItemStackTo(itemstack1, 11, this.slots.size() - 13, false)) {
							return ItemStack.EMPTY;
						}
					}
				} else if (!this.moveItemStackTo(itemstack1, this.slots.size() - 13, this.slots.size() - 4, false)) {
					return ItemStack.EMPTY;
				}
			}
			
			slot.onTake(playerIn, itemstack1);
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			return itemstack;
		}
		return ItemStack.EMPTY;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public Level getLevel() {
		return this.world;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}