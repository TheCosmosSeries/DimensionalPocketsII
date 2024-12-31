package com.tcn.dimensionalpocketsii.pocket.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerRecipeBookBlockEntity;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class ContainerModuleBlastFurnace extends CosmosContainerRecipeBookBlockEntity<SingleRecipeInput, AbstractCookingRecipe> {
	private final Container container;
	private final ContainerData data;
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;
	private final RecipeBookType recipeBookType;
	
	public ContainerModuleBlastFurnace(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
	    this(indexIn, playerInventoryIn, new SimpleContainer(3), new SimpleContainerData(4), extraData.readBlockPos());
	}
	
	public ContainerModuleBlastFurnace(int indexIn, Inventory playerInventoryIn, Container furnaceInventoryIn, ContainerData furnaceDataIn, BlockPos posIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_BLAST_FURNACE.get(), indexIn, playerInventoryIn, null, posIn);
		
		this.recipeType = RecipeType.BLASTING;
		this.recipeBookType = RecipeBookType.BLAST_FURNACE;
		checkContainerSize(furnaceInventoryIn, 3);
		checkContainerDataCount(furnaceDataIn, 4);
		this.container = furnaceInventoryIn;
		this.data = furnaceDataIn;
		
		this.addSlot(new Slot(furnaceInventoryIn, 0, 52, 17));
		this.addSlot(new SlotModuleBlastFurnaceFuel(this, furnaceInventoryIn, 1, 52, 53));
		this.addSlot(new FurnaceResultSlot(playerInventoryIn.player, furnaceInventoryIn, 2, 112, 35));

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 12 + i1 * 18, 88 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 12 + l * 18, 146));
		}

		this.addDataSlots(furnaceDataIn);
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents recipeItemHelperIn) {
		if (this.container instanceof StackedContentsCompatible) {
			((StackedContentsCompatible) this.container).fillStackedContents(recipeItemHelperIn);
		}
	}

	@Override
	public void clearCraftingContent() {
		this.container.clearContent();
	}

	@Override
	public boolean recipeMatches(RecipeHolder<AbstractCookingRecipe> recipeIn) {
		return recipeIn.value().matches(new SingleRecipeInput(this.container.getItem(0)), this.getLevel());
	}

	@Override
	public int getResultSlotIndex() {
		return 2;
	}

	@Override
	public int getGridWidth() {
		return 1;
	}

	@Override
	public int getGridHeight() {
		return 1;
	}

	@Override
	public int getSize() {
		return 3;
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
			if (indexIn == 2) {
				if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (indexIn != 1 && indexIn != 0) {
				if (this.canSmelt(itemstack1)) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.isFuel(itemstack1)) {
					if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (indexIn >= 3 && indexIn < 30) {
					if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (indexIn >= 30 && indexIn < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}

	@SuppressWarnings({ "unchecked" })
	protected boolean canSmelt(ItemStack stackIn) {
		return this.getLevel().getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) this.recipeType, new SingleRecipeInput(stackIn), this.getLevel()).isPresent();
	}

	protected boolean isFuel(ItemStack stackIn) {
		return stackIn.getBurnTime(this.recipeType) > 0;
	}

	public int getBurnProgress() {
		int i = this.data.get(2);
		int j = this.data.get(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}
	
	public int getLitProgress() {
		int i = this.data.get(1);
		if (i == 0) {
			i = 200;
		}

		return this.data.get(0) * 13 / i;
	}

	public boolean isLit() {
		return this.data.get(0) > 0;
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return this.recipeBookType;
	}
	
	@Override
	public boolean shouldMoveToInventory(int p_150463_) {
		return p_150463_ != 1;
	}
}