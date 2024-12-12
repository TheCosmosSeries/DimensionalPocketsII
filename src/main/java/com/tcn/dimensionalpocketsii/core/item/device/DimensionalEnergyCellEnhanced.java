package com.tcn.dimensionalpocketsii.core.item.device;

import java.util.List;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class DimensionalEnergyCellEnhanced extends CosmosEnergyItem {

	public DimensionalEnergyCellEnhanced(Item.Properties properties, Properties energyProperties) {
		super(properties, energyProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.energy_cell_enhanced_info"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			} 
			
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.energy_cell_enhanced_shift_one"));
			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.info.energy_cell_enhanced_shift_two"));
			tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.info.energy_cell_enhanced_shift_three"));
			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.info.energy_cell_enhanced_limitation"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stackIn = playerIn.getItemInHand(handIn);

		if (playerIn.isShiftKeyDown()) {
			this.setActive(!this.isActive(stackIn), stackIn);
			
			playerIn.swing(handIn);
			return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
		}
		
		return InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
	}
	
	@Override
	public boolean isFoil(ItemStack stackIn) {
		return this.isActive(stackIn);
	}

	@Override
	public void inventoryTick(ItemStack stackIn, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isClientSide) {
			if (this.hasEnergy(stackIn)) {
				if (this.isActive(stackIn)) {
					if (itemSlot >= 0 && itemSlot <= 8) {
						if (entityIn instanceof ServerPlayer) {
							ServerPlayer player = (ServerPlayer) entityIn;
							
							Inventory inv = player.getInventory();
							
							for (int i = 0; i < inv.getContainerSize(); i++) {
								ItemStack testStack = inv.getItem(i);
								Item testItem = testStack.getItem();
								
								if (!(testItem instanceof DimensionalEnergyCell) && !(testItem instanceof DimensionalEnergyCellEnhanced)) {
									if (testItem instanceof ICosmosEnergyItem) {
										ICosmosEnergyItem energyItem = (ICosmosEnergyItem) testItem;
										
										if (energyItem.canReceiveEnergy(testStack)) {
											int lowest = Math.min(energyItem.getMaxReceive(testStack), this.getMaxExtract(stackIn));
											
											this.extractEnergy(stackIn, energyItem.receiveEnergy(testStack, lowest, false), false);
										}
									}
								}
							}
						}
					} else {
						this.setActive(false, stackIn);
					}
				}
			}
		}
	}

	public boolean isActive(ItemStack stackIn) {
		if (stackIn.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag stack_tag = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
			
			if (stack_tag.contains("nbt_data")) {
				CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
				
				if (nbt_data.contains("active")) {
					return nbt_data.getBoolean("active");
				}
			}
		}
		
		return false;
	}
	
	public void setActive(boolean active, ItemStack stackIn) {
		if (this.hasEnergy(stackIn)) {
			if (stackIn.has(DataComponents.CUSTOM_DATA)) {
				CompoundTag stack_tag = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
					nbt_data.putBoolean("active", active);
					stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
				} else {
					CompoundTag nbt_data = new CompoundTag();
					
					nbt_data.putBoolean("active", active);
					stack_tag.put("nbt_data", nbt_data);
					stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
	
				}
			} else {
				CompoundTag stack_tag = new CompoundTag();
				CompoundTag nbt_data = new CompoundTag();
				
				nbt_data.putBoolean("active", active);
				
				stack_tag.put("nbt_data", nbt_data);
				stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
			}
		}
	}
}