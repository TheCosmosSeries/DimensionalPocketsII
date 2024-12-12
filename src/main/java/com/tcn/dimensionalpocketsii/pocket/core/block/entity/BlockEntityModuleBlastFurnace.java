package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unchecked")
public class BlockEntityModuleBlastFurnace extends CosmosBlockEntityUpdateable implements IBlockInteract, Container, WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible, IBlockEntityUIMode {
	private static final int[] SLOTS_FOR_UP = new int[] { 0 };
	private static final int[] SLOTS_FOR_DOWN = new int[] { 0 };
	private static final int[] SLOTS_FOR_SIDES = new int[] { 0, 2, 1 };
	
	public NonNullList<ItemStack> inventoryItems = NonNullList.withSize(3, ItemStack.EMPTY);
	
	private int litTime;
	private int litDuration;
	private int cookingProgress;
	private int cookingTotalTime;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public final ContainerData dataAccess = new ContainerData() {
		@Override
		public int get(int p_221476_1_) {
			switch (p_221476_1_) {
			case 0:
				return BlockEntityModuleBlastFurnace.this.litTime;
			case 1:
				return BlockEntityModuleBlastFurnace.this.litDuration;
			case 2:
				return BlockEntityModuleBlastFurnace.this.cookingProgress;
			case 3:
				return BlockEntityModuleBlastFurnace.this.cookingTotalTime;
			default:
				return 0;
			}
		}
		
		@Override
		public void set(int p_221477_1_, int p_221477_2_) {
			switch (p_221477_1_) {
			case 0:
				BlockEntityModuleBlastFurnace.this.litTime = p_221477_2_;
				break;
			case 1:
				BlockEntityModuleBlastFurnace.this.litDuration = p_221477_2_;
				break;
			case 2:
				BlockEntityModuleBlastFurnace.this.cookingProgress = p_221477_2_;
				break;
			case 3:
				BlockEntityModuleBlastFurnace.this.cookingTotalTime = p_221477_2_;
			}

		}

		@Override
		public int getCount() {
			return 4;
		}
	};
	
	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
	protected final RecipeType<? extends AbstractCookingRecipe> recipeType;
	private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

	private Pocket pocket;
	
	public BlockEntityModuleBlastFurnace(BlockPos posIn, BlockState stateIn) {
		super(ModRegistrationManager.BLOCK_ENTITY_TYPE_BLAST_FURNACE.get(), posIn, stateIn);
		
		this.recipeType = RecipeType.BLASTING;
		this.quickCheck = RecipeManager.createCheck((RecipeType<AbstractCookingRecipe>)recipeType);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return StorageManager.getPocketFromChunkPosition(this.getLevel(), CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}

	public void sendUpdates(boolean update) {
		super.sendUpdates(update);
	}
	
	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		if (this.getPocket().exists()) {
			this.getPocket().writeToNBT(compound, provider);
		}
		
		compound.putInt("BurnTime", this.litTime);
		compound.putInt("CookTime", this.cookingProgress);
		compound.putInt("CookTimeTotal", this.cookingTotalTime);
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		CompoundTag compoundnbt = new CompoundTag();
		this.recipesUsed.forEach((p_235643_1_, p_235643_2_) -> { compoundnbt.putInt(p_235643_1_.toString(), p_235643_2_); });
		compound.put("RecipesUsed", compoundnbt);

		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}

	public void saveToItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		CompoundTag compound = stackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		
		compound.putInt("BurnTime", this.litTime);
		compound.putInt("CookTime", this.cookingProgress);
		compound.putInt("CookTimeTotal", this.cookingTotalTime);
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		CompoundTag compoundnbt = new CompoundTag();
		this.recipesUsed.forEach((p_235643_1_, p_235643_2_) -> { compoundnbt.putInt(p_235643_1_.toString(), p_235643_2_); });
		compound.put("RecipesUsed", compoundnbt);

		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		
		stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
	}
	
	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);

		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound, provider);
		}
		
		this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
		
		this.litTime = compound.getInt("BurnTime");
		this.cookingProgress = compound.getInt("CookTime");
		this.cookingTotalTime = compound.getInt("CookTimeTotal");
		this.litDuration = this.getBurnDuration(this.inventoryItems.get(1));
		CompoundTag compoundnbt = compound.getCompound("RecipesUsed");

		for (String s : compoundnbt.getAllKeys()) {
			this.recipesUsed.put(ResourceLocation.parse(s), compoundnbt.getInt(s));
		}

		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}

	public void loadFromItemStack(ItemStack stackIn, HolderLookup.Provider provider) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag compound = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
			this.litTime = compound.getInt("BurnTime");
			this.cookingProgress = compound.getInt("CookTime");
			this.cookingTotalTime = compound.getInt("CookTimeTotal");
			this.litDuration = this.getBurnDuration(this.inventoryItems.get(1));
			CompoundTag compoundnbt = compound.getCompound("RecipesUsed");

			for (String s : compoundnbt.getAllKeys()) {
				this.recipesUsed.put(ResourceLocation.parse(s), compoundnbt.getInt(s));
			}

			this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
			this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
			this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
		}
	}
	
	/**
	 * Set the data once it has been received. [NBT > TE] (READ)
	 */
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
		this.sendUpdates(true);
	}
	
	/**
	 * Retrieve the data to be stored. [TE > NBT] (WRITE)
	 */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag, provider);
		return tag;
	}
	
	/**
	 * Actually sends the data to the server. [NBT > SER]
	 */
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	/**
	 * Method is called once packet has been received by the client. [SER > CLT]
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
		super.onDataPacket(net, pkt, provider);
		CompoundTag tag_ = pkt.getTag();
		this.handleUpdateTag(tag_, provider);
	}
	
	@Override
	public void onLoad() { }

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		levelIn.sendBlockUpdated(pos, state, state, 3);
		this.setChanged();
		this.sendUpdates(true);
		
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return ItemInteractionResult.FAIL;
		}
		
		if (!playerIn.isShiftKeyDown()) {
			if (levelIn.isClientSide) {
				return ItemInteractionResult.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayer serverPlayer) {
					if (this.canPlayerAccess(playerIn)) {
						serverPlayer.openMenu(state.getMenuProvider(levelIn, pos), (packetBuffer) -> { packetBuffer.writeBlockPos(pos); });
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return ItemInteractionResult.FAIL;
					}
				}
				
				return ItemInteractionResult.SUCCESS;
			}
		} else {
			if(!levelIn.isClientSide) {
				CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
				Pocket pocketIn = StorageManager.getPocketFromChunkPosition(levelIn, chunkPos);
				
				if(pocketIn.exists()) {
					if (CosmosUtil.holdingWrench(playerIn)) {
						if (pocketIn.checkIfOwner(playerIn)) {
							ItemStack stack = new ItemStack(ModRegistrationManager.MODULE_BLAST_FURNACE.get());
							this.saveToItemStack(stack, levelIn.registryAccess());
							
							levelIn.setBlockAndUpdate(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState());
							levelIn.removeBlockEntity(pos);
							
							CosmosUtil.addStack(levelIn, playerIn, stack);
							
							return ItemInteractionResult.SUCCESS;
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
							return ItemInteractionResult.FAIL;
						}
					} 
					
					else if (CosmosUtil.handEmpty(playerIn)) {
						pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
						return ItemInteractionResult.SUCCESS;
					}
				}
			}
		}
		
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}
	
	private boolean isLit() {
		return this.litTime > 0;
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityModuleBlastFurnace blockEntityIn) {
		boolean flag = blockEntityIn.isLit();
        boolean flag1 = false;
        if (blockEntityIn.isLit()) {
            blockEntityIn.litTime--;
        }

        ItemStack itemstack = blockEntityIn.inventoryItems.get(1);
        ItemStack itemstack1 = blockEntityIn.inventoryItems.get(0);
        boolean flag2 = !itemstack1.isEmpty();
        boolean flag3 = !itemstack.isEmpty();
        if (blockEntityIn.isLit() || flag3 && flag2) {
            RecipeHolder<?> recipeholder;
            if (flag2) {
                recipeholder = blockEntityIn.quickCheck.getRecipeFor(new SingleRecipeInput(itemstack1), levelIn).orElse(null);
            } else {
                recipeholder = null;
            }

            int i = blockEntityIn.getMaxStackSize();
            if (!blockEntityIn.isLit() && canBurn(levelIn.registryAccess(), recipeholder, blockEntityIn.inventoryItems, i, blockEntityIn)) {
                blockEntityIn.litTime = blockEntityIn.getBurnDuration(itemstack);
                blockEntityIn.litDuration = blockEntityIn.litTime;
                if (blockEntityIn.isLit()) {
                    flag1 = true;
                    if (itemstack.hasCraftingRemainingItem())
                        blockEntityIn.inventoryItems.set(1, itemstack.getCraftingRemainingItem());
                    else
                    if (flag3) {
                        //Item item = itemstack.getItem();
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            blockEntityIn.inventoryItems.set(1, itemstack.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (blockEntityIn.isLit() && canBurn(levelIn.registryAccess(), recipeholder, blockEntityIn.inventoryItems, i, blockEntityIn)) {
                blockEntityIn.cookingProgress++;
                if (blockEntityIn.cookingProgress == blockEntityIn.cookingTotalTime) {
                    blockEntityIn.cookingProgress = 0;
                    blockEntityIn.cookingTotalTime = getTotalCookTime(levelIn, blockEntityIn);
                    if (burn(levelIn.registryAccess(), recipeholder, blockEntityIn.inventoryItems, i, blockEntityIn)) {
                        blockEntityIn.setRecipeUsed(recipeholder);
                    }

                    flag1 = true;
                }
            } else {
                blockEntityIn.cookingProgress = 0;
            }
        } else if (!blockEntityIn.isLit() && blockEntityIn.cookingProgress > 0) {
            blockEntityIn.cookingProgress = Mth.clamp(blockEntityIn.cookingProgress - 2, 0, blockEntityIn.cookingTotalTime);
        }

        if (flag != blockEntityIn.isLit()) {
            flag1 = true;
            stateIn = stateIn.setValue(BlockWallFurnace.LIT, Boolean.valueOf(blockEntityIn.isLit()));
            levelIn.setBlock(posIn, stateIn, 3);
        }

        if (flag1) {
            setChanged(levelIn, posIn, stateIn);
        }
	}

    private static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, BlockEntityModuleBlastFurnace furnace) {
        if (!inventory.get(0).isEmpty() && recipe != null) {
            ItemStack itemstack = ((RecipeHolder<? extends AbstractCookingRecipe>) recipe).value().assemble(new SingleRecipeInput(furnace.getItem(0)), registryAccess);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = inventory.get(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(itemstack1, itemstack)) {
                    return false;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= maxStackSize && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize() // Neo fix: make furnace respect stack sizes in furnace recipes
                        ? true
                        : itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Neo fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

	private static boolean burn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, BlockEntityModuleBlastFurnace furnace) {
        if (recipe != null && canBurn(registryAccess, recipe, inventory, maxStackSize, furnace)) {
            ItemStack itemstack = inventory.get(0);
            ItemStack itemstack1 = ((RecipeHolder<? extends AbstractCookingRecipe>) recipe).value().assemble(new SingleRecipeInput(furnace.getItem(0)), registryAccess);
            ItemStack itemstack2 = inventory.get(2);
            if (itemstack2.isEmpty()) {
                inventory.set(2, itemstack1.copy());
            } else if (ItemStack.isSameItemSameComponents(itemstack2, itemstack1)) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (itemstack.is(Blocks.WET_SPONGE.asItem()) && !inventory.get(1).isEmpty() && inventory.get(1).is(Items.BUCKET)) {
                inventory.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    protected int getBurnDuration(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            return fuel.getBurnTime(this.recipeType);
        }
    }

    private static int getTotalCookTime(Level level, BlockEntityModuleBlastFurnace blockEntity) {
        SingleRecipeInput singlerecipeinput = new SingleRecipeInput(blockEntity.getItem(0));
        return blockEntity.quickCheck.getRecipeFor(singlerecipeinput, level).map(p_300840_ -> p_300840_.value().getCookingTime()).orElse(200);
    }

    public static boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(null) > 0;
    }

	@Override
	public int[] getSlotsForFace(Direction directionIn) {
		if (directionIn == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		} else {
			return directionIn == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int indexIn, ItemStack stackIn, @Nullable Direction directionIn) {
		return this.canPlaceItem(indexIn, stackIn);
	}

	@Override
	public boolean canTakeItemThroughFace(int indexIn, ItemStack stackIn, Direction directionIn) {
		if (directionIn == Direction.DOWN && indexIn == 1) {
			Item item = stackIn.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int getContainerSize() {
		return this.inventoryItems.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryItems) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int indexIn) {
		return this.inventoryItems.get(indexIn);
	}

	@Override
	public ItemStack removeItem(int indexIn, int countIn) {
		return ContainerHelper.removeItem(this.inventoryItems, indexIn, countIn);
	}

	@Override
	public ItemStack removeItemNoUpdate(int indexIn) {
		return ContainerHelper.takeItem(this.inventoryItems, indexIn);
	}

	@Override
	public void setItem(int indexIn, ItemStack stackIn) {
		ItemStack itemstack = this.inventoryItems.get(indexIn);
        boolean flag = !stackIn.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stackIn);
        this.inventoryItems.set(indexIn, stackIn);
        stackIn.limitSize(this.getMaxStackSize(stackIn));
        if (indexIn == 0 && !flag) {
            this.cookingTotalTime = getTotalCookTime(this.level, this);
            this.cookingProgress = 0;
            this.setChanged();
        }
	}

	@Override
	public boolean stillValid(Player playerIn) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return playerIn.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public boolean canPlaceItem(int indexIn, ItemStack stackIn) {
		if (indexIn == 2) {
			return false;
		} else if (indexIn != 1) {
			return true;
		} else {
			ItemStack itemstack = this.inventoryItems.get(1);
			return stackIn.getBurnTime(this.recipeType) > 0 || stackIn.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
		}
	}

	@Override
	public void clearContent() {
		this.inventoryItems.clear();
	}

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.id();
            this.recipesUsed.addTo(resourcelocation, 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, List<ItemStack> items) {
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);

        for (RecipeHolder<?> recipeholder : list) {
            if (recipeholder != null) {
                player.triggerRecipeCrafted(recipeholder, this.inventoryItems);
            }
        }

        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
        List<RecipeHolder<?>> list = Lists.newArrayList();

        for (Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent(p_300839_ -> {
                list.add((RecipeHolder<?>)p_300839_);
                createExperience(level, popVec, entry.getIntValue(), ((AbstractCookingRecipe)p_300839_.value()).getExperience());
            });
        }

        return list;
    }

	private static void createExperience(Level worldIn, Vec3 posIn, int craftedAmount, float experience) {
		int i = Mth.floor((float) craftedAmount * experience);
		float f = Mth.frac((float) craftedAmount * experience);
		if (f != 0.0F && Math.random() < (double) f) {
			++i;
		}

		while (i > 0) {
			int j = ExperienceOrb.getExperienceValue(i);
			i -= j;
			worldIn.addFreshEntity(new ExperienceOrb(worldIn, posIn.x, posIn.y, posIn.z, j));
		}

	}

	@Override
	public void fillStackedContents(StackedContents itemHelperIn) {
		for (ItemStack itemstack : this.inventoryItems) {
			itemHelperIn.accountStack(itemstack);
		}
	}

	@Override
	public EnumUIMode getUIMode() {
		return this.uiMode;
	}

	@Override
	public void setUIMode(EnumUIMode modeIn) {
		this.uiMode = modeIn;
	}

	@Override
	public void cycleUIMode() {
		this.uiMode = EnumUIMode.getNextStateFromState(this.uiMode);
	}

	@Override
	public EnumUIHelp getUIHelp() {
		return this.uiHelp;
	}

	@Override
	public void setUIHelp(EnumUIHelp modeIn) {
		this.uiHelp = modeIn;
	}

	@Override
	public void cycleUIHelp() {
		this.uiHelp = EnumUIHelp.getNextStateFromState(this.uiHelp);
	}

	@Override
	public EnumUILock getUILock() {
		return this.uiLock;
	}

	@Override
	public void setUILock(EnumUILock modeIn) {
		this.uiLock = modeIn;
	}

	@Override
	public void cycleUILock() {
		this.uiLock = EnumUILock.getNextStateFromState(this.uiLock);
	}

	@Override
	public void setOwner(Player playerIn) { }

	@Override
	public boolean canPlayerAccess(Player playerIn) {
		if (this.getUILock().equals(EnumUILock.PUBLIC)) {
			return true;
		} else {
			if (this.getPocket().checkIfOwner(playerIn)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean checkIfOwner(Player playerIn) {
		if (this.getPocket().checkIfOwner(playerIn)) {
			return true;
		}
		return false;
	}
}