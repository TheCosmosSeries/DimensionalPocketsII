package com.tcn.dimensionalpocketsii.core.item.device;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.pocket.core.shift.ShifterCore;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionalEjector extends Item {

	public DimensionalEjector(Item.Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
		return false;
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return true;
	}
	
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		if (playerIn.isShiftKeyDown()) {
			if (!PocketUtil.isDimensionEqual(worldIn, Level.OVERWORLD)) {
				ItemStack itemStack = CosmosUtil.getStack(playerIn, handIn);		
				itemStack.shrink(1);
				
				ResourceKey<Level> dimension = worldIn.dimension();
				
				ShifterCore.sendPlayerToDimensionSpawn(playerIn, Level.OVERWORLD, ComponentHelper.style2(ComponentColour.GREEN, "dimensionalpocketsii.item.message.ejector.success", Value.ORANGE + dimension.location().getNamespace() + Value.LIGHT_GRAY + ": " + Value.ORANGE + dimension.location().getPath()));
			} else {
				CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.shifter_core.same_dimension"));
			}
		}
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.ejector_info"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			} 
			
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.ejector_shift_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.info.ejector_shift_two"));
			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.info.ejector_shift_limitation"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}
}