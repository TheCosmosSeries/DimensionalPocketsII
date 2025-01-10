package com.tcn.dimensionalpocketsii.pocket.core.registry;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.Files;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.system.io.CosmosIOHandler;
import com.tcn.dimensionalpocketsii.ModReferences.INTERFACE.FILE;
import com.tcn.dimensionalpocketsii.ModReferences.INTERFACE.FOLDER;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.gson.PocketChunkInfo;
import com.tcn.dimensionalpocketsii.pocket.core.registry.InterfaceManager.RegistryFileType;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class BackupManager {
	
	public enum BackupType {
		SYSTEM(0, true, true, "system"),
		USER(1, false, true, "user"),
		CONVERSION(2, true, false, "conversion"),
		UNKNOWN(-1, false, false, "unknown");
		
		private final int index;
		private final boolean automatic;
		private final boolean includeDate;
		private final String descriptor;
		
		BackupType (int indexIn, boolean automaticIn, boolean includeDateIn, String descriptorIn) {
			this.index = indexIn;
			this.automatic = automaticIn;
			this.includeDate = includeDateIn;
			this.descriptor = descriptorIn;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public boolean getAutomatic() {
			return this.automatic;
		}

		public boolean getIncludeDate() {
			return this.includeDate;
		}
		
		public String getDescriptor() {
			return this.descriptor;
		}
	}
	
	public static boolean createFullBackup(Map<PocketChunkInfo, Pocket> pocketRegistryIn, BackupType backupTypeIn, HolderLookup.Provider provider) {
		String date = (backupTypeIn.getIncludeDate() ? " [" + CosmosUtil.getDateYMD(true, "-") + "] " : " ");
		String descriptor = (backupTypeIn.getDescriptor() != "" ? "[" + backupTypeIn.getDescriptor() + "]" : "");
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		StringBuilder filePath = new StringBuilder();
		
		try {
			if (server != null) {
				if (server.isSingleplayer()) {
					filePath.append("saves/");
				}
				
				filePath.append(CosmosIOHandler.getServerLevelId(server) + "/" + FOLDER.REGISTRY_BACKUP + "/" + FILE.REGISTRY_BACKUP + date + descriptor + ".zip");
				
				File tempFile = saveToFile(pocketRegistryIn, FOLDER.REGISTRY_BACKUP, FILE.REGISTRY_BACKUP, RegistryFileType.JSON, provider, true, true);
				CosmosIOHandler.createFile(tempFile);
				
				try {
					ZipOutputStream zipOutStream = new ZipOutputStream(new FileOutputStream(filePath.toString()));
					
					addEntry(zipOutStream, FILE.REGISTRY, RegistryFileType.DAT, saveToFile(pocketRegistryIn, "", FILE.REGISTRY, RegistryFileType.DAT, provider, true, true), true);
					addEntry(zipOutStream, FILE.REGISTRY, RegistryFileType.JSON, saveToFile(pocketRegistryIn, "", FILE.REGISTRY, RegistryFileType.JSON, provider, true, true), true);
					addEntry(zipOutStream, FILE.GEN_PARAMS, RegistryFileType.JSON, saveToFile(pocketRegistryIn, "", FILE.GEN_PARAMS, RegistryFileType.JSON, provider, true, true), true);
									
					zipOutStream.close();
					
					DimensionalPockets.CONSOLE.info("[Backup System] <createFullBackup> Full Backup created.");
				} catch (Exception e) {
					DimensionalPockets.CONSOLE.fatal("[Backup System] <createFullBackup> Unable to create Backup. See stacktrace for more info:", e);
				}

				tempFile.delete();

				return true;
			}
		} catch (Exception e) {
			DimensionalPockets.CONSOLE.fatal("[Backup System] <createFullBackup> Unable to create Backup. See stacktrace for more info:", e);
		}
		
		return false;
	}
	
	public static File saveToFile(Map<PocketChunkInfo, Pocket> pocketRegistryIn, String folderName, String fileName, RegistryFileType fileType, HolderLookup.Provider provider, boolean includeSaveFolder, boolean backup) {
		return InterfaceManager.Registry.saveToFile(pocketRegistryIn, folderName, fileName, fileType, provider, includeSaveFolder, backup).get();
	}
	
	private static boolean addEntry(ZipOutputStream zipOutputStream, String entryPath, RegistryFileType fileType, File fileToAdd, boolean deleteFile) {
		try {
			zipOutputStream.putNextEntry(new ZipEntry(entryPath + fileType.getExtension()));
			Files.copy(fileToAdd, zipOutputStream);
			zipOutputStream.closeEntry();
			
			if (deleteFile) {
				fileToAdd.delete();
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
