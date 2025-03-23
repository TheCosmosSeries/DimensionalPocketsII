package com.tcn.dimensionalpocketsii.core.item.tool;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItemBEWLR;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.dimensionalpocketsii.DimReference;
import com.tcn.dimensionalpocketsii.client.renderer.DimensionalTridentBEWLR;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;


@SuppressWarnings("unused")
public class DimensionalTrident extends TridentItem implements Vanishable, ICosmosEnergyItem, ICosmosEnergyItemBEWLR {
	private ImmutableMultimap<Attribute, AttributeModifier> defaultModifiers;

	private int maxEnergyStored;
	private int maxExtract;
	private int maxReceive;
	private int maxUse;
	private boolean doesExtract;
	private boolean doesCharge;
	private boolean doesDisplayEnergyInTooltip;
	private ComponentColour barColour;

	public DimensionalTrident(Item.Properties properties, CosmosEnergyItem.Properties energyProperties) {
		super(properties);
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 12.0D, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double) 0.8F, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();

		this.maxEnergyStored = energyProperties.maxEnergyStored;
		this.maxExtract = energyProperties.maxExtract;
		this.maxReceive = energyProperties.maxReceive;
		this.maxUse = energyProperties.maxUse;
		this.doesExtract = energyProperties.doesExtract;
		this.doesCharge = energyProperties.doesCharge;
		this.doesDisplayEnergyInTooltip = energyProperties.doesDisplayEnergyInTooltip;
		this.barColour = energyProperties.barColour;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.item.info.trident"));
			
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

		if (stack.hasTag()) {
			CompoundTag stackTag = stack.getTag();
			tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.energy_item.stored").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.RED + CosmosUtil.formatIntegerMillion(stackTag.getInt("energy")) + Value.LIGHT_GRAY + " / " + Value.RED + CosmosUtil.formatIntegerMillion(this.getMaxEnergyStored(stack)) + Value.LIGHT_GRAY + " ]")));
		}
		
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slotIn, ItemStack stackIn) {
		if (!this.hasEnergy(stackIn)) {
			return ImmutableMultimap.of();
		} else {
			return this.getDefaultAttributeModifiers(slotIn);
		}
	}
	
	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return new DimensionalTridentBEWLR();
			}
		});
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 60000;
	}

	@Override
	public boolean canAttackBlock(BlockState stateIn, Level worldIn, BlockPos posIn, Player playerIn) {
		return !playerIn.isCreative();
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stackIn) {
		return UseAnim.SPEAR;
	}

	@Override
	public void releaseUsing(ItemStack stackIn, Level worldIn, LivingEntity livingEntityIn, int p_77615_4_) {
		if (livingEntityIn instanceof Player) {
			Player playerentity = (Player) livingEntityIn;
			int i = this.getUseDuration(stackIn) - p_77615_4_;
			if (i >= 10) {
				int j = EnchantmentHelper.getRiptide(stackIn);
				if (j <= 0 || playerentity.isInWaterOrRain()) {
					if (!worldIn.isClientSide) {
						this.extractEnergy(stackIn, this.getMaxUse(stackIn), false);
						
						if (j == 0) {
							DimensionalTridentEntity tridententity = new DimensionalTridentEntity(worldIn, playerentity, stackIn);
							tridententity.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F, 2.5F + (float) j * 0.5F, 1.0F);
							
							if (playerentity.getAbilities().instabuild) {
								tridententity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
							}

							worldIn.addFreshEntity(tridententity);
							worldIn.playSound((Player) null, tridententity, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
							if (!playerentity.getAbilities().instabuild) {
								playerentity.getInventory().removeItem(stackIn);
							}
						}
					}

					playerentity.awardStat(Stats.ITEM_USED.get(this));
					if (j > 0) {
						float f7 = playerentity.getYRot();
						float f = playerentity.getXRot();
						float f1 = -Mth.sin(f7 * ((float) Math.PI / 180F))
								* Mth.cos(f * ((float) Math.PI / 180F));
						float f2 = -Mth.sin(f * ((float) Math.PI / 180F));
						float f3 = Mth.cos(f7 * ((float) Math.PI / 180F))
								* Mth.cos(f * ((float) Math.PI / 180F));
						float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
						float f5 = 3.0F * ((1.0F + (float) j) / 4.0F);
						f1 = f1 * (f5 / f4);
						f2 = f2 * (f5 / f4);
						f3 = f3 * (f5 / f4);
						playerentity.push((double) f1, (double) f2, (double) f3);
						playerentity.startAutoSpinAttack(20);
						if (playerentity.isOnGround()) {
							float f6 = 1.1999999F;
							playerentity.move(MoverType.SELF, new Vec3(0.0D, (double) 1.1999999F, 0.0D));
						}

						SoundEvent soundevent;
						if (j >= 3) {
							soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
						} else if (j == 2) {
							soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
						} else {
							soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
						}

						worldIn.playSound((Player) null, playerentity, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
					}

				}
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stackIn = playerIn.getItemInHand(handIn);
		
		if (!this.hasEnergy(stackIn)) {
			return InteractionResultHolder.fail(stackIn);
		} else if (EnchantmentHelper.getRiptide(stackIn) > 0 && !playerIn.isInWaterOrRain()) {
			return InteractionResultHolder.fail(stackIn);
		} else {
			playerIn.startUsingItem(handIn);
			return InteractionResultHolder.success(stackIn);
		}
	}

	@Override
	public boolean mineBlock(ItemStack stackIn, Level worldIn, BlockState stateIn, BlockPos posIn, LivingEntity livingEntityIn) {
		if ((double) stateIn.getDestroySpeed(worldIn, posIn) != 0.0D) {
			this.extractEnergy(stackIn, this.getMaxUse(stackIn) / 4, false);
		}

		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack stackIn, LivingEntity target, LivingEntity attacker) {
		if (this.hasEnergy(stackIn)) {
			this.extractEnergy(stackIn, this.getMaxUse(stackIn), false);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stackIn, Player player, Entity entity) {
		if (this.hasEnergy(stackIn)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean onEntitySwing(ItemStack stackIn, LivingEntity entity) {
		if (this.hasEnergy(stackIn)) {
			return false;
		}
		
		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slotTypeIn) {
		return slotTypeIn == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slotTypeIn);
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@Override
	public int getMaxEnergyStored(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTrident) ? 0 : ((DimensionalTrident)item).maxEnergyStored;
	}

	@Override
	public int getMaxExtract(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTrident) ? 0 : ((DimensionalTrident)item).maxExtract;
	}

	@Override
	public int getMaxReceive(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTrident) ? 0 : ((DimensionalTrident)item).maxReceive;
	}

	@Override
	public int getMaxUse(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTrident) ? 0 : ((DimensionalTrident)item).maxUse;
	}

	@Override
	public boolean doesExtract(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTrident) ? false : ((DimensionalTrident)item).doesExtract;
	}

	@Override
	public boolean doesCharge(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTrident) ? false : ((DimensionalTrident)item).doesCharge;
	}

	@Override
	public boolean doesDisplayEnergyInTooltip(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTrident) ? false : ((DimensionalTrident)item).doesDisplayEnergyInTooltip;
	}
	
	@Override
	public boolean canReceiveEnergy(ItemStack stackIn) {
		return this.getEnergy(stackIn) < this.getMaxEnergyStored(stackIn);
	}

	@Override
	public double getScaledEnergy(ItemStack stackIn, int scaleIn) {
		Item item = stackIn.getItem();
		
		if (item instanceof ICosmosEnergyItem) {
			return (double) this.getEnergy(stackIn) * scaleIn / (double) this.getMaxEnergyStored(stackIn);
		}
		
		return 0;
	}

	@Override
	public double getScaledEnergy(ItemStack stackIn, float scaleIn) {
		Item item = stackIn.getItem();
		
		if (item instanceof ICosmosEnergyItem) {
			return (double) this.getEnergy(stackIn) * scaleIn / (double) this.getMaxEnergyStored(stackIn);
		}
		
		return 0;
	}

	@Override
	public int receiveEnergy(ItemStack stackIn, int energy, boolean simulate) {
		if (this.canReceiveEnergy(stackIn)) {
			if(this.doesCharge(stackIn)) {
				int storedReceived = Math.min(this.getMaxEnergyStored(stackIn) - this.getEnergy(stackIn), Math.min(this.getMaxReceive(stackIn), energy));
				
				if (!simulate) {
					this.setEnergy(stackIn, this.getEnergy(stackIn) + storedReceived);
				}
				
				return storedReceived;
			} 
		}
		
		return 0;
	}

	@Override
	public int extractEnergy(ItemStack stackIn, int energy, boolean simulate) {
		if (this.canExtractEnergy(stackIn)) {
			if (this.doesExtract(stackIn)) {
				int storedExtracted = Math.min(this.getEnergy(stackIn), Math.min(this.getMaxExtract(stackIn), energy));
				
				if (!simulate) {
					this.setEnergy(stackIn, this.getEnergy(stackIn) - storedExtracted);
				}
				
				return storedExtracted;
			}
		}
		
		return 0;
	}

	@Override
	public boolean isBarVisible(ItemStack stackIn) {
		return true;
	}
	
	@Override
	public int getBarColor(ItemStack stackIn) {
		return this.barColour.dec();
	}
	
	@Override
	public int getBarWidth(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof ICosmosEnergyItem) ? 0 : Mth.clamp(Math.round((float) ((ICosmosEnergyItem) item).getScaledEnergy(stackIn, 13)), 0, 13);
	}
}