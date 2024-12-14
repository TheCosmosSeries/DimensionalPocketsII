package com.tcn.dimensionalpocketsii.core.item;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum CoreItemTier implements Tier {
	
	DIMENSIONAL(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 4000, 12.0F, 6.0F, 25, () -> Ingredient.of(ModRegistrationManager.DIMENSIONAL_INGOT.get())),
	DIMENSIONAL_ENHANCED(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 6000, 16.0F, 8.0F, 35, () -> Ingredient.of(ModRegistrationManager.DIMENSIONAL_INGOT.get()));

    private final TagKey<Block> incorrectBlocksForDrops;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

	private CoreItemTier(TagKey<Block> incorrectBlockForDrops, int usesIn, float speedIn, float damageBonusIn, int enchantmentValueIn, Supplier<Ingredient> repairIngredientIn) {
        this.incorrectBlocksForDrops = incorrectBlockForDrops;
		this.uses = usesIn;
		this.speed = speedIn;
		this.damage = damageBonusIn;
		this.enchantmentValue = enchantmentValueIn;
        this.repairIngredient = Suppliers.memoize(repairIngredientIn::get);
	}

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }
    
	@Override
	public int getUses() {
		return this.uses;
	}
	
	@Override
	public float getSpeed() {
		return this.speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.damage;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}