package com.tcn.dimensionalpocketsii.pocket.core.blockentity;

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
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkHooks;

public class BlockEntityModuleBlastFurnace extends CosmosBlockEntityUpdateable implements IBlockInteract, Container, WorldlyContainer, RecipeHolder, StackedContentsCompatible, IBlockEntityUIMode {
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
	
	private Pocket pocket;
	
	public BlockEntityModuleBlastFurnace(BlockPos posIn, BlockState stateIn) {
		super(ModBusManager.BLOCK_ENTITY_TYPE_BLAST_FURNACE, posIn, stateIn);
		
		this.recipeType = RecipeType.BLASTING;
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return PocketRegistryManager.getPocketFromChunkPosition(CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}

	public void sendUpdates(boolean update) {
		super.sendUpdates(update);
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		if (this.getPocket().exists()) {
			this.getPocket().writeToNBT(compound);
		}
		
		compound.putInt("BurnTime", this.litTime);
		compound.putInt("CookTime", this.cookingProgress);
		compound.putInt("CookTimeTotal", this.cookingTotalTime);
		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		CompoundTag compoundnbt = new CompoundTag();
		this.recipesUsed.forEach((p_235643_1_, p_235643_2_) -> { compoundnbt.putInt(p_235643_1_.toString(), p_235643_2_); });
		compound.put("RecipesUsed", compoundnbt);

		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}

	public void saveToItemStack(ItemStack stackIn) {
		CompoundTag compound = stackIn.getOrCreateTag();
		
		compound.putInt("BurnTime", this.litTime);
		compound.putInt("CookTime", this.cookingProgress);
		compound.putInt("CookTimeTotal", this.cookingTotalTime);
		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		CompoundTag compoundnbt = new CompoundTag();
		this.recipesUsed.forEach((p_235643_1_, p_235643_2_) -> { compoundnbt.putInt(p_235643_1_.toString(), p_235643_2_); });
		compound.put("RecipesUsed", compoundnbt);

		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);

		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound);
		}
		
		this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);
		this.litTime = compound.getInt("BurnTime");
		this.cookingProgress = compound.getInt("CookTime");
		this.cookingTotalTime = compound.getInt("CookTimeTotal");
		this.litDuration = this.getBurnDuration(this.inventoryItems.get(1));
		CompoundTag compoundnbt = compound.getCompound("RecipesUsed");

		for (String s : compoundnbt.getAllKeys()) {
			this.recipesUsed.put(new ResourceLocation(s), compoundnbt.getInt(s));
		}

		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}

	public void loadFromItemStack(ItemStack stackIn) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			this.inventoryItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(compound, this.inventoryItems);
			this.litTime = compound.getInt("BurnTime");
			this.cookingProgress = compound.getInt("CookTime");
			this.cookingTotalTime = compound.getInt("CookTimeTotal");
			this.litDuration = this.getBurnDuration(this.inventoryItems.get(1));
			CompoundTag compoundnbt = compound.getCompound("RecipesUsed");

			for (String s : compoundnbt.getAllKeys()) {
				this.recipesUsed.put(new ResourceLocation(s), compoundnbt.getInt(s));
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
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
		this.sendUpdates(true);
	}
	
	/**
	 * Retrieve the data to be stored. [TE > NBT] (WRITE)
	 */
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag);
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
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag_ = pkt.getTag();
		this.handleUpdateTag(tag_);
	}
	
	@Override
	public void onLoad() { }

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		worldIn.sendBlockUpdated(pos, state, state, 3);
		this.setChanged();
		this.sendUpdates(true);
		
		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return InteractionResult.FAIL;
		}
		
		if (!playerIn.isShiftKeyDown()) {
			if (worldIn.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayer) {
					if (this.canPlayerAccess(playerIn)) {
						NetworkHooks.openGui((ServerPlayer)playerIn, state.getMenuProvider(worldIn, pos), (packetBuffer) -> { packetBuffer.writeBlockPos(pos); });
					} else {
						CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
						return InteractionResult.FAIL;
					}
				}
				
				return InteractionResult.SUCCESS;
			}
		} else {
			if(!worldIn.isClientSide) {
				CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
				Pocket pocketIn = PocketRegistryManager.getPocketFromChunkPosition(chunkPos);
				
				if(pocketIn.exists()) {
					if (CosmosUtil.holdingWrench(playerIn)) {
						if (pocketIn.checkIfOwner(playerIn)) {
							ItemStack stack = new ItemStack(ModBusManager.MODULE_BLAST_FURNACE);
							this.saveToItemStack(stack);
							
							worldIn.setBlockAndUpdate(pos, ModBusManager.BLOCK_WALL.defaultBlockState());
							worldIn.removeBlockEntity(pos);
							
							CosmosUtil.addStack(worldIn, playerIn, stack);
							
							return InteractionResult.SUCCESS;
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
							return InteractionResult.FAIL;
						}
					} 
					
					else if (CosmosUtil.handEmpty(playerIn)) {
						pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		
		return InteractionResult.SUCCESS;
	}

	private boolean isLit() {
		return this.litTime > 0;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityModuleBlastFurnace entityIn) {
		boolean flag = entityIn.isLit();
		boolean flag1 = false;
		
		if (entityIn.isLit()) {
			--entityIn.litTime;
		}

		if (!entityIn.level.isClientSide) {
			ItemStack itemstack = entityIn.inventoryItems.get(1);
			
			if (entityIn.isLit() || !itemstack.isEmpty() && !entityIn.inventoryItems.get(0).isEmpty()) {
				Recipe<?> irecipe = entityIn.level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) entityIn.recipeType, entityIn, entityIn.level).orElse(null);
				
				if (!entityIn.isLit() && entityIn.canBurn(irecipe)) {
					entityIn.litTime = entityIn.getBurnDuration(itemstack);
					entityIn.litDuration = entityIn.litTime;
					
					if (entityIn.isLit()) {
						flag1 = true;
						
						if (itemstack.hasContainerItem())
							entityIn.inventoryItems.set(1, itemstack.getContainerItem());
						else if (!itemstack.isEmpty()) {
							Item item = itemstack.getItem();
							itemstack.shrink(1);
							if (itemstack.isEmpty()) {
								entityIn.inventoryItems.set(1, itemstack.getContainerItem());
							}
						}
					}
				}

				if (entityIn.isLit() && entityIn.canBurn(irecipe)) {
					++entityIn.cookingProgress;
					if (entityIn.cookingProgress == entityIn.cookingTotalTime) {
						entityIn.cookingProgress = 0;
						entityIn.cookingTotalTime = entityIn.getTotalCookTime();
						entityIn.burn(irecipe);
						flag1 = true;
					}
				} else {
					entityIn.cookingProgress = 0;
				}
			} else if (!entityIn.isLit() && entityIn.cookingProgress > 0) {
				entityIn.cookingProgress = Mth.clamp(entityIn.cookingProgress - 2, 0, entityIn.cookingTotalTime);
			}

			if (flag != entityIn.isLit()) {
				flag1 = true;
				entityIn.level.setBlock(entityIn.worldPosition, entityIn.level.getBlockState(entityIn.worldPosition).setValue(BlockWallBlastFurnace.LIT, Boolean.valueOf(entityIn.isLit())), 3);
			}
		}

		if (flag1) {
			entityIn.setChanged();
		}

	}

	protected boolean canBurn(@Nullable Recipe<?> recipeIn) {
		if (!this.inventoryItems.get(0).isEmpty() && recipeIn != null) {
			ItemStack itemstack = recipeIn.getResultItem();
			if (itemstack.isEmpty()) {
				return false;
			} else {
				ItemStack itemstack1 = this.inventoryItems.get(2);
				if (itemstack1.isEmpty()) {
					return true;
				} else if (!itemstack1.sameItem(itemstack)) {
					return false;
				} else if (itemstack1.getCount() + itemstack.getCount() <= this.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
					return true;
				} else {
					return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
				}
			}
		} else {
			return false;
		}
	}

	private void burn(@Nullable Recipe<?> recipeIn) {
		if (recipeIn != null && this.canBurn(recipeIn)) {
			ItemStack itemstack = this.inventoryItems.get(0);
			ItemStack itemstack1 = recipeIn.getResultItem();
			ItemStack itemstack2 = this.inventoryItems.get(2);
			if (itemstack2.isEmpty()) {
				this.inventoryItems.set(2, itemstack1.copy());
			} else if (itemstack2.getItem() == itemstack1.getItem()) {
				itemstack2.grow(itemstack1.getCount());
			}

			if (!this.level.isClientSide) {
				this.setRecipeUsed(recipeIn);
			}

			if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.inventoryItems.get(1).isEmpty() && this.inventoryItems.get(1).getItem() == Items.BUCKET) {
				this.inventoryItems.set(1, new ItemStack(Items.WATER_BUCKET));
			}

			itemstack.shrink(1);
		}
	}

	@SuppressWarnings("unused")
	protected int getBurnDuration(ItemStack stackIn) {
		if (stackIn.isEmpty()) {
			return 0;
		} else {
			Item item = stackIn.getItem();
			return ForgeHooks.getBurnTime(stackIn, this.recipeType);
		}
	}

	@SuppressWarnings("unchecked")
	protected int getTotalCookTime() {
		return this.level.getRecipeManager() .getRecipeFor((RecipeType<AbstractCookingRecipe>) this.recipeType, this, this.level) .map(AbstractCookingRecipe::getCookingTime).orElse(200);
	}

	public static boolean isFuel(ItemStack stackIn, RecipeType<?> recipeType) {
		return ForgeHooks.getBurnTime(stackIn, recipeType) > 0;
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
		boolean flag = !stackIn.isEmpty() && stackIn.sameItem(itemstack) && ItemStack.tagMatches(stackIn, itemstack);
		this.inventoryItems.set(indexIn, stackIn);
		
		if (stackIn.getCount() > this.getMaxStackSize()) {
			stackIn.setCount(this.getMaxStackSize());
		}

		if (indexIn == 0 && !flag) {
			this.cookingTotalTime = this.getTotalCookTime();
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
			return isFuel(stackIn, this.recipeType) || stackIn.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
		}
	}

	@Override
	public void clearContent() {
		this.inventoryItems.clear();
	}

	@Override
	public void setRecipeUsed(@Nullable Recipe<?> recipeIn) {
		if (recipeIn != null) {
			ResourceLocation resourcelocation = recipeIn.getId();
			this.recipesUsed.addTo(resourcelocation, 1);
		}
	}

	@Nullable
	@Override
	public Recipe<?> getRecipeUsed() {
		return null;
	}

	@Override
	public void awardUsedRecipes(Player playerIn) { }

	public void awardUsedRecipesAndPopExperience(Player playerIn) {
		List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(playerIn.level, playerIn.position());
		playerIn.awardRecipes(list);
		this.recipesUsed.clear();
	}

	public List<Recipe<?>> getRecipesToAwardAndPopExperience(Level worldIn, Vec3 vec3) {
		List<Recipe<?>> list = Lists.newArrayList();

		for (Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
			worldIn.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> { list.add(recipe); createExperience(worldIn, vec3, entry.getIntValue(), ((AbstractCookingRecipe) recipe).getExperience()); });
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

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return handlers[facing.get3DDataValue()].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (int x = 0; x < handlers.length; x++) {
			handlers[x].invalidate();
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