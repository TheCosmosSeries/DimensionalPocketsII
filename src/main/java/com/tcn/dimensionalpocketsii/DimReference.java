package com.tcn.dimensionalpocketsii;

import java.util.ArrayList;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.network.chat.BaseComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DimReference {
	
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
	
	public static class CONFIG_DEFAULTS {
		public static final ArrayList<String> BLOCKED_BLOCKS = new ArrayList<String>() { 
			private static final long serialVersionUID = 5L;
			
			{
				add("lucky:lucky_block");
				add("chancecubes:chance_cube");
				add("chancecubes:chance_icosahedron");
				add("chancecubes:giant_chance_cube");
				add("chancecubes:cube_dispenser");
				add("xreliquary:wraith_node");
				//add("");
			};
		};
		
		public static final ArrayList<String> BLOCKED_ITEMS = new ArrayList<String>() {
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
		
		public static final ArrayList<String> BLOCKED_COMMANDS = new ArrayList<String>() {
			private static final long serialVersionUID = 5L;
			
			{
				add("teleport");
				add("tp");
				add("kill");
				add("setblock");
				//add("");
			};
		};
	}
	
	public static class MESSAGES {
		public static final BaseComponent WELCOME = (BaseComponent) ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.welcome_one")
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

		public static final String BLOCKS = PRE + "blocks/";
		public static final String ITEMS = RESOURCE + "items/";
		
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
		
		public static final ResourceLocation SHIELD = new ResourceLocation(DimensionalPockets.MOD_ID, "entity/dimensional_shield");
		public static final ResourceLocation SHIELD_NO_PATTERN =  new ResourceLocation(DimensionalPockets.MOD_ID, "entity/dimensional_shield_nopattern");

		public static final ResourceLocation SHIELD_ENHANCED = new ResourceLocation(DimensionalPockets.MOD_ID, "entity/dimensional_shield_enhanced");
		public static final ResourceLocation SHIELD_ENHANCED_NO_PATTERN =  new ResourceLocation(DimensionalPockets.MOD_ID, "entity/dimensional_shield_enhanced_nopattern");
	}
	
	public static class INTEGRATION {
		public static class JEI {
			public static final ResourceLocation UPGRADE_UID = new ResourceLocation(DimensionalPockets.MOD_ID, "upgrade_category");
		}
	}
	
	public static class GUI {
		public static class RESOURCE {
			
			public static final ResourceLocation[] POCKET = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/background_normal.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/background_normal_dark.png") };
			public static final ResourceLocation[] POCKET_SIDE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/background_side.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/background_side_dark.png") };
			public static final ResourceLocation[] POCKET_OVERLAY_NORMAL = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/overlay_normal.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/overlay_normal_dark.png") };
			public static final ResourceLocation[] POCKET_OVERLAY_SIDE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/overlay_side.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/overlay_side_dark.png") };
			public static final ResourceLocation[] POCKET_BASE_NORMAL = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/base_normal.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/base_normal_dark.png") };
			public static final ResourceLocation[] POCKET_BASE_SIDE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/base_side.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "pocket/base_side_dark.png") };
			
			public static final ResourceLocation[] CONNECTOR = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "connector/background_normal.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "connector/background_normal_dark.png") };
			public static final ResourceLocation[] CONNECTOR_SIDE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "connector/background_side.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "connector/background_side_dark.png") };
			public static final ResourceLocation[] CONNECTOR_OVERLAY_NORMAL = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "connector/overlay_normal.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "connector/overlay_normal_dark.png") };
			public static final ResourceLocation[] CONNECTOR_OVERLAY_SIDE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "connector/overlay_side.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "connector/overlay_side_dark.png") };
			public static final ResourceLocation[] CONNECTOR_BASE_NORMAL = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "connector/base_normal.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "connector/base_normal_dark.png") };
			public static final ResourceLocation[] CONNECTOR_BASE_SIDE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "connector/base_side.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "connector/base_side_dark.png") };
			
			public static final ResourceLocation[] CHARGER = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "charger/background.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "charger/background_dark.png") };
			public static final ResourceLocation[] CHARGER_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "charger/overlay.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "charger/overlay_dark.png") };
			public static final ResourceLocation[] CHARGER_BASE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "charger/base.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "charger/base_dark.png") };

			public static final ResourceLocation[] GENERATOR = new ResourceLocation[] { new ResourceLocation (DimReference.RESOURCE.GUI + "generator/background.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "generator/background_dark.png") };
			public static final ResourceLocation[] GENERATOR_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "generator/overlay.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "generator/overlay_dark.png") };
			public static final ResourceLocation[] GENERATOR_BASE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "generator/base.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "generator/base_dark.png") };

			public static final ResourceLocation[] CRAFTER = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "crafter/background.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "crafter/background_dark.png") };
			public static final ResourceLocation[] CRAFTER_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "crafter/overlay.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "crafter/overlay_dark.png") };
			public static final ResourceLocation[] CRAFTER_BASE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "crafter/base.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "crafter/base_dark.png") };

			public static final ResourceLocation[] SMITHING_TABLE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "smithing_table/background.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "smithing_table/background_dark.png") };
			public static final ResourceLocation[] SMITHING_TABLE_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "smithing_table/overlay.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "smithing_table/overlay_dark.png") };
			public static final ResourceLocation[] SMITHING_TABLE_BASE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "smithing_table/base.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "smithing_table/base_dark.png") };

			public static final ResourceLocation[] FURNACE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "furnace/background.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "furnace/background_dark.png") };
			public static final ResourceLocation[] FURNACE_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "furnace/overlay.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "furnace/overlay_dark.png") };
			public static final ResourceLocation[] FURNACE_BASE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "furnace/base.png"),  new ResourceLocation(DimReference.RESOURCE.GUI + "furnace/base_dark.png") };

			public static final ResourceLocation[] ARMOUR_WORKBENCH = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "armour_workbench/background.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "armour_workbench/background_dark.png") };
			public static final ResourceLocation[] ARMOUR_WORKBENCH_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "armour_workbench/overlay.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "armour_workbench/overlay_dark.png") };
			public static final ResourceLocation[] ARMOUR_WORKBENCH_BASE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "armour_workbench/base.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "armour_workbench/base_dark.png") };

			public static final ResourceLocation[] UPGRADE_STATION = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "upgrade_station/background.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "upgrade_station/background_dark.png") };
			public static final ResourceLocation[] UPGRADE_STATION_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "upgrade_station/overlay.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "upgrade_station/overlay_dark.png") };
			public static final ResourceLocation[] UPGRADE_STATION_BASE = new ResourceLocation[] { new ResourceLocation(DimReference.RESOURCE.GUI + "upgrade_station/base.png"), new ResourceLocation(DimReference.RESOURCE.GUI + "upgrade_station/base_dark.png") };
			public static final ResourceLocation UPGRADE_STATION_JEI = new ResourceLocation(DimReference.RESOURCE.GUI + "upgrade_station/jei.png");
			
			public static final ResourceLocation[] ELYTRAPLATE_SETTINGS = new ResourceLocation[] { new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/background.png"), new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/background_dark.png") };
			public static final ResourceLocation[] ELYTRAPLATE_SETTINGS_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/overlay.png"), new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/settings/overlay_dark.png") };
			
			public static final ResourceLocation[] ELYTRAPLATE_ENDER_CHEST = new ResourceLocation[] { new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/background.png"), new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/background_dark.png") };
			public static final ResourceLocation[] ELYTRAPLATE_ENDER_CHEST_OVERLAY = new ResourceLocation[] { new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/overlay.png"), new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/elytraplate/ender_chest/overlay_dark.png") };
			
			public static final ResourceLocation ELYTRAPLATE_VISOR = new ResourceLocation(DimReference.RESOURCE.GUI + "elytraplate/visor.png");
			
			public static final ResourceLocation[] FOCUS = new ResourceLocation[] { new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/focus/base.png"), new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/focus/base_dark.png") };
			public static final ResourceLocation[] FOCUS_SLOTS = new ResourceLocation[] { new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/focus/slots.png"), new ResourceLocation(DimensionalPockets.MOD_ID, "textures/gui/focus/slots_dark.png") };
			
			public static final ResourceLocation GUI_DIMENSIONAL_BUTTON = new ResourceLocation(DimReference.RESOURCE.GUI + "gui_dimensional_button.png");
			public static final ResourceLocation GUI_DIMENSIONAL_BUTTON_0 = new ResourceLocation(DimReference.RESOURCE.GUI + "gui_dimensional_button_0.png");
		}
	}
}