package com.tcn.dimensionalpocketsii.core.item.tool;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMultimap;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.interfaces.ICosmosEnergyItemBEWLR;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.dimensionalpocketsii.client.renderer.DimensionalTridentBEWLR;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

@SuppressWarnings("unused")
public class DimensionalTridentEnhanced extends TridentItem implements ICosmosEnergyItem, ICosmosEnergyItemBEWLR {
	private ImmutableMultimap<Attribute, AttributeModifier> defaultModifiers;

	private int maxEnergyStored;
	private int maxExtract;
	private int maxReceive;
	private int maxUse;
	private boolean doesExtract;
	private boolean doesCharge;
	private boolean doesDisplayEnergyInTooltip;
	private ComponentColour barColour;

	public DimensionalTridentEnhanced(Item.Properties properties, CosmosEnergyItem.Properties energyProperties) {
		super(properties);

		this.maxEnergyStored = energyProperties.maxEnergyStored;
		this.maxExtract = energyProperties.maxExtract;
		this.maxReceive = energyProperties.maxReceive;
		this.maxUse = energyProperties.maxUse;
		this.doesExtract = energyProperties.doesExtract;
		this.doesCharge = energyProperties.doesCharge;
		this.doesDisplayEnergyInTooltip = energyProperties.doesDisplayEnergyInTooltip;
		this.barColour = energyProperties.barColour;
	}

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 16, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, 0.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            )
            .build();
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

		if (stack.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag stackTag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.energy_item.stored").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.RED + CosmosUtil.formatIntegerMillion(stackTag.getInt("energy")) + Value.LIGHT_GRAY + " / " + Value.RED + CosmosUtil.formatIntegerMillion(this.getMaxEnergyStored(stack)) + Value.LIGHT_GRAY + " ]")));
		}
		
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return new DimensionalTridentBEWLR();
			}
		});
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entityIn) {
		return 40000;
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
							DimensionalTridentEntity tridententity = new DimensionalTridentEntity(levelIn, playerEntity, stackIn);
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

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		
		if (!this.hasEnergy(stack)) {
			return InteractionResultHolder.fail(stack);
		} else if (EnchantmentHelper.getTridentSpinAttackStrength(stack, playerIn) > 0.0F && !playerIn.isInWaterOrRain()) {
			return InteractionResultHolder.fail(stack);
		} else {
			playerIn.startUsingItem(handIn);
			return InteractionResultHolder.consume(stack);
		}
	}

    @Override
    public int getEnchantmentValue() {
        return 3;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownTrident throwntrident = new ThrownTrident(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
        throwntrident.pickup = AbstractArrow.Pickup.ALLOWED;
        return throwntrident;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_TRIDENT_ACTIONS.contains(itemAbility);
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
	public int getMaxEnergyStored(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTridentEnhanced) ? 0 : ((DimensionalTridentEnhanced)item).maxEnergyStored;
	}

	@Override
	public int getMaxExtract(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTridentEnhanced) ? 0 : ((DimensionalTridentEnhanced)item).maxExtract;
	}

	@Override
	public int getMaxReceive(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTridentEnhanced) ? 0 : ((DimensionalTridentEnhanced)item).maxReceive;
	}

	@Override
	public int getMaxUse(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTridentEnhanced) ? 0 : ((DimensionalTridentEnhanced)item).maxUse;
	}

	@Override
	public boolean doesExtract(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTridentEnhanced) ? false : ((DimensionalTridentEnhanced)item).doesExtract;
	}

	@Override
	public boolean doesCharge(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTridentEnhanced) ? false : ((DimensionalTridentEnhanced)item).doesCharge;
	}

	@Override
	public boolean doesDisplayEnergyInTooltip(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalTridentEnhanced) ? false : ((DimensionalTridentEnhanced)item).doesDisplayEnergyInTooltip;
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