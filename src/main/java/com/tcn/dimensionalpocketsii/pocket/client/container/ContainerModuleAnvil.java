package com.tcn.dimensionalpocketsii.pocket.client.container;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

@SuppressWarnings("unused")
public class ContainerModuleAnvil extends CosmosContainerMenuBlockEntity {
	public static final int INPUT_SLOT = 0;
	public static final int ADDITIONAL_SLOT = 1;
	public static final int RESULT_SLOT = 2;

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final boolean DEBUG_COST = false;
	public static final int MAX_NAME_LENGTH = 50;
	public int repairItemCountCost;
	private String itemName;
	private final DataSlot cost = DataSlot.standalone();
	private static final int COST_FAIL = 0;
	private static final int COST_BASE = 1;
	private static final int COST_ADDED_BASE = 1;
	private static final int COST_REPAIR_MATERIAL = 1;
	private static final int COST_REPAIR_SACRIFICE = 2;
	private static final int COST_INCOMPATIBLE_PENALTY = 1;
	private static final int COST_RENAME = 1;

	protected final ResultContainer resultSlots = new ResultContainer();
	
	protected final Container inputSlots = new SimpleContainer(2) {
		
		@Override
		public void setChanged() {
			super.setChanged();
			ContainerModuleAnvil.this.slotsChanged(this);
		}
	};
	
	public ContainerModuleAnvil(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerModuleAnvil(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModRegistrationManager.CONTAINER_TYPE_ANVIL.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		this.addSlot(new Slot(inputSlots, 0, 30, 51));
		this.addSlot(new Slot(inputSlots, 1, 79, 51));
		this.addSlot(new Slot(resultSlots, 2, 138, 51) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return false;
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				return ContainerModuleAnvil.this.mayPickup(playerIn, this.hasItem());
			}

			@Override
			public void onTake(Player playerIn, ItemStack stackIn) {
				ContainerModuleAnvil.this.onTake(playerIn, stackIn);
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
		
	    this.addDataSlot(this.cost);
	}

	protected boolean mayPickup(Player playerIn, boolean bool) {
		 return (playerIn.getAbilities().instabuild || playerIn.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
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
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputSlots));
    }

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, ModRegistrationManager.BLOCK_WALL_ANVIL.get());
	}

	protected void onTake(Player playerIn, ItemStack stackIn) {
		if (!playerIn.getAbilities().instabuild) {
			playerIn.giveExperienceLevels(-this.cost.get());
		}

		float breakChance = ContainerModuleAnvil.onAnvilRepair(playerIn, stackIn, ContainerModuleAnvil.this.inputSlots.getItem(0), ContainerModuleAnvil.this.inputSlots.getItem(1));
		
		this.inputSlots.setItem(0, ItemStack.EMPTY);
		if (this.repairItemCountCost > 0) {
			ItemStack itemstack = this.inputSlots.getItem(1);
			if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
				itemstack.shrink(this.repairItemCountCost);
				this.inputSlots.setItem(1, itemstack);
			} else {
				this.inputSlots.setItem(1, ItemStack.EMPTY);
			}
		} else {
			this.inputSlots.setItem(1, ItemStack.EMPTY);
		}

		this.cost.set(0);
		this.access.execute((level, pos) -> {
			BlockState blockstate = level.getBlockState(pos);
			if (!playerIn.getAbilities().instabuild && blockstate.is(BlockTags.ANVIL)
					&& playerIn.getRandom().nextFloat() < breakChance) {
				BlockState blockstate1 = AnvilBlock.damage(blockstate);
				if (blockstate1 == null) {
					level.removeBlock(pos, false);
					level.levelEvent(1029, pos, 0);
				} else {
					level.setBlock(pos, blockstate1, 2);
					level.levelEvent(1030, pos, 0);
				}
			} else {
				level.levelEvent(1030, pos, 0);
			}

		});
	}

	private void shrinkStackInSlot(int slotIndex) {
		ItemStack itemstack = this.inputSlots.getItem(slotIndex);
		itemstack.shrink(1);
		this.inputSlots.setItem(slotIndex, itemstack);
	}

	public void createResult() {
		ItemStack itemstack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int i = 0;
        long j = 0L;
        int k = 0;
        if (!itemstack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemstack)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemstack1));
            j += (long)itemstack.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue() + (long)itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0)).intValue();
            this.repairItemCountCost = 0;
            boolean flag = false;
            if (!ContainerModuleAnvil.onAnvilChange(this, itemstack, itemstack2, resultSlots, itemName, j, this.player)) return;
            if (!itemstack2.isEmpty()) {
                flag = itemstack2.has(DataComponents.STORED_ENCHANTMENTS);
                if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                    int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    int j3;
                    for (j3 = 0; l2 > 0 && j3 < itemstack2.getCount(); j3++) {
                        int k3 = itemstack1.getDamageValue() - l2;
                        itemstack1.setDamageValue(k3);
                        i++;
                        l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    }

                    this.repairItemCountCost = j3;
                } else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    if (itemstack1.isDamageableItem() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
                        Holder<Enchantment> holder = entry.getKey();
                        int i2 = itemenchantments$mutable.getLevel(holder);
                        int j2 = entry.getIntValue();
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        Enchantment enchantment = holder.value();
                        // Neo: Respect IItemExtension#supportsEnchantment - we also delegate the logic for Enchanted Books to this method.
                        // Though we still allow creative players to combine any item with any enchantment in the anvil here.
                        boolean flag1 = itemstack.supportsEnchantment(holder);
                        if (this.player.getAbilities().instabuild) {
                            flag1 = true;
                        }

                        for (Holder<Enchantment> holder1 : itemenchantments$mutable.keySet()) {
                            if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
                                flag1 = false;
                                i++;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (j2 > enchantment.getMaxLevel()) {
                                j2 = enchantment.getMaxLevel();
                            }

                            itemenchantments$mutable.set(holder, j2);
                            int l3 = enchantment.getAnvilCost();
                            if (flag) {
                                l3 = Math.max(1, l3 / 2);
                            }

                            i += l3 * j2;
                            if (itemstack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
                if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    itemstack1.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                }
            } else if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                k = 1;
                i += k;
                itemstack1.remove(DataComponents.CUSTOM_NAME);
            }
            if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

            int k2 = (int)Mth.clamp(j + (long)i, 0L, 2147483647L);
            this.cost.set(k2);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (k == i && k > 0 && this.cost.get() >= 40) {
                this.cost.set(39);
            }

            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int i3 = itemstack1.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                if (i3 < itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0))) {
                    i3 = itemstack2.getOrDefault(DataComponents.REPAIR_COST, Integer.valueOf(0));
                }

                if (k != i || k == 0) {
                    i3 = calculateIncreasedRepairCost(i3);
                }

                itemstack1.set(DataComponents.REPAIR_COST, i3);
                EnchantmentHelper.setEnchantments(itemstack1, itemenchantments$mutable.toImmutable());
            }

            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
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
				if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (indexIn != 0 && indexIn != 1) {
				if (indexIn >= 3 && indexIn < 39) {
					int i = this.shouldQuickMoveToAdditionalSlot(itemstack) ? 1 : 0;
					if (!this.moveItemStackTo(itemstack1, i, 2, false)) {
						return ItemStack.EMPTY;
					}
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

	@Override
	public boolean canTakeItemForPickAll(ItemStack stackIn, Slot slotIn) {
		return slotIn.container != this.resultSlots && super.canTakeItemForPickAll(stackIn, slotIn);
	}

	protected boolean shouldQuickMoveToAdditionalSlot(ItemStack stackIn) {
		return false;
	}

	public static int calculateIncreasedRepairCost(int costIn) {
		return costIn * 2 + 1;
	}

    public boolean setItemName(String itemName) {
        String s = validateName(itemName);
        if (s != null && !s.equals(this.itemName)) {
            this.itemName = s;
            if (this.getSlot(2).hasItem()) {
                ItemStack itemstack = this.getSlot(2).getItem();
                if (StringUtil.isBlank(s)) {
                    itemstack.remove(DataComponents.CUSTOM_NAME);
                } else {
                    itemstack.set(DataComponents.CUSTOM_NAME, Component.literal(s));
                }
            }

            this.createResult();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private static String validateName(String itemName) {
        String s = StringUtil.filterText(itemName);
        return s.length() <= 50 ? s : null;
    }

	public int getCost() {
		return this.cost.get();
	}
	
    public void setMaximumCost(long value) {
        this.cost.set((int)Mth.clamp(value, 0L, Integer.MAX_VALUE));
    }

    public static boolean onAnvilChange(ContainerModuleAnvil container, ItemStack left, ItemStack right, Container outputSlot, String name, long baseCost, Player player) {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost, player);
        if (NeoForge.EVENT_BUS.post(e).isCanceled()) {
            return false;
        }
        if (e.getOutput().isEmpty()) {
            return true;
        }

        outputSlot.setItem(0, e.getOutput());
        container.setMaximumCost(e.getCost());
        container.repairItemCountCost = e.getMaterialCost();
        return false;
    }

    public static float onAnvilRepair(Player player, ItemStack output, ItemStack left, ItemStack right) {
        AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
        NeoForge.EVENT_BUS.post(e);
        return e.getBreakChance();
    }
}