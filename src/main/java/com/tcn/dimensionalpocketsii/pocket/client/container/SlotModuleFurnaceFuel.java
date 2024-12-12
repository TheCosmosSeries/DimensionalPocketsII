package com.tcn.dimensionalpocketsii.pocket.client.container;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SlotModuleFurnaceFuel extends Slot {
   private final ContainerModuleFurnace menu;

   public SlotModuleFurnaceFuel(ContainerModuleFurnace menuIn, Container inventoryIn, int p_i50084_3_, int p_i50084_4_, int p_i50084_5_) {
      super(inventoryIn, p_i50084_3_, p_i50084_4_, p_i50084_5_);
      this.menu = menuIn;
   }

   public boolean mayPlace(ItemStack stackIn) {
      return this.menu.isFuel(stackIn) || isBucket(stackIn);
   }

   public int getMaxStackSize(ItemStack stackIn) {
      return isBucket(stackIn) ? 1 : super.getMaxStackSize(stackIn);
   }

   public static boolean isBucket(ItemStack stackIn) {
      return stackIn.getItem() == Items.BUCKET;
   }
}
