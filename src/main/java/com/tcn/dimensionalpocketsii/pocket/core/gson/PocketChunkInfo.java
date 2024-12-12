package com.tcn.dimensionalpocketsii.pocket.core.gson;

import java.util.ArrayList;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;

import net.minecraft.nbt.CompoundTag;

public class PocketChunkInfo {
	
	private static final String NBT_CHUNK_LIST = "chunk_list";
	private static final String NBT_SINGLE_CHUNK = "single_chunk";
	private static final String NBT_DOMINANT_CHUNK = "dominant_chunk";
	
	public static PocketChunkInfo EMPTY = new PocketChunkInfo(new CosmosChunkPos(0, 0), true);
	private ArrayList<CosmosChunkPos> chunks = new ArrayList<CosmosChunkPos>();
	
	private boolean isSingleChunk = true;
	
	public PocketChunkInfo(CosmosChunkPos dominantChunkIn) {
		this(dominantChunkIn, true);
	}
	
	public PocketChunkInfo(CosmosChunkPos dominantChunkIn, boolean isSingleChunkIn) {
		this.chunks.add(dominantChunkIn);
		
		this.isSingleChunk = isSingleChunkIn;
		
		if (!isSingleChunkIn) {
			this.isSingleChunk = false;
			
			this.chunks.add(new CosmosChunkPos(dominantChunkIn.add(1, 0)));
			this.chunks.add(new CosmosChunkPos(dominantChunkIn.add(0, 1)));
			this.chunks.add(new CosmosChunkPos(dominantChunkIn.add(1, 1)));
		}
	}
	
	public boolean isChunkContained(CosmosChunkPos chunkIn) {
		return this.chunks.contains(chunkIn);
	}
	
	public CosmosChunkPos getDominantChunk() {
		return this.chunks.get(0);
	}
	
	public ArrayList<CosmosChunkPos> getChunks() {
		return this.chunks;
	}
	
	public boolean isSingleChunk() {
		return this.isSingleChunk;
	}
	
	public void save(CompoundTag tag) {
		tag.putBoolean(NBT_SINGLE_CHUNK, this.isSingleChunk);
		
		CompoundTag chunks = new CompoundTag();
		
		CompoundTag dominantChunk = new CompoundTag();
		this.chunks.get(0).saveRaw(dominantChunk);
		tag.put(NBT_DOMINANT_CHUNK, dominantChunk);
		
		for (int i = 1; i < this.chunks.size(); i++) {
			CompoundTag chunkTag = new CompoundTag();
			
			this.chunks.get(i).saveRaw(chunkTag);
			chunks.put(Integer.toString(i), chunkTag);
		}
		
		tag.put(NBT_CHUNK_LIST, chunks);
	}
	
	public static PocketChunkInfo load(CompoundTag tag) {
		if (tag.contains(NBT_CHUNK_LIST)) {
			//CompoundTag chunks = tag.getCompound(NBT_CHUNK_LIST);
			
			return new PocketChunkInfo(CosmosChunkPos.loadRaw(tag.getCompound(NBT_DOMINANT_CHUNK)), tag.contains(NBT_SINGLE_CHUNK) ? tag.getBoolean(NBT_SINGLE_CHUNK) : true);
		} else {
			return new PocketChunkInfo(CosmosChunkPos.loadRaw(tag), tag.getBoolean(NBT_SINGLE_CHUNK));
		}
	}
	
	@Override
	public String toString() {
		if (!this.isSingleChunk) {
			String array = this.chunks.get(1).toString() + ", " + this.chunks.get(2).toString() + ", " + this.chunks.get(3).toString();
			
			return "{ DominantChunk: " + this.chunks.get(0).toString() + " | SingleChunk: " + this.isSingleChunk + " | ChunkArray: [" + array + "] }";
		}
		
		return "{ DominantChunk: " + this.chunks.get(0).toString() + " | SingleChunk: " + this.isSingleChunk + " }";
	}
}
