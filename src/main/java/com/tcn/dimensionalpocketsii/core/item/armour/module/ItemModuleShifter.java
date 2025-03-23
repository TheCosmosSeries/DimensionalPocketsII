package com.tcn.dimensionalpocketsii.core.item.armour.module;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemModuleShifter extends CosmosItem implements IModuleItem {

	public ItemModuleShifter(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.armour_module.shifter"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.item.info.armour_module.shifter_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.item.info.armour_module.shifter_two_pre")
				.append(ComponentHelper.style(ComponentColour.YELLOW, ModBusManager.SUIT_SHIFT.getKey().getName()))
				.append(ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.item.info.elytraplate_key"))
			);
			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.item.info.armour_module.shifter_three"));
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public BaseElytraModule getModule() {
		return BaseElytraModule.SHIFTER;
	}
	
	@Override
	public boolean doesInformationCarry() {
		return false;
	}
	
	@Override
	public boolean transferInformation(ItemStack fromStack, ItemStack toStack, boolean simulate) {
		if (fromStack.getItem() instanceof ItemModuleShifter) {
			if (fromStack.hasTag()) {
				CompoundTag stack_tag = fromStack.getTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag nbtData = stack_tag.getCompound("nbt_data");
					
					CompoundTag copyTag = nbtData.copy();
					
					if (copyTag.contains("colour")) {
						copyTag.remove("colour");
					}
					
					if (!simulate) {
						toStack.getOrCreateTag().put("nbt_data", copyTag);
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
}