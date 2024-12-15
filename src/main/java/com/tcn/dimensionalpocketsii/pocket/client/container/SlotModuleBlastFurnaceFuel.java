package com.tcn.dimensionalpocketsii.pocket.client.container;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SlotModuleBlastFurnaceFuel extends Slot {
   private final ContainerModuleBlastFurnace menu;

   public SlotModuleBlastFurnaceFuel(ContainerModuleBlastFurnace menuIn, Container inventoryIn, int indexIn, int xIn, int yIn) {
      super(inventoryIn, indexIn, xIn, yIn);
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
