package com.tcn.dimensionalpocketsii.pocket.core.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectBlockPosDimension;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.ModConfigManager;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.gson.PocketChunkInfo;
import com.tcn.dimensionalpocketsii.pocket.core.registry.InterfaceManager.RegistryFileType;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChunkLoadingManager {
	private static LinkedHashMap<CosmosChunkPos, ObjectBlockPosDimension> chunkLoadedBlocks = new LinkedHashMap<>();
	private static ArrayList<CosmosChunkPos> chunkLoadedRooms = new ArrayList<>();
	
	private static ChunkLoadingManager INSTANCE = new ChunkLoadingManager();
	
	public ChunkLoadingManager getInstance() {
		return INSTANCE;
	}
	
	public static LinkedHashMap<CosmosChunkPos, ObjectBlockPosDimension> getChunkLoadedBlocks() {
		return chunkLoadedBlocks;
	}
	
	public static ArrayList<CosmosChunkPos> getChunkLoadedRooms() {
		return chunkLoadedRooms;
	}
	
	public static void clearLoadedBlocks() {
		chunkLoadedBlocks.clear();
	}
	
	public static void clearLoadedRooms() {
		chunkLoadedRooms.clear();
	}
	
	public static void addBlock(Level levelIn, CosmosChunkPos chunk, BlockPos loader) {
		if (levelIn instanceof ServerLevel) {
			ServerLevel serverLevel = (ServerLevel) levelIn;
			
			if (chunkLoadedBlocks.containsKey(chunk) && chunkLoadedBlocks.get(chunk).getPos().equals(loader)) {
				DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <add> The following Pocket Block was already marked as loaded: { " + chunk + ", " + levelIn.dimension().location().getNamespace() + ":" + levelIn.dimension().location().getPath() + " }");
				return;
			}
			
			if (!chunkLoadedBlocks.containsKey(chunk)) {
				serverLevel.setChunkForced(chunk.getX(), chunk.getZ(), true);
				chunkLoadedBlocks.put(chunk, new ObjectBlockPosDimension(loader, levelIn.dimension().location()));
				DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <add> Marked the following Pocket Block to the be loaded: { " + chunk + ", " + levelIn.dimension().location().getNamespace() + ":" + levelIn.dimension().location().getPath() + " }");
			}
		}
	}

	public static void removeBlock(Level levelIn, CosmosChunkPos chunk, BlockPos loader) {
		if (levelIn instanceof ServerLevel) {
			ServerLevel serverLevel = (ServerLevel) levelIn;
			
			if (!chunkLoadedBlocks.containsKey(chunk) || !chunkLoadedBlocks.get(chunk).getPos().equals(loader)) {
				DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <remove> Something tried to remove a loaded Pocket Block that was never loaded before { " + chunk + ", " + levelIn.dimension().location().getNamespace() + ":" + levelIn.dimension().location().getPath() + " }");
				return;
			}
			
			if (chunkLoadedBlocks.get(chunk).getPos().equals(loader)) {
				serverLevel.setChunkForced(chunk.getX(), chunk.getZ(), false);
				chunkLoadedBlocks.remove(chunk);
				DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <remove> Removed the following Pocket Block from the list of loaded rooms: { " + chunk + ", " + levelIn.dimension().location().getNamespace() + ":" + levelIn.dimension().location().getPath() + " }");
			}
		}
	}

	public static void addRoom(CosmosChunkPos chunk) {
		ServerLevel serverLevel = StorageManager.getServerLevel();
		
		if (chunkLoadedRooms.contains(chunk)) {
			DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <add> The following Pocket Room was already marked as loaded: { " + chunk + ", " + serverLevel.dimension().location().getNamespace() + ":" + serverLevel.dimension().location().getPath() + " }");
			return;
		}
		
		if (!chunkLoadedRooms.contains(chunk)) {
			if (PocketUtil.isDimensionEqual(serverLevel, ModDimensionManager.POCKET_WORLD)) {
				serverLevel.setChunkForced(chunk.getX(), chunk.getZ(), true);
				chunkLoadedRooms.add(chunk);
				DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <add> Marked the following Pocket Room to the be loaded: { " + chunk + ", " + serverLevel.dimension().location().getNamespace() + ":" + serverLevel.dimension().location().getPath() + " }");
			}
		}
	}

	public static void removeRoom(CosmosChunkPos chunk) {
		ServerLevel serverLevel = StorageManager.getServerLevel();
		
		if (!chunkLoadedRooms.contains(chunk)) {
			DimensionalPockets.CONSOLE.warning("[PocketChunkLoader] <remove> Something tried to remove a loaded Pocket Room that was never loaded before { " + chunk + ", " + serverLevel.dimension().location().getNamespace() + ":" + serverLevel.dimension().location().getPath() + " }");
			return;
		}
		
		if (chunkLoadedRooms.contains(chunk)) {
			if (PocketUtil.isDimensionEqual(serverLevel, ModDimensionManager.POCKET_WORLD)) {
				serverLevel.setChunkForced(chunk.getX(), chunk.getZ(), false);
				chunkLoadedRooms.remove(chunk);
				DimensionalPockets.CONSOLE.debug("[PocketChunkLoader] <remove> Removed the following pocket room from the list of loaded rooms: { " + chunk + ", " + serverLevel.dimension().location().getNamespace() + ":" + serverLevel.dimension().location().getPath() + " }");
			}
		}
	}

	@SubscribeEvent
	public static void onTick(final LevelTickEvent.Pre event) {
		if (!(event.getLevel() instanceof ServerLevel)) {
			return;
		}
		
		ServerLevel serverLevel = (ServerLevel) event.getLevel();
		ServerChunkCache chunkProvider = serverLevel.getChunkSource();
		
		int tickSpeed = serverLevel.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
		
		if (tickSpeed > 0) {
			if (ModConfigManager.getInstance().getKeepChunksLoaded()) {
				tickLoadedBlocks(serverLevel, chunkProvider, tickSpeed);
				tickLoadedRooms(serverLevel, chunkProvider, tickSpeed);
				
				//DimensionalPockets.CONSOLE.info("Ticking actual");
				//System.out.println("Ticking pls no");
			}
		}
	}
	
	public static void tickLoadedBlocks(ServerLevel levelIn, ServerChunkCache chunkProviderIn, int tickSpeedIn) {
		for (Entry<CosmosChunkPos, ObjectBlockPosDimension> object : chunkLoadedBlocks.entrySet()) {
			CosmosChunkPos pos = object.getKey();
			ResourceLocation dimension = object.getValue().getDimension();
			
			if (levelIn.dimension().location().equals(dimension)) {
				if (chunkProviderIn.chunkMap.getPlayers(new ChunkPos(pos.getX(), pos.getZ()), false).size() == 0) {
					levelIn.tickChunk(levelIn.getChunk(pos.getX(), pos.getZ()), tickSpeedIn);
				}
			}
		}
	}

	public static void tickLoadedRooms(ServerLevel levelIn, ServerChunkCache chunkProviderIn, int tickSpeedIn) {
		for (CosmosChunkPos pos : chunkLoadedRooms) {
			if (chunkProviderIn.chunkMap.getPlayers(new ChunkPos(pos.getX(), pos.getZ()), false).size() == 0) {
				Pocket pocket = StorageManager.getPocketFromChunkPosition(levelIn, pos);
				
				if (pocket.getChunkInfo() != null) {
					levelIn.tickChunk(levelIn.getChunk(pos.getX(), pos.getZ()), tickSpeedIn);
					
					if (!pocket.getChunkInfo().isSingleChunk()) {
						levelIn.tickChunk(levelIn.getChunk(pos.getX() + 1, pos.getZ() + 0), tickSpeedIn);
						levelIn.tickChunk(levelIn.getChunk(pos.getX() + 0, pos.getZ() + 1), tickSpeedIn);
						levelIn.tickChunk(levelIn.getChunk(pos.getX() + 1, pos.getZ() + 1), tickSpeedIn);
					}
				}
			}
		}
	}

	public static void addBlockToChunkLoader(Level levelIn, BlockPos posIn) {
		ChunkPos pos = levelIn.getChunk(posIn).getPos();
		
		addBlock(levelIn, new CosmosChunkPos(pos.x, pos.z), posIn);
		saveToStorage();
	}
	
	public static void removeBlockFromChunkLoader(Level levelIn, BlockPos posIn) {
		ChunkPos pos = levelIn.getChunk(posIn).getPos();
		
		removeBlock(levelIn, new CosmosChunkPos(pos.x, pos.z), posIn);
		saveToStorage();
	}

	public static void addPocketToChunkLoader(Pocket pocketIn) {
		PocketChunkInfo chunkInfo = pocketIn.getChunkInfo();
		
		addRoom(chunkInfo.getDominantChunk());
		saveToStorage();
	}

	public static void removePocketFromChunkLoader(Pocket pocketIn) {
		PocketChunkInfo chunkInfo = pocketIn.getChunkInfo();
		
		removeRoom(chunkInfo.getDominantChunk());
		saveToStorage();
	}
	
	public static void saveToStorage() {
		InterfaceManager.LoadedBlocks.saveToFile(chunkLoadedBlocks, RegistryFileType.DAT);
		InterfaceManager.LoadedRooms.saveToFile(chunkLoadedRooms, RegistryFileType.DAT);
	}
	
	public static void loadFromStorage() {
		clearLoadedBlocks();
		clearLoadedRooms();
		
		chunkLoadedBlocks = InterfaceManager.LoadedBlocks.loadFromFile(RegistryFileType.DAT);
		chunkLoadedRooms = InterfaceManager.LoadedRooms.loadFromFile(RegistryFileType.DAT);
	}
	
}