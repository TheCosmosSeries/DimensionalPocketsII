package com.tcn.dimensionalpocketsii.pocket.core.registry;

import java.util.LinkedHashMap;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.system.primative.ObjectHolder2;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.PocketsConfigManager;
import com.tcn.dimensionalpocketsii.core.management.PocketsDimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.gson.PocketChunkInfo;
import com.tcn.dimensionalpocketsii.pocket.core.registry.BackupManager.BackupType;
import com.tcn.dimensionalpocketsii.pocket.core.registry.InterfaceManager.RegistryFileType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * @author TheCosmicNebula_
 */
public class StorageManager {
	private static LinkedHashMap<PocketChunkInfo, Pocket> registryMap = new LinkedHashMap<>();

	//How far the pockets are apart (This value - 1 is how many chunks between each pocket in the X or Z axis. [NOT DYNAMIC]
	private static final int pocketChunkSpacing = 4;
	
	//The offset in the Y direction of how far each pocket is above the bedrock layer in the Pocket Dimension. (0 is the bedrock layer) [NOT DYNAMIC]
	private static final int pocketYOffset = 1;
	
	//The interior size of the structure + 1. [Not a dynamic value. If different size pockets are required, extra dimensions will have to be added] [NOT DYNAMIC]
	private static final int pocketSize = 15;
	
	//A class that contains information about where [(CosmosChunkPos(X,Z)) and Direction] to generate the next Pocket.
	private static GenerationParameters generationParams = new GenerationParameters();
	
	public static class GenerationParameters {
		private CosmosChunkPos currentChunk = new CosmosChunkPos(0, 0);
		private Direction nextPocketCoordsDirection = Direction.NORTH;
	}
	
	public static int getPocketYOffset() {
		return pocketYOffset;
	}
	
	public static int getPocketSize() {
		return pocketSize;
	}

	public static ServerLevel getServerLevel() {
		return ServerLifecycleHooks.getCurrentServer().getLevel(PocketsDimensionManager.POCKET_WORLD);
	}
	
	public static LinkedHashMap<PocketChunkInfo, Pocket> getRegistry() {
		return registryMap;
	}
	
	public static void clearRegistry() {
		registryMap.clear();
		
		DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry] <clearmap> Pocket Map Cleared");
	}
	
	public static Pocket getPocketFromChunkPosition(Level levelIn, CosmosChunkPos chunkPos) {
		Pocket[] pocket = new Pocket[] { new Pocket(null) };
		boolean[] found = new boolean[] { false };
		
		if (chunkPos != null) {
			registryMap.forEach((info, pock) -> {
				if (info.isChunkContained(chunkPos)) {
					pocket[0] = pock;
					found[0] = true;
				}
			});
			
			if (!found[0]) {
				if (levelIn != null) {
					if (!levelIn.isClientSide()) {
						DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + chunkPos + " }. Chunk Position not present in map. <ChunkPos>");
					}
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + chunkPos + " }. Chunk Position <null>!! <ChunkPos>");
				}
			}
		} else {
			if (levelIn != null) {
				if (!levelIn.isClientSide()) {
					DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + chunkPos + " }. Chunk Position <null>!! <ChunkPos>");
				}
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + chunkPos + " }. Chunk Position <null>!! <ChunkPos>");
			}
		}
		
		return pocket[0];
	}

	public static Pocket getPocketFromChunkInfo(Level levelIn, PocketChunkInfo pocketChunkInfoIn) {
		Pocket[] pocket = new Pocket[] { new Pocket(null) };
		
		if (pocketChunkInfoIn != null) {
			registryMap.forEach((info, pock) -> {
				if (info.getDominantChunk().equals(pocketChunkInfoIn.getDominantChunk()) && info.isSingleChunk() == pocketChunkInfoIn.isSingleChunk()) {
					pocket[0] = pock;
				}
			});
			
			if (pocket[0].getChunkInfo() == null) {

				if (levelIn != null) {
					if (!levelIn.isClientSide()) {
						DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + pocketChunkInfoIn + " }. Chunk Position not present in map. <ChunkInfo>");
					}
				} else {
					DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + pocketChunkInfoIn + " }. Chunk Position not present in map. <ChunkInfo>");
				}
			}
			
		} else {
			if (levelIn != null) {
				if (!levelIn.isClientSide()) {
					DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + pocketChunkInfoIn + " }. Chunk Position <null>!! <ChunkInfo>");
				}
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + pocketChunkInfoIn + " }. Chunk Position <null>!! <ChunkInfo>");
			}
		}
		
		return pocket[0];
	}

	public static CosmosChunkPos getNextPocketChunkPosition(CosmosChunkPos currentCoords) {
		CosmosChunkPos[] result = new CosmosChunkPos[] { currentCoords };
		
		registryMap.keySet().stream().forEach((test) -> {
			if (test.getDominantChunk().equals(result[0])) {
				CosmosChunkPos offset = new CosmosChunkPos(0, 0).offset(generationParams.nextPocketCoordsDirection);
				
				result[0] = new CosmosChunkPos(result[0].getX(), result[0].getZ()).add(offset.getX() * pocketChunkSpacing, offset.getZ() * pocketChunkSpacing);
			}
		});
		
		Direction clockwise = generationParams.nextPocketCoordsDirection.getClockWise();
		CosmosChunkPos probe = new CosmosChunkPos(0, 0).offset(clockwise);
		CosmosChunkPos probe2 = new CosmosChunkPos(probe.getX() * pocketChunkSpacing, probe.getZ() * pocketChunkSpacing);
		
		CosmosChunkPos probed = new CosmosChunkPos(result[0]).add(probe2);
		
		registryMap.keySet().stream().forEach((test) -> {
			if (test.getDominantChunk() == probed) {
				generationParams.nextPocketCoordsDirection = clockwise;
			}
		});
		
		return result[0];
	}
	
	public static ObjectHolder2<PocketChunkInfo, Pocket> getChunkInfoForPocket(ResourceKey<Level> dimension, BlockPos pos) {
		for(Pocket pocket : registryMap.values()) {
			ResourceKey<Level> pocket_dimension = pocket.getSourceBlockDimension();
			
			if (pocket_dimension.equals(dimension) && pocket.doesBlockArrayContain(pos, dimension.location())) {
				return new ObjectHolder2<>(pocket.getChunkInfo(), pocket);
			} 
		}
		
		return null;
	}

	public static Pocket getOrCreatePocket(Level levelIn, BlockPos coordSetSource, boolean isSingleChunk) {
		ResourceKey<Level> dimIDSource = levelIn.dimension();
		ObjectHolder2<PocketChunkInfo, Pocket> chunk = getChunkInfoForPocket(dimIDSource, coordSetSource);
		
		if (chunk != null && getPocketFromChunkInfo(levelIn, chunk.getFirst()) != null) {
			return getPocketFromChunkInfo(levelIn, chunk.getFirst());
		} else {
			if (!registryMap.isEmpty()) {
				generationParams.currentChunk = getNextPocketChunkPosition(generationParams.currentChunk);
				
				Pocket pocket = new Pocket(generationParams.currentChunk, isSingleChunk, dimIDSource, coordSetSource);
				registryMap.put(new PocketChunkInfo(pocket.getDominantChunkPos(), isSingleChunk), pocket);
				saveRegistry(levelIn.registryAccess());
				return pocket;
			} else {
				Pocket pocket = new Pocket(generationParams.currentChunk, isSingleChunk, dimIDSource, coordSetSource);
				registryMap.put(new PocketChunkInfo(pocket.getDominantChunkPos(), isSingleChunk), pocket);
				saveRegistry(levelIn.registryAccess());
				return pocket;
			}
		}
	}

	public static void updatePocket(PocketChunkInfo chunkPos, ResourceKey<Level> dimension, BlockPos pos, HolderLookup.Provider provider) {
		Pocket link = registryMap.get(chunkPos);
		
		if (link == null) {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <updatepocket> Unable to update Pocket: { " + chunkPos + " }. No Pocket exists with this Chunk Posision.");
			return;
		}

		link.setSourceBlockDimension(dimension);
		link.addPosToBlockArray(pos, dimension.location());

		saveRegistry(provider);
	}

	public static void updatePocket(CosmosChunkPos chunkPos, Level levelIn, BlockPos pos) {
		Pocket[] link = new Pocket[] { new Pocket(null) };
		
		registryMap.forEach((info, pock) -> {
			if (info.isChunkContained(chunkPos)) {
				link[0] = pock;
			}
		});
		
		if (link[0] == null) {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <updatepocket> Unable to update Pocket: { " + chunkPos + " }. No Pocket exists with this Chunk Posision.");
			return;
		}

		link[0].setSourceBlockDimension(levelIn.dimension());
		link[0].addPosToBlockArray(pos, levelIn.dimension().location());

		saveRegistry(levelIn.registryAccess());
	}
	
	public static void createBackup(BackupType backupTypeIn, HolderLookup.Provider provider) {
		if (PocketsConfigManager.getInstance().getCreateBackups()) {
			BackupManager.createFullBackup(registryMap, backupTypeIn, provider);
		}
	}

	public static void saveRegistry(HolderLookup.Provider provider) {
		InterfaceManager.Registry.saveToFile(registryMap, RegistryFileType.DAT, provider);
		InterfaceManager.GenerationParams.saveToFile(generationParams);

		DimensionalPockets.CONSOLE.debug("[Pocket Registry] {server} <saveData> Pocket data saved to File System.");
	}
	
	public static void loadRegistry(HolderLookup.Provider provider) {
		clearRegistry();
		registryMap = InterfaceManager.Registry.loadFromFile(RegistryFileType.DAT, provider);
		generationParams = InterfaceManager.GenerationParams.loadFromFile();
		
		if (!registryMap.isEmpty()) {
			DimensionalPockets.CONSOLE.debug("[Pocket Registry] {server} <loadData> Pocket data loaded from File System.");
			DimensionalPockets.CONSOLE.debug("[Pocket Registry] {server} <loadData> Backup of Pocket data created.");
		} else {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry] {server} <loadData> Pocket File System returned an empty Map. This must be a new world. Creating a fresh Map.");
		}
	}
	
}