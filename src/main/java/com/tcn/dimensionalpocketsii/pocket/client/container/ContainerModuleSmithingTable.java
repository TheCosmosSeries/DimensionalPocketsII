package com.tcn.dimensionalpocketsii.pocket.client.container;

import java.util.List;
import java.util.OptionalInt;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

public class ContainerModuleSmithingTable extends CosmosContainerMenuBlockEntity {
	public static final int TEMPLATE_SLOT = 0;
	public static final int BASE_SLOT = 1;
	public static final int ADDITIONAL_SLOT = 2;
	public static final int RESULT_SLOT = 3;

	private final List<Integer> inputSlotIndexes;
	protected final ResultContainer resultSlots = new ResultContainer();
    private final int resultSlotIndex;
	
	protected final Container inputSlots;
	
	@Nullable
	private RecipeHolder<SmithingRecipe> selectedRecipe;
	private final List<RecipeHolder<SmithingRecipe>> recipes;

	public ContainerModuleSmithingTable(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleSmithingTable(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(PocketsRegistrationManager.CONTAINER_TYPE_SMITHING_TABLE.get(), indexIn, playerInventoryIn, accessIn, posIn);
        ItemCombinerMenuSlotDefinition itemcombinermenuslotdefinition = this.createInputSlotDefinitions();
        this.inputSlots = this.createContainer(itemcombinermenuslotdefinition.getNumOfInputSlots());
        this.inputSlotIndexes = itemcombinermenuslotdefinition.getInputSlotIndexes();
        this.resultSlotIndex = itemcombinermenuslotdefinition.getResultSlotIndex();
        
		this.recipes = this.getLevel().getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
		this.addSlot(new Slot(inputSlots, 0, 12, 51) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return ContainerModuleSmithingTable.this.recipes.stream().anyMatch(recipe -> recipe.value().isTemplateIngredient(stackIn));
			}
		});
		this.addSlot(new Slot(inputSlots, 1, 30, 51) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return ContainerModuleSmithingTable.this.recipes.stream().anyMatch(recipe -> recipe.value().isBaseIngredient(stackIn));
			}
		});
		this.addSlot(new Slot(inputSlots, 2, 48, 51) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return ContainerModuleSmithingTable.this.recipes.stream().anyMatch(recipe -> recipe.value().isAdditionIngredient(stackIn));
			}
		});
		
		this.addSlot(new Slot(resultSlots, 3, 102, 51) {
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

    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
            .withSlot(0, 8, 48, p_266643_ -> this.recipes.stream().anyMatch(p_300804_ -> p_300804_.value().isTemplateIngredient(p_266643_)))
            .withSlot(1, 26, 48, p_286208_ -> this.recipes.stream().anyMatch(p_300802_ -> p_300802_.value().isBaseIngredient(p_286208_)))
            .withSlot(2, 44, 48, p_286207_ -> this.recipes.stream().anyMatch(p_300798_ -> p_300798_.value().isAdditionIngredient(p_286207_)))
            .withResultSlot(3, 98, 48)
            .build();
    }

	@Override
	public void slotsChanged(Container containerIn) {
		super.slotsChanged(containerIn);
		
		if (containerIn == this.inputSlots) {
			this.createResult();
		}
	}
	
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((p_39796_, p_39797_) -> this.clearContainer(player, this.inputSlots));
    }

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, PocketsRegistrationManager.BLOCK_WALL_SMITHING_TABLE.get());
	}

	protected boolean mayPickup(Player p_40268_, boolean p_40269_) {
		return this.selectedRecipe != null && this.selectedRecipe.value().matches(this.createRecipeInput(), this.getLevel());
	}

	protected void onTake(Player playerIn, ItemStack stackIn) {
		stackIn.onCraftedBy(playerIn.level(), playerIn, stackIn.getCount());
		this.resultSlots.awardUsedRecipes(playerIn, this.getRelevantItems());
		this.shrinkStackInSlot(0);
		this.shrinkStackInSlot(1);
		this.shrinkStackInSlot(2);
		this.access.execute((level, pos) -> {
			level.levelEvent(1044, pos, 0);
		});
	}

	private List<ItemStack> getRelevantItems() {
		return List.of(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
	}

	private void shrinkStackInSlot(int p_40271_) {
		ItemStack itemstack = this.inputSlots.getItem(p_40271_);
		if (!itemstack.isEmpty()) {
			itemstack.shrink(1);
			this.inputSlots.setItem(p_40271_, itemstack);
		}

	}

    public void createResult() {
        SmithingRecipeInput smithingrecipeinput = this.createRecipeInput();
        List<RecipeHolder<SmithingRecipe>> list = this.getLevel().getRecipeManager().getRecipesFor(RecipeType.SMITHING, smithingrecipeinput, this.getLevel());
        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            RecipeHolder<SmithingRecipe> recipeholder = list.get(0);
            ItemStack itemstack = recipeholder.value().assemble(smithingrecipeinput, this.getLevel().registryAccess());
            if (itemstack.isItemEnabled(this.getLevel().enabledFeatures())) {
                this.selectedRecipe = recipeholder;
                this.resultSlots.setRecipeUsed(recipeholder);
                this.resultSlots.setItem(0, itemstack);
            }
        }
    }

	public ItemStack quickMoveStack(Player p_39792_, int p_39793_) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(p_39793_);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			int i = this.getInventorySlotStart();
			int j = this.getUseRowEnd();
			if (p_39793_ == this.getResultSlot()) {
				if (!this.moveItemStackTo(itemstack1, i, j, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (this.inputSlotIndexes.contains(p_39793_)) {
				if (!this.moveItemStackTo(itemstack1, i, j, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.canMoveIntoInputSlots(itemstack1) && p_39793_ >= this.getInventorySlotStart()
					&& p_39793_ < this.getUseRowEnd()) {
				int k = this.getSlotToQuickMoveTo(itemstack);
				if (!this.moveItemStackTo(itemstack1, k, this.getResultSlot(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (p_39793_ >= this.getInventorySlotStart() && p_39793_ < this.getInventorySlotEnd()) {
				if (!this.moveItemStackTo(itemstack1, this.getUseRowStart(), this.getUseRowEnd(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (p_39793_ >= this.getUseRowStart() && p_39793_ < this.getUseRowEnd() && !this
					.moveItemStackTo(itemstack1, this.getInventorySlotStart(), this.getInventorySlotEnd(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(p_39792_, itemstack1);
		}

		return itemstack;
	}

	public boolean canTakeItemForPickAll(ItemStack stackIn, Slot slotIn) {
		return slotIn.container != this.resultSlots && super.canTakeItemForPickAll(stackIn, slotIn);
	}

    public boolean canMoveIntoInputSlots(ItemStack stack) {
        return this.findSlotToQuickMoveTo(stack).isPresent();
    }

    private OptionalInt findSlotToQuickMoveTo(ItemStack stack) {
        return this.recipes
            .stream()
            .flatMapToInt(p_300800_ -> findSlotMatchingIngredient(p_300800_.value(), stack).stream())
            .filter(p_294045_ -> !this.getSlot(p_294045_).hasItem())
            .findFirst();
    }

    private static OptionalInt findSlotMatchingIngredient(SmithingRecipe recipe, ItemStack stack) {
        if (recipe.isTemplateIngredient(stack)) {
            return OptionalInt.of(0);
        } else if (recipe.isBaseIngredient(stack)) {
            return OptionalInt.of(1);
        } else {
            return recipe.isAdditionIngredient(stack) ? OptionalInt.of(2) : OptionalInt.empty();
        }
    }

	public int getSlotToQuickMoveTo(ItemStack p_267159_) {
		return this.inputSlots.isEmpty() ? 0 : this.inputSlotIndexes.get(0);
	}

	public int getResultSlot() {
		return this.resultSlotIndex;
	}

	private int getInventorySlotStart() {
		return this.getResultSlot() + 1;
	}

	private int getInventorySlotEnd() {
		return this.getInventorySlotStart() + 27;
	}

	private int getUseRowStart() {
		return this.getInventorySlotEnd();
	}

	private int getUseRowEnd() {
		return this.getUseRowStart() + 9;
	}

    private SmithingRecipeInput createRecipeInput() {
        return new SmithingRecipeInput(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
    }

    private SimpleContainer createContainer(int p_size) {
        return new SimpleContainer(p_size) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerModuleSmithingTable.this.slotsChanged(this);
            }
        };
    }
}