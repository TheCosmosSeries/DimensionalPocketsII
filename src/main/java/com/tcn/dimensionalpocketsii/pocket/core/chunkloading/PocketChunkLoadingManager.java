package com.tcn.dimensionalpocketsii.pocket.core.chunkloading;

import java.util.Map.Entry;

import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectBlockPosDimension;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.ConfigurationManager;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PocketChunkLoadingManager {

	public static Capability<ChunkTrackerBlock> CAPABILITY_TRACKER_BLOCK = CapabilityManager.get(new CapabilityToken<>() { });
	public static Capability<ChunkTrackerRoom> CAPABILITY_TRACKER_ROOM = CapabilityManager.get(new CapabilityToken<>() { });
	
	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(ChunkTrackerBlock.class);
		event.register(ChunkTrackerRoom.class);
	}
	
	@SubscribeEvent
	public static void attachCaps(AttachCapabilitiesEvent<Level> event) {
		Level level = event.getObject();
		
		if (level.isClientSide || !(level instanceof ServerLevel)) {
			return;
		}
		
		ServerLevel serverLevel = (ServerLevel) level;
		
		LazyOptional<ChunkTrackerBlock> tracker_block = LazyOptional.of(() -> new ChunkTrackerBlock(serverLevel));
		event.addCapability(new ResourceLocation(DimensionalPockets.MOD_ID, "chunk_tracker_block"), ChunkTrackerBlock.getCapability(CAPABILITY_TRACKER_BLOCK, tracker_block));
		event.addListener(tracker_block::invalidate);
		
		LazyOptional<ChunkTrackerRoom> tracker_room = LazyOptional.of(() -> new ChunkTrackerRoom(serverLevel));
		event.addCapability(new ResourceLocation(DimensionalPockets.MOD_ID, "chunk_tracker_room"), ChunkTrackerRoom.getCapability(CAPABILITY_TRACKER_ROOM, tracker_room));
		event.addListener(tracker_room::invalidate);
	}

	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.END || !(event.world instanceof ServerLevel)) {
			return;
		}

		ServerLevel serverLevel = (ServerLevel) event.world;
		ServerChunkCache chunkProvider = serverLevel.getChunkSource();
		int tickSpeed = serverLevel.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
		
		if (tickSpeed > 0) {
			serverLevel.getCapability(CAPABILITY_TRACKER_BLOCK).ifPresent(tracker -> {
				tickBlock(tracker, serverLevel, chunkProvider, tickSpeed);
			});
			
			if (PocketUtil.isDimensionEqual(serverLevel, DimensionManager.POCKET_WORLD)) {
				serverLevel.getCapability(CAPABILITY_TRACKER_ROOM).ifPresent(tracker -> {
					tickRoom(tracker, serverLevel, chunkProvider, tickSpeed);
				});
			}
		}
	}
	
	public static void tickBlock(ChunkTrackerBlock tracker, ServerLevel levelIn, ServerChunkCache chunkProviderIn, int tickSpeedIn) {
		for (Entry<ChunkPos, ObjectBlockPosDimension> object : ChunkTrackerBlock.getBlocks().entrySet()) {
			ChunkPos pos = object.getKey();
			ResourceLocation dimension = object.getValue().getDimension();
			
			if (levelIn.dimension().location().equals(dimension)) {
				if (chunkProviderIn.chunkMap.getPlayers(pos, false).size() == 0) {
					if (ConfigurationManager.getInstance().getKeepChunksLoaded()) {
						levelIn.tickChunk(levelIn.getChunk(pos.x, pos.z), tickSpeedIn);
					}
				}
			}
		}
	}

	public static void tickRoom(ChunkTrackerRoom tracker, ServerLevel levelIn, ServerChunkCache chunkProviderIn, int tickSpeedIn) {
		for (ChunkPos pos : ChunkTrackerRoom.getRooms()) {
			if (chunkProviderIn.chunkMap.getPlayers(pos, false).size() == 0) {
				if (ConfigurationManager.getInstance().getKeepChunksLoaded()) {
					levelIn.tickChunk(levelIn.getChunk(pos.x, pos.z), tickSpeedIn);
				}
			}
		}
	}

	public static void addBlockToChunkLoader(Level levelIn, BlockPos posIn) {
		levelIn.getCapability(PocketChunkLoadingManager.CAPABILITY_TRACKER_BLOCK).ifPresent(tracker -> {
			ChunkPos pos = levelIn.getChunk(posIn).getPos();
			
			tracker.add(levelIn.dimension(), new ChunkPos(pos.x, pos.z), posIn);
		});
	}
	
	public static void removeBlockFromChunkLoader(Level levelIn, BlockPos posIn) {
		levelIn.getCapability(PocketChunkLoadingManager.CAPABILITY_TRACKER_BLOCK).ifPresent(tracker -> {
			ChunkPos pos = levelIn.getChunk(posIn).getPos();
			
			tracker.remove(levelIn.dimension(), new ChunkPos(pos.x, pos.z), posIn);
		});
	}

	public static void addPocketToChunkLoader(Pocket pocketIn) {
		Level levelA = PocketRegistryManager.getLevelForPockets();
		
		levelA.getCapability(PocketChunkLoadingManager.CAPABILITY_TRACKER_ROOM).ifPresent(tracker -> {
			ChunkPos pos = levelA.getChunk(CosmosChunkPos.scaleFromChunkPos(pocketIn.getChunkPos())).getPos();
			
			tracker.addRoom(new ChunkPos(pos.x, pos.z));
		});
	}

	public static void removePocketFromChunkLoader(Pocket pocketIn) {
		Level levelA = PocketRegistryManager.getLevelForPockets();
		
		levelA.getCapability(PocketChunkLoadingManager.CAPABILITY_TRACKER_ROOM).ifPresent(tracker -> {
			ChunkPos pos = levelA.getChunk(CosmosChunkPos.scaleFromChunkPos(pocketIn.getChunkPos())).getPos();
			
			tracker.removeRoom(new ChunkPos(pos.x, pos.z));
		});
	}
}