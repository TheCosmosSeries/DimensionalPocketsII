package com.tcn.dimensionalpocketsii.core.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class UpgradingRecipeInput implements RecipeInput {

	private ItemStack focus;
	
	private ItemStack item1;
	private ItemStack item2;
	private ItemStack item3;
	private ItemStack item4;
	private ItemStack item5;
	private ItemStack item6;
	private ItemStack item7;
	private ItemStack item8;
	
	public UpgradingRecipeInput(ItemStack focus, ItemStack item1, ItemStack item2, ItemStack item3, ItemStack item4, ItemStack item5, ItemStack item6, ItemStack item7, ItemStack item8) {
		this.focus = focus;
		
		this.item1 = item1;
		this.item2 = item2;
		this.item3 = item3;
		this.item4 = item4;
		this.item5 = item5;
		this.item6 = item6;
		this.item7 = item7;
		this.item8 = item8;
	}
   
	@Override
    public ItemStack getItem(int indexIn) {
        return switch (indexIn) {
            case 0 -> this.focus;
            case 1 -> this.item1;
            case 2 -> this.item2;
            case 3 -> this.item3;
            case 4 -> this.item4;
            case 5 -> this.item5;
            case 6 -> this.item6;
            case 7 -> this.item7;
            case 8 -> this.item8;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + indexIn);
        };
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        return this.focus.isEmpty() && this.item1.isEmpty() && this.item2.isEmpty() && this.item3.isEmpty() && this.item4.isEmpty() && this.item5.isEmpty() && this.item6.isEmpty() && this.item7.isEmpty() && this.item8.isEmpty();
    }
}