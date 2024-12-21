package com.tcn.dimensionalpocketsii.core.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tcn.dimensionalpocketsii.core.management.ModRecipeManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class UpgradeStationRecipe implements Recipe<UpgradingRecipeInput> {
	
	private final Ingredient focusIngredient;
	private final List<Ingredient> topIngredients;
	private final List<Ingredient> middleIngredients;
	private final List<Ingredient> bottomIngredients;
	private final ItemStack resultIngredient;
	public ResourceLocation ident;
	
	private static final int TOP_ROW = 3;
	private static final int MID_ROW = 2;
	private static final int BOT_ROW = 3;

	public UpgradeStationRecipe(Ingredient focusIngredientIn, List<Ingredient> topIngredientsIn, List<Ingredient> middleIngredientsIn, List<Ingredient> bottomIngredientsIn, ItemStack resultIngredientIn) {
		this.focusIngredient = focusIngredientIn;
		this.topIngredients = topIngredientsIn;
		this.middleIngredients = middleIngredientsIn;
		this.bottomIngredients = bottomIngredientsIn;
		this.resultIngredient = resultIngredientIn;
	}
	
	@Override
	public boolean matches(UpgradingRecipeInput inputIn, Level levelIn) {
		if (!size(Loc.TOP, TOP_ROW) || !size(Loc.MID, MID_ROW) || !size(Loc.BOT, BOT_ROW)) {
			return false;
		}
		
		boolean flagFocus = test(Loc.FOCUS, inputIn, 0, 0);
		boolean flagTopRow = test(Loc.TOP, inputIn, 0, 1) && test(Loc.TOP, inputIn, 1, 2) && test(Loc.TOP, inputIn, 2, 3);
		boolean flagMidRow = test(Loc.MID, inputIn, 0, 4) && test(Loc.MID, inputIn, 1, 5);
		boolean flagBotRow = test(Loc.BOT, inputIn, 0, 6) && test(Loc.BOT, inputIn, 1, 7) && test(Loc.BOT, inputIn, 2, 8);
		
		return flagFocus && flagTopRow && flagMidRow && flagBotRow;
	}
	
	public boolean size(Loc location, int size) {
		if (location.equals(Loc.TOP)) {
			return this.topIngredients.size() == size;
		} else if (location.equals(Loc.MID)) {
			return this.middleIngredients.size() == size;
		} else if (location.equals(Loc.BOT)) {
			return this.bottomIngredients.size() == size;
		} else {
			return false;
		}
	}
	
	public boolean test(Loc location, UpgradingRecipeInput inputIn, int recipeIndex, int containerIndex) {
		if (location.equals(Loc.FOCUS)) {
			return this.focusIngredient.test(inputIn.getItem(containerIndex));
		} else if (location.equals(Loc.TOP)) {
			return this.topIngredients.get(recipeIndex).test(inputIn.getItem(containerIndex));
		} else if (location.equals(Loc.MID)) {
			return this.middleIngredients.get(recipeIndex).test(inputIn.getItem(containerIndex));
		} else if (location.equals(Loc.BOT)) {
			return this.bottomIngredients.get(recipeIndex).test(inputIn.getItem(containerIndex));
		} else {
			return false;
		}
	}

	public boolean isFocusIngredient(ItemStack stackIn) {
		return this.getFocusIngredient().test(stackIn);
	}
	
	@Override
	public ItemStack assemble(UpgradingRecipeInput recipeInput, HolderLookup.Provider accessIn) {
		ItemStack itemstack = recipeInput.getItem(0).transmuteCopy(this.resultIngredient.getItem(), this.resultIngredient.getCount());
        itemstack.applyComponents(this.resultIngredient.getComponentsPatch());
        return itemstack;
	}

	@Override
	public boolean canCraftInDimensions(int xIn, int yIn) {
		return xIn * yIn >= 2;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider accessIn) {
		return this.getResultItemStack().copy();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModRegistrationManager.MODULE_UPGRADE_STATION.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeManager.RECIPE_SERIALIZER_UPGRADE_STATION.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeManager.RECIPE_TYPE_UPGRADE_STATION.get();
	}
	
	@Override
	public boolean isIncomplete() {
		return Stream.of(
			this.focusIngredient, 
			this.getTopIngredient(0), this.getTopIngredient(1), this.getTopIngredient(2), 
			this.getMiddleIngredient(0), this.getMiddleIngredient(1), 
			this.getBottomIngredient(0), this.getBottomIngredient(1), this.getBottomIngredient(2)
			).anyMatch((ingredient) -> {
				return ingredient.getItems().length == 0;
			}
		);
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(this.focusIngredient, this.topIngredients.get(0), this.topIngredients.get(1), this.topIngredients.get(2), this.middleIngredients.get(0), this.middleIngredients.get(1), this.bottomIngredients.get(0), this.bottomIngredients.get(1), this.bottomIngredients.get(2));
	}
	
	public List<Ingredient> getIngredientList() {
		return List.of(this.focusIngredient, this.topIngredients.get(0), this.topIngredients.get(1), this.topIngredients.get(2), this.middleIngredients.get(0), this.middleIngredients.get(1), this.bottomIngredients.get(0), this.bottomIngredients.get(1), this.bottomIngredients.get(2));
	}
	
	@Override
	public boolean isSpecial() {
		return true;
	}
	
	public Ingredient getFocusIngredient() {
		return this.focusIngredient;
	}

	public Ingredient getTopIngredient(int index) {
		return this.topIngredients.get(index);
	}

	public Ingredient getMiddleIngredient(int index) {
		return this.middleIngredients.get(index);
	}

	public Ingredient getBottomIngredient(int index) {
		return this.bottomIngredients.get(index);
	}
	
	public ItemStack getResultItemStack() {
		return this.resultIngredient;
	}
	
	public static enum Loc {
		FOCUS,
		TOP,
		MID,
		BOT;
	}

	public static class Serializer implements RecipeSerializer<UpgradeStationRecipe> {		
		public static final MapCodec<UpgradeStationRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
    			Ingredient.CODEC_NONEMPTY.fieldOf("focus").forGetter(recipe -> recipe.focusIngredient),
                Ingredient.LIST_CODEC.fieldOf("topRow").forGetter(recipe -> recipe.topIngredients),
                Ingredient.LIST_CODEC.fieldOf("middleRow").forGetter(recipe -> recipe.middleIngredients),
                Ingredient.LIST_CODEC.fieldOf("bottomRow").forGetter(recipe -> recipe.bottomIngredients),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.resultIngredient)
            ).apply(instance, UpgradeStationRecipe::new)
        );
		
		public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeStationRecipe> STREAM_CODEC = StreamCodec.of(
				UpgradeStationRecipe.Serializer::toNetwork, UpgradeStationRecipe.Serializer::fromNetwork
		);
	    
	    @Override
	    public MapCodec<UpgradeStationRecipe> codec() {
	        return CODEC;
	    }
	    
	    @Override
	    public StreamCodec<RegistryFriendlyByteBuf, UpgradeStationRecipe> streamCodec() {
	        return STREAM_CODEC;
	    }
	    
		private static UpgradeStationRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			Ingredient focus = Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn);
			
			ArrayList<Ingredient> topRow = new ArrayList<Ingredient>();
			for (int i = 0; i < TOP_ROW; i++) {
				topRow.add(i, Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn));
			}

			ArrayList<Ingredient> middleRow = new ArrayList<Ingredient>();
			for (int i = 0; i < MID_ROW; i++) {
				middleRow.add(i, Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn));
			}

			ArrayList<Ingredient> bottomRow = new ArrayList<Ingredient>();
			for (int i = 0; i < BOT_ROW; i++) {
				bottomRow.add(i, Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn));
			}
			
			ItemStack result = ItemStack.STREAM_CODEC.decode(extraDataIn);
			
			return new UpgradeStationRecipe(focus, topRow, middleRow, bottomRow, result);
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, UpgradeStationRecipe recipeIn) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, recipeIn.focusIngredient);
			
            for (Ingredient ingredient : recipeIn.topIngredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, ingredient);
            }

            for (Ingredient ingredient : recipeIn.middleIngredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, ingredient);
            }

            for (Ingredient ingredient : recipeIn.bottomIngredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, ingredient);
            }
            
			ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.resultIngredient);
		}
	}
}