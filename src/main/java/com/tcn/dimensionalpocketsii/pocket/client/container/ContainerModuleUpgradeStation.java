package com.tcn.dimensionalpocketsii.pocket.client.container;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotArmourItem;
import com.tcn.cosmoslibrary.client.container.slot.SlotBooleanItem;
import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.dimensionalpocketsii.core.management.PocketsRecipeManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsSoundManager;
import com.tcn.dimensionalpocketsii.core.recipe.UpgradeStationRecipe;
import com.tcn.dimensionalpocketsii.core.recipe.UpgradingRecipeInput;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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
	private RecipeHolder<UpgradeStationRecipe> selectedRecipe;
//	private final List<RecipeHolder<UpgradeStationRecipe>> recipes;

	public ContainerModuleUpgradeStation(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleUpgradeStation(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_UPGRADE_STATION.get(), indexIn, playerInventoryIn, accessIn, posIn);
//        this.recipes = this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipeManager.RECIPE_TYPE_UPGRADE_STATION.get());

		//Focus Slot
		this.addSlot(new SlotBooleanItem(inputSlots, 0, 76, 42, true, 1));

		//Input Slots
		this.addSlot(new SlotBooleanItem(inputSlots, 1, 55, 21, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 2, 76, 21, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 3, 97, 21, true, 1));

		this.addSlot(new SlotBooleanItem(inputSlots, 4, 55, 42, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 5, 97, 42, true, 1));
		
		this.addSlot(new SlotBooleanItem(inputSlots, 6, 55, 63, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 7, 76, 63, true, 1));
		this.addSlot(new SlotBooleanItem(inputSlots, 8, 97, 63, true, 1));

		//Output Slot
		this.addSlot(new SlotRestrictedAccess(resultSlots, 9, 139, 42, false, true) {
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
		
		//Armour Slots
		this.addSlot(new SlotArmourItem(playerInventoryIn, 39, 25, 15, this.player, 0));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 38, 25, 33, this.player, 1));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 37, 25, 51, this.player, 2));
		this.addSlot(new SlotArmourItem(playerInventoryIn, 36, 25, 69, this.player, 3));
	}

    protected boolean mayPickup(Player player, boolean hasStack) {
        return this.selectedRecipe != null && this.selectedRecipe.value().matches(this.createRecipeInput(), this.getLevel());
    }

	@Override
	public void slotsChanged(Container containerIn) {
		super.slotsChanged(containerIn);
		
		if (containerIn == this.inputSlots) {
			this.access.execute((level, pos) -> this.createResult(level, this.player));
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
		return stillValid(this.access, playerIn, PocketsRegistrationManager.BLOCK_WALL_UPGRADE_STATION.get());
	}

	@Override
	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void removeSlotListener(ContainerListener listenerIn) {
		super.removeSlotListener(listenerIn);
	}

	protected void onTake(Player playerIn, ItemStack stackIn) {
		stackIn.onCraftedBy(playerIn.level(), playerIn, stackIn.getCount());
		
		this.resultSlots.awardUsedRecipes(playerIn, this.getRelevantItems());
		for (int i = 0; i < 9; i++) {
			if (!inputSlots.getItem(i).isEmpty()) {
				this.shrinkStackInSlot(i);
			}
		}
		
		player.level().playSound(playerIn, this.getBlockPos(), PocketsSoundManager.TINK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	private List<ItemStack> getRelevantItems() {
		return List.of(this.resultSlots.getItem(0));
	}

    private UpgradingRecipeInput createRecipeInput() {
        return new UpgradingRecipeInput(
    		this.inputSlots.getItem(0), 
    		this.inputSlots.getItem(1), this.inputSlots.getItem(2), this.inputSlots.getItem(3), 
    		this.inputSlots.getItem(4), this.inputSlots.getItem(5), 
    		this.inputSlots.getItem(6), this.inputSlots.getItem(7), this.inputSlots.getItem(8)
        );
    }

	private void shrinkStackInSlot(int slotIndex) {
		ItemStack itemstack = this.inputSlots.getItem(slotIndex);
		itemstack.shrink(1);
		this.inputSlots.setItem(slotIndex, itemstack);
	}

	public void createResult(Level levelIn, Player playerIn) {
		if (!levelIn.isClientSide) {
			UpgradingRecipeInput recipeInput = this.createRecipeInput();
			ServerPlayer serverPlayer = (ServerPlayer) playerIn;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<RecipeHolder<UpgradeStationRecipe>> optional = levelIn.getServer().getRecipeManager().getRecipeFor(PocketsRecipeManager.RECIPE_TYPE_UPGRADE_STATION.get(), recipeInput, levelIn);
			
//			System.out.println(levelIn.getServer().getRecipeManager().getAllRecipesFor(ModRecipeManager.RECIPE_TYPE_UPGRADE_STATION.get()).get(0).value().getFocusIngredient().getItems()[0].getItem());
			
//			System.out.println(recipeInput.getItem(0).getItem());
			
			for (int i = 0; i < 9; i++) {
//				System.out.println(i + ": " + recipeInput.getItem(i).getItem());
			}
			
			if (optional.isPresent()) {
				RecipeHolder<UpgradeStationRecipe> recipeholder = optional.get();
				UpgradeStationRecipe craftingrecipe = recipeholder.value();
				this.selectedRecipe = recipeholder;

				if (resultSlots.setRecipeUsed(levelIn, serverPlayer, recipeholder)) {
					ItemStack itemstack1 = craftingrecipe.assemble(recipeInput, levelIn.registryAccess());
					if (itemstack1.isItemEnabled(levelIn.enabledFeatures())) {
						itemstack = itemstack1;
					}
				}
			}
			this.resultSlots.setRecipeUsed(selectedRecipe);
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
			} else if (indexIn >= 1 && indexIn <= 9) {
				if (!this.moveItemStackTo(itemstack1, 10, this.slots.size() - 9, false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn > 9 && indexIn < this.slots.size()) {
				if (itemstack.getItem() instanceof ArmorItem && indexIn < this.slots.size() - 4) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, false)) { 
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 4, this.slots.size(), false)) {
							return ItemStack.EMPTY;
						}
					}
				} else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
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
}