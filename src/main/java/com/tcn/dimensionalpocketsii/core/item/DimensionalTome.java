package com.tcn.dimensionalpocketsii.core.item;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.client.screen.ScreenItemTome;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DimensionalTome extends Item {

	public DimensionalTome(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		if (worldIn.isClientSide) {
			this.openScreen(playerIn);
		}
		
		playerIn.swing(handIn);
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void openScreen(Player playerIn) {
		Minecraft.getInstance().setScreen(new ScreenItemTome(true, playerIn.getUUID(), CosmosUtil.getStack(playerIn)));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.tome_info"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.tome_shift_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.info.tome_shift_two"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
	}
	
	public static void setPage(ItemStack stackIn, int page) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			compound.putInt("page", page);
		} else {
			CompoundTag compound = new CompoundTag();
			
			compound.putInt("page", page);
			
			stackIn.setTag(compound);
		}
	}
	
	public static int getPage(ItemStack stackIn) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			return compound.getInt("page");
		}
		
		return 0;
	}
	
	public static void setUIMode(ItemStack stackIn, EnumUIMode mode) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			compound.putInt("mode", mode.getIndex());
		} else {
			CompoundTag compound = new CompoundTag();
			
			compound.putInt("mode", mode.getIndex());
			
			stackIn.setTag(compound);
		}
	}
	
	public static EnumUIMode getUIMode(ItemStack stackIn) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			return EnumUIMode.getStateFromIndex(compound.getInt("mode"));
		}
		
		return EnumUIMode.DARK;
	}
}
