package com.tcn.dimensionalpocketsii.pocket.core.registry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcn.cosmoslibrary.common.enums.EnumGeneralAllowState;
import com.tcn.cosmoslibrary.common.enums.EnumGeneratedState;
import com.tcn.cosmoslibrary.common.enums.EnumLockState;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumTrapState;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.nbt.CosmosNBTHelper.Const;
import com.tcn.cosmoslibrary.common.nbt.CosmosNBTIOHandler;
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
import com.tcn.cosmoslibrary.system.io.CosmosIOHandler;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.ModReferences.INTERFACE.FILE;
import com.tcn.dimensionalpocketsii.ModReferences.INTERFACE.FOLDER;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.gson.GsonAdapterPocketChunkInfo;
import com.tcn.dimensionalpocketsii.pocket.core.gson.GsonAdapterPocketItemsList;
import com.tcn.dimensionalpocketsii.pocket.core.gson.PocketChunkInfo;
import com.tcn.dimensionalpocketsii.pocket.core.registry.BackupManager.BackupType;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager.GenerationParameters;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class InterfaceManager {

	public enum RegistryFileType {
		JSON(0, "json", ".json", true),
		DAT(1, "dat", ".dat", false);
		
		private final int index;
		private final String name;
		private final String extension;
		private final boolean isPlainText;

		RegistryFileType(int indexIn, String nameIn, String extensionIn, boolean isPlainTextIn) {
			this.index = indexIn;
			this.name = nameIn;
			this.extension = extensionIn;
			this.isPlainText = isPlainTextIn;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public String getName() {
			return this.name;
		}

		public String getExtension() {
			return this.extension;
		}

		public boolean isPlainText() {
			return this.isPlainText;
		}
	}
	
	private static class GSON {
		private static Gson LOADER = new GsonBuilder()
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
			.registerTypeAdapter(PocketChunkInfo.class, new GsonAdapterPocketChunkInfo())
			.enableComplexMapKeySerialization().setPrettyPrinting().create();
	}
		
	public class Registry {
		public static Optional<File> saveToFile(Map<PocketChunkInfo, Pocket> pocketRegistryIn, RegistryFileType fileType, HolderLookup.Provider provider) {
			return saveToFile(pocketRegistryIn, FOLDER.REGISTRY, FILE.REGISTRY, fileType, provider, true, false);
		}
	
		public static Optional<File> saveToFile(Map<PocketChunkInfo, Pocket> pocketRegistryIn, String folderName, String fileName, RegistryFileType fileType, HolderLookup.Provider provider, boolean includeSaveFolder, boolean backup) {
			return saveRaw(retrieveOrCreateFile(folderName, fileName, fileType, includeSaveFolder, true), pocketRegistryIn, fileType, provider);
		}
	
		private static Optional<File> saveRaw(File fileIn, Map<PocketChunkInfo, Pocket> pocketRegistryIn, RegistryFileType fileTypeIn, HolderLookup.Provider provider) {
			if (fileTypeIn.isPlainText) {
				Collection<Pocket> values = pocketRegistryIn.values();
				Pocket[] tempArray = values.toArray(new Pocket[values.size()]);
				
				try (FileWriter writer = new FileWriter(fileIn)) {
					GSON.LOADER.toJson(tempArray, writer);
					writer.flush();
					
					return Optional.ofNullable(fileIn);
				} catch (Exception e) {
					DimensionalPockets.CONSOLE.fatal("[File System Error] <saveRegistryRaw> Could not save PocketRegistry {" + fileTypeIn.getExtension() + "} See stacktrace for more info:", e);
				}
			} else {
				Collection<Pocket> values = pocketRegistryIn.values();
				ArrayList<Pocket> array = new ArrayList<>(values);
	
				CompoundTag tag = new CompoundTag();
				
				tag.putInt("size", array.size());
				
				for (int i = 0; i < array.size(); i++) {
					Pocket pocket = array.get(i);
					
					CompoundTag pocketTag = new CompoundTag();
					pocket.writeToNBT(pocketTag, provider);
					
					tag.put(Integer.toString(i), pocketTag);
				}
				
				try {
					CosmosNBTIOHandler.write(tag, fileIn);
					return Optional.ofNullable(fileIn);
				} catch (Exception e) {
					DimensionalPockets.CONSOLE.fatal("[File System Error] <saveRegistryRaw> Could not save PocketRegistry {" + fileTypeIn.getExtension() + "} See stacktrace for more info:", e);
				}
			}
			return Optional.empty();
		}
		
		public static LinkedHashMap<PocketChunkInfo, Pocket> loadFromFile(RegistryFileType fileTypeIn, HolderLookup.Provider provider) {
			return loadFromFile(FOLDER.REGISTRY, FILE.REGISTRY, fileTypeIn, provider, true);
		}
		
		public static LinkedHashMap<PocketChunkInfo, Pocket> loadFromFile(String folderName, String fileName, RegistryFileType fileTypeIn, HolderLookup.Provider provider, boolean broadcast) {
			File loadedFile = retrieveOrCreateFile(folderName, fileName, fileTypeIn, true, false);
			
			if (!fileTypeIn.isPlainText()) {
				if (!loadedFile.exists()) {
					File jsonFile = retrieveOrCreateFile(folderName, fileName, RegistryFileType.JSON, true, false);
					
					if (jsonFile.exists()) {
						CosmosIOHandler.createFile(loadedFile);
						
						LinkedHashMap<PocketChunkInfo, Pocket> freshRegistry = loadRaw(jsonFile, RegistryFileType.JSON, provider, true);
						BackupManager.createFullBackup(freshRegistry, BackupType.CONVERSION, provider);
						
						saveRaw(loadedFile, freshRegistry, fileTypeIn, provider);
						
						jsonFile.delete();
						
						return freshRegistry;
					}
				}
			}
			
			return loadRaw(retrieveOrCreateFile(folderName, fileName, fileTypeIn, true, true), fileTypeIn, provider, broadcast);
		}
		
		private static LinkedHashMap<PocketChunkInfo, Pocket> loadRaw(File fileIn, RegistryFileType fileTypeIn, HolderLookup.Provider provider, boolean broadcast) {
			LinkedHashMap<PocketChunkInfo, Pocket> newPocketRegistry = new LinkedHashMap<>();
			
			if (fileTypeIn.isPlainText()) {
				Pocket[] pocketArray = null;
				
				try (FileReader reader = new FileReader(fileIn)) {
					pocketArray = GSON.LOADER.fromJson(reader, Pocket[].class);
					reader.close();
				} catch (Exception e) {
					DimensionalPockets.CONSOLE.fatal("[File System Error] <loadPocketRegistry> Could not load PocketRegistry. See stacktrace for more info:", e);
				}
				
				if (pocketArray != null) {
					if (broadcast) {
						DimensionalPockets.CONSOLE.info("[File System Load] <loadPocketRegistry> Begin loading Pockets from PocketRegistry.");
					}
					
					for (Pocket pocket : pocketArray) {
						if (pocket.energy_capacity != ModReferences.CONSTANT.POCKET_FE_CAP) {
							pocket.energy_capacity = ModReferences.CONSTANT.POCKET_FE_CAP;
						}
						
						if (pocket.energy_max_receive != ModReferences.CONSTANT.POCKET_FE_REC) {
							pocket.energy_max_receive = ModReferences.CONSTANT.POCKET_FE_REC;
						}
						
						if (pocket.energy_max_extract != ModReferences.CONSTANT.POCKET_FE_EXT) {
							pocket.energy_max_extract = ModReferences.CONSTANT.POCKET_FE_EXT;
						}
	
						if (pocket.fluid_tank.getFluidTank().getCapacity() != (ModReferences.CONSTANT.POCKET_FLUID_CAP)) {
							pocket.fluid_tank.getFluidTank().setCapacity((ModReferences.CONSTANT.POCKET_FLUID_CAP));
						}
	
						if (pocket.getChunkInfo() == null && pocket.chunk_pos != null) {
							pocket.chunk_info = new PocketChunkInfo(pocket.chunk_pos, true);
						}
						
						newPocketRegistry.put(pocket.getChunkInfo(), pocket);
						
						if (broadcast) {
							if (pocket.getOwner() != null) {
								DimensionalPockets.CONSOLE.info("[Pocket Load] <loadPocketRegistry> Pocket loaded into Registry: { ' claimed ' | '" + pocket.getChunkInfo() + "' | '" + pocket.getOwnerName() + "' }");
							} else {
								DimensionalPockets.CONSOLE.info("[Pocket load] <loadPocketRegistry> Pocket loaded into Registry: { 'unclaimed' | '" + pocket.getChunkInfo() + "' }");
							}
						}
					}
					
					if (broadcast) {
						DimensionalPockets.CONSOLE.info("[File System Load] <loadPocketRegistry> Finished loading Pockets from PocketRegistry.");
					}
				}
			} else {
				try {
					if (broadcast) {
						DimensionalPockets.CONSOLE.info("[File System Load] <loadPocketRegistry> Begin loading Pockets from PocketRegistry.");
					}
					
					try {
						CompoundTag readTag = CosmosNBTIOHandler.read(fileIn);
						
						if (readTag.contains("size")) {
							int size = readTag.getInt("size");
							
							for (int i = 0; i < size; i++) {
								CompoundTag pocketTag = readTag.getCompound(Integer.toString(i));
								
								Pocket pocket = Pocket.readFromNBT(pocketTag, provider);
								
								newPocketRegistry.put(pocket.getChunkInfo(), pocket);
								
								if (broadcast) {	
									if (pocket.getOwner() != null) {
										DimensionalPockets.CONSOLE.info("[Pocket Load] <loadPocketRegistry> Pocket loaded into Registry: { ' claimed ' | '" + pocket.getChunkInfo() + "' | '" + pocket.getOwnerName() + "' }");
									} else {
										DimensionalPockets.CONSOLE.info("[Pocket load] <loadPocketRegistry> Pocket loaded into Registry: { 'unclaimed' | '" + pocket.getChunkInfo() + "' }");
									}
								}
							}
						}
					} catch (Exception e) {
						
					}
					
					if (broadcast) {
						DimensionalPockets.CONSOLE.info("[File System Load] <loadPocketRegistry> Finished loading Pockets from PocketRegistry.");
					}
				} catch (Exception e) {
					DimensionalPockets.CONSOLE.fatal("[File System Error] <loadPocketRegistry> Could not load PocketRegistry. See stacktrace for more info:", e);
				}
			}
			
			return newPocketRegistry;
		}
	}
	
	public class LoadedBlocks {
		public static Optional<File> saveToFile(Map<CosmosChunkPos, ObjectBlockPosDimension> loadedBlocksIn, RegistryFileType fileType) {
			return saveToFile(loadedBlocksIn, FOLDER.DATA, FILE.LOADED_BLOCKS, fileType, true, false);
		}
	
		public static Optional<File> saveToFile(Map<CosmosChunkPos, ObjectBlockPosDimension> loadedBlocksIn, String folderName, String fileName, RegistryFileType fileType, boolean includeSaveFolder, boolean backup) {
			return saveRaw(retrieveOrCreateFile(folderName, fileName, fileType, includeSaveFolder, true), loadedBlocksIn);
		}
	
		private static Optional<File> saveRaw(File fileIn, Map<CosmosChunkPos, ObjectBlockPosDimension> loadedBlocksIn) {
			CompoundTag tag = new CompoundTag();
			
			for (Entry<CosmosChunkPos, ObjectBlockPosDimension> entry : loadedBlocksIn.entrySet()) {
				CompoundTag chunkTag = new CompoundTag();
				chunkTag.putLong("chunk", entry.getKey().toLong());
				tag.put(entry.getKey().getX() + ";" + entry.getKey().getZ(), chunkTag);
				
				CompoundTag dimTag = new CompoundTag();
				entry.getValue().save(dimTag);
				chunkTag.put("dim", dimTag);
			}
			
			try {
				CosmosNBTIOHandler.write(tag, fileIn);
				return Optional.ofNullable(fileIn);
			} catch (Exception e) {
				DimensionalPockets.CONSOLE.fatal("[File System Error] <saveRegistryRaw> Could not save LoadedBlocks {DAT}. See stacktrace for more info:", e);
			}
			
			return Optional.empty();
		}
	
		public static LinkedHashMap<CosmosChunkPos, ObjectBlockPosDimension> loadFromFile(RegistryFileType fileTypeIn) {
			return loadFromFile(FOLDER.DATA, FILE.LOADED_BLOCKS, fileTypeIn);
		}
		
		public static LinkedHashMap<CosmosChunkPos, ObjectBlockPosDimension> loadFromFile(String folderName, String fileName, RegistryFileType fileTypeIn) {
			return loadRaw(retrieveOrCreateFile(folderName, fileName, fileTypeIn, true, true));
		}
		
		private static LinkedHashMap<CosmosChunkPos, ObjectBlockPosDimension> loadRaw(File fileIn) {
			LinkedHashMap<CosmosChunkPos, ObjectBlockPosDimension> loadedBlocks = new LinkedHashMap<>();
			
			try {
				try {
					CompoundTag readTag = CosmosNBTIOHandler.read(fileIn);
					
					for (String key : readTag.getAllKeys()) {
						CompoundTag chunkTag = readTag.getCompound(key);
						CosmosChunkPos chunk = new CosmosChunkPos(chunkTag.getLong("chunk"));
						
						CompoundTag dimTag = chunkTag.getCompound("dim");
						ObjectBlockPosDimension dim = ObjectBlockPosDimension.load(dimTag);
						
						loadedBlocks.put(chunk, dim);
					}
				} catch (Exception e) {
					
				}
			} catch (Exception e) {
				DimensionalPockets.CONSOLE.fatal("[File System Error] <loadPocketRegistry> Could not load LoadedBlocks {DAT}. See stacktrace for more info:", e);
			}
		
			return loadedBlocks;
		}
	}
	
	public class LoadedRooms {
		public static Optional<File> saveToFile(ArrayList<CosmosChunkPos> loadedRoomsIn, RegistryFileType fileType) {
			return saveToFile(loadedRoomsIn, FOLDER.DATA, FILE.LOADED_ROOMS, fileType, true, false);
		}
	
		public static Optional<File> saveToFile(ArrayList<CosmosChunkPos> loadedBlocksIn, String folderName, String fileName, RegistryFileType fileType, boolean includeSaveFolder, boolean backup) {
			return saveRaw(retrieveOrCreateFile(folderName, fileName, fileType, includeSaveFolder, true), loadedBlocksIn);
		}
	
		private static Optional<File> saveRaw(File fileIn, ArrayList<CosmosChunkPos> loadedBlocksIn) {
			CompoundTag tag = new CompoundTag();

			for (int i = 0; i < loadedBlocksIn.size(); i++) {
				CosmosChunkPos pos = loadedBlocksIn.get(i);
				CompoundTag chunk = new CompoundTag();
				
				chunk.putInt(Const.NBT_POS_X_KEY, pos.getX());
				chunk.putInt(Const.NBT_POS_Z_KEY, pos.getZ());
				
				tag.put(Integer.toString(i), chunk);
			}
			
			tag.putInt("size", loadedBlocksIn.size());
					
			try {
				CosmosNBTIOHandler.write(tag, fileIn);
				return Optional.ofNullable(fileIn);
			} catch (Exception e) {
				DimensionalPockets.CONSOLE.fatal("[File System Error] <saveRegistryRaw> Could not save LoadedRooms {DAT}. See stacktrace for more info:", e);
			}
			
			return Optional.empty();
		}
	
		public static ArrayList<CosmosChunkPos> loadFromFile(RegistryFileType fileTypeIn) {
			return loadFromFile(FOLDER.DATA, FILE.LOADED_ROOMS, fileTypeIn);
		}
		
		public static ArrayList<CosmosChunkPos> loadFromFile(String folderName, String fileName, RegistryFileType fileTypeIn) {
			return loadRaw(retrieveOrCreateFile(folderName, fileName, fileTypeIn, true, true));
		}
		
		private static ArrayList<CosmosChunkPos> loadRaw(File fileIn) {
			ArrayList<CosmosChunkPos> loadedRooms = new ArrayList<>();
			
			try {
				try {
					CompoundTag readTag = CosmosNBTIOHandler.read(fileIn);
					
					for (int i = 0; i < readTag.getInt("size"); i++) {
						CompoundTag chunkTag = readTag.getCompound(Integer.toString(i));
						
						loadedRooms.add(new CosmosChunkPos(chunkTag.getInt(Const.NBT_POS_X_KEY), chunkTag.getInt(Const.NBT_POS_Z_KEY)));
					}
				} catch (Exception e) {
					
				}
			} catch (Exception e) {
				DimensionalPockets.CONSOLE.fatal("[File System Error] <loadPocketRegistry> Could not load LoadedRooms {DAT}. See stacktrace for more info:", e);
			}
		
			return loadedRooms;
		}
	}
	
	public class GenerationParams {
		public static boolean saveToFile(GenerationParameters pocketGenParameters) {
			File dataFile = retrieveOrCreateFile(FOLDER.REGISTRY, FILE.GEN_PARAMS, RegistryFileType.JSON, true, true);
			
			try (FileWriter writer = new FileWriter(dataFile)) {
				GSON.LOADER.toJson(pocketGenParameters, writer);
				writer.flush();
				
				return true;
			} catch (Exception e) {
				DimensionalPockets.CONSOLE.fatal("[File System Error] <savePocketGenParams> Could not save pocketGenParams via [FileWriter]. See stacktrace for more info:", e);
				return false;
			}
		}
	
		public static GenerationParameters loadFromFile() {
			try {
				File dataFile = retrieveOrCreateFile(FOLDER.REGISTRY, FILE.GEN_PARAMS, RegistryFileType.JSON, true, true);
				
				if (dataFile.exists()) {
					try (FileReader dataReader = new FileReader(dataFile)) {
						GenerationParameters pocketGenParams = GSON.LOADER.fromJson(dataReader, GenerationParameters.class);
						dataReader.close();
						
						if (pocketGenParams != null) {
							return pocketGenParams;
						}
					} catch (Exception e) {
						DimensionalPockets.CONSOLE.fatal("[File System Error] <loadPocketGenParams> Could not load pocketGenParams via [FileWriter]. See stacktrace for more info:", e);
					}
				}
			} catch (Exception e) {
				DimensionalPockets.CONSOLE.fatal("[File System Error] <loadPocketGenParams> Could not load pocketGenParams. See stacktrace for more info:", e);
			}
			
			return new GenerationParameters();
		}
	}

	public static File retrieveOrCreateFile(String folderName, String fileName, RegistryFileType fileTypeIn, boolean includeSaveFolder, boolean createFile) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		StringBuilder filePath = new StringBuilder();
		
		if (server != null) {
			if (includeSaveFolder) {
				if (server.isSingleplayer()) {
					filePath.append("saves/");
				}
			}
			
			filePath.append(CosmosIOHandler.getServerLevelId(server) + "/" + folderName + "/" + fileName + fileTypeIn.getExtension());
			
			File file = server.getFile(filePath.toString()).toFile();
			
			if (!file.exists()) {
				if (createFile) {
					CosmosIOHandler.createFile(file);
				}
			}
			
			return file;
		}
		
		return new File(".");
	}
}