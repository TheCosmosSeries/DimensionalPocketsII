package com.tcn.dimensionalpocketsii.client.container;

import com.tcn.cosmoslibrary.client.container.slot.SlotArmourItem;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ContainerElytraplateEnderChest extends AbstractContainerMenu {
	private final Container container;
	private final int containerRows;

	private ItemStack stack;
	protected final Player player;
	private final Level world;

	public static ContainerElytraplateEnderChest createContainerServerSide(int windowID, Inventory playerInventoryIn, Container containerIn, ItemStack stackIn) {
		return new ContainerElytraplateEnderChest(windowID, playerInventoryIn, containerIn, stackIn);
	}

	public static ContainerElytraplateEnderChest createContainerClientSide(int windowID, Inventory playerInventoryIn, RegistryFriendlyByteBuf extraData) {
		return new ContainerElytraplateEnderChest(windowID, playerInventoryIn, new SimpleContainer(9 * 3), ItemStack.STREAM_CODEC.decode(extraData));
	}
	
	public ContainerElytraplateEnderChest(int indexIn, Inventory playerInventoryIn, Container containerIn, ItemStack stackIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST.get(), indexIn);
		
		this.stack = stackIn;
		this.player = playerInventoryIn.player;
		this.world = playerInventoryIn.player.level();
		
		checkContainerSize(containerIn, 3 * 9);
		this.container = containerIn;
		this.containerRows = 3;
		containerIn.startOpen(playerInventoryIn.player);
		
		int i = (this.containerRows - 4) * 18;

		for (int j = 0; j < this.containerRows; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(containerIn, k + j * 9, 21 + k * 18, 15 + j * 18));
			}
		}
		
		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(playerInventoryIn, j1 + l * 9 + 9, 31 + j1 * 18, 98 + l * 18 + i));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventoryIn, i1, 31 + i1 * 18, 155 + i));
		}

		//Armour Slots
		this.addSlot(new SlotArmourItem(playerInventoryIn, 39, 11, 80, this.player, 0));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 38, 11, 99, this.player, 1));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 37, 11, 118, this.player, 2));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 36, 11, 137, this.player, 3));
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return this.container.stillValid(playerIn);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= this.slots.size() - 4 && indexIn < this.slots.size()) {
				if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size() - 4, false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn < this.containerRows * 9) {
				if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size() - 4, false)) {
					return ItemStack.EMPTY;
				}
			} else if (itemstack.getItem() instanceof ArmorItem) {
				if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
					if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		this.container.stopOpen(playerIn);
	}

	public Container getContainer() {
		return this.container;
	}

	public int getRowCount() {
		return this.containerRows;
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