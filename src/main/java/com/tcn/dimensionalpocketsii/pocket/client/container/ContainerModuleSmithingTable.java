package com.tcn.dimensionalpocketsii.pocket.client.container;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;

public class ContainerModuleSmithingTable extends CosmosContainerMenuBlockEntity {
	public static final int INPUT_SLOT = 0;
	public static final int ADDITIONAL_SLOT = 1;
	public static final int RESULT_SLOT = 2;

	protected final ResultContainer resultSlots = new ResultContainer();
	
	protected final Container inputSlots = new SimpleContainer(2) {
		
		@Override
		public void setChanged() {
			super.setChanged();
			ContainerModuleSmithingTable.this.slotsChanged(this);
		}
	};
	
	@Nullable
	private UpgradeRecipe selectedRecipe;
	private final List<UpgradeRecipe> recipes;

	public ContainerModuleSmithingTable(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleSmithingTable(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModBusManager.CONTAINER_TYPE_SMITHING_TABLE, indexIn, playerInventoryIn, accessIn, posIn);
		this.recipes = this.getLevel().getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
		
		this.addSlot(new Slot(inputSlots, 0, 30, 51));
		this.addSlot(new Slot(inputSlots, 1, 79, 51));
		this.addSlot(new Slot(resultSlots, 2, 138, 51) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return false;
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				return ContainerModuleSmithingTable.this.mayPickup(playerIn, this.hasItem());
			}

			@Override
			public void onTake(Player playerIn, ItemStack stackIn) {
				ContainerModuleSmithingTable.this.onTake(playerIn, stackIn);
			}
		});
		
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 12 + i1 * 18, 88 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 12 + l * 18, 146));
		}

	}

	protected boolean mayPickup(Player playerIn, boolean bool) {
		return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.getLevel());
	}

	@Override
	public void slotsChanged(Container containerIn) {
		super.slotsChanged(containerIn);
		
		if (containerIn == this.inputSlots) {
			this.createResult();
		}
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		this.access.execute((p_39796_, p_39797_) -> {
			this.clearContainer(playerIn, this.inputSlots);
		});
	}


	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, ModBusManager.BLOCK_WALL_SMITHING_TABLE);
	}

	protected void onTake(Player playerIn, ItemStack stackIn) {
		stackIn.onCraftedBy(playerIn.level, playerIn, stackIn.getCount());
		
		this.resultSlots.awardUsedRecipes(playerIn);
		this.shrinkStackInSlot(0);
		this.shrinkStackInSlot(1);
		
		this.access.execute((p_40263_, p_40264_) -> {
			p_40263_.levelEvent(1044, p_40264_, 0);
		});
	}

	private void shrinkStackInSlot(int slotIndex) {
		ItemStack itemstack = this.inputSlots.getItem(slotIndex);
		itemstack.shrink(1);
		this.inputSlots.setItem(slotIndex, itemstack);
	}

	public void createResult() {
		List<UpgradeRecipe> list = this.getLevel().getRecipeManager().getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.getLevel());
		
		if (list.isEmpty()) {
			this.resultSlots.setItem(0, ItemStack.EMPTY);
		} else {
			this.selectedRecipe = list.get(0);
			ItemStack itemstack = this.selectedRecipe.assemble(this.inputSlots);
			this.resultSlots.setRecipeUsed(this.selectedRecipe);
			this.resultSlots.setItem(0, itemstack);
		}
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (indexIn == 2) {
				if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (indexIn != 0 && indexIn != 1) {
				if (indexIn >= 3 && indexIn < this.slots.size()) {
					int i = this.shouldQuickMoveToAdditionalSlot(itemstack) ? 1 : 0;
					
					if (!this.moveItemStackTo(itemstack1, i, 2, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), false)) {
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

	@Override
	public boolean canTakeItemForPickAll(ItemStack stackIn, Slot slotIn) {
		return slotIn.container != this.resultSlots && super.canTakeItemForPickAll(stackIn, slotIn);
	}

	protected boolean shouldQuickMoveToAdditionalSlot(ItemStack p_40255_) {
		return this.recipes.stream().anyMatch((p_40261_) -> {
			return p_40261_.isAdditionIngredient(p_40255_);
		});
	}
}