package com.tcn.dimensionalpocketsii.pocket.core.management;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * @author TheCosmicNebula_
 */
public class PocketRegistryManager {
	private static Map<CosmosChunkPos, Pocket> pocketMap = new LinkedHashMap<>();

	//How far the pockets are apart (This value - 1 is how many chunks between each pocket in the X or Z axis. [NOT DYNAMIC]
	private static final int pocketChunkSpacing = 2;
	
	//The offset in the Y direction of how far each pocket is above the bedrock layer in the Pocket Dimension. (0 is the bedrock layer) [NOT DYNAMIC]
	private static final int pocketYOffset = 1;
	
	//The interior size of the structure + 1. [Not a dynamic value. If different size pockets are required, extra dimensions will have to be added] [NOT DYNAMIC]
	private static final int pocketSize = 15;
	
	//A class that contains information about where [(CosmosChunkPos(X,Z)) and Direction] to generate the next Pocket.
	private static PocketGenParameters pocketGenParameters = new PocketGenParameters();

	static class PocketGenParameters {
		private CosmosChunkPos currentChunk = new CosmosChunkPos(0, 0);
		private Direction nextPocketCoordsDirection = Direction.NORTH;
	}
	
	public static int getPocketYOffset() {
		return pocketYOffset;
	}
	
	public static int getPocketSize() {
		return pocketSize;
	}

	public static ServerLevel getLevelForPockets() {
		return ServerLifecycleHooks.getCurrentServer().getLevel(DimensionManager.POCKET_WORLD);
	}
	
	public static Map<CosmosChunkPos, Pocket> getPocketMap() {
		return pocketMap;
	}
	
	public static void clearPocketMap() {
		pocketMap.clear();
		
		DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry] <clearmap> Pocket Map Cleared");
	}

	public static Pocket getPocketFromChunkPosition(CosmosChunkPos chunkPos) {
		Pocket pocket = new Pocket(null);
		
		if (chunkPos != null){
			if (pocketMap.containsKey(chunkPos)) {
				pocket = pocketMap.get(chunkPos);
			} else {
				DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + chunkPos + " }. Chunk Position not present in map.");
			}
		} else {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <getpocket> Unable to retrieve Pocket: { " + chunkPos + " }. Chunk Position <null>!!");
		}
		
		return pocket;
	}

	public static CosmosChunkPos getNextPocketChunkPosition(CosmosChunkPos currentCoords) {
		CosmosChunkPos result = currentCoords;
		
		while (pocketMap.containsKey(result)) {
			CosmosChunkPos offset = new CosmosChunkPos(0, 0).offset(pocketGenParameters.nextPocketCoordsDirection);
			
			result = new CosmosChunkPos(result.getX(), result.getZ()).add(offset.getX() * pocketChunkSpacing, offset.getZ() * pocketChunkSpacing);
		}
		
		Direction clockwise = pocketGenParameters.nextPocketCoordsDirection.getClockWise();
		CosmosChunkPos probe = new CosmosChunkPos(0, 0).offset(clockwise);
		CosmosChunkPos probe2 = new CosmosChunkPos(probe.getX() * pocketChunkSpacing, probe.getZ() * pocketChunkSpacing);
		
		CosmosChunkPos probed = new CosmosChunkPos(result).add(probe2);
		
		if (!pocketMap.containsKey(probed)) {
			pocketGenParameters.nextPocketCoordsDirection = clockwise;
		}
		
		return result;
	}
	
	public static CosmosChunkPos getChunkPositionForPocket(ResourceKey<Level> dimension, BlockPos pos) {
		for(Pocket pocket : pocketMap.values()) {
			ResourceKey<Level> pocket_dimension = pocket.getSourceBlockDimension();
			
			if (pocket_dimension.equals(dimension) && pocket.doesBlockArrayContain(pos, dimension.location())) {
				return pocket.getChunkPos();
			} 
		}
		
		return null;
	}
	
	public static Pocket getOrCreatePocket(Level world, BlockPos coordSetSource) {
		ResourceKey<Level> dimIDSource = world.dimension();
		CosmosChunkPos chunk = getChunkPositionForPocket(dimIDSource, coordSetSource);
		Pocket pocket = new Pocket(null);
		
		if (chunk != null && getPocketFromChunkPosition(chunk) != null) {
			pocket = getPocketFromChunkPosition(chunk);
		} else {
			if (!pocketMap.isEmpty()) {
				pocketGenParameters.currentChunk = getNextPocketChunkPosition(pocketGenParameters.currentChunk);
				
				pocket = new Pocket(pocketGenParameters.currentChunk, dimIDSource, coordSetSource);
				pocketMap.put(pocket.getChunkPos(), pocket);
				saveData();
			} else {
				pocket = new Pocket(pocketGenParameters.currentChunk, dimIDSource, coordSetSource);
				pocketMap.put(pocket.getChunkPos(), pocket);
				saveData();
			}
		}
		
		return pocket;
	}

	public static void updatePocket(CosmosChunkPos chunkPos, ResourceKey<Level> dimension, BlockPos pos) {
		Pocket link = pocketMap.get(chunkPos);
		
		if (link == null) {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry Error] <updatepocket> Unable to update Pocket: { " + chunkPos + " }. No Pocket exists with this Chunk Posision.");
			return;
		}

		link.setSourceBlockDimension(dimension);
		link.addPosToBlockArray(pos, dimension.location());

		saveData();
	}
	
	public static void saveData() {
		PocketFileSystemManager.saveBackLinkMap(pocketMap);
		PocketFileSystemManager.savePocketGenParams(pocketGenParameters);

		DimensionalPockets.CONSOLE.debug("[Pocket Registry] {server} <save> Pocket data saved to File System.");
	}

	public static void loadData() {
		pocketMap = PocketFileSystemManager.loadBackLinkMap();
		pocketGenParameters = PocketFileSystemManager.loadPocketGenParams();
		
		if (!pocketMap.isEmpty()) {
			DimensionalPockets.CONSOLE.debug("[Pocket Registry] {server} <load> Pocket data loaded from File System.");
		} else {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket Registry] {server} <load> Pocket File System returned an empty Map. This must be a new world. Creating a fresh Map.");
		}
	}
	
}