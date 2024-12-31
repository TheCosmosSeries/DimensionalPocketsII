package com.tcn.dimensionalpocketsii.core.advancement;

import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CoreTriggers {
	
	public static void triggerUseShifter(ServerPlayer playerIn, ItemStack stack) {
		PocketsRegistrationManager.SHIFTER_TRIGGER.get().trigger(playerIn, stack);
	}
}
