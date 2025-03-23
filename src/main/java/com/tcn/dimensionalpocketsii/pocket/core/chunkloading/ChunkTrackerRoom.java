package com.tcn.dimensionalpocketsii.pocket.core.chunkloading;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.tcn.cosmoslibrary.common.nbt.CosmosNBTHelper.Const;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ChunkTrackerRoom {
	private static final List<ChunkPos> ROOMS = new ArrayList<>();
	private final ServerLevel level;
	
	public ChunkTrackerRoom() { 
		this.level = null;
	}
	
	public ChunkTrackerRoom(ServerLevel levelIn) {
		this.level = levelIn;
	}
	
	public void addRoom(ChunkPos chunk) {
		if (ROOMS.contains(chunk)) {
			DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <add> The following Pocket Room was already marked as loaded: { " + chunk + ", " + level.dimension().location().getNamespace() + ":" + level.dimension().location().getPath() + " }");
			return;
		}
		
		if (!ROOMS.contains(chunk)) {
			if (PocketUtil.isDimensionEqual(level, DimensionManager.POCKET_WORLD)) {
				level.setChunkForced(chunk.x, chunk.z, true);
				ROOMS.add(chunk);
				DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <add> Marked the following Pocket Room to the be loaded: { " + chunk + ", " + level.dimension().location().getNamespace() + ":" + level.dimension().location().getPath() + " }");
			}
		}
	}

	public void removeRoom(ChunkPos chunk) {
		if (!ROOMS.contains(chunk)) {
			DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <remove> Something tried to remove a loaded Pocket Room that was never loaded before { " + chunk + ", " + level.dimension().location().getNamespace() + ":" + level.dimension().location().getPath() + " }");
			return;
		}
		
		if (ROOMS.contains(chunk)) {
			if (PocketUtil.isDimensionEqual(level, DimensionManager.POCKET_WORLD)) {
				level.setChunkForced(chunk.x, chunk.z, false);
				ROOMS.remove(chunk);
				DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <remove> Removed the following pocket room from the list of loaded rooms: { " + chunk + ", " + level.dimension().location().getNamespace() + ":" + level.dimension().location().getPath() + " }");
			}
		}
	}
	
	public static List<ChunkPos> getRooms() {
		return ROOMS;
	}
	
	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		CompoundTag roomTag = new CompoundTag();
		
		for (int i = 0; i < ROOMS.size(); i++) {
			ChunkPos pos = ROOMS.get(i);
			CompoundTag chunk = new CompoundTag();
			
			chunk.putInt(Const.NBT_POS_X_KEY, pos.x);
			chunk.putInt(Const.NBT_POS_Z_KEY, pos.z);
			
			roomTag.put(Integer.toString(i), chunk);
		}
		roomTag.putInt("size", ROOMS.size());
		compound.put("rooms", roomTag);
		
		return compound;
	}

	public void load(Tag tag) {
		if (tag instanceof CompoundTag compound) {
			CompoundTag roomTag = compound.getCompound("rooms");
			
			for (int i = 0; i < roomTag.getInt("size"); i++) {
				CompoundTag chunkTag = roomTag.getCompound(Integer.toString(i));
				
				this.addRoom(new ChunkPos(chunkTag.getInt(Const.NBT_POS_X_KEY), chunkTag.getInt(Const.NBT_POS_Z_KEY)));
			}
		}
	}

	public static ICapabilitySerializable<?> getCapability(Capability<ChunkTrackerRoom> capIn, LazyOptional<ChunkTrackerRoom> opt) {
		return new ICapabilitySerializable<>() {
			
			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				return cap == capIn ? opt.cast() : LazyOptional.empty();
			}
			
			@Override
			public Tag serializeNBT() {
				return opt.map(ChunkTrackerRoom::save).orElse(null);
			}
			
			@Override
			public void deserializeNBT(Tag nbt) {
				opt.ifPresent(chunkTracker -> chunkTracker.load(nbt));
			}
		};
	}
}