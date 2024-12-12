package com.tcn.dimensionalpocketsii.pocket.core.shift;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class Shifter {

	private ResourceKey<Level> dimension_key;
	private EnumShiftDirection direction;
	private BlockPos target_pos;
	private float target_yaw;
	private float target_pitch;
	private boolean playVanillaSound;
	private boolean sendMessage;
	private boolean safeSpawn;

	public Shifter(ResourceKey<Level> dimensionKeyIn, EnumShiftDirection directionIn, BlockPos targetPosIn, float targetYawIn, float targetPitchIn, boolean playVanillaSoundIn, boolean sendMessageIn, boolean safeSpawnIn) {
		this.dimension_key = dimensionKeyIn;
		this.direction = directionIn;
		this.target_pos = targetPosIn;
		this.target_yaw = targetYawIn;
		this.target_pitch = targetPitchIn;
		this.playVanillaSound = playVanillaSoundIn;
		this.sendMessage = sendMessageIn;
		this.safeSpawn = safeSpawnIn;
	}

	public BlockPos getTargetPos() {
		return this.target_pos;
	}
	
	public double[] getTargetPosA () {
		return new double[] { this.target_pos.getX(), this.target_pos.getY(), this.target_pos.getZ() };
	}

	public float getTargetYaw() {
		return this.target_yaw;
	}

	public float getTargetPitch() {
		return this.target_pitch;
	}
	
	public float[] getTargetRotation() {
		return new float[] { this.target_yaw, this.target_pitch };
	}

	public ResourceKey<Level> getDimensionKey() {
		return this.dimension_key;
	}

	public EnumShiftDirection getDirection() {
		return this.direction;
	}
	
	public boolean getSafeSpawn() {
		return this.safeSpawn;
	}
	
	public static Shifter createTeleporter(ResourceKey<Level> dimensionKeyIn, EnumShiftDirection directionIn, BlockPos targetPosIn, float targetYawIn, float targetPitchIn, boolean playVanillaSoundIn, boolean sendMessageIn, boolean safeSpawnIn) {
		return new Shifter(dimensionKeyIn, directionIn, targetPosIn, targetYawIn, targetPitchIn, playVanillaSoundIn, sendMessageIn, safeSpawnIn);
	}
	
	public boolean playVanillaSound() {
		return this.playVanillaSound;
	}

	public boolean getSendMessage() {
		return this.sendMessage;
	}
}