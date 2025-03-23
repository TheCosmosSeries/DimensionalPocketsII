package com.tcn.dimensionalpocketsii.pocket.core.chunkloading;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.tcn.cosmoslibrary.registry.gson.object.ObjectBlockPosDimension;
import com.tcn.dimensionalpocketsii.DimensionalPockets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ChunkTrackerBlock {
	private static final Map<ChunkPos, ObjectBlockPosDimension> BLOCKS = new HashMap<>();
	private final ServerLevel level;
	
	public ChunkTrackerBlock() { 
		this.level = null;
	}
	
	public ChunkTrackerBlock(ServerLevel levelIn) { 
		this.level = levelIn;
	}
	
	public void add(ResourceKey<Level> dimensionIn, ChunkPos chunk, BlockPos loader) {
		if (BLOCKS.containsKey(chunk) && BLOCKS.get(chunk).getPos().equals(loader)) {
			DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <add> The following Pocket Block was already marked as loaded: { " + chunk + ", " + dimensionIn.location().getNamespace() + ":" + dimensionIn.location().getPath() + " }");
			return;
		}
		
		if (!BLOCKS.containsKey(chunk)) {
			level.setChunkForced(chunk.x, chunk.z, true);
			BLOCKS.put(chunk, new ObjectBlockPosDimension(loader, dimensionIn.location()));
			DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <add> Marked the following Pocket Block to the be loaded: { " + chunk + ", " + dimensionIn.location().getNamespace() + ":" + dimensionIn.location().getPath() + " }");
		}
	}

	public void remove(ResourceKey<Level> dimensionIn, ChunkPos chunk, BlockPos loader) {
		if (!BLOCKS.containsKey(chunk) || !BLOCKS.get(chunk).getPos().equals(loader)) {
			DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <remove> Something tried to remove a loaded Pocket Block that was never loaded before { " + chunk + ", " + dimensionIn.location().getNamespace() + ":" + dimensionIn.location().getPath() + " }");
			return;
		}
		
		if (BLOCKS.get(chunk).getPos().equals(loader)) {
			level.setChunkForced(chunk.x, chunk.z, false);
			BLOCKS.remove(chunk);
			DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <remove> Removed the following Pocket Block from the list of loaded rooms: { " + chunk + ", " + dimensionIn.location().getNamespace() + ":" + dimensionIn.location().getPath() + " }");
		}
	}
	
	public static Map<ChunkPos, ObjectBlockPosDimension> getBlocks() {
		return BLOCKS;
	}
	
	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		CompoundTag blockTag = new CompoundTag();
		
		for (Entry<ChunkPos, ObjectBlockPosDimension> entry : BLOCKS.entrySet()) {
			CompoundTag chunkTag = new CompoundTag();
			chunkTag.putLong("chunk", entry.getKey().toLong());
			blockTag.put(entry.getKey().x + ";" + entry.getKey().z, chunkTag);
			
			CompoundTag dimTag = new CompoundTag();
			entry.getValue().save(dimTag);
			chunkTag.put("dim", dimTag);
		}
		compound.put("blocks", blockTag);
		
		return compound;
	}

	public void load(Tag tag) {
		if (tag instanceof CompoundTag compound) {
			CompoundTag blockTag = compound.getCompound("blocks");
			
			for (String key : blockTag.getAllKeys()) {
				CompoundTag chunkTag = blockTag.getCompound(key);
				ChunkPos chunk = new ChunkPos(chunkTag.getLong("chunk"));
				
				CompoundTag dimTag = chunkTag.getCompound("dim");
				ObjectBlockPosDimension dim = ObjectBlockPosDimension.load(dimTag);
				
				this.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, dim.getDimension()), chunk, dim.getPos());
			}
		}
	}
	
	public static ICapabilitySerializable<?> getCapability(Capability<ChunkTrackerBlock> capIn, LazyOptional<ChunkTrackerBlock> opt) {
		return new ICapabilitySerializable<>() {
			
			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				return cap == capIn ? opt.cast() : LazyOptional.empty();
			}
			
			@Override
			public Tag serializeNBT() {
				return opt.map(ChunkTrackerBlock::save).orElse(null);
			}
			
			@Override
			public void deserializeNBT(Tag nbt) {
				opt.ifPresent(chunkTracker -> chunkTracker.load(nbt));
			}
		};
	}
}