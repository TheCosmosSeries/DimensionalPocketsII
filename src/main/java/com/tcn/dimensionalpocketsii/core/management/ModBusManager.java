package com.tcn.dimensionalpocketsii.core.management;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.InputConstants;
import com.tcn.cosmoslibrary.client.entity.layer.CosmosLayerArmourColourable;
import com.tcn.cosmoslibrary.client.entity.layer.CosmosLayerElytra;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.block.CosmosBlockModelUnplaceable;
import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.item.CosmosItemEffect;
import com.tcn.cosmoslibrary.common.item.CosmosItemTool;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;
import com.tcn.cosmoslibrary.common.tab.CosmosCreativeModeTab;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyArmourItemColourable;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.dimensionalpocketsii.DimReference;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.client.colour.ColourBlockPocket;
import com.tcn.dimensionalpocketsii.client.colour.ColourBlockWall;
import com.tcn.dimensionalpocketsii.client.colour.ColourItem;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateConnector;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateEnderChest;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateSettings;
import com.tcn.dimensionalpocketsii.client.renderer.RendererDimensionalTrident;
import com.tcn.dimensionalpocketsii.client.renderer.RendererDimensionalTridentEnhanced;
import com.tcn.dimensionalpocketsii.client.screen.ScreenConfiguration;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateConnector;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateEnderChest;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateSettings;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEnhancedEntity;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEntity;
import com.tcn.dimensionalpocketsii.core.impl.IRarityPocket;
import com.tcn.dimensionalpocketsii.core.item.CoreArmourMaterial;
import com.tcn.dimensionalpocketsii.core.item.CoreItemTier;
import com.tcn.dimensionalpocketsii.core.item.DimensionalTome;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplateScreen;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplateShift;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleBattery;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleEnderChest;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleScreen;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleShifter;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleSolar;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleVisor;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEjector;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEnergyCell;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEnergyCellEnhanced;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalShifter;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalShifterEnhanced;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalAxe;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalBow;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalHoe;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalPickaxe;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalShield;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalShieldEnhanced;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalShovel;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalSword;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalTrident;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalTridentEnhanced;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerFocus;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerPocket;
import com.tcn.dimensionalpocketsii.pocket.client.renderer.ter.RendererBlockEntityModuleCreativeFluid;
import com.tcn.dimensionalpocketsii.pocket.client.renderer.ter.RendererBlockEntityModuleFluidDisplay;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenFocus;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockFocus;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallBase;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallCharger;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallConnector;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallCrafter;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallDoor;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallEdge;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallEnergyDisplay;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallFluidDisplay;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallZCreativeEnergy;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallZCreativeFluid;
import com.tcn.dimensionalpocketsii.pocket.core.block.ItemBlockFocus;
import com.tcn.dimensionalpocketsii.pocket.core.block.ItemBlockPocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityFocus;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleFluidDisplay;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityZModuleCreativeEnergy;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityZModuleCreativeFluid;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleBase;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleEnergyDisplay;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleFluidDisplay;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleFocus;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleZCreativeEnergy;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleZCreativeFluid;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusManager {
	
	private static final Rarity RARITYPOCKET = Rarity.create("Pocket", ChatFormatting.DARK_PURPLE);
	private static final Rarity RARITY_ARMOUR = Rarity.create("Armour Module", ChatFormatting.GOLD);
	private static final Rarity RARITY_POCKET = Rarity.create("Pocket Module", ChatFormatting.AQUA);
	private static final Rarity RARITY_CREATIVE = Rarity.create("Creative", ChatFormatting.LIGHT_PURPLE);
	
	public static final CosmosCreativeModeTab DIM_POCKETS_BLOCKS_GROUP = new CosmosCreativeModeTab(DimensionalPockets.MOD_ID + ".blocks", () -> new ItemStack(ModBusManager.BLOCK_POCKET));
	public static final CosmosCreativeModeTab DIM_POCKETS_ITEMS_GROUP = new CosmosCreativeModeTab(DimensionalPockets.MOD_ID + ".items", () -> new ItemStack(ModBusManager.DIMENSIONAL_INGOT));
	public static final CosmosCreativeModeTab DIM_POCKETS_TOOLS_GROUP = new CosmosCreativeModeTab(DimensionalPockets.MOD_ID + ".tools", () -> new ItemStack(ModBusManager.DIMENSIONAL_SWORD));

	public static final Item DIMENSIONAL_TOME = new DimensionalTome(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(RARITYPOCKET));
	
	public static final Item DIMENSIONAL_SHARD = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).rarity(RARITYPOCKET));
	public static final Item DIMENSIONAL_INGOT = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).rarity(RARITYPOCKET));
	public static final Item DIMENSIONAL_INGOT_ENHANCED = new CosmosItemEffect(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(Rarity.RARE).fireResistant());
	public static final Item DIMENSIONAL_GEM = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).rarity(RARITYPOCKET));
	
	public static final Item DIMENSIONAL_DUST = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).rarity(RARITYPOCKET));
	public static final Item DIMENSIONAL_PEARL = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(16).rarity(RARITYPOCKET));
	public static final Item DIMENSIONAL_THREAD = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).rarity(RARITYPOCKET));
	
	public static final Item NETHER_STAR_SHARD = new CosmosItemEffect(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(16).rarity(Rarity.RARE).fireResistant());
	public static final Item ELYTRA_WING = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(2).rarity(Rarity.RARE));
	
	public static final Item DIMENSIONAL_WRENCH = new CosmosItemTool(new Item.Properties().stacksTo(1).tab(DIM_POCKETS_TOOLS_GROUP));
	
	public static final Item DIMENSIONAL_DEVICE_BASE = new CosmosItem(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().stacksTo(16));
	public static final Item DIMENSIONAL_EJECTOR = new DimensionalEjector(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).stacksTo(4));
	
	public static final Item DIMENSIONAL_SHIFTER = new DimensionalShifter(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().stacksTo(1).rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().maxEnergyStored(5000000).maxIO(50000).maxUse(100000));
	public static final Item DIMENSIONAL_SHIFTER_ENHANCED = new DimensionalShifterEnhanced(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().stacksTo(1).rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(100000).maxUse(50000));
	
	public static final Item DIMENSIONAL_ENERGY_CELL = new DimensionalEnergyCell(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().stacksTo(1).rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(100000));
	public static final Item DIMENSIONAL_ENERGY_CELL_ENHANCED = new DimensionalEnergyCellEnhanced(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().stacksTo(1).rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(50000000).maxIO(200000));
	
	public static final Item DIMENSIONAL_SWORD = new DimensionalSword(CoreItemTier.DIMENSIONAL, 5, -1.0F, false, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY));
	public static final Item DIMENSIONAL_PICKAXE = new DimensionalPickaxe(CoreItemTier.DIMENSIONAL, 2, -2.0F, false, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY));
	public static final Item DIMENSIONAL_AXE = new DimensionalAxe(CoreItemTier.DIMENSIONAL, 7, -1.5F, false, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY));
	public static final Item DIMENSIONAL_SHOVEL = new DimensionalShovel(CoreItemTier.DIMENSIONAL,  1, -2.5F, false, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY));
	public static final Item DIMENSIONAL_HOE = new DimensionalHoe(CoreItemTier.DIMENSIONAL, 1, -2.5F, false, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY));
	
	public static final Item DIMENSIONAL_BOW = new DimensionalBow(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(10000), 64, 2, 1.5F, 1.4F);
	public static final Item DIMENSIONAL_TRIDENT = new DimensionalTrident(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY));
	public static final Item DIMENSIONAL_SHIELD = new DimensionalShield(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().stacksTo(1).rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY));
	
	public static final Item DIMENSIONAL_HELMET = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL, EquipmentSlot.HEAD, true, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(10000));
	public static final Item DIMENSIONAL_CHESTPLATE = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL, EquipmentSlot.CHEST, false, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(12000));
	public static final Item DIMENSIONAL_LEGGINGS = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL, EquipmentSlot.LEGS, false, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(10000));
	public static final Item DIMENSIONAL_BOOTS = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL, EquipmentSlot.FEET, false, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(RARITYPOCKET), new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(8000));

	public static final Item DIMENSIONAL_SWORD_ENHANCED = new DimensionalSword(CoreItemTier.DIMENSIONAL_ENHANCED, 5, 0.0F, true, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY_ENHANCED));
	public static final Item DIMENSIONAL_PICKAXE_ENHANCED = new DimensionalPickaxe(CoreItemTier.DIMENSIONAL_ENHANCED, 2, -2.0F, true, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY_ENHANCED));
	public static final Item DIMENSIONAL_AXE_ENHANCED = new DimensionalAxe(CoreItemTier.DIMENSIONAL_ENHANCED, 7, -1.5F, true, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY_ENHANCED));
	public static final Item DIMENSIONAL_SHOVEL_ENHANCED = new DimensionalShovel(CoreItemTier.DIMENSIONAL_ENHANCED, 1, -2.5F, true, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY_ENHANCED));
	public static final Item DIMENSIONAL_HOE_ENHANCED = new DimensionalHoe(CoreItemTier.DIMENSIONAL_ENHANCED, 1, -2.5F, true, new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY_ENHANCED));

	public static final Item DIMENSIONAL_BOW_ENHANCED = new DimensionalBow(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(4000000).maxIO(200000).maxUse(20000), 64, 2, 2.25F, 2.0F);
	public static final Item DIMENSIONAL_TRIDENT_ENHANCED = new DimensionalTridentEnhanced(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY_ENHANCED));
	public static final Item DIMENSIONAL_SHIELD_ENHANCED = new DimensionalShieldEnhanced(new Item.Properties().tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().stacksTo(1).rarity(Rarity.RARE), new CosmosEnergyItem.Properties().setStatsFromArray(DimReference.CONSTANT.ENERGY_ENHANCED));
	
	public static final Item DIMENSIONAL_HELMET_ENHANCED = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL_ENHANCED, EquipmentSlot.HEAD, true, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(10000));
	public static final Item DIMENSIONAL_CHESTPLATE_ENHANCED = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL_ENHANCED, EquipmentSlot.CHEST, false, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(12000));
	public static final Item DIMENSIONAL_LEGGINGS_ENHANCED = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL_ENHANCED, EquipmentSlot.LEGS, false, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(10000));
	public static final Item DIMENSIONAL_BOOTS_ENHANCED = new CosmosEnergyArmourItemColourable(CoreArmourMaterial.DIMENSIONAL_ENHANCED, EquipmentSlot.FEET, false, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).fireResistant().rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(8000));
	
	public static final Item DIMENSIONAL_ELYTRAPLATE = new DimensionalElytraplate(CoreArmourMaterial.DIMENSIONAL_ENHANCED, EquipmentSlot.CHEST, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).rarity(Rarity.RARE).fireResistant(), false, new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(200000).maxUse(6000));
	public static MenuType<ContainerElytraplateConnector> CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR;
	public static MenuType<ContainerElytraplateSettings> CONTAINER_TYPE_ELYTRAPLATE_SETTINGS;
	public static MenuType<ContainerElytraplateEnderChest> CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST;
	
	@Deprecated(forRemoval = true, since = "1.18.1-5.2.0.0")
	public static final Item DIMENSIONAL_ELYTRAPLATE_SHIFT = new DimensionalElytraplateShift(CoreArmourMaterial.DIMENSIONAL_ENHANCED, EquipmentSlot.CHEST, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).rarity(Rarity.RARE).fireResistant(), false, new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(200000).maxUse(6000));
	@Deprecated(forRemoval = true, since = "1.18.1-5.2.0.0")
	public static final Item DIMENSIONAL_ELYTRAPLATE_SCREEN = new DimensionalElytraplateScreen(CoreArmourMaterial.DIMENSIONAL_ENHANCED, EquipmentSlot.CHEST, (new Item.Properties()).tab(DIM_POCKETS_TOOLS_GROUP).rarity(Rarity.RARE).fireResistant(), false, new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(200000).maxUse(6000));
	
	public static final Item ARMOUR_MODULE_SCREEN = new ItemModuleScreen(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(RARITY_ARMOUR));
	public static final Item ARMOUR_MODULE_SHIFTER = new ItemModuleShifter(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(RARITY_ARMOUR));
	public static final Item ARMOUR_MODULE_VISOR = new ItemModuleVisor(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(RARITY_ARMOUR));
	public static final Item ARMOUR_MODULE_SOLAR = new ItemModuleSolar(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(RARITY_ARMOUR));
	public static final Item ARMOUR_MODULE_BATTERY = new ItemModuleBattery(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(RARITY_ARMOUR));
	public static final Item ARMOUR_MODULE_ENDER_CHEST = new ItemModuleEnderChest(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(1).rarity(RARITY_ARMOUR));
	
	public static final Item MODULE_BASE = new ModuleBase(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_CONNECTOR = new ModuleConnector(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_CHARGER = new ModuleCharger(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_CRAFTER = new ModuleCrafter(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_SMITHING_TABLE = new ModuleSmithingTable(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_FURNACE = new ModuleFurnace(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_BLAST_FURNACE = new ModuleBlastFurnace(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_ENERGY_DISPLAY = new ModuleEnergyDisplay(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_FLUID_DISPLAY = new ModuleFluidDisplay(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_ARMOUR_WORKBENCH = new ModuleArmourWorkbench(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_GENERATOR = new ModuleGenerator(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_UPGRADE_STATION = new ModuleUpgradeStation(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	public static final Item MODULE_FOCUS = new ModuleFocus(new Item.Properties().tab(DIM_POCKETS_ITEMS_GROUP).stacksTo(8).rarity(RARITY_POCKET));
	
	public static final Item MODULE_CREATIVE_ENERGY = new ModuleZCreativeEnergy(new Item.Properties().tab(DIM_POCKETS_BLOCKS_GROUP).stacksTo(8).rarity(RARITY_CREATIVE));
	public static final Item MODULE_CREATIVE_FLUID = new ModuleZCreativeFluid(new Item.Properties().tab(DIM_POCKETS_BLOCKS_GROUP).stacksTo(8).rarity(RARITY_CREATIVE));
	
	public static final Block BLOCK_DIMENSIONAL_ORE = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.0F, 4.0F));
	public static final Block BLOCK_DEEPSLATE_DIMENSIONAL_ORE = new CosmosBlock(Block.Properties.of(Material.STONE, MaterialColor.DEEPSLATE).requiresCorrectToolForDrops().strength(8.0F, 8.0F).sound(SoundType.DEEPSLATE));
	public static final Block BLOCK_DIMENSIONAL_ORE_NETHER = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	public static final Block BLOCK_DIMENSIONAL_ORE_END = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(8.0F, 8.0F));
	
	public static final Block BLOCK_DIMENSIONAL = new CosmosBlock(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	public static final Block BLOCK_DIMENSIONAL_METAL = new CosmosBlock(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	public static final Block BLOCK_DIMENSIONAL_GEM = new CosmosBlock(Block.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	
	public static final Block BLOCK_DIMENSIONAL_CORE = new CosmosBlockModelUnplaceable(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));

	public static final Block BLOCK_WALL = new BlockWallBase(Block.Properties.of(Material.HEAVY_METAL).strength(-1,3600000.0F).lightLevel((state) -> { return 15; }));
	public static final Block BLOCK_WALL_EDGE = new BlockWallEdge(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));

	public static final Block BLOCK_WALL_DOOR = new BlockWallDoor(Block.Properties.of(Material.METAL).strength(-1,3600000.0F).noOcclusion().lightLevel((state) -> { return 15; }));
	
	public static final Block BLOCK_POCKET = new BlockPocket(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).noOcclusion());
	public static final BlockItem BLOCK_ITEM_POCKET = new ItemBlockPocket(BLOCK_POCKET, new Item.Properties().stacksTo(1).setNoRepair().tab(DIM_POCKETS_BLOCKS_GROUP).rarity(RARITYPOCKET).fireResistant());
	public static BlockEntityType<BlockEntityPocket> BLOCK_ENTITY_TYPE_POCKET;
	public static MenuType<ContainerPocket> CONTAINER_TYPE_POCKET;
	
	public static final Block BLOCK_WALL_CONNECTOR = new BlockWallConnector(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityModuleConnector> BLOCK_ENTITY_TYPE_CONNECTOR;
	public static MenuType<ContainerModuleConnector> CONTAINER_TYPE_CONNECTOR;
	
	public static final Block BLOCK_WALL_CHARGER = new BlockWallCharger(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityModuleCharger> BLOCK_ENTITY_TYPE_CHARGER;
	public static MenuType<ContainerModuleCharger> CONTAINER_TYPE_CHARGER;
	
	public static final Block BLOCK_WALL_CRAFTER = new BlockWallCrafter(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityModuleCrafter> BLOCK_ENTITY_TYPE_CRAFTER;
	public static MenuType<ContainerModuleCrafter> CONTAINER_TYPE_CRAFTER;

	public static final Block BLOCK_WALL_SMITHING_TABLE = new BlockWallSmithingTable(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityModuleSmithingTable> BLOCK_ENTITY_TYPE_SMITHING_TABLE;
	public static MenuType<ContainerModuleSmithingTable> CONTAINER_TYPE_SMITHING_TABLE;

	public static final Block BLOCK_WALL_FURNACE = new BlockWallFurnace(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityModuleFurnace> BLOCK_ENTITY_TYPE_FURNACE;
	public static MenuType<ContainerModuleFurnace> CONTAINER_TYPE_FURNACE;

	public static final Block BLOCK_WALL_BLAST_FURNACE = new BlockWallBlastFurnace(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityModuleBlastFurnace> BLOCK_ENTITY_TYPE_BLAST_FURNACE;
	public static MenuType<ContainerModuleBlastFurnace> CONTAINER_TYPE_BLAST_FURNACE;

	public static final Block BLOCK_WALL_ENERGY_DISPLAY = new BlockWallEnergyDisplay(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks());
	
	public static final Block BLOCK_WALL_FLUID_DISPLAY = new BlockWallFluidDisplay(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks().dynamicShape().noOcclusion());
	public static BlockEntityType<BlockEntityModuleFluidDisplay> BLOCK_ENTITY_TYPE_FLUID_DISPLAY;

	public static final Block BLOCK_WALL_ARMOUR_WORKBENCH = new BlockWallArmourWorkbench(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks());
	public static BlockEntityType<BlockEntityModuleArmourWorkbench> BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH;
	public static MenuType<ContainerModuleArmourWorkbench> CONTAINER_TYPE_ARMOUR_WORKBENCH;

	public static final Block BLOCK_WALL_UPGRADE_STATION = new BlockWallUpgradeStation(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks());
	public static BlockEntityType<BlockEntityModuleUpgradeStation> BLOCK_ENTITY_TYPE_UPGRADE_STATION;
	public static MenuType<ContainerModuleUpgradeStation> CONTAINER_TYPE_UPGRADE_STATION;

	public static final Block BLOCK_WALL_GENERATOR = new BlockWallGenerator(Block.Properties.of(Material.HEAVY_METAL).strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityModuleGenerator> BLOCK_ENTITY_TYPE_GENERATOR;
	public static MenuType<ContainerModuleGenerator> CONTAINER_TYPE_GENERATOR;

	public static final Block BLOCK_FOCUS = new BlockFocus(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(-1, 3600000.0F));
	public static final BlockItem BLOCK_ITEM_FOCUS = new ItemBlockFocus(BLOCK_FOCUS, new Item.Properties().setNoRepair().tab(DIM_POCKETS_BLOCKS_GROUP).rarity(RARITYPOCKET).fireResistant());
	public static BlockEntityType<BlockEntityFocus> BLOCK_ENTITY_TYPE_FOCUS;
	public static MenuType<ContainerFocus> CONTAINER_TYPE_FOCUS;
	
	public static final Block BLOCK_WALL_CREATIVE_ENERGY = new BlockWallZCreativeEnergy(Block.Properties.of(Material.HEAVY_METAL).strength(-1,3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityZModuleCreativeEnergy> BLOCK_ENTITY_TYPE_CREATIVE_ENERGY;

	public static final Block BLOCK_WALL_CREATIVE_FLUID = new BlockWallZCreativeFluid(Block.Properties.of(Material.HEAVY_METAL).strength(-1,3600000.0F).lightLevel((state) -> { return 15; }));
	public static BlockEntityType<BlockEntityZModuleCreativeFluid> BLOCK_ENTITY_TYPE_CREATIVE_FLUID;

	public static EntityType<DimensionalTridentEntity> ENTITY_TYPE_TRIDENT;
	public static EntityType<DimensionalTridentEnhancedEntity> ENTITY_TYPE_TRIDENT_ENHANCED;

	public static KeyMapping SUIT_SCREEN;
	public static KeyMapping SUIT_SCREEN_ENDER_CHEST;
	public static KeyMapping SUIT_SHIFT;
	public static KeyMapping SUIT_SETTINGS;
	
	@SubscribeEvent
	public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		event.getRegistry().registerAll(
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_tome", DIMENSIONAL_TOME),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_shard", DIMENSIONAL_SHARD),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_ingot", DIMENSIONAL_INGOT),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_ingot_enhanced", DIMENSIONAL_INGOT_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_gem", DIMENSIONAL_GEM),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_dust", DIMENSIONAL_DUST),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_pearl", DIMENSIONAL_PEARL),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_thread", DIMENSIONAL_THREAD),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "nether_star_shard", NETHER_STAR_SHARD),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "elytra_wing", ELYTRA_WING),

			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_wrench", DIMENSIONAL_WRENCH),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_device_base", DIMENSIONAL_DEVICE_BASE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_ejector", DIMENSIONAL_EJECTOR),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_shifter", DIMENSIONAL_SHIFTER),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_shifter_enhanced", DIMENSIONAL_SHIFTER_ENHANCED),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_energy_cell", DIMENSIONAL_ENERGY_CELL),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_energy_cell_enhanced", DIMENSIONAL_ENERGY_CELL_ENHANCED),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_sword", DIMENSIONAL_SWORD),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_pickaxe", DIMENSIONAL_PICKAXE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_axe", DIMENSIONAL_AXE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_shovel", DIMENSIONAL_SHOVEL),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_hoe", DIMENSIONAL_HOE),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_bow", DIMENSIONAL_BOW),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_trident", DIMENSIONAL_TRIDENT),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_shield", DIMENSIONAL_SHIELD),

			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_sword_enhanced", DIMENSIONAL_SWORD_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_pickaxe_enhanced", DIMENSIONAL_PICKAXE_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_axe_enhanced", DIMENSIONAL_AXE_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_shovel_enhanced", DIMENSIONAL_SHOVEL_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_hoe_enhanced", DIMENSIONAL_HOE_ENHANCED),

			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_bow_enhanced", DIMENSIONAL_BOW_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_trident_enhanced", DIMENSIONAL_TRIDENT_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_shield_enhanced", DIMENSIONAL_SHIELD_ENHANCED),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_helmet", DIMENSIONAL_HELMET),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_chestplate", DIMENSIONAL_CHESTPLATE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_leggings", DIMENSIONAL_LEGGINGS),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_boots", DIMENSIONAL_BOOTS),

			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_helmet_enhanced", DIMENSIONAL_HELMET_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_chestplate_enhanced", DIMENSIONAL_CHESTPLATE_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_leggings_enhanced", DIMENSIONAL_LEGGINGS_ENHANCED),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_boots_enhanced", DIMENSIONAL_BOOTS_ENHANCED),

			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_elytraplate", DIMENSIONAL_ELYTRAPLATE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_elytraplate_shift", DIMENSIONAL_ELYTRAPLATE_SHIFT),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "dimensional_elytraplate_screen", DIMENSIONAL_ELYTRAPLATE_SCREEN),

			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "armour_module_screen", ARMOUR_MODULE_SCREEN),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "armour_module_shifter", ARMOUR_MODULE_SHIFTER),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "armour_module_visor", ARMOUR_MODULE_VISOR),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "armour_module_solar", ARMOUR_MODULE_SOLAR),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "armour_module_battery", ARMOUR_MODULE_BATTERY),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "armour_module_ender_chest", ARMOUR_MODULE_ENDER_CHEST),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_base", MODULE_BASE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_connector", MODULE_CONNECTOR),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_charger", MODULE_CHARGER),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_crafter", MODULE_CRAFTER),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_smithing_table", MODULE_SMITHING_TABLE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_furnace", MODULE_FURNACE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_blast_furnace", MODULE_BLAST_FURNACE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_energy_display", MODULE_ENERGY_DISPLAY),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_fluid_display", MODULE_FLUID_DISPLAY),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_armour_workbench", MODULE_ARMOUR_WORKBENCH),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_upgrade_station", MODULE_UPGRADE_STATION),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_generator", MODULE_GENERATOR),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_focus", MODULE_FOCUS),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_creative_energy", MODULE_CREATIVE_ENERGY),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "module_creative_fluid", MODULE_CREATIVE_FLUID)
		);
		
		//Register BlockItems
		for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
			final ResourceLocation blockRegistryName = block.getRegistryName();
			Preconditions.checkNotNull(blockRegistryName, "Registry Name of Block \"" + block + "\" of class \"" + block.getClass().getName() + "\"is null! This is not allowed!");

			if (!blockRegistryName.getNamespace().equals(DimensionalPockets.MOD_ID)) {
				continue;
			}
			
			if (block instanceof IRarityPocket) {
				final Item.Properties properties = new Item.Properties().rarity(RARITYPOCKET);
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, blockItem));
			} 
			
			else if (block instanceof IBlankCreativeTab) {
				final Item.Properties properties = new Item.Properties();
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, blockItem));
			}
			
			else if (block instanceof BlockPocket) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, BLOCK_ITEM_POCKET));
			} else if (block instanceof BlockFocus) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, BLOCK_ITEM_FOCUS));
			}
			
			else if (block instanceof CosmosBlockModelUnplaceable) {
				final Item.Properties properties = new Item.Properties().tab(DIM_POCKETS_BLOCKS_GROUP).stacksTo(1);
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, blockItem));
			}
			
			else {
				final Item.Properties properties = new Item.Properties().tab(DIM_POCKETS_BLOCKS_GROUP);
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, blockItem));
			}
		}
		
		DimensionalPockets.CONSOLE.startup("Item Registration complete.");
	}

	@SubscribeEvent
	public static void onBlockRegistry(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional_ore", BLOCK_DIMENSIONAL_ORE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_deepslate_dimensional_ore", BLOCK_DEEPSLATE_DIMENSIONAL_ORE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional_ore_nether", BLOCK_DIMENSIONAL_ORE_NETHER),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional_ore_end", BLOCK_DIMENSIONAL_ORE_END),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional", BLOCK_DIMENSIONAL),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional_metal", BLOCK_DIMENSIONAL_METAL),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional_gem", BLOCK_DIMENSIONAL_GEM),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional_core", BLOCK_DIMENSIONAL_CORE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_pocket", BLOCK_POCKET),

			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_dimensional_focus", BLOCK_FOCUS),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall", BLOCK_WALL),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_edge", BLOCK_WALL_EDGE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_door", BLOCK_WALL_DOOR),
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_connector", BLOCK_WALL_CONNECTOR),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_charger", BLOCK_WALL_CHARGER),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_crafter", BLOCK_WALL_CRAFTER),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_smithing_table", BLOCK_WALL_SMITHING_TABLE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_furnace", BLOCK_WALL_FURNACE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_blast_furnace", BLOCK_WALL_BLAST_FURNACE),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_energy_display", BLOCK_WALL_ENERGY_DISPLAY),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_fluid_display", BLOCK_WALL_FLUID_DISPLAY),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_armour_workbench", BLOCK_WALL_ARMOUR_WORKBENCH),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_upgrade_station", BLOCK_WALL_UPGRADE_STATION),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_generator", BLOCK_WALL_GENERATOR),
			
			//setupString(DimensionalPockets.MOD_ID, "block_conduit_energy", BLOCK_CONDUIT_ENERGY)
			
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_creative_energy", BLOCK_WALL_CREATIVE_ENERGY),
			CosmosRuntimeHelper.setupString(DimensionalPockets.MOD_ID, "block_wall_creative_fluid", BLOCK_WALL_CREATIVE_FLUID)
		);
		
		DimensionalPockets.CONSOLE.startup("Block Registration complete.");
	}
	
	@SubscribeEvent
	public static void onMenuTypeRegistry(final RegistryEvent.Register<MenuType<?>> event) {
		CONTAINER_TYPE_POCKET = IForgeMenuType.create(ContainerPocket::createContainerClientSide); CONTAINER_TYPE_POCKET.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_pocket"));
		CONTAINER_TYPE_CONNECTOR = IForgeMenuType.create(ContainerModuleConnector::createContainerClientSide); CONTAINER_TYPE_CONNECTOR.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_connector"));
		CONTAINER_TYPE_CHARGER = IForgeMenuType.create(ContainerModuleCharger::new); CONTAINER_TYPE_CHARGER.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_charger"));
		
		CONTAINER_TYPE_CRAFTER = IForgeMenuType.create(ContainerModuleCrafter::new); CONTAINER_TYPE_CRAFTER.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_crafter"));
		CONTAINER_TYPE_SMITHING_TABLE = IForgeMenuType.create(ContainerModuleSmithingTable::new); CONTAINER_TYPE_SMITHING_TABLE.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_smithing_table"));
		CONTAINER_TYPE_FURNACE = IForgeMenuType.create(ContainerModuleFurnace::new); CONTAINER_TYPE_FURNACE.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_furnace"));
		CONTAINER_TYPE_BLAST_FURNACE = IForgeMenuType.create(ContainerModuleBlastFurnace::new); CONTAINER_TYPE_BLAST_FURNACE.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_blast_furnace"));
		
		CONTAINER_TYPE_ARMOUR_WORKBENCH = IForgeMenuType.create(ContainerModuleArmourWorkbench::new); CONTAINER_TYPE_ARMOUR_WORKBENCH.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_armour_workbench"));
		CONTAINER_TYPE_UPGRADE_STATION = IForgeMenuType.create(ContainerModuleUpgradeStation::new); CONTAINER_TYPE_UPGRADE_STATION.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_upgrade_station"));
		CONTAINER_TYPE_GENERATOR = IForgeMenuType.create(ContainerModuleGenerator::new); CONTAINER_TYPE_GENERATOR.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_generator"));
		CONTAINER_TYPE_FOCUS = IForgeMenuType.create(ContainerFocus::new); CONTAINER_TYPE_FOCUS.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_focus"));
		
		CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR = IForgeMenuType.create(ContainerElytraplateConnector::createContainerClientSide); CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_elytraplate"));
		CONTAINER_TYPE_ELYTRAPLATE_SETTINGS = IForgeMenuType.create(ContainerElytraplateSettings::createContainerClientSide); CONTAINER_TYPE_ELYTRAPLATE_SETTINGS.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_elytraplate_settings"));
		CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST = IForgeMenuType.create(ContainerElytraplateEnderChest::createContainerClientSide); CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST.setRegistryName(new ResourceLocation(DimensionalPockets.MOD_ID, "container_elytraplate_ender_chest"));
		
		event.getRegistry().registerAll(
			CONTAINER_TYPE_POCKET, CONTAINER_TYPE_CONNECTOR, CONTAINER_TYPE_CHARGER, 
			
			CONTAINER_TYPE_CRAFTER, CONTAINER_TYPE_SMITHING_TABLE, CONTAINER_TYPE_FURNACE, CONTAINER_TYPE_BLAST_FURNACE, 
			
			CONTAINER_TYPE_ARMOUR_WORKBENCH, CONTAINER_TYPE_UPGRADE_STATION, CONTAINER_TYPE_GENERATOR, CONTAINER_TYPE_FOCUS,
			
			CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR, CONTAINER_TYPE_ELYTRAPLATE_SETTINGS, CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST
		);
		
		DimensionalPockets.CONSOLE.startup("MenuType<> Registration complete.");
	}
	
	@SubscribeEvent
	public static void onBlockEntityTypeRegistry(final RegistryEvent.Register<BlockEntityType<?>> event) {	
		BLOCK_ENTITY_TYPE_POCKET = BlockEntityType.Builder.<BlockEntityPocket>of(BlockEntityPocket::new, BLOCK_POCKET).build(null); BLOCK_ENTITY_TYPE_POCKET.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_pocket");
		BLOCK_ENTITY_TYPE_CONNECTOR = BlockEntityType.Builder.<BlockEntityModuleConnector>of(BlockEntityModuleConnector::new, BLOCK_WALL_CONNECTOR).build(null); BLOCK_ENTITY_TYPE_CONNECTOR.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_connector");
		
		BLOCK_ENTITY_TYPE_CHARGER = BlockEntityType.Builder.<BlockEntityModuleCharger>of(BlockEntityModuleCharger::new, BLOCK_WALL_CHARGER).build(null); BLOCK_ENTITY_TYPE_CHARGER.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_charger");
		BLOCK_ENTITY_TYPE_CRAFTER = BlockEntityType.Builder.<BlockEntityModuleCrafter>of(BlockEntityModuleCrafter::new, BLOCK_WALL_CRAFTER).build(null); BLOCK_ENTITY_TYPE_CRAFTER.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_crafter");
		BLOCK_ENTITY_TYPE_SMITHING_TABLE = BlockEntityType.Builder.<BlockEntityModuleSmithingTable>of(BlockEntityModuleSmithingTable::new, BLOCK_WALL_SMITHING_TABLE).build(null); BLOCK_ENTITY_TYPE_SMITHING_TABLE.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_smithing_table");
		BLOCK_ENTITY_TYPE_FURNACE = BlockEntityType.Builder.<BlockEntityModuleFurnace>of(BlockEntityModuleFurnace::new, BLOCK_WALL_FURNACE).build(null); BLOCK_ENTITY_TYPE_FURNACE.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_furnace");
		BLOCK_ENTITY_TYPE_BLAST_FURNACE = BlockEntityType.Builder.<BlockEntityModuleBlastFurnace>of(BlockEntityModuleBlastFurnace::new, BLOCK_WALL_BLAST_FURNACE).build(null); BLOCK_ENTITY_TYPE_BLAST_FURNACE.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_blast_furnace");
		
		BLOCK_ENTITY_TYPE_FLUID_DISPLAY = BlockEntityType.Builder.<BlockEntityModuleFluidDisplay>of(BlockEntityModuleFluidDisplay::new, BLOCK_WALL_FLUID_DISPLAY).build(null); BLOCK_ENTITY_TYPE_FLUID_DISPLAY.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_fluid_displau");
		BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH = BlockEntityType.Builder.<BlockEntityModuleArmourWorkbench>of(BlockEntityModuleArmourWorkbench::new, BLOCK_WALL_ARMOUR_WORKBENCH).build(null); BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_armour_workbench");
		BLOCK_ENTITY_TYPE_UPGRADE_STATION = BlockEntityType.Builder.<BlockEntityModuleUpgradeStation>of(BlockEntityModuleUpgradeStation::new, BLOCK_WALL_UPGRADE_STATION).build(null); BLOCK_ENTITY_TYPE_UPGRADE_STATION.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_upgrade_station");
		BLOCK_ENTITY_TYPE_GENERATOR = BlockEntityType.Builder.<BlockEntityModuleGenerator>of(BlockEntityModuleGenerator::new, BLOCK_WALL_GENERATOR).build(null); BLOCK_ENTITY_TYPE_GENERATOR.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_generator");
		BLOCK_ENTITY_TYPE_FOCUS = BlockEntityType.Builder.<BlockEntityFocus>of(BlockEntityFocus::new, BLOCK_FOCUS).build(null); BLOCK_ENTITY_TYPE_FOCUS.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_focus");
		
		BLOCK_ENTITY_TYPE_CREATIVE_ENERGY = BlockEntityType.Builder.<BlockEntityZModuleCreativeEnergy>of(BlockEntityZModuleCreativeEnergy::new, BLOCK_WALL_CREATIVE_ENERGY).build(null); BLOCK_ENTITY_TYPE_CREATIVE_ENERGY.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_creative_energy");
		BLOCK_ENTITY_TYPE_CREATIVE_FLUID = BlockEntityType.Builder.<BlockEntityZModuleCreativeFluid>of(BlockEntityZModuleCreativeFluid::new, BLOCK_WALL_CREATIVE_FLUID).build(null); BLOCK_ENTITY_TYPE_CREATIVE_FLUID.setRegistryName(DimensionalPockets.MOD_ID, "tile_entity_creative_fluid");
		
		event.getRegistry().registerAll(
			BLOCK_ENTITY_TYPE_POCKET, 
			BLOCK_ENTITY_TYPE_CONNECTOR, BLOCK_ENTITY_TYPE_CHARGER, BLOCK_ENTITY_TYPE_CRAFTER, BLOCK_ENTITY_TYPE_SMITHING_TABLE, BLOCK_ENTITY_TYPE_FURNACE, BLOCK_ENTITY_TYPE_BLAST_FURNACE,
			BLOCK_ENTITY_TYPE_FLUID_DISPLAY, BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH, BLOCK_ENTITY_TYPE_UPGRADE_STATION, BLOCK_ENTITY_TYPE_GENERATOR, BLOCK_ENTITY_TYPE_FOCUS, 
			
			BLOCK_ENTITY_TYPE_CREATIVE_ENERGY, BLOCK_ENTITY_TYPE_CREATIVE_FLUID
		);
		
		DimensionalPockets.CONSOLE.startup("BlockEntityType<> Registration complete.");
	}

	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_FLUID_DISPLAY, RendererBlockEntityModuleFluidDisplay::new);
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_CREATIVE_FLUID, RendererBlockEntityModuleCreativeFluid::new);
		
		DimensionalPockets.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}
	
	@SubscribeEvent
	public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> event) {
		ENTITY_TYPE_TRIDENT = EntityType.Builder.<DimensionalTridentEntity>of(DimensionalTridentEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("dimensional_trident_type");
		ENTITY_TYPE_TRIDENT.setRegistryName(DimensionalPockets.MOD_ID, "dimensional_trident_type");
		
		ENTITY_TYPE_TRIDENT_ENHANCED = EntityType.Builder.<DimensionalTridentEnhancedEntity>of(DimensionalTridentEnhancedEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("dimensional_trident_enhanced_type");
		ENTITY_TYPE_TRIDENT_ENHANCED.setRegistryName(DimensionalPockets.MOD_ID, "dimensional_trident_enhanced_type");
		
		event.getRegistry().registerAll(ENTITY_TYPE_TRIDENT, ENTITY_TYPE_TRIDENT_ENHANCED);
		
		DimensionalPockets.CONSOLE.startup("EntityType<> Registration complete.");
	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onEntityRenderersAddLayersEvent(final EntityRenderersEvent.AddLayers event) {
		EntityModelSet modelSet = event.getEntityModels();
		Minecraft mc = Minecraft.getInstance();
		
		mc.getEntityRenderDispatcher().renderers.forEach((entityType, entityRenderer) -> {
			if (entityRenderer instanceof LivingEntityRenderer<?, ?> && entityType != EntityType.VEX) {
				ResourceLocation type = entityType.getRegistryName();
				
				LivingEntityRenderer<LivingEntity, ?> renderer = (LivingEntityRenderer<LivingEntity, ?>) entityRenderer;
				
				if (renderer.getModel() instanceof HumanoidModel<?>) {
					LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>> humanRenderer = (LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>>) renderer;
					
					ModelLayerLocation innerArmour = ModelLayers.PLAYER_INNER_ARMOR;
					ModelLayerLocation outerArmour = ModelLayers.PLAYER_OUTER_ARMOR;
					
					List<ModelLayerLocation> locations = ModelLayers.getKnownLocations().toList();
					
					for (int i = 0; i < locations.size(); i++) {
						ModelLayerLocation location = locations.get(i);
						
						if (location.getModel().equals(type)) {
							if (location.getLayer().equals("inner_armor")) {
								innerArmour = location;
							}
							
							if (location.getLayer().equals("outer_armor")) {
								outerArmour = location;
							}
						}
					}
					
					if (innerArmour != null && outerArmour != null) {
						humanRenderer.addLayer(new CosmosLayerArmourColourable<>(humanRenderer, new HumanoidModel<>(modelSet.bakeLayer(innerArmour)), new HumanoidModel<>(modelSet.bakeLayer(outerArmour))));
						DimensionalPockets.CONSOLE.debug("LivingEntityRenderer for: { " + entityType.getRegistryName() + " } Dimensional Armour Layer added.");
					}
					
					humanRenderer.addLayer(new CosmosLayerElytra<>(humanRenderer, modelSet, new ResourceLocation(DimensionalPockets.MOD_ID, "textures/entity/dimensional_elytra_base.png")));
					DimensionalPockets.CONSOLE.debug("LivingEntityRenderer for: { " + entityType.getRegistryName() + " } Elytra Layer added.");
				}
			}
		});
		
		LivingEntityRenderer<Player, PlayerModel<Player>> playerRendererAlt = event.getSkin("default");
		LivingEntityRenderer<Player, PlayerModel<Player>> playerRendererSlim = event.getSkin("slim");

		if (playerRendererAlt != null) {
			playerRendererAlt.addLayer(new CosmosLayerElytra<>(playerRendererAlt, modelSet, new ResourceLocation(DimensionalPockets.MOD_ID, "textures/entity/dimensional_elytra_base.png")));
			playerRendererAlt.addLayer(new CosmosLayerArmourColourable<>(playerRendererAlt, new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
			DimensionalPockets.CONSOLE.debug("Player Renderer {default} Custom Layers added.");
		} else {
			DimensionalPockets.CONSOLE.fatal("Player Renderer {default} <null>!! Report this issue to the Mod Author");
		}
		
		if (playerRendererSlim != null) {
			playerRendererSlim.addLayer(new CosmosLayerElytra<>(playerRendererSlim, modelSet, new ResourceLocation(DimensionalPockets.MOD_ID, "textures/entity/dimensional_elytra_base.png")));
			playerRendererSlim.addLayer(new CosmosLayerArmourColourable<>(playerRendererSlim, new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR))));
			DimensionalPockets.CONSOLE.debug("Player Renderer {slim} Custom Layers added.");
		} else {
			DimensionalPockets.CONSOLE.fatal("Player Renderer {slim} <null>!! Report this issue to the Mod Author");
		}
		
		DimensionalPockets.CONSOLE.startup("EntityRenderer Layer Registration complete.");
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onModelRegistryEvent(ModelRegistryEvent event) {
		CosmosRuntimeHelper.registerSpecialModels(DimensionalPockets.MOD_ID, 
			"item/dimensional_elytraplate_base", 
			"item/dimensional_elytraplate_shifter",
			"item/dimensional_elytraplate_connect",
			"item/dimensional_elytraplate_visor",
			"item/dimensional_elytraplate_solar",
			"item/dimensional_elytraplate_battery"
		);
		
		DimensionalPockets.CONSOLE.startup("Model Registration complete..");
	}
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onTextureStitchEventPre(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS) {
			event.addSprite(DimReference.RESOURCE.SHIELD);
			event.addSprite(DimReference.RESOURCE.SHIELD_NO_PATTERN);

			event.addSprite(DimReference.RESOURCE.SHIELD_ENHANCED);
			event.addSprite(DimReference.RESOURCE.SHIELD_ENHANCED_NO_PATTERN);
			
			DimensionalPockets.CONSOLE.startup("Texture Stitch Pre complete..");
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(ModLoadingContext context) {
		context.registerExtensionPoint(ConfigGuiFactory.class, () -> ScreenConfiguration.getInstance());
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(CONTAINER_TYPE_POCKET, ScreenPocket::new);
		MenuScreens.register(CONTAINER_TYPE_CONNECTOR, ScreenModuleConnector::new);
		MenuScreens.register(CONTAINER_TYPE_CHARGER, ScreenModuleCharger::new);
		MenuScreens.register(CONTAINER_TYPE_CRAFTER, ScreenModuleCrafter::new);
		MenuScreens.register(CONTAINER_TYPE_SMITHING_TABLE, ScreenModuleSmithingTable::new);
		MenuScreens.register(CONTAINER_TYPE_FURNACE, ScreenModuleFurnace::new);
		MenuScreens.register(CONTAINER_TYPE_BLAST_FURNACE, ScreenModuleBlastFurnace::new);
		MenuScreens.register(CONTAINER_TYPE_ARMOUR_WORKBENCH, ScreenModuleArmourWorkbench::new);
		MenuScreens.register(CONTAINER_TYPE_UPGRADE_STATION, ScreenModuleUpgradeStation::new);
		MenuScreens.register(CONTAINER_TYPE_GENERATOR, ScreenModuleGenerator::new);
		MenuScreens.register(CONTAINER_TYPE_FOCUS, ScreenFocus::new);
		
		MenuScreens.register(CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR, ScreenElytraplateConnector::new);
		MenuScreens.register(CONTAINER_TYPE_ELYTRAPLATE_SETTINGS, ScreenElytraplateSettings::new);
		MenuScreens.register(CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST, ScreenElytraplateEnderChest::new);
		
		SUIT_SCREEN = new KeyMapping("dimensionalpocketsii.keybind.suit_screen", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_BACKSLASH, "dimensionalpocketsii.keybind.category");
		SUIT_SCREEN_ENDER_CHEST = new KeyMapping("dimensionalpocketsii.keybind.suit_ender_chest", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_WORLD_1, "dimensionalpocketsii.keybind.category");
		SUIT_SHIFT = new KeyMapping("dimensionalpocketsii.keybind.suit_shift", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "dimensionalpocketsii.keybind.category");
		SUIT_SETTINGS = new KeyMapping("dimensionalpocketsii.keybind.suit_mode_change", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_APOSTROPHE, "dimensionalpocketsii.keybind.category");
		
		CosmosRuntimeHelper.registerKeyBindings(SUIT_SCREEN, SUIT_SCREEN_ENDER_CHEST, SUIT_SHIFT, SUIT_SETTINGS);
		
		CosmosRuntimeHelper.setRenderLayers(RenderType.cutoutMipped(),
			BLOCK_POCKET,
			
			BLOCK_WALL_CHARGER, BLOCK_WALL_CONNECTOR, BLOCK_WALL_CRAFTER, BLOCK_WALL_SMITHING_TABLE, BLOCK_WALL_FURNACE, BLOCK_WALL_BLAST_FURNACE, 
			BLOCK_WALL_ENERGY_DISPLAY, BLOCK_WALL_FLUID_DISPLAY, BLOCK_WALL_ARMOUR_WORKBENCH, BLOCK_WALL_UPGRADE_STATION, BLOCK_WALL_GENERATOR,
			BLOCK_DIMENSIONAL_CORE, BLOCK_FOCUS,
			
			BLOCK_WALL_CREATIVE_ENERGY, BLOCK_WALL_CREATIVE_FLUID
		);
		
		CosmosRuntimeHelper.registerBlockColours(new ColourBlockPocket(), BLOCK_POCKET);
		
		CosmosRuntimeHelper.registerBlockColours(new ColourBlockWall(), 
			BLOCK_WALL, BLOCK_WALL_EDGE, BLOCK_WALL_DOOR,
			
			BLOCK_WALL_CONNECTOR, BLOCK_WALL_CHARGER, BLOCK_WALL_CRAFTER, BLOCK_WALL_SMITHING_TABLE, BLOCK_FOCUS,
			BLOCK_WALL_FURNACE, BLOCK_WALL_BLAST_FURNACE, BLOCK_WALL_ENERGY_DISPLAY, BLOCK_WALL_FLUID_DISPLAY, BLOCK_WALL_ARMOUR_WORKBENCH, BLOCK_WALL_UPGRADE_STATION, BLOCK_WALL_GENERATOR
		);
		
		CosmosRuntimeHelper.registerItemColours(new ColourItem(),
			BLOCK_ITEM_POCKET, BLOCK_DIMENSIONAL_CORE, BLOCK_WALL, BLOCK_WALL_EDGE,
			
			DIMENSIONAL_DEVICE_BASE, DIMENSIONAL_SHIFTER, DIMENSIONAL_SHIFTER_ENHANCED, DIMENSIONAL_EJECTOR,
			DIMENSIONAL_ENERGY_CELL, DIMENSIONAL_ENERGY_CELL_ENHANCED,
			
			DIMENSIONAL_HELMET, DIMENSIONAL_CHESTPLATE, DIMENSIONAL_LEGGINGS, DIMENSIONAL_BOOTS,
			DIMENSIONAL_HELMET_ENHANCED, DIMENSIONAL_CHESTPLATE_ENHANCED, DIMENSIONAL_LEGGINGS_ENHANCED, DIMENSIONAL_BOOTS_ENHANCED,
			
			DIMENSIONAL_ELYTRAPLATE,
			
			BLOCK_WALL_CONNECTOR, BLOCK_WALL_CHARGER, BLOCK_WALL_CRAFTER, BLOCK_WALL_SMITHING_TABLE, BLOCK_FOCUS,
			BLOCK_WALL_FURNACE, BLOCK_WALL_BLAST_FURNACE, BLOCK_WALL_ENERGY_DISPLAY, BLOCK_WALL_FLUID_DISPLAY, BLOCK_WALL_ARMOUR_WORKBENCH, BLOCK_WALL_UPGRADE_STATION, BLOCK_WALL_GENERATOR
		);

		ItemProperties.register(DIMENSIONAL_BOW, new ResourceLocation("pull"), (stackIn, clientWorldIn, livingEntityIn, o) -> { if (livingEntityIn == null) { return 0.0F; } else { return livingEntityIn.getUseItem() != stackIn ? 0.0F : (float) (stackIn.getUseDuration() - livingEntityIn.getUseItemRemainingTicks()) / 20.0F; }});
		ItemProperties.register(DIMENSIONAL_BOW, new ResourceLocation("pulling"), (stackIn, clientWorldIn, livingEntityIn, o) -> { return livingEntityIn != null && livingEntityIn.isUsingItem() && livingEntityIn.getUseItem() == stackIn ? 1.0F : 0.0F; });
		
		ItemProperties.register(DIMENSIONAL_BOW_ENHANCED, new ResourceLocation("pull"), (stackIn, clientWorldIn, livingEntityIn, o) -> { if (livingEntityIn == null) { return 0.0F; } else { return livingEntityIn.getUseItem() != stackIn ? 0.0F : (float) (stackIn.getUseDuration() - livingEntityIn.getUseItemRemainingTicks()) / 20.0F; }});
		ItemProperties.register(DIMENSIONAL_BOW_ENHANCED, new ResourceLocation("pulling"), (stackIn, clientWorldIn, livingEntityIn, o) -> { return livingEntityIn != null && livingEntityIn.isUsingItem() && livingEntityIn.getUseItem() == stackIn ? 1.0F : 0.0F; });
		
		ItemProperties.register(DIMENSIONAL_TRIDENT, new ResourceLocation("throwing"), (stackIn, clientWorldIn, livingEntityIn, o) -> { return livingEntityIn != null && livingEntityIn.isUsingItem() && livingEntityIn.getUseItem() == stackIn ? 1.0F : 0.0F; });
		ItemProperties.register(DIMENSIONAL_TRIDENT_ENHANCED, new ResourceLocation("throwing"), (stackIn, clientWorldIn, livingEntityIn, o) -> { return livingEntityIn != null && livingEntityIn.isUsingItem() && livingEntityIn.getUseItem() == stackIn ? 1.0F : 0.0F; });
		
		ItemProperties.register(DIMENSIONAL_SHIELD, new ResourceLocation("blocking"), (stackIn, clientWorldIn, livingEntityIn, o) -> { return livingEntityIn != null && livingEntityIn.isUsingItem() && livingEntityIn.getUseItem() == stackIn ? 1.0F : 0.0F; });
		ItemProperties.register(DIMENSIONAL_SHIELD_ENHANCED, new ResourceLocation("blocking"), (stackIn, clientWorldIn, livingEntityIn, o) -> { return livingEntityIn != null && livingEntityIn.isUsingItem() && livingEntityIn.getUseItem() == stackIn ? 1.0F : 0.0F; });
		
		EntityRenderers.register(ENTITY_TYPE_TRIDENT, RendererDimensionalTrident::new);
		EntityRenderers.register(ENTITY_TYPE_TRIDENT_ENHANCED, RendererDimensionalTridentEnhanced::new);
	}
}