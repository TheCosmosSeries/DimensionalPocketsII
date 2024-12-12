package com.tcn.dimensionalpocketsii.pocket.core.event;

import com.tcn.dimensionalpocketsii.pocket.core.block.entity.AbstractBlockEntityPocket;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class PocketBlockEvent extends Event implements ICancellableEvent {
	
	private final Level level;
	private final LivingEntity placer;
	private final AbstractBlockEntityPocket blockEntity;
	private final BlockPos blockPos;
	
	public PocketBlockEvent(Level levelIn, LivingEntity placerIn, AbstractBlockEntityPocket entityIn, BlockPos posIn) {
		this.level = levelIn;
		this.placer = placerIn;
		
		this.blockEntity = entityIn;
		this.blockPos = posIn;
	}

	public Level getLevel() {
		return this.level;
	}

	public LivingEntity getPlacer() {
		return this.placer;
	}
	
	public AbstractBlockEntityPocket getBlockEntity() {
		return this.blockEntity;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public static class PlacePocketBlock extends PocketBlockEvent {

		public PlacePocketBlock(Level levelIn, LivingEntity placerIn, AbstractBlockEntityPocket entityIn, BlockPos posIn) {
			super(levelIn, placerIn, entityIn, posIn);
		}
	}

	public static class PickupPocketBlock extends PocketBlockEvent {

		public PickupPocketBlock(Level levelIn, LivingEntity placerIn, AbstractBlockEntityPocket entityIn, BlockPos posIn) {
			super(levelIn, placerIn, entityIn, posIn);
		}
	}
}