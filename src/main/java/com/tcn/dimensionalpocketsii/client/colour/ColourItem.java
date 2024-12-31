package com.tcn.dimensionalpocketsii.client.colour;

import com.tcn.cosmoslibrary.common.item.CosmosArmourItemColourable;
import com.tcn.cosmoslibrary.common.item.CosmosArmourItemElytra;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColourItem implements ItemColor {

	@Override
	public int getColor(ItemStack stack, int itemLayerIn) {
		Item item = stack.getItem();
		
		if (item.equals(PocketsRegistrationManager.BLOCK_POCKET.get().asItem()) || item.equals(PocketsRegistrationManager.BLOCK_POCKET_ENHANCED.get().asItem())) {
			if (stack.has(DataComponents.CUSTOM_DATA)) {
				CompoundTag stack_tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
					
					if (nbt_data.contains("colour")) {
						int colour = nbt_data.getInt("colour");
						
						if (colour == ComponentColour.POCKET_PURPLE.decOpaque()) {
							return ComponentColour.POCKET_PURPLE_LIGHT.decOpaque();
						}
						
						return colour;
					}
				}
			}
		} else if (item.equals(PocketsRegistrationManager.DIMENSIONAL_SHIFTER.get()) || item.equals(PocketsRegistrationManager.DIMENSIONAL_SHIFTER_ENHANCED.get())) {
			if (stack.has(DataComponents.CUSTOM_DATA)) {
				CompoundTag stack_tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag compound_tag = stack_tag.getCompound("nbt_data");
					
					if (compound_tag.contains("colour")) {
						int decimal = compound_tag.getInt("colour");
						
						if (itemLayerIn == 0) {
							if (decimal == ComponentColour.POCKET_PURPLE.decOpaque()) {
								return ComponentColour.POCKET_PURPLE_LIGHT.decOpaque();
							} else {
								return FastColor.ARGB32.opaque(decimal);
							}
						} else {
							return ComponentColour.WHITE.decOpaque();
						}
					}
				}
			}
		} else if (item instanceof CosmosArmourItemElytra itemElytra) {
			if (stack.has(DataComponents.CUSTOM_DATA)) {
				CompoundTag stack_tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag compound_tag = stack_tag.getCompound("nbt_data");
					
					int colour = ComponentColour.POCKET_PURPLE_LIGHT.decOpaque();
					int wing_colour = ComponentColour.LIGHT_GRAY.decOpaque();
					
					if (compound_tag.contains("colour")) {
						colour = FastColor.ARGB32.opaque(compound_tag.getInt("colour"));
					}
					
					if (compound_tag.contains("wing_colour")) {
						wing_colour = FastColor.ARGB32.opaque(compound_tag.getInt("wing_colour"));
					}
					
					if (itemLayerIn == 0) {
						return colour;
					} else if (itemLayerIn == 1) {
						return wing_colour;
					} else {
						return ComponentColour.WHITE.decOpaque();
					}
				}
			} 

			if (itemLayerIn == 1) {
				return ComponentColour.LIGHT_GRAY.decOpaque();
			}
		}
		
		else if (item instanceof CosmosArmourItemColourable itemArmourColourable) {
			if (stack.has(DataComponents.CUSTOM_DATA)) {
				CompoundTag stack_tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag compound_tag = stack_tag.getCompound("nbt_data");
					
					int colour = ComponentColour.POCKET_PURPLE_LIGHT.decOpaque();
					
					if (compound_tag.contains("colour")) {
						colour = compound_tag.getInt("colour");
					}
					
					if (itemLayerIn == 0) {
						return FastColor.ARGB32.opaque(colour);
					}else {
						return ComponentColour.WHITE.decOpaque();
					}
				}
			} 
		}
		
		if (itemLayerIn == 0) {
			return ComponentColour.POCKET_PURPLE_LIGHT.decOpaque();
		} else {
			return ComponentColour.WHITE.decOpaque();
		}
	}
}