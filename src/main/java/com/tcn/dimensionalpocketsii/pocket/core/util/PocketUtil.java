package com.tcn.dimensionalpocketsii.pocket.core.util;

import com.tcn.dimensionalpocketsii.DimensionalPockets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PocketUtil {
	
	private static final String NBT_DIMENSIONAL_POCKET_KEY = "pocket_data";
	
	public static boolean isDimensionEqual(Level worldIn, ResourceKey<Level> dimensionIn) {
		return worldIn.dimension().equals(dimensionIn);
	}
	
	public static boolean hasPocketKey(CompoundTag compoundIn) {
		return compoundIn.contains(NBT_DIMENSIONAL_POCKET_KEY);
	}
	
	public static CompoundTag getPlayerPersistTag(Player playerIn) {
		CompoundTag tag = playerIn.getPersistentData();
		
		CompoundTag pocketTag;
		if (tag.contains(DimensionalPockets.MOD_ID)) {
			pocketTag = tag.getCompound(DimensionalPockets.MOD_ID);
		} else {
			pocketTag = new CompoundTag();
			pocketTag.putBoolean("givenInfoBook", false);
			tag.put(DimensionalPockets.MOD_ID, pocketTag);
		}
		
		return pocketTag;
	}
}
