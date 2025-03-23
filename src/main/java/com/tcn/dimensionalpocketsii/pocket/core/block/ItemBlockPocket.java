package com.tcn.dimensionalpocketsii.pocket.core.block;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ItemBlockPocket extends BlockItem {

	public ItemBlockPocket(Block block, Item.Properties prop) {
		super(block, prop);
	}
	
	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable Level worldIn, List<Component> toolTipIn, TooltipFlag flagIn) {
		super.appendHoverText(stackIn, worldIn, toolTipIn, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			toolTipIn.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.block.pocket"));
			
			if (ComponentHelper.displayShiftForDetail) {
				toolTipIn.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			toolTipIn.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.block.pocket_shift_one"));
			toolTipIn.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.info.block.pocket_shift_two"));
			toolTipIn.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.info.block.pocket_shift_three"));
			toolTipIn.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.info.block.pocket_shift_four"));
			toolTipIn.add(ComponentHelper.shiftForLessDetails());
		}
		
		if (stackIn.hasTag()) {
			if (stackIn.hasTag()) {
				CompoundTag tag = stackIn.getTag();
				if (tag.contains("nbt_data")) {
					CompoundTag nbt_data = tag.getCompound("nbt_data");
					
					if (nbt_data.contains("chunk_set")) {
						CompoundTag chunk_set = nbt_data.getCompound("chunk_set");
						
						int x = chunk_set.getInt("X");
						int z = chunk_set.getInt("Z");
						
						
						
						toolTipIn.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.pocket.pocket").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_BLUE + x + Value.LIGHT_GRAY + ", " + Value.BRIGHT_BLUE + z + Value.LIGHT_GRAY + " ]")));
					}
				}
			}
		}
	}

	@Override
	public boolean isDamageable(ItemStack stackIn) {
		return false;
	}
}