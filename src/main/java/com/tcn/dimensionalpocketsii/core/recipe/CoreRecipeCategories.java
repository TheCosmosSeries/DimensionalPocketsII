package com.tcn.dimensionalpocketsii.core.recipe;

import java.util.List;
import java.util.function.Supplier;

import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class CoreRecipeCategories {
	public static final EnumProxy<RecipeBookCategories> CUSTOM_RECIPE_CATEGORY_ENUM_PROXY = new EnumProxy<>(
			RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(ModRegistrationManager.MODULE_UPGRADE_STATION.get()))
	);
}