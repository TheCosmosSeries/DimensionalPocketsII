package com.tcn.dimensionalpocketsii;

import java.util.ArrayList;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModReferences {
	
	public static class CONSTANT {		
		public static final int POCKET_HELD_ITEMS_SIZE = 48;      // 48
		public static final int POCKET_HELD_ITEMS_SIZE_WITH = 54; // 54
		
		public static final int POCKET_FE_CAP = 500000000;
		public static final int POCKET_FE_REC = 100000;
		public static final int POCKET_FE_EXT = 100000;
		
		public static final int POCKET_FLUID_CAP = 1000000;
		
		public static final int DEFAULT_COLOUR = ComponentColour.POCKET_PURPLE.dec();
		
		public static final int[] ENERGY = new int[] { 2000000, 50000, 4000 };
		public static final int[] ENERGY_ENHANCED = new int[] { 4000000, 100000, 6000 };
		
		public static final ComponentColour ENERGYBARCOLOUR = ComponentColour.RED;
	}
	
	public static class BlockedObjects {
		public static ArrayList<String> BLOCKS = new ArrayList<String>() { 
			private static final long serialVersionUID = 5L;
			
			{
				add("minecraft:bedrock");
				add("lucky:lucky_block");
				add("chancecubes:chance_cube");
				add("chancecubes:chance_icosahedron");
				add("chancecubes:giant_chance_cube");
				add("chancecubes:cube_dispenser");
				add("xreliquary:wraith_node");
				//add("");
			};
		};
		
		public static ArrayList<String> ITEMS = new ArrayList<String>() {
			private static final long serialVersionUID = 5L;
			
			{
				add("minecraft:chorus_fruit");
				add("minecraft:ender_pearl");
				add("xreliquary:ender_staff");
				add("inventorypets:pet_nether_portal");
				add("inventorypets:pet_enderman");
				add("inventorypets:pet_silverfish");
				add("mana-and-artifice:spell");
				add("mana-and-artifice:spell_book");
				add("mana-and-artifice:grimoire");
				add("notenoughwands:teleportation_wand");
				//add("");
			};
		};
		
		public static ArrayList<String> COMMANDS = new ArrayList<String>() {
			private static final long serialVersionUID = 5L;
			
			{
				add("teleport");
				add("tp");
				add("kill");
				add("setblock");
				add("clone");
				add("fill");
				add("place");
				add("setworldspawn");
				add("spawnpoint");
				add("spectate");
				add("spreadplayers");
				add("worldborder");
				add("/");
				//add("");
			};
		};
	}
	
	public static class INTERFACE {
		public static class FOLDER {
			public static final String REGISTRY = "dimpockets";
			public static final String REGISTRY_BACKUP = REGISTRY + "/backups";
			public static final String DATA = REGISTRY + "/data";
		}
		
		public static class FILE {
			public static final String REGISTRY = "pocketRegistry";
			public static final String REGISTRY_BACKUP = "registry-backup";

			public static final String GEN_PARAMS = "pocketGenParameters";
			
			public static final String LOADED_BLOCKS = "loaded_blocks";
			public static final String LOADED_ROOMS  = "loaded_rooms";
		}
	}
	
	public static class MESSAGES {
		@Deprecated(forRemoval = true, since = "1.19.2")
		public static final MutableComponent WELCOME = ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.welcome_one")
			.append(ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.welcome_two"))
			.append(ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.welcome_three"))
			.append(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.welcome_four"))
			.append(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.version")
		);
	}
	
	public static class RESOURCE {
		public static final String PRE = DimensionalPockets.MOD_ID + ":";

		public static final String RESOURCE = PRE + "textures/";
		public static final String GUI = RESOURCE + "gui/";

		public static final String BLOCKS = PRE + "block/";
		public static final String ITEMS = RESOURCE + "item/";
		
		public static final String MODELS = RESOURCE + "models/";
		
		public static final VoxelShape[] BOUNDING_BOXES_STANDARD = new VoxelShape[] {
			Block.box(4.80D, 4.80D, 4.80D, 11.2D, 11.2D, 11.2D), //BASE
			Block.box(4.80D, 0.00D, 4.80D, 11.2D, 4.80D, 11.2D), // DOWN
			Block.box(4.80D, 11.2D, 4.80D, 11.2D, 16.0D, 11.2D), // UP
			Block.box(4.80D, 4.80D, 0.00D, 11.2D, 11.2D, 4.80D), // NORTH
			Block.box(4.80D, 4.80D, 11.2D, 11.2D, 11.2D, 16.0D), // SOUTH
			Block.box(0.00D, 4.80D, 4.80D, 4.80D, 11.2D, 11.2D), // WEST
			Block.box(11.2D, 4.80D, 4.80D, 16.0D, 11.2D, 11.2D), // EAST
		};
		
		public static final ResourceLocation SHIELD = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "entity/dimensional_shield");
		public static final ResourceLocation SHIELD_NO_PATTERN =  ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "entity/dimensional_shield_nopattern");

		public static final ResourceLocation SHIELD_ENHANCED = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "entity/dimensional_shield_enhanced");
		public static final ResourceLocation SHIELD_ENHANCED_NO_PATTERN =  ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "entity/dimensional_shield_enhanced_nopattern");
	}
	
	public static class INTEGRATION {
		public static class JEI {
			public static final ResourceLocation UPGRADE_UID = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "upgrade_category");
		}
	}
	
	public static class GUI {
		public static class RESOURCE {
			
			public static final ResourceLocation[] POCKET = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/background_normal.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/background_normal_dark.png") };
			public static final ResourceLocation[] POCKET_SIDE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/background_side.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/background_side_dark.png") };
			public static final ResourceLocation[] POCKET_OVERLAY_NORMAL = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/overlay_normal.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/overlay_normal_dark.png") };
			public static final ResourceLocation[] POCKET_OVERLAY_SIDE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/overlay_side.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/overlay_side_dark.png") };
			public static final ResourceLocation[] POCKET_BASE_NORMAL = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/base_normal.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/base_normal_dark.png") };
			public static final ResourceLocation[] POCKET_BASE_SIDE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/base_side.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "pocket/base_side_dark.png") };
			
			public static final ResourceLocation[] CONNECTOR = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/background_normal.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/background_normal_dark.png") };
			public static final ResourceLocation[] CONNECTOR_SIDE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/background_side.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/background_side_dark.png") };
			public static final ResourceLocation[] CONNECTOR_OVERLAY_NORMAL = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/overlay_normal.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/overlay_normal_dark.png") };
			public static final ResourceLocation[] CONNECTOR_OVERLAY_SIDE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/overlay_side.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/overlay_side_dark.png") };
			public static final ResourceLocation[] CONNECTOR_BASE_NORMAL = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/base_normal.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/base_normal_dark.png") };
			public static final ResourceLocation[] CONNECTOR_BASE_SIDE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/base_side.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "connector/base_side_dark.png") };
			
			public static final ResourceLocation[] CHARGER = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "charger/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "charger/background_dark.png") };
			public static final ResourceLocation[] CHARGER_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "charger/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "charger/overlay_dark.png") };
			public static final ResourceLocation[] CHARGER_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "charger/base.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "charger/base_dark.png") };

			public static final ResourceLocation[] GENERATOR = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "generator/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "generator/background_dark.png") };
			public static final ResourceLocation[] GENERATOR_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "generator/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "generator/overlay_dark.png") };
			public static final ResourceLocation[] GENERATOR_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "generator/base.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "generator/base_dark.png") };

			public static final ResourceLocation[] CRAFTER = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "crafter/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "crafter/background_dark.png") };
			public static final ResourceLocation[] CRAFTER_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "crafter/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "crafter/overlay_dark.png") };
			public static final ResourceLocation[] CRAFTER_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "crafter/base.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "crafter/base_dark.png") };

			public static final ResourceLocation[] SMITHING_TABLE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "smithing_table/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "smithing_table/background_dark.png") };
			public static final ResourceLocation[] SMITHING_TABLE_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "smithing_table/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "smithing_table/overlay_dark.png") };
			public static final ResourceLocation[] SMITHING_TABLE_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "smithing_table/base.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "smithing_table/base_dark.png") };

			public static final ResourceLocation[] FURNACE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "furnace/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "furnace/background_dark.png") };
			public static final ResourceLocation[] FURNACE_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "furnace/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "furnace/overlay_dark.png") };
			public static final ResourceLocation[] FURNACE_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "furnace/base.png"),  ResourceLocation.parse(ModReferences.RESOURCE.GUI + "furnace/base_dark.png") };

			public static final ResourceLocation[] ARMOUR_WORKBENCH = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "armour_workbench/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "armour_workbench/background_dark.png") };
			public static final ResourceLocation[] ARMOUR_WORKBENCH_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "armour_workbench/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "armour_workbench/overlay_dark.png") };
			public static final ResourceLocation[] ARMOUR_WORKBENCH_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "armour_workbench/base.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "armour_workbench/base_dark.png") };

			public static final ResourceLocation[] UPGRADE_STATION = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "upgrade_station/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "upgrade_station/background_dark.png") };
			public static final ResourceLocation[] UPGRADE_STATION_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "upgrade_station/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "upgrade_station/overlay_dark.png") };
			public static final ResourceLocation[] UPGRADE_STATION_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "upgrade_station/base.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "upgrade_station/base_dark.png") };
			public static final ResourceLocation UPGRADE_STATION_JEI = ResourceLocation.parse(ModReferences.RESOURCE.GUI + "upgrade_station/jei.png");

			public static final ResourceLocation[] ANVIL = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "anvil/background.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "anvil/background_dark.png") };
			public static final ResourceLocation[] ANVIL_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "anvil/overlay.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "anvil/overlay_dark.png") };
			public static final ResourceLocation[] ANVIL_BASE = new ResourceLocation[] { ResourceLocation.parse(ModReferences.RESOURCE.GUI + "anvil/base.png"), ResourceLocation.parse(ModReferences.RESOURCE.GUI + "anvil/base_dark.png") };

			public static final ResourceLocation[] ELYTRAPLATE_SETTINGS = new ResourceLocation[] { ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/background.png"), ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/background_dark.png") };
			public static final ResourceLocation[] ELYTRAPLATE_SETTINGS_OVERLAY = new ResourceLocation[] { ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/overlay.png"), ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/overlay_dark.png") };
			
			public static final ResourceLocation[] ELYTRAPLATE_ENDER_CHEST = new ResourceLocation[] { ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/background.png"), ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/background_dark.png") };
			public static final ResourceLocation[] ELYTRAPLATE_ENDER_CHEST_OVERLAY = new ResourceLocation[] { ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/overlay.png"), ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/overlay_dark.png") };
			
			public static final ResourceLocation ELYTRAPLATE_VISOR = ResourceLocation.parse(ModReferences.RESOURCE.GUI + "elytraplate/visor.png");
			
			public static final ResourceLocation[] FOCUS = new ResourceLocation[] { ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/focus/base.png"), ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/focus/base_dark.png") };
			public static final ResourceLocation[] FOCUS_SLOTS = new ResourceLocation[] { ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/focus/slots.png"), ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/gui/focus/slots_dark.png") };
			
			public static final ResourceLocation GUI_DIMENSIONAL_BUTTON = ResourceLocation.parse(ModReferences.RESOURCE.GUI + "gui_dimensional_button.png");
			public static final ResourceLocation GUI_DIMENSIONAL_BUTTON_0 = ResourceLocation.parse(ModReferences.RESOURCE.GUI + "gui_dimensional_button_0.png");
		}
	}
}