package com.tcn.dimensionalpocketsii.core.entity;

import javax.annotation.Nullable;

import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DimensionalTridentEnhancedEntity extends AbstractArrow {
	
	private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(DimensionalTridentEnhancedEntity.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(DimensionalTridentEnhancedEntity.class, EntityDataSerializers.BOOLEAN);
	
	private ItemStack tridentItem = new ItemStack(ModBusManager.DIMENSIONAL_TRIDENT_ENHANCED);
	private boolean dealtDamage;
	public int clientSideReturnTridentTickCount;

	public DimensionalTridentEnhancedEntity(EntityType<? extends DimensionalTridentEnhancedEntity> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}

	public DimensionalTridentEnhancedEntity(Level worldIn, LivingEntity livingEntityIn, ItemStack stackIn) {
		super(ModBusManager.ENTITY_TYPE_TRIDENT_ENHANCED, livingEntityIn, worldIn);
		this.tridentItem = stackIn.copy();
		this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getLoyalty(stackIn));
		this.entityData.set(ID_FOIL, stackIn.hasFoil());
	}

	@OnlyIn(Dist.CLIENT)
	public DimensionalTridentEnhancedEntity(Level worldIn, double x, double y, double z) {
		super(ModBusManager.ENTITY_TYPE_TRIDENT_ENHANCED, x, y, z, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ID_LOYALTY, (byte) 0);
		this.entityData.define(ID_FOIL, false);
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		if ((this.dealtDamage || this.isNoPhysics()) && entity != null) {
			int i = this.entityData.get(ID_LOYALTY);
			if (i > 0 && !this.isAcceptibleReturnOwner()) {
				if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
					this.spawnAtLocation(this.getPickupItem(), 0.1F);
				}

				this.discard();
			} else if (i > 0) {
				this.setNoPhysics(true);
				Vec3 vector3d = new Vec3(entity.getX() - this.getX(), entity.getEyeY() - this.getY(), entity.getZ() - this.getZ());
				this.setPosRaw(this.getX(), this.getY() + vector3d.y * 0.015D * (double) i, this.getZ());
				if (this.level.isClientSide) {
					this.yOld = this.getY();
				}

				double d0 = 0.05D * (double) i;
				this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vector3d.normalize().scale(d0)));
				if (this.clientSideReturnTridentTickCount == 0) {
					this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
				}

				++this.clientSideReturnTridentTickCount;
			}
		}

		super.tick();
	}

	private boolean isAcceptibleReturnOwner() {
		Entity entity = this.getOwner();
		if (entity != null && entity.isAlive()) {
			return !(entity instanceof ServerPlayer) || !entity.isSpectator();
		} else {
			return false;
		}
	}

	@Override
	protected ItemStack getPickupItem() {
		return this.tridentItem.copy();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isFoil() {
		return this.entityData.get(ID_FOIL);
	}

	@Override
	@Nullable
	protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
		return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
	}

	@Override
	protected void onHitEntity(EntityHitResult rayTraceResult) {
		Entity entity = rayTraceResult.getEntity();
		float f = 8.0F;
		if (entity instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) entity;
			f += EnchantmentHelper.getDamageBonus(this.tridentItem, livingentity.getMobType());
		}

		Entity entity1 = this.getOwner();
		DamageSource damagesource = DamageSource.trident(this, (Entity) (entity1 == null ? this : entity1));
		
		this.dealtDamage = true;
		SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
		
		if (entity.hurt(damagesource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (entity instanceof LivingEntity) {
				LivingEntity livingentity1 = (LivingEntity) entity;
				if (entity1 instanceof LivingEntity) {
					EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
					EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity1);
				}

				this.doPostHurtEffects(livingentity1);
			}
		}

		this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
		float f1 = 1.0F;
		if (this.level instanceof ServerLevel && this.level.isThundering()
				&& EnchantmentHelper.hasChanneling(this.tridentItem)) {
			BlockPos blockpos = entity.blockPosition();
			if (this.level.canSeeSky(blockpos)) {
				LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level);
				lightningboltentity.moveTo(Vec3.atBottomCenterOf(blockpos));
				lightningboltentity.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer) entity1 : null);
				this.level.addFreshEntity(lightningboltentity);
				soundevent = SoundEvents.TRIDENT_THUNDER;
				f1 = 5.0F;
			}
		}

		this.playSound(soundevent, f1, 1.0F);
	}

	public boolean isChanneling() {
		return EnchantmentHelper.hasChanneling(this.tridentItem);
	}
	
	@Override
	protected boolean tryPickup(Player playerIn) {
		return super.tryPickup(playerIn) || this.isNoPhysics() && this.ownedBy(playerIn) && playerIn.getInventory().add(this.getPickupItem());
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.TRIDENT_HIT_GROUND;
	}

	@Override
	public void playerTouch(Player playerIn) {
		 if (this.ownedBy(playerIn) || this.getOwner() == null) {
	         super.playerTouch(playerIn);
	      }
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Trident", 10)) {
			this.tridentItem = ItemStack.of(compound.getCompound("Trident"));
		}

		this.dealtDamage = compound.getBoolean("DealtDamage");
		this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getLoyalty(this.tridentItem));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.put("Trident", this.tridentItem.save(new CompoundTag()));
		compound.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	public void tickDespawn() {
		int i = this.entityData.get(ID_LOYALTY);
		if (this.pickup != AbstractArrow.Pickup.ALLOWED || i <= 0) {
			super.tickDespawn();
		}

	}

	@Override
	protected float getWaterInertia() {
		return 0.99F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}
	
}