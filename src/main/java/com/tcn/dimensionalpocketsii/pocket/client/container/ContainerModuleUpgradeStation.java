package com.tcn.dimensionalpocketsii.pocket.client.container;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotBooleanItem;
import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.dimensionalpocketsii.core.crafting.UpgradeStationRecipe;
import com.tcn.dimensionalpocketsii.core.management.CraftingManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.core.management.SoundManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerModuleUpgradeStation extends CosmosContainerMenuBlockEntity {
	
	protected final ResultContainer resultSlots = new ResultContainer();
	
	protected final Container inputSlots = new SimpleContainer(9) {
		
		@Override
		public void setChanged() {
			super.setChanged();
			ContainerModuleUpgradeStation.this.slotsChanged(this);
		}
	};
	
	@Nullable
	private UpgradeStationRecipe selectedRecipe;
	private final List<UpgradeStationRecipe> recipes;

	public ContainerModuleUpgradeStation(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleUpgradeStation(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModBusManager.CONTAINER_TYPE_UPGRADE_STATION, indexIn, playerInventoryIn, accessIn, posIn);
		this.recipes = this.getLevel().getRecipeManager().getAllRecipesFor(CraftingManager.RECIPE_TYPE_UPGRADE_STATION);
		
		//Focus Slot
		this.addSlot(new SlotBooleanItem(inputSlots, 0, 63, 42, true, 1));

		//Input Slots
		this.addSlot(new SlotBooleanItem(inputSlots, 1, 42, 21, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 2, 63, 21, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 3, 84, 21, true, 1));

		this.addSlot(new SlotBooleanItem(inputSlots, 4, 42, 42, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 5, 84, 42, true, 1));
		
		this.addSlot(new SlotBooleanItem(inputSlots, 6, 42, 63, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 7, 63, 63, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 8, 84, 63, true, 1));

		//Output Slot
		this.addSlot(new SlotRestrictedAccess(resultSlots, 9, 126, 42, false, true) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return false;
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				return ContainerModuleUpgradeStation.this.mayPickup(playerIn, this.hasItem());
			}

			@Override
			public void onTake(Player playerIn, ItemStack stackIn) {
				ContainerModuleUpgradeStation.this.onTake(playerIn, stackIn);
			}
		});
		
		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 12 + i1 * 18, 102 + k * 18));
			}
		}

		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 12 + l * 18, 160));
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
		this.access.execute((level, pos) -> {
			this.clearContainer(playerIn, this.inputSlots);
		});
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, ModBusManager.BLOCK_WALL_UPGRADE_STATION);
	}

	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

	@OnlyIn(Dist.CLIENT)
	public void removeSlotListener(ContainerListener listenerIn) {
		super.removeSlotListener(listenerIn);
	}

	protected void onTake(Player playerIn, ItemStack stackIn) {
		stackIn.onCraftedBy(playerIn.level, playerIn, stackIn.getCount());
		
		this.resultSlots.awardUsedRecipes(playerIn);
		for (int i = 0; i < 9; i++) {
			if (!inputSlots.getItem(i).isEmpty()) {
				this.shrinkStackInSlot(i);
			}
		}
		
		player.getLevel().playSound(playerIn, this.getBlockPos(), SoundManager.GENERIC.TINK, SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	private void shrinkStackInSlot(int slotIndex) {
		ItemStack itemstack = this.inputSlots.getItem(slotIndex);
		itemstack.shrink(1);
		this.inputSlots.setItem(slotIndex, itemstack);
	}

	public void createResult() {
		List<UpgradeStationRecipe> list = this.getLevel().getRecipeManager().getRecipesFor(CraftingManager.RECIPE_TYPE_UPGRADE_STATION, this.inputSlots, this.getLevel());
		
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
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn <= 9) {
				if (!this.moveItemStackTo(itemstack1, 10, this.slots.size() - 9, false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn > 9 && indexIn < this.slots.size()) {
				if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
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

	@Override
	public boolean canTakeItemForPickAll(ItemStack stackIn, Slot slotIn) {
		return slotIn.container != this.resultSlots && super.canTakeItemForPickAll(stackIn, slotIn);
	}

	protected boolean shouldQuickMoveToAdditionalSlot(ItemStack stackIn) {
		return this.recipes.stream().anyMatch((recipe) -> {
			return recipe.isFocusIngredient(stackIn);
		});
	}
}