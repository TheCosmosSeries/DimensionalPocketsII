package com.tcn.dimensionalpocketsii.pocket.core.management;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcn.cosmoslibrary.common.enums.EnumGeneralAllowState;
import com.tcn.cosmoslibrary.common.enums.EnumGeneratedState;
import com.tcn.cosmoslibrary.common.enums.EnumLockState;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumTrapState;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterBlockPosDimension;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterConnectionType;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterFluidTankCustom;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterGeneralAllowState;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterGeneratedState;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterItemStack;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterLockState;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterSideState;
import com.tcn.cosmoslibrary.registry.gson.GsonAdapterTrapState;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectBlockPosDimension;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectConnectionType;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;
import com.tcn.dimensionalpocketsii.DimReference;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.gson.GsonAdapterPocketItemsList;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager.PocketGenParameters;

import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PocketFileSystemManager {

	private static Gson GSON = new GsonBuilder()
			.registerTypeAdapter(ItemStack.class, new GsonAdapterItemStack())
			.registerTypeAdapter(ObjectFluidTankCustom.class, new GsonAdapterFluidTankCustom())
			.registerTypeAdapter(ObjectConnectionType.class, new GsonAdapterConnectionType())
			.registerTypeAdapter(EnumLockState.class, new GsonAdapterLockState())
			.registerTypeAdapter(EnumSideState.class, new GsonAdapterSideState())
			.registerTypeAdapter(EnumTrapState.class, new GsonAdapterTrapState())
			.registerTypeAdapter(EnumGeneratedState.class, new GsonAdapterGeneratedState())
			.registerTypeAdapter(EnumGeneralAllowState.class, new GsonAdapterGeneralAllowState())
			.registerTypeAdapter(NonNullList.class, new GsonAdapterPocketItemsList())
			.registerTypeAdapter(ObjectBlockPosDimension.class, new GsonAdapterBlockPosDimension())
			.enableComplexMapKeySerialization().setPrettyPrinting().create();

	private static final String currentBackLinkFile = "pocketRegistry";
	private static final String pocketGenParamsFile = "pocketGenParameters";
	
	private static File getFile(String fileName) throws IOException {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		StringBuilder file_path = new StringBuilder();
		StringBuilder backup_path = new StringBuilder();
		
		try {
			if (server != null) {
				if (server.isSingleplayer()) {
					file_path.append("saves/");
					backup_path.append("saves/");
				}
				
				final Object save = ObfuscationReflectionHelper.getPrivateValue(MinecraftServer.class, server, "f_129744_");
				try {
					if (save instanceof LevelStorageSource.LevelStorageAccess) {
						String save_name = ((LevelStorageAccess) save).getLevelId();
						
						file_path.append(save_name);
						backup_path.append(save_name);
					}
				} catch (Exception e) {
					DimensionalPockets.CONSOLE.fatal("[File System Error] <getfile> Unable to get LevelId. See stacktrace for more info:", e);
				}
				
				file_path.append("/dimpockets/" + fileName + ".json");
				backup_path.append("/dimpockets/" + fileName + "_BACKUP.json");
				
				File save_file = server.getFile(file_path.toString());
				File backup_file = server.getFile(backup_path.toString());
				
				if (!save_file.exists()) {
					save_file.getParentFile().mkdirs();
					save_file.createNewFile();
				}
				
				//Makes a backup
				if (!backup_file.exists()) {
					backup_file.getParentFile().mkdirs();
					backup_file.createNewFile();	
					FileUtils.copyFile(save_file, backup_file);
				} else {
					FileUtils.copyFile(save_file, backup_file);
				}
				
				return save_file;
			}
		} catch (Exception e) {
			DimensionalPockets.CONSOLE.fatal("[File System Error] <getfile> Unable to create Registry File. See stacktrace for more info:", e);
		}
		return new File(".");
	}
	
	public static void saveBackLinkMap(Map<CosmosChunkPos, Pocket> backLinkMap) {
		try {
			File registryFile = getFile(currentBackLinkFile);
			
			Collection<Pocket> values = backLinkMap.values();
			Pocket[] tempArray = values.toArray(new Pocket[values.size()]);
			
			try (FileWriter writer = new FileWriter(registryFile)) {
				GSON.toJson(tempArray, writer);
				writer.flush();
			}
		} catch (Exception e) {
			DimensionalPockets.CONSOLE.fatal("[File System Error] <save> Could not save backLinkFile. See stacktrace for more info:", e);
		}
	}

	//@SuppressWarnings("unchecked")
	public static Map<CosmosChunkPos, Pocket> loadBackLinkMap() {
		Map<CosmosChunkPos, Pocket> backLinkMap = new LinkedHashMap<>();
		
		try {
			File registryFile = getFile(currentBackLinkFile);
			
			Pocket[] pocket_array = null;
			try (FileReader reader = new FileReader(registryFile)) {
				pocket_array = GSON.fromJson(reader, Pocket[].class);
			} catch (Exception e) {
				DimensionalPockets.CONSOLE.fatal("[File System Error] <load> Could not load backLinkFile. See stacktrace for more info:", e);
			}
			
			if (pocket_array != null) {
				DimensionalPockets.CONSOLE.info("[File System Load] <loadpockets> Begin loading Pockets from backLinkFile.");
				
				for (Pocket link : pocket_array) {
					if (link.energy_capacity != DimReference.CONSTANT.POCKET_FE_CAP) {
						link.energy_capacity = DimReference.CONSTANT.POCKET_FE_CAP;
						DimensionalPockets.CONSOLE.debugWarn("[Pocket Legacy Check] <powercapacity> Value different from expected. This has been corrected.");
					}
					
					if (link.energy_max_receive != DimReference.CONSTANT.POCKET_FE_REC) {
						link.energy_max_receive = DimReference.CONSTANT.POCKET_FE_REC;
						DimensionalPockets.CONSOLE.debugWarn("[Pocket Legacy Check] <powermaxreceieve> Value different from expected. This has been corrected.");
					}
					
					if (link.energy_max_extract != DimReference.CONSTANT.POCKET_FE_EXT) {
						link.energy_max_extract = DimReference.CONSTANT.POCKET_FE_EXT;
						DimensionalPockets.CONSOLE.debugWarn("[Pocket Legacy Check] <powermaxextract> Value different from expected. This has been corrected.");
					}

					if (link.fluid_tank.getFluidTank().getCapacity() != (DimReference.CONSTANT.POCKET_FLUID_CAP)) {
						link.fluid_tank.getFluidTank().setCapacity((DimReference.CONSTANT.POCKET_FLUID_CAP));
						DimensionalPockets.CONSOLE.debugWarn("[Pocket Legacy Check] <fluidcapacity> Value different from expected. This has been corrected.");
					}
					/*
					if (link.item_array.size() != DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE) {
						NonNullList<?> newList = NonNullList.withSize(DimReference.CONSTANT.POCKET_HELD_ITEMS_SIZE, link.item_array);
						
						link.item_array = (NonNullList<ItemStack>) newList;
						DimensionalPockets.CONSOLE.debugWarn("[Pocket Legacy Check] <itemarray> Item Array Size different from expected. This has been corrected.");
					}
					*/
					backLinkMap.put(link.getChunkPos(), link);
					
					if (link.getOwner() != null) {
						DimensionalPockets.CONSOLE.info("[Pocket Load] <claimed> Pocket loaded: { " + link.getChunkPos() + " } Owner: { " + link.getOwnerName() + " }");
					} else {
						DimensionalPockets.CONSOLE.info("[Pocket load] <unclaimed> Pocket loaded: { " + link.getChunkPos() + " } No owner.");
					}
				}
				
				DimensionalPockets.CONSOLE.debug("[File System Load] <loadpockets> Finished loading Pockets from backLinkFile.");
			}
		} catch (Exception e) {
			DimensionalPockets.CONSOLE.fatal("[File System Error] <load> Could not load backLinkFile. See stacktrace for more info:", e);
		}
		
		return backLinkMap;
	}

	public static void savePocketGenParams(PocketGenParameters pocketGenParameters) {
		try {
			File dataFile = getFile(pocketGenParamsFile);
			
			try (FileWriter writer = new FileWriter(dataFile)) {
				GSON.toJson(pocketGenParameters, writer);
			}
		} catch (Exception e) {
			DimensionalPockets.CONSOLE.fatal("[File System Error] <save> Could not save pocketGenParams. See stacktrace for more info:", e);
		}
	}

	public static PocketGenParameters loadPocketGenParams() {
		try {
			File dataFile = getFile(pocketGenParamsFile);
			
			if (dataFile.exists()) {
				try (FileReader dataReader = new FileReader(dataFile)) {
					PocketGenParameters pocketGenParams = GSON.fromJson(dataReader, PocketGenParameters.class);
					if (pocketGenParams != null)
						return pocketGenParams;
				}
			}
		} catch (Exception e) {
			DimensionalPockets.CONSOLE.fatal("[File System Error] <load> Could not load pocketGenParams. See stacktrace for more info:", e);
		}
		return new PocketGenParameters();
	}
}