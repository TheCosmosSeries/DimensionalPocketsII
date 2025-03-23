package com.tcn.dimensionalpocketsii.core.crafting;

import java.util.List;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.tcn.dimensionalpocketsii.core.management.CraftingManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class UpgradeStationRecipe implements Recipe<Container> {
	public final Ingredient focus;
	public final Ingredient[] topRow;
	public final Ingredient[] middleRow;
	public final Ingredient[] bottomRow;
	public final ItemStack result;
	private final ResourceLocation id;

	public UpgradeStationRecipe(ResourceLocation idIn, Ingredient focusIn, Ingredient[] topRowIn, Ingredient[] middleRowIn, Ingredient[] bottomRowIn, ItemStack resultIn) {
		this.id = idIn;
		this.focus = focusIn;
		this.topRow = topRowIn;
		this.middleRow = middleRowIn;
		this.bottomRow = bottomRowIn;
		this.result = resultIn;
	}

	@Override
	public boolean matches(Container containerIn, Level levelIn) {
		boolean flagFocus = this.focus.test(containerIn.getItem(0));
		boolean flagTopRow = this.topRow[0].test(containerIn.getItem(1)) && this.topRow[1].test(containerIn.getItem(2)) && this.topRow[2].test(containerIn.getItem(3));
		boolean flagMiddleRow = this.middleRow[0].test(containerIn.getItem(4)) && this.middleRow[1].test(containerIn.getItem(5));
		boolean flagBottomRow = this.bottomRow[0].test(containerIn.getItem(6)) && this.bottomRow[1].test(containerIn.getItem(7)) && this.bottomRow[2].test(containerIn.getItem(8));
		
		return flagFocus && flagTopRow && flagMiddleRow && flagBottomRow;
	}

	@Override
	public ItemStack assemble(Container containerIn) {
		ItemStack itemstack = this.result.copy();
		CompoundTag compoundtag = containerIn.getItem(0).getTag();
		
		if (compoundtag != null) {
			itemstack.setTag(compoundtag.copy());
		}

		return itemstack;
	}

	@Override
	public boolean canCraftInDimensions(int xIn, int yIn) {
		return xIn * yIn >= 2;
	}

	@Override
	public ItemStack getResultItem() {
		return this.result;
	}

	public boolean isFocusIngredient(ItemStack stackIn) {
		return this.focus.test(stackIn);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModBusManager.MODULE_UPGRADE_STATION);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CraftingManager.RECIPE_SERIALIZER_UPGRADE_STATION;
	}

	@Override
	public RecipeType<?> getType() {
		return CraftingManager.RECIPE_TYPE_UPGRADE_STATION;
	}
	
	@Override
	public boolean isIncomplete() {
		return Stream.of(this.focus, this.topRow[0], this.topRow[1], this.topRow[2], this.middleRow[0], this.middleRow[1], this.bottomRow[0], this.bottomRow[1], this.bottomRow[2]).anyMatch((p_151284_) -> {
			return p_151284_.getItems().length == 0;
		});
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(this.focus, this.topRow[0], this.topRow[1], this.topRow[2], this.middleRow[0], this.middleRow[1], this.bottomRow[0], this.bottomRow[1], this.bottomRow[2]);
	}
	
	public List<Ingredient> getIngredientList() {
		return List.of(this.focus, this.topRow[0], this.topRow[1], this.topRow[2], this.middleRow[0], this.middleRow[1], this.bottomRow[0], this.bottomRow[1], this.bottomRow[2]);
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<UpgradeStationRecipe> {
		
		@Override
		public UpgradeStationRecipe fromJson(ResourceLocation idIn, JsonObject recipeIn) {
			Ingredient focus = Ingredient.fromJson(GsonHelper.getAsJsonObject(recipeIn, "focus"));

			JsonObject topRowObj = GsonHelper.getAsJsonObject(recipeIn, "topRow");
			Ingredient[] topRow = new Ingredient[] {
				topRowObj.has("left") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(topRowObj, "left")) : Ingredient.EMPTY,
				topRowObj.has("middle") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(topRowObj, "middle")) : Ingredient.EMPTY,
				topRowObj.has("right") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(topRowObj, "right")) : Ingredient.EMPTY
			};

			JsonObject middleRowObj = GsonHelper.getAsJsonObject(recipeIn, "middleRow");
			Ingredient[] middleRow = new Ingredient[] {
				middleRowObj.has("left") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(middleRowObj, "left")) : Ingredient.EMPTY,
				middleRowObj.has("right") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(middleRowObj, "right")) : Ingredient.EMPTY
			};
			
			JsonObject bottomRowObj = GsonHelper.getAsJsonObject(recipeIn, "bottomRow");
			Ingredient[] bottomRow = new Ingredient[] {
				bottomRowObj.has("left") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(bottomRowObj, "left")) : Ingredient.EMPTY,
				bottomRowObj.has("middle") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(bottomRowObj, "middle")) : Ingredient.EMPTY,
				bottomRowObj.has("right") ? Ingredient.fromJson(GsonHelper.getAsJsonObject(bottomRowObj, "right")) : Ingredient.EMPTY
			};
			
			ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(recipeIn, "result"));
			
			return new UpgradeStationRecipe(idIn, focus, topRow, middleRow, bottomRow, result);
		}

		@Override
		public UpgradeStationRecipe fromNetwork(ResourceLocation idIn, FriendlyByteBuf extraDataIn) {
			Ingredient focus = Ingredient.fromNetwork(extraDataIn);
			
			Ingredient[] topRow = new Ingredient[] { null, null, null };
			for (int i = 0; i < 3; i++) {
				topRow[i] = Ingredient.fromNetwork(extraDataIn);
			}

			Ingredient[] middleRow = new Ingredient[] { null, null };
			for (int i = 0; i < 2; i++) {
				middleRow[i] = Ingredient.fromNetwork(extraDataIn);
			}
			
			Ingredient[] bottomRow = new Ingredient[] { null, null, null };
			for (int i = 0; i < 3; i++) {
				bottomRow[i] = Ingredient.fromNetwork(extraDataIn);
			}
			
			ItemStack result = extraDataIn.readItem();
			
			return new UpgradeStationRecipe(idIn, focus, topRow, middleRow, bottomRow, result);
		}

		@Override
		public void toNetwork(FriendlyByteBuf extraDataIn, UpgradeStationRecipe recipeIn) {
			recipeIn.focus.toNetwork(extraDataIn);
			
			for (int i = 0; i < 3; i ++) {
				recipeIn.topRow[i].toNetwork(extraDataIn);
			}

			for (int i = 0; i < 2; i ++) {
				recipeIn.middleRow[i].toNetwork(extraDataIn);
			}
			
			for (int i = 0; i < 3; i ++) {
				recipeIn.bottomRow[i].toNetwork(extraDataIn);
			}
			
			extraDataIn.writeItem(recipeIn.result);
		}
	}
}
