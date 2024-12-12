package com.tcn.dimensionalpocketsii.client.container;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.container.slot.SlotBucket;
import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class ContainerElytraplateConnector extends AbstractContainerMenu {

	private ItemStack stack;
	protected final Player player;
	private final Level world;
	private Pocket pocket;
	private Container bucketSlots;
	
	public static ContainerElytraplateConnector createContainerServerSide(int windowID, Inventory playerInventory, ItemStack stackIn) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			if (compound.contains("nbt_data")) {
				CompoundTag nbtData = compound.getCompound("nbt_data");
				
				if (nbtData.contains("chunk_pos")) {
					CompoundTag chunkPos = nbtData.getCompound("chunk_pos");

					int x = chunkPos.getInt("x");
					int z = chunkPos.getInt("z");
					CosmosChunkPos chunk = new CosmosChunkPos(x, z);
					
					Pocket pocket_ = StorageManager.getPocketFromChunkPosition(null, chunk);
					
					if (pocket_ != null) {
						return new ContainerElytraplateConnector(windowID, playerInventory, pocket_, new SimpleContainer(2), pocket_, stackIn);
					}
				}
			}
		}
		
		return new ContainerElytraplateConnector(windowID, playerInventory, new SimpleContainer(ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH), new SimpleContainer(2), new Pocket(null), stackIn);
	}

	public static ContainerElytraplateConnector createContainerClientSide(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
		return new ContainerElytraplateConnector(windowID, playerInventory, new SimpleContainer(ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE_WITH), new SimpleContainer(2), Pocket.readFromNBT(extraData.readNbt(), extraData.registryAccess()), ItemStack.STREAM_CODEC.decode(extraData));
	}
	
	protected ContainerElytraplateConnector(int id, Inventory playerInventoryIn, Container pocketIn, Container contentsIn, @Nullable Pocket pocketActual, ItemStack stackIn) {
		super(ModRegistrationManager.CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR.get(), id);
		
		this.stack = stackIn;
		this.player = playerInventoryIn.player;
		this.world = playerInventoryIn.player.level();
		
		this.bucketSlots = contentsIn;
		
		if (pocketActual != null) {
			this.pocket = pocketActual;
		}
		
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
		this.addSlot(new SlotBucket(contentsIn, 0, 60, 184) {
			
			@Override
			public void set(ItemStack stackIn) {
				super.set(stackIn);
				
				if (stackIn.getItem() instanceof BucketItem) {
					Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(stackIn);
					
					if (fluidStack.isPresent()) {
						FluidStack fluid = fluidStack.get();
						
						if (fluid != null) {
							int amount = ContainerElytraplateConnector.this.pocket.fill(fluid, FluidAction.SIMULATE);
							
							if (amount == fluid.getAmount()) {
								if (contentsIn.getItem(1).getItem().equals(Items.BUCKET) && contentsIn.getItem(1).getCount() < 17) {
									ContainerElytraplateConnector.this.pocket.fill(fluid, FluidAction.EXECUTE);
									
									stackIn.shrink(1);
									contentsIn.getItem(1).grow(1);
									this.setChanged();
								} if (contentsIn.getItem(1).isEmpty()) {
									ContainerElytraplateConnector.this.pocket.fill(fluid, FluidAction.EXECUTE);
									
									stackIn.shrink(1);
									contentsIn.setItem(1, new ItemStack(Items.BUCKET));
									this.setChanged();
								}
							}
						}
					} else {
						if (ContainerElytraplateConnector.this.pocket.getCurrentFluidAmount() > 0) {
							if (contentsIn.getItem(51).isEmpty()) {
								ItemStack fillStack = FluidUtil.tryFillContainer(stackIn, ContainerElytraplateConnector.this.pocket.getFluidTank(), 1000, null, true).result;
								
								if (fillStack.getItem() instanceof BucketItem) {
									stackIn.shrink(1);
									contentsIn.setItem(1, fillStack);
									this.setChanged();
								} else {
									stackIn.shrink(1);
									contentsIn.setItem(1, new ItemStack(Items.BUCKET));
									this.setChanged();
								}
							}
						}	
					}
				}
			}
		});
		this.addSlot(new SlotRestrictedAccess(contentsIn, 1, 60, 205, 1, false, true));
		
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
	public ItemStack getStack() {
		return this.stack;
	}

	public Level getLevel() {
		return this.world;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Pocket getPocket() {
		return this.pocket;
	}

	@Override
	public void removed(Player playerIn) {
		if (!this.bucketSlots.getItem(0).isEmpty() || !this.bucketSlots.getItem(1).isEmpty()) {
			playerIn.getInventory().placeItemBackInInventory(this.bucketSlots.removeItemNoUpdate(0));
			playerIn.getInventory().placeItemBackInInventory(this.bucketSlots.removeItemNoUpdate(1));
		}
	}
}