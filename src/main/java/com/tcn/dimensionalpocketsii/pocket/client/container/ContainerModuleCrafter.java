package com.tcn.dimensionalpocketsii.pocket.client.container;

import java.util.Optional;

import com.tcn.cosmoslibrary.client.container.CosmosContainerRecipeBookBlockEntity;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerModuleCrafter extends CosmosContainerRecipeBookBlockEntity<CraftingContainer> {
	private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
	private final ResultContainer resultSlots = new ResultContainer();
	
	public ContainerModuleCrafter(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleCrafter(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModBusManager.CONTAINER_TYPE_CRAFTER, indexIn, playerInventoryIn, accessIn, posIn);
		
		this.addSlot(new ResultSlot(playerInventoryIn.player, this.craftSlots, this.resultSlots, 0, 128, 35));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(this.craftSlots, j + i * 3, 34 + j * 18, 17 + i * 18));
			}
		}

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 12 + i1 * 18, 88 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 12 + l * 18, 146));
		}

	}

	protected static void slotChangedCraftingGrid(AbstractContainerMenu indexIn, Level worldIn, Player playerIn, CraftingContainer inventoryIn, ResultContainer resultIn) {
		if (!worldIn.isClientSide) {
			ServerPlayer serverplayerentity = (ServerPlayer) playerIn;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = worldIn.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, inventoryIn, worldIn);
			
			if (optional.isPresent()) {
				CraftingRecipe icraftingrecipe = optional.get();
				if (resultIn.setRecipeUsed(worldIn, serverplayerentity, icraftingrecipe)) {
					itemstack = icraftingrecipe.assemble(inventoryIn);
				}
			}

			resultIn.setItem(0, itemstack);
			serverplayerentity.connection.send(new ClientboundContainerSetSlotPacket(indexIn.containerId, indexIn.incrementStateId(), 0, itemstack));
		}
	}

	@Override
	public void slotsChanged(Container inventoryIn) {
		this.access.execute((worldIn, posIn) -> { slotChangedCraftingGrid(this, worldIn, this.player, this.craftSlots, this.resultSlots); });
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents recipeItemHelperIn) {
		this.craftSlots.fillStackedContents(recipeItemHelperIn);
	}

	@Override
	public void clearCraftingContent() {
		this.craftSlots.clearContent();
		this.resultSlots.clearContent();
	}

	@Override
	public boolean recipeMatches(Recipe<? super CraftingContainer> recipeIn) {
		return recipeIn.matches(this.craftSlots, this.player.level);
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		this.access.execute((worldIn, posIn) -> { this.clearContainer(playerIn, this.craftSlots); });
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, ModBusManager.BLOCK_WALL_CRAFTER);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (indexIn == 0) {
				this.access.execute((worldIn, posIn) -> { itemstack1.getItem().onCraftedBy(itemstack1, worldIn, playerIn); });
				
				if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (indexIn >= 10 && indexIn < 46) {
				if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
					if (indexIn < 37) {
						if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
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
			if (indexIn == 0) {
				playerIn.drop(itemstack1, false);
			}
		}

		return itemstack;
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stackIn, Slot slotIn) {
		return slotIn.container != this.resultSlots && super.canTakeItemForPickAll(stackIn, slotIn);
	}

	@Override
	public int getResultSlotIndex() {
		return 0;
	}

	@Override
	public int getGridWidth() {
		return this.craftSlots.getWidth();
	}

	@Override
	public int getGridHeight() {
		return this.craftSlots.getHeight();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getSize() {
		return 10;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	public boolean shouldMoveToInventory(int p_150635_) {
		return p_150635_ != this.getResultSlotIndex();
	}
}
