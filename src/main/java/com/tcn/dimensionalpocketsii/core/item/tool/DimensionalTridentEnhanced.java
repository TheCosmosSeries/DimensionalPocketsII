package com.tcn.dimensionalpocketsii.core.item.tool;

import java.util.List;
import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyTridentItem;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEnhancedEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public class DimensionalTridentEnhanced extends CosmosEnergyTridentItem {

	public DimensionalTridentEnhanced(Item.Properties properties, CosmosEnergyItem.Properties energyProperties, int useDuration, int enchantmentValue, Supplier<BlockEntityWithoutLevelRenderer> bewlrSupplierIn) {
		super(properties, energyProperties, useDuration, enchantmentValue, bewlrSupplierIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.trident_enhanced"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			} 
			
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.item.info.tool_charge"));
			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.item.info.tool_usage_enhanced"));
			tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.item.info.tool_energy_enhanced"));
			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.item.info.tool_limitation"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		
		super.appendHoverText(stack, context, tooltip, flagIn);
	}

	@Override
	public void releaseUsing(ItemStack stackIn, Level levelIn, LivingEntity livingEntityIn, int timeLeft) {
		if (livingEntityIn instanceof Player playerEntity) {
			int i = this.getUseDuration(stackIn, livingEntityIn) - timeLeft;
			
			if (i >= 10) {
                float j = EnchantmentHelper.getTridentSpinAttackStrength(stackIn, playerEntity);
				if (!(j > 0.0F) || playerEntity.isInWaterOrRain()) {
					Holder<SoundEvent> holder = EnchantmentHelper.pickHighestLevel(stackIn, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
					if (!levelIn.isClientSide) {
						this.extractEnergy(stackIn, this.getMaxUse(stackIn), false);
						
						if (j == 0.0F) {
							DimensionalTridentEnhancedEntity tridententity = new DimensionalTridentEnhancedEntity(levelIn, playerEntity, stackIn);
							tridententity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, 2.5F + (float) j * 0.5F, 1.0F);
							
							if (playerEntity.getAbilities().instabuild) {
								tridententity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
							}

							levelIn.addFreshEntity(tridententity);
							levelIn.playSound(null, tridententity, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
							if (!playerEntity.getAbilities().instabuild) {
								playerEntity.getInventory().removeItem(stackIn);
							}
						}
					}

					playerEntity.awardStat(Stats.ITEM_USED.get(this));
					if (j > 0) {
						float f7 = playerEntity.getYRot();
						float f = playerEntity.getXRot();
						float f1 = -Mth.sin(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
						float f2 = -Mth.sin(f * ((float) Math.PI / 180F));
						float f3 = Mth.cos(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
						float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
						float f5 = 3.0F * ((1.0F + (float) j) / 4.0F);
						
						f1 = f1 * (f5 / f4);
						f2 = f2 * (f5 / f4);
						f3 = f3 * (f5 / f4);
						
						playerEntity.push((double) f1, (double) f2, (double) f3);
						playerEntity.startAutoSpinAttack(20, 8.0F, stackIn);
						if (playerEntity.onGround()) {
							float f6 = 1.1999999F;
							playerEntity.move(MoverType.SELF, new Vec3(0.0D, (double) 1.1999999F, 0.0D));
						}

                        levelIn.playSound(null, playerEntity, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
					}
				}
			}
		}
	}

}