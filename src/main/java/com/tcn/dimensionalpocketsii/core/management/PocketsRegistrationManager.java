package com.tcn.dimensionalpocketsii.core.management;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.tcn.cosmoslibrary.client.entity.layer.CosmosLayerArmourColourable;
import com.tcn.cosmoslibrary.client.entity.layer.CosmosLayerElytra;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.block.CosmosBlockModelUnplaceable;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.item.CosmosItemEffect;
import com.tcn.cosmoslibrary.common.item.CosmosItemTool;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyArmourItemColourable;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyShieldItem;
import com.tcn.cosmoslibrary.runtime.common.CosmosRuntime;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.client.colour.ColourBlockPocket;
import com.tcn.dimensionalpocketsii.client.colour.ColourBlockWall;
import com.tcn.dimensionalpocketsii.client.colour.ColourItem;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateConnector;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateEnderChest;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateSettings;
import com.tcn.dimensionalpocketsii.client.renderer.RendererDimensionalTrident;
import com.tcn.dimensionalpocketsii.client.renderer.RendererDimensionalTridentEnhanced;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateConnector;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateEnderChest;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateSettings;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateVisor;
import com.tcn.dimensionalpocketsii.core.advancement.UseShifterTrigger;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEnhancedEntity;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEntity;
import com.tcn.dimensionalpocketsii.core.item.CoreItemRarity;
import com.tcn.dimensionalpocketsii.core.item.CoreItemTier;
import com.tcn.dimensionalpocketsii.core.item.DimensionalTome;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleBattery;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleEnderChest;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleFirework;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleScreen;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleShifter;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleSolar;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemModuleVisor;
import com.tcn.dimensionalpocketsii.core.item.armour.module.ItemSmithingUpgrade;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEjector;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEnergyCell;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalEnergyCellEnhanced;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalShifter;
import com.tcn.dimensionalpocketsii.core.item.device.DimensionalShifterEnhanced;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalAxe;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalBow;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalHoe;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalPickaxe;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalShovel;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalSword;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalTrident;
import com.tcn.dimensionalpocketsii.core.item.tool.DimensionalTridentEnhanced;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerFocus;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleAnvil;
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
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerPocketEnhanced;
import com.tcn.dimensionalpocketsii.pocket.client.renderer.be.RendererBlockEntityModuleCreativeFluid;
import com.tcn.dimensionalpocketsii.pocket.client.renderer.be.RendererBlockEntityModuleFluidDisplay;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenFocus;
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenModuleAnvil;
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
import com.tcn.dimensionalpocketsii.pocket.client.screen.ScreenPocketEnhanced;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockFocus;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockPocketEnhanced;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallAnvil;
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
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallGlass;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallZCreativeEnergy;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallZCreativeFluid;
import com.tcn.dimensionalpocketsii.pocket.core.block.ItemBlockFocus;
import com.tcn.dimensionalpocketsii.pocket.core.block.ItemBlockPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.ItemBlockPocketEnhanced;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityFocus;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleAnvil;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleArmourWorkbench;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleBlastFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleCharger;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleCrafter;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleFluidDisplay;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleFurnace;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleGenerator;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityPocketEnhanced;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityZModuleCreativeEnergy;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityZModuleCreativeFluid;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleAnvil;
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
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleGlass;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleSmithingTable;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleUpgradeStation;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleZCreativeEnergy;
import com.tcn.dimensionalpocketsii.pocket.core.item.module.ModuleZCreativeFluid;

import net.minecraft.Util;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PocketsRegistrationManager {
	
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DimensionalPockets.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DimensionalPockets.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DimensionalPockets.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, DimensionalPockets.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DimensionalPockets.MOD_ID);
	public static final DeferredRegister<ArmorMaterial> ARMOUR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, DimensionalPockets.MOD_ID);
	public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, DimensionalPockets.MOD_ID);
	
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DimensionalPockets.MOD_ID);

	public static final ArrayList<Supplier<? extends ItemLike>> TAB_BLOCKS = new ArrayList<>();
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_ITEMS = new ArrayList<>();
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_TOOLS = new ArrayList<>();
	
	public static final Supplier<CreativeModeTab> DIM_POCKETS_BLOCKS_GROUP = TABS.register("dimensionalpocketsii.blocks", 
		() -> CreativeModeTab.builder().title(ComponentHelper.style(ComponentColour.POCKET_PURPLE_GUI, "Dimensional Pockets: Blocks")).icon(() -> { 
			return new ItemStack(PocketsRegistrationManager.BLOCK_POCKET.get()); 
		}).displayItems((params, output) -> TAB_BLOCKS.forEach(itemLike -> output.accept(itemLike.get()))).build()
	);

	public static final Supplier<CreativeModeTab> DIM_POCKETS_ITEMS_GROUP = TABS.register("dimensionalpocketsii.items", 
		() -> CreativeModeTab.builder().title(ComponentHelper.style(ComponentColour.POCKET_PURPLE_GUI, "Dimensional Pockets: Items")).icon(() -> { 
			return new ItemStack(PocketsRegistrationManager.DIMENSIONAL_INGOT.get()); 
		}).displayItems((params, output) -> TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get()))).build()
	);

	public static final Supplier<CreativeModeTab> DIM_POCKETS_TOOLS_GROUP = TABS.register("dimensionalpocketsii.tools", 
		() -> CreativeModeTab.builder().title(ComponentHelper.style(ComponentColour.POCKET_PURPLE_GUI, "Dimensional Pockets: Tools")).icon(() -> { 
			return new ItemStack(PocketsRegistrationManager.DIMENSIONAL_SWORD.get()); 
		}).displayItems((params, output) -> TAB_TOOLS.forEach(itemLike -> output.accept(itemLike.get()))).build()
	);
	
	public static final Holder<ArmorMaterial> ARMOUR_MATERIAL_DIMENSIONAL = ARMOUR_MATERIALS.register("dimensional", () -> new ArmorMaterial(
		Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
			map.put(ArmorItem.Type.BOOTS, 5);
			map.put(ArmorItem.Type.LEGGINGS, 8);
			map.put(ArmorItem.Type.CHESTPLATE, 10);
			map.put(ArmorItem.Type.HELMET, 5);
			map.put(ArmorItem.Type.BODY, 14);
		}), 30, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(PocketsRegistrationManager.DIMENSIONAL_INGOT.get()),
		List.of(
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "base/tex"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "base/tex_overlay"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "base/tex_alpha"), "", false)
		), 6.0F, 0.4F)
	);

	public static final Holder<ArmorMaterial> ARMOUR_MATERIAL_DIMENSIONAL_ENHANCED = ARMOUR_MATERIALS.register("dimensional_enhanced", () -> new ArmorMaterial(
		Util.make(new EnumMap<>(ArmorItem.Type.class), (enumMap) -> {
			enumMap.put(ArmorItem.Type.BOOTS, 7); 
			enumMap.put(ArmorItem.Type.LEGGINGS, 10); 
			enumMap.put(ArmorItem.Type.CHESTPLATE, 12); 
			enumMap.put(ArmorItem.Type.HELMET, 7); 
			enumMap.put(ArmorItem.Type.BODY, 16);
		}), 40, SoundEvents.ARMOR_EQUIP_GENERIC,() -> Ingredient.of(PocketsRegistrationManager.DIMENSIONAL_INGOT.get()),
		List.of(
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "enhanced/tex"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "enhanced/tex_overlay"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "enhanced/tex_alpha"), "", false)
		), 7.0F, 0.6F)
	);

	public static final Holder<ArmorMaterial> ARMOUR_MATERIAL_SPECIAL = ARMOUR_MATERIALS.register("special", () -> new ArmorMaterial(
		Util.make(new EnumMap<>(ArmorItem.Type.class), (enumMap) -> {
			enumMap.put(ArmorItem.Type.BOOTS, 7); 
			enumMap.put(ArmorItem.Type.LEGGINGS,10); 
			enumMap.put(ArmorItem.Type.CHESTPLATE, 12); 
			enumMap.put(ArmorItem.Type.HELMET, 7);
			enumMap.put(ArmorItem.Type.BODY, 16);
		}), 50, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(PocketsRegistrationManager.DIMENSIONAL_INGOT.get()),
		List.of(
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "special/tex"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "special/tex_overlay"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "special/tex_alpha"), "", false)
		), 7.5F, 0.7F )
	);

	public static final Holder<ArmorMaterial> ARMOUR_MATERIAL_SPECIAL_SHIFTER = ARMOUR_MATERIALS.register("special_shifter", () -> new ArmorMaterial(
		Util.make(new EnumMap<>(ArmorItem.Type.class), (enumMap) -> {
			enumMap.put(ArmorItem.Type.BOOTS, 7); 
			enumMap.put(ArmorItem.Type.LEGGINGS,10); 
			enumMap.put(ArmorItem.Type.CHESTPLATE, 12); 
			enumMap.put(ArmorItem.Type.HELMET, 7);
			enumMap.put(ArmorItem.Type.BODY, 16);
		}), 50, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(PocketsRegistrationManager.DIMENSIONAL_INGOT.get()),
		List.of(
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "shifter/tex"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "shifter/tex_overlay"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "shifter/tex_alpha"), "", false)
		), 7.5F, 0.7F)
	);

	public static final Holder<ArmorMaterial> ARMOUR_MATERIAL_SPECIAL_VISOR = ARMOUR_MATERIALS.register("special_visor", () -> new ArmorMaterial(
		Util.make(new EnumMap<>(ArmorItem.Type.class), (enumMap) -> {
			enumMap.put(ArmorItem.Type.BOOTS, 7); 
			enumMap.put(ArmorItem.Type.LEGGINGS,10); 
			enumMap.put(ArmorItem.Type.CHESTPLATE, 12); 
			enumMap.put(ArmorItem.Type.HELMET, 7);
			enumMap.put(ArmorItem.Type.BODY, 16);
		}), 50, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(PocketsRegistrationManager.DIMENSIONAL_INGOT.get()),
		List.of(
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "visor/tex"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "visor/tex_overlay"), "", false),
			new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "visor/tex_alpha"), "", false)
		), 7.5F, 0.7F)
	);

	public static final Rarity RARITY_POCKET  = CoreItemRarity.POCKET.getValue();
	public static final Rarity RARITY_ARMOUR  = CoreItemRarity.MODULE_ARMOUR.getValue();
	public static final Rarity RARITY_ENHANCED = CoreItemRarity.MODULE_POCKET.getValue();
	public static final Rarity RARITY_CREATIVE = CoreItemRarity.CREATIVE.getValue();
	
	public static final DeferredItem<Item> DIMENSIONAL_TOME = addToItemTab(ITEMS.register("dimensional_tome", () -> new DimensionalTome(new Item.Properties().stacksTo(1).rarity(RARITY_POCKET))));
	
	public static final DeferredItem<Item> DIMENSIONAL_SHARD = addToItemTab(ITEMS.register("dimensional_shard", () -> new CosmosItem(new Item.Properties().rarity(RARITY_POCKET))));
	public static final DeferredItem<Item> DIMENSIONAL_INGOT = addToItemTab(ITEMS.register("dimensional_ingot", () -> new CosmosItem(new Item.Properties().rarity(RARITY_POCKET))));
	public static final DeferredItem<Item> DIMENSIONAL_INGOT_ENHANCED = addToItemTab(ITEMS.register("dimensional_ingot_enhanced", () -> new CosmosItemEffect(new Item.Properties().rarity(Rarity.RARE).fireResistant())));
	public static final DeferredItem<Item> DIMENSIONAL_GEM = addToItemTab(ITEMS.register("dimensional_gem", () -> new CosmosItem(new Item.Properties().rarity(RARITY_POCKET))));
	
	public static final DeferredItem<Item> DIMENSIONAL_DUST = addToItemTab(ITEMS.register("dimensional_dust", () -> new CosmosItem(new Item.Properties().rarity(RARITY_POCKET))));
	public static final DeferredItem<Item> DIMENSIONAL_PEARL = addToItemTab(ITEMS.register("dimensional_pearl", () -> new CosmosItem(new Item.Properties().stacksTo(16).rarity(RARITY_POCKET))));
	public static final DeferredItem<Item> DIMENSIONAL_THREAD = addToItemTab(ITEMS.register("dimensional_thread", () -> new CosmosItem(new Item.Properties().rarity(RARITY_POCKET))));
	
	public static final DeferredItem<Item> NETHER_STAR_SHARD = addToItemTab(ITEMS.register("nether_star_shard", () -> new CosmosItemEffect(new Item.Properties().stacksTo(16).rarity(Rarity.RARE).fireResistant())));
	public static final DeferredItem<Item> ELYTRA_WING = addToItemTab(ITEMS.register("elytra_wing", () -> new CosmosItem(new Item.Properties().stacksTo(2).rarity(Rarity.RARE))));
	
	public static final DeferredItem<Item> DIMENSIONAL_WRENCH = addToToolsTab(ITEMS.register("dimensional_wrench", () -> new CosmosItemTool(new Item.Properties().stacksTo(1))));
	
	public static final DeferredItem<Item> DIMENSIONAL_DEVICE_BASE = addToToolsTab(ITEMS.register("dimensional_device_base", () -> new CosmosItem(new Item.Properties().fireResistant().stacksTo(16))));
	public static final DeferredItem<Item> DIMENSIONAL_EJECTOR = addToToolsTab(ITEMS.register("dimensional_ejector", () -> new DimensionalEjector(new Item.Properties().stacksTo(4))));
	
	public static final DeferredItem<DimensionalShifter> DIMENSIONAL_SHIFTER = addToToolsTab(ITEMS.register("dimensional_shifter", () -> new DimensionalShifter(new Item.Properties().fireResistant().stacksTo(1).rarity(RARITY_POCKET), new CosmosEnergyItem.Properties().maxEnergyStored(5000000).maxIO(50000).maxUse(60000))));
	public static final DeferredItem<DimensionalShifterEnhanced> DIMENSIONAL_SHIFTER_ENHANCED = addToToolsTab(ITEMS.register("dimensional_shifter_enhanced", () -> new DimensionalShifterEnhanced(new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(100000).maxUse(50000))));
	
	public static final DeferredItem<DimensionalEnergyCell> DIMENSIONAL_ENERGY_CELL = addToToolsTab(ITEMS.register("dimensional_energy_cell", () -> new DimensionalEnergyCell(new Item.Properties().fireResistant().stacksTo(1).rarity(RARITY_POCKET), new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(100000))));
	public static final DeferredItem<DimensionalEnergyCellEnhanced> DIMENSIONAL_ENERGY_CELL_ENHANCED = addToToolsTab(ITEMS.register("dimensional_energy_cell_enhanced", () -> new DimensionalEnergyCellEnhanced(new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.RARE), new CosmosEnergyItem.Properties().maxEnergyStored(50000000).maxIO(200000))));
	
	public static final DeferredItem<DimensionalSword> DIMENSIONAL_SWORD = addToToolsTab(ITEMS.register("dimensional_sword", () -> 
		new DimensionalSword(CoreItemTier.DIMENSIONAL, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET).attributes(SwordItem.createAttributes(CoreItemTier.DIMENSIONAL, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY)))
	);
	
	public static final DeferredItem<DimensionalPickaxe> DIMENSIONAL_PICKAXE = addToToolsTab(ITEMS.register("dimensional_pickaxe", () -> 
		new DimensionalPickaxe(CoreItemTier.DIMENSIONAL, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET).attributes(PickaxeItem.createAttributes(CoreItemTier.DIMENSIONAL, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY)))
	);
	
	public static final DeferredItem<DimensionalAxe> DIMENSIONAL_AXE = addToToolsTab(ITEMS.register("dimensional_axe", () -> 
		new DimensionalAxe(CoreItemTier.DIMENSIONAL, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET).attributes(AxeItem.createAttributes(CoreItemTier.DIMENSIONAL, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY)))
	);
	
	public static final DeferredItem<DimensionalShovel> DIMENSIONAL_SHOVEL = addToToolsTab(ITEMS.register("dimensional_shovel", () -> 
		new DimensionalShovel(CoreItemTier.DIMENSIONAL, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET).attributes(ShovelItem.createAttributes(CoreItemTier.DIMENSIONAL, 3, -2.4F)),  
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY)))
	);
	
	public static final DeferredItem<DimensionalHoe> DIMENSIONAL_HOE = addToToolsTab(ITEMS.register("dimensional_hoe", () -> 
		new DimensionalHoe(CoreItemTier.DIMENSIONAL, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET).attributes(HoeItem.createAttributes(CoreItemTier.DIMENSIONAL, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY)))
	);
	
	public static final DeferredItem<DimensionalBow> DIMENSIONAL_BOW = addToToolsTab(ITEMS.register("dimensional_bow", () -> 
		new DimensionalBow(
			new Item.Properties().fireResistant().rarity(RARITY_POCKET), 
			new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(10000), 6.0F))
	);
	
	public static final DeferredItem<DimensionalTrident> DIMENSIONAL_TRIDENT = addToToolsTab(ITEMS.register("dimensional_trident", () -> 
		new DimensionalTrident(
			new Item.Properties().fireResistant().rarity(RARITY_POCKET).attributes(DimensionalTridentEnhanced.createAttributes(16.0F, 0.6F)).component(DataComponents.TOOL, DimensionalTridentEnhanced.createToolProperties(1.0F, 2)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY), 50000, 2))
	);
	
	public static final DeferredItem<CosmosEnergyShieldItem> DIMENSIONAL_SHIELD = addToToolsTab(ITEMS.register("dimensional_shield", () -> 
		new CosmosEnergyShieldItem(
			new Item.Properties().fireResistant().stacksTo(1).rarity(RARITY_POCKET).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY), 
			ModReferences.RESOURCE.SHIELD, ModReferences.RESOURCE.SHIELD_NO_PATTERN))
	);
	
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_HELMET = addToToolsTab(ITEMS.register(
		"dimensional_helmet", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL, ArmorItem.Type.HELMET, true, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET), 
			new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(6000)))
	);
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_CHESTPLATE = addToToolsTab(ITEMS.register(
		"dimensional_chestplate", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL, ArmorItem.Type.CHESTPLATE, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET), 
			new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(7000)))
	);
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_LEGGINGS = addToToolsTab(ITEMS.register(
		"dimensional_leggings", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL, ArmorItem.Type.LEGGINGS, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET), 
			new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(6000)))
	);
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_BOOTS = addToToolsTab(ITEMS.register(
		"dimensional_boots", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL, ArmorItem.Type.BOOTS, false, 
			new Item.Properties().fireResistant().rarity(RARITY_POCKET), 
			new CosmosEnergyItem.Properties().maxEnergyStored(2000000).maxIO(100000).maxUse(5000)))
	);

	
	public static final DeferredItem<DimensionalSword> DIMENSIONAL_SWORD_ENHANCED = addToToolsTab(ITEMS.register("dimensional_sword_enhanced", () -> 
		new DimensionalSword(CoreItemTier.DIMENSIONAL_ENHANCED, true, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE).attributes(SwordItem.createAttributes(CoreItemTier.DIMENSIONAL_ENHANCED, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY_ENHANCED)))
	);
	public static final DeferredItem<DimensionalPickaxe> DIMENSIONAL_PICKAXE_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_pickaxe_enhanced", () -> 
		new DimensionalPickaxe(CoreItemTier.DIMENSIONAL_ENHANCED, true, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE).attributes(PickaxeItem.createAttributes(CoreItemTier.DIMENSIONAL_ENHANCED, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY_ENHANCED)))
	);
	public static final DeferredItem<DimensionalAxe> DIMENSIONAL_AXE_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_axe_enhanced", () -> 
		new DimensionalAxe(CoreItemTier.DIMENSIONAL_ENHANCED, true, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE).attributes(AxeItem.createAttributes(CoreItemTier.DIMENSIONAL_ENHANCED, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY_ENHANCED)))
	);
	public static final DeferredItem<DimensionalShovel> DIMENSIONAL_SHOVEL_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_shovel_enhanced", () -> 
		new DimensionalShovel(CoreItemTier.DIMENSIONAL_ENHANCED, true, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE).attributes(ShovelItem.createAttributes(CoreItemTier.DIMENSIONAL_ENHANCED, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY_ENHANCED)))
	);
	public static final DeferredItem<DimensionalHoe> DIMENSIONAL_HOE_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_hoe_enhanced", () -> 
		new DimensionalHoe(CoreItemTier.DIMENSIONAL_ENHANCED, true, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE).attributes(HoeItem.createAttributes(CoreItemTier.DIMENSIONAL_ENHANCED, 3, -2.4F)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY_ENHANCED)))
	);
	public static final DeferredItem<DimensionalBow> DIMENSIONAL_BOW_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_bow_enhanced", () -> 
		new DimensionalBow(
			new Item.Properties().fireResistant().rarity(Rarity.RARE), 
			new CosmosEnergyItem.Properties().maxEnergyStored(4000000).maxIO(200000).maxUse(15000), 9.0F))
	);
	public static final DeferredItem<DimensionalTridentEnhanced> DIMENSIONAL_TRIDENT_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_trident_enhanced", () -> 
		new DimensionalTridentEnhanced(
			new Item.Properties().fireResistant().rarity(Rarity.RARE).attributes(DimensionalTridentEnhanced.createAttributes(12.0F, -1.0F)).component(DataComponents.TOOL, DimensionalTridentEnhanced.createToolProperties(1.0F, 2)), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY_ENHANCED), 40000, 3))
	);
	public static final DeferredItem<CosmosEnergyShieldItem> DIMENSIONAL_SHIELD_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_shield_enhanced", () -> 
		new CosmosEnergyShieldItem(
			new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY), 
			new CosmosEnergyItem.Properties().setStatsFromArray(ModReferences.CONSTANT.ENERGY_ENHANCED), 
			ModReferences.RESOURCE.SHIELD_ENHANCED, ModReferences.RESOURCE.SHIELD_ENHANCED_NO_PATTERN))
	);
	
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_HELMET_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_helmet_enhanced", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL_ENHANCED, ArmorItem.Type.HELMET, true, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE), 
			new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(6000)))
	);
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_CHESTPLATE_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_chestplate_enhanced", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL_ENHANCED, ArmorItem.Type.CHESTPLATE, false, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE), 
			new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(7000)))
	);
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_LEGGINGS_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_leggings_enhanced", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL_ENHANCED, ArmorItem.Type.LEGGINGS, false, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE), 
			new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(6000)))
	);
	public static final DeferredItem<CosmosEnergyArmourItemColourable> DIMENSIONAL_BOOTS_ENHANCED = addToToolsTab(ITEMS.register(
		"dimensional_boots_enhanced", () -> 
		new CosmosEnergyArmourItemColourable(ARMOUR_MATERIAL_DIMENSIONAL_ENHANCED, ArmorItem.Type.BOOTS, false, 
			new Item.Properties().fireResistant().rarity(Rarity.RARE), 
			new CosmosEnergyItem.Properties().maxEnergyStored(6000000).maxIO(100000).maxUse(5000)))
	);
	
	public static final DeferredItem<DimensionalElytraplate> DIMENSIONAL_ELYTRAPLATE = addToToolsTab(ITEMS.register("dimensional_elytraplate", () -> new DimensionalElytraplate(ARMOUR_MATERIAL_DIMENSIONAL_ENHANCED, ArmorItem.Type.CHESTPLATE, (new Item.Properties().rarity(Rarity.RARE).fireResistant()), false, new CosmosEnergyItem.Properties().maxEnergyStored(10000000).maxIO(200000).maxUse(3500))));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerElytraplateConnector>> CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR = MENU_TYPES.register("container_elytraplate", () -> IMenuTypeExtension.create(ContainerElytraplateConnector::createContainerClientSide));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerElytraplateSettings>> CONTAINER_TYPE_ELYTRAPLATE_SETTINGS = MENU_TYPES.register("container_elytraplate_settings", () -> IMenuTypeExtension.create(ContainerElytraplateSettings::createContainerClientSide));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerElytraplateEnderChest>> CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST = MENU_TYPES.register("container_elytraplate_ender_chest", () -> IMenuTypeExtension.create(ContainerElytraplateEnderChest::createContainerClientSide));
	
	public static final DeferredItem<ItemModuleScreen> ARMOUR_MODULE_SCREEN = addToItemTab(ITEMS.register("armour_module_screen", () -> new ItemModuleScreen(new Item.Properties().stacksTo(1).rarity(RARITY_ARMOUR))));
	public static final DeferredItem<ItemModuleShifter> ARMOUR_MODULE_SHIFTER = addToItemTab(ITEMS.register("armour_module_shifter", () -> new ItemModuleShifter(new Item.Properties().stacksTo(1).rarity(RARITY_ARMOUR))));
	public static final DeferredItem<ItemModuleVisor> ARMOUR_MODULE_VISOR = addToItemTab(ITEMS.register("armour_module_visor", () -> new ItemModuleVisor(new Item.Properties().stacksTo(1).rarity(RARITY_ARMOUR))));
	public static final DeferredItem<ItemModuleSolar> ARMOUR_MODULE_SOLAR = addToItemTab(ITEMS.register("armour_module_solar", () -> new ItemModuleSolar(new Item.Properties().stacksTo(1).rarity(RARITY_ARMOUR))));
	public static final DeferredItem<ItemModuleBattery> ARMOUR_MODULE_BATTERY = addToItemTab(ITEMS.register("armour_module_battery", () -> new ItemModuleBattery(new Item.Properties().stacksTo(1).rarity(RARITY_ARMOUR))));
	public static final DeferredItem<ItemModuleEnderChest> ARMOUR_MODULE_ENDER_CHEST = addToItemTab(ITEMS.register("armour_module_ender_chest", () -> new ItemModuleEnderChest(new Item.Properties().stacksTo(1).rarity(RARITY_ARMOUR))));
	public static final DeferredItem<ItemModuleFirework> ARMOUR_MODULE_FIREWORK = addToItemTab(ITEMS.register("armour_module_firework", () -> new ItemModuleFirework(new Item.Properties().stacksTo(1).rarity(RARITY_ARMOUR))));
	
	public static final DeferredItem<Item> MODULE_BASE = addToItemTab(ITEMS.register("module_base", () -> new ModuleBase(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_CONNECTOR = addToItemTab(ITEMS.register("module_connector", () -> new ModuleConnector(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_CHARGER = addToItemTab(ITEMS.register("module_charger", () -> new ModuleCharger(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_CRAFTER = addToItemTab(ITEMS.register("module_crafter", () -> new ModuleCrafter(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_SMITHING_TABLE = addToItemTab(ITEMS.register("module_smithing_table", () -> new ModuleSmithingTable(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_FURNACE = addToItemTab(ITEMS.register("module_furnace", () -> new ModuleFurnace(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_BLAST_FURNACE = addToItemTab(ITEMS.register("module_blast_furnace", () -> new ModuleBlastFurnace(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_ENERGY_DISPLAY = addToItemTab(ITEMS.register("module_energy_display", () -> new ModuleEnergyDisplay(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_FLUID_DISPLAY = addToItemTab(ITEMS.register("module_fluid_display", () -> new ModuleFluidDisplay(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_ARMOUR_WORKBENCH = addToItemTab(ITEMS.register("module_armour_workbench", () -> new ModuleArmourWorkbench(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_GENERATOR = addToItemTab(ITEMS.register("module_generator", () -> new ModuleGenerator(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_UPGRADE_STATION = addToItemTab(ITEMS.register("module_upgrade_station", () -> new ModuleUpgradeStation(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_FOCUS = addToItemTab(ITEMS.register("module_focus", () -> new ModuleFocus(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_ANVIL = addToItemTab(ITEMS.register("module_anvil", () -> new ModuleAnvil(new Item.Properties().stacksTo(8).rarity(RARITY_ENHANCED))));
	public static final DeferredItem<Item> MODULE_GLASS = addToItemTab(ITEMS.register("module_glass", () -> new ModuleGlass(new Item.Properties().stacksTo(64).rarity(RARITY_ENHANCED))));
	
	public static final DeferredItem<Item> MODULE_CREATIVE_ENERGY = addToItemTab(ITEMS.register("module_creative_energy", () -> new ModuleZCreativeEnergy(new Item.Properties().stacksTo(8).rarity(RARITY_CREATIVE))));
	public static final DeferredItem<Item> MODULE_CREATIVE_FLUID = addToItemTab(ITEMS.register("module_creative_fluid", () -> new ModuleZCreativeFluid(new Item.Properties().stacksTo(8).rarity(RARITY_CREATIVE))));

	public static final DeferredItem<Item> DIMENSIONAL_UPGRADE_TEMPLATE = addToToolsTab(ITEMS.register("dimensional_upgrade_template", () -> new ItemSmithingUpgrade(new Item.Properties().rarity(RARITY_ENHANCED).stacksTo(16))));
	
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_ORE = BLOCKS.register("block_dimensional_ore", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 4.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_ORE = addToBlockTab(ITEMS.register("block_dimensional_ore", () -> new BlockItem(BLOCK_DIMENSIONAL_ORE.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_DEEPSLATE_DIMENSIONAL_ORE = BLOCKS.register("block_deepslate_dimensional_ore", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 8.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredItem<Item> ITEM_DEEPSLATE_DIMENSIONAL_ORE = addToBlockTab(ITEMS.register("block_deepslate_dimensional_ore", () -> new BlockItem(BLOCK_DEEPSLATE_DIMENSIONAL_ORE.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_ORE_NETHER = BLOCKS.register("block_dimensional_ore_nether", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_ORE_NETHER = addToBlockTab(ITEMS.register("block_dimensional_ore_nether", () -> new BlockItem(BLOCK_DIMENSIONAL_ORE_NETHER.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_ORE_END = BLOCKS.register("block_dimensional_ore_end", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(8.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_ORE_END = addToBlockTab(ITEMS.register("block_dimensional_ore_end", () -> new BlockItem(BLOCK_DIMENSIONAL_ORE_END.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL = BLOCKS.register("block_dimensional", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL = addToBlockTab(ITEMS.register("block_dimensional", () -> new BlockItem(BLOCK_DIMENSIONAL.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_SLAB = BLOCKS.register("block_dimensional_slab", () -> new SlabBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_SLAB = addToBlockTab(ITEMS.register("block_dimensional_slab", () -> new BlockItem(BLOCK_DIMENSIONAL_SLAB.get(), new Item.Properties())));

	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_STAIR = BLOCKS.register("block_dimensional_stair", () -> new StairBlock(BLOCK_DIMENSIONAL.get().defaultBlockState(), Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_STAIR = addToBlockTab(ITEMS.register("block_dimensional_stair", () -> new BlockItem(BLOCK_DIMENSIONAL_STAIR.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_METAL = BLOCKS.register("block_dimensional_metal", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_METAL = addToBlockTab(ITEMS.register("block_dimensional_metal", () -> new BlockItem(BLOCK_DIMENSIONAL_METAL.get(), new Item.Properties())));

	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_METAL_ENHANCED = BLOCKS.register("block_dimensional_metal_enhanced", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_METAL_ENHANCED = addToBlockTab(ITEMS.register("block_dimensional_metal_enhanced", () -> new BlockItem(BLOCK_DIMENSIONAL_METAL_ENHANCED.get(), new Item.Properties().rarity(RARITY_ENHANCED))));
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_GEM = BLOCKS.register("block_dimensional_gem", () -> new CosmosBlock(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_GEM = addToBlockTab(ITEMS.register("block_dimensional_gem", () -> new BlockItem(BLOCK_DIMENSIONAL_GEM.get(), new Item.Properties())));
	
	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_CORE = BLOCKS.register("block_dimensional_core", () -> new CosmosBlockModelUnplaceable(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_CORE = addToBlockTab(ITEMS.register("block_dimensional_core", () -> new BlockItem(BLOCK_DIMENSIONAL_CORE.get(), new Item.Properties().rarity(RARITY_POCKET))));

	public static final DeferredBlock<Block> BLOCK_DIMENSIONAL_CORE_ENHANCED = BLOCKS.register("block_dimensional_core_enhanced", () -> new CosmosBlockModelUnplaceable(Block.Properties.of().requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final DeferredItem<Item> ITEM_DIMENSIONAL_CORE_ENHANCED = addToBlockTab(ITEMS.register("block_dimensional_core_enhanced", () -> new BlockItem(BLOCK_DIMENSIONAL_CORE_ENHANCED.get(), new Item.Properties().rarity(RARITY_ENHANCED))));
	
	public static final DeferredBlock<Block> BLOCK_WALL = BLOCKS.register("block_wall", () -> new BlockWallBase(Block.Properties.of().strength(-1,3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL = ITEMS.register("block_wall", () -> new BlockItem(BLOCK_WALL.get(), new Item.Properties()));
	
	public static final DeferredBlock<Block> BLOCK_WALL_EDGE = BLOCKS.register("block_wall_edge", () -> new BlockWallEdge(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_EDGE = ITEMS.register("block_wall_edge", () -> new BlockItem(BLOCK_WALL_EDGE.get(), new Item.Properties()));

	public static final DeferredBlock<Block> BLOCK_WALL_GLASS = BLOCKS.register("block_wall_glass", () -> new BlockWallGlass(glassProperties(true).strength(-1, 3600000.0F)));
	public static final DeferredItem<Item> ITEM_WALL_GLASS = ITEMS.register("block_wall_glass", () -> new BlockItem(BLOCK_WALL_GLASS.get(), new Item.Properties()));
	
	public static final DeferredBlock<Block> BLOCK_WALL_DOOR = BLOCKS.register("block_wall_door", () -> new BlockWallDoor(Block.Properties.of().strength(-1,3600000.0F).noOcclusion().lightLevel((state) -> { return 15; }), BlockSetType.register(new BlockSetType("dimensional"))));
	public static final DeferredItem<Item> ITEM_WALL_DOOR = ITEMS.register("block_wall_door", () -> new BlockItem(BLOCK_WALL_DOOR.get(), new Item.Properties()));
	
	public static final DeferredBlock<Block> BLOCK_POCKET = BLOCKS.register("block_pocket", () -> new BlockPocket(Block.Properties.of().strength(-1, 3600000.0F).noOcclusion()));
	public static final DeferredItem<Item> BLOCK_ITEM_POCKET = addToBlockTab(ITEMS.register("block_pocket", () -> new ItemBlockPocket(BLOCK_POCKET.get(), new Item.Properties().stacksTo(1).setNoRepair().rarity(RARITY_POCKET).fireResistant())));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityPocket>> BLOCK_ENTITY_TYPE_POCKET = BLOCK_ENTITY_TYPES.register("tile_entity_pocket", () -> BlockEntityType.Builder.of(BlockEntityPocket::new, BLOCK_POCKET.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerPocket>> CONTAINER_TYPE_POCKET = MENU_TYPES.register("container_pocket", () -> IMenuTypeExtension.create(ContainerPocket::createContainerClientSide));
	
	public static final DeferredBlock<Block> BLOCK_POCKET_ENHANCED = BLOCKS.register("block_pocket_enhanced", () -> new BlockPocketEnhanced(Block.Properties.of().strength(-1, 3600000.0F).noOcclusion()));
	public static final DeferredItem<Item> BLOCK_ITEM_POCKET_ENHANCED = addToBlockTab(ITEMS.register("block_pocket_enhanced", () -> new ItemBlockPocketEnhanced(BLOCK_POCKET_ENHANCED.get(), new Item.Properties().stacksTo(1).setNoRepair().rarity(RARITY_ENHANCED).fireResistant())));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityPocketEnhanced>> BLOCK_ENTITY_TYPE_POCKET_ENHANCED = BLOCK_ENTITY_TYPES.register("tile_entity_pocket_enhanced", () -> BlockEntityType.Builder.of(BlockEntityPocketEnhanced::new, BLOCK_POCKET_ENHANCED.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerPocketEnhanced>> CONTAINER_TYPE_POCKET_ENHANCED = MENU_TYPES.register("container_pocket_enhanced", () -> IMenuTypeExtension.create(ContainerPocketEnhanced::createContainerClientSide));
	
	public static final DeferredBlock<Block> BLOCK_WALL_CONNECTOR = BLOCKS.register("block_wall_connector", () -> new BlockWallConnector(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_CONNECTOR = ITEMS.register("block_wall_connector", () -> new BlockItem(BLOCK_WALL_CONNECTOR.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleConnector>> BLOCK_ENTITY_TYPE_CONNECTOR = BLOCK_ENTITY_TYPES.register("tile_entity_connector", () -> BlockEntityType.Builder.of(BlockEntityModuleConnector::new, BLOCK_WALL_CONNECTOR.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleConnector>> CONTAINER_TYPE_CONNECTOR = MENU_TYPES.register("container_connector", () -> IMenuTypeExtension.create(ContainerModuleConnector::createContainerClientSide));
	
	public static final DeferredBlock<Block> BLOCK_WALL_CHARGER = BLOCKS.register("block_wall_charger", () -> new BlockWallCharger(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_CHARGER = ITEMS.register("block_wall_charger", () -> new BlockItem(BLOCK_WALL_CHARGER.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleCharger>> BLOCK_ENTITY_TYPE_CHARGER = BLOCK_ENTITY_TYPES.register("tile_entity_charger", () -> BlockEntityType.Builder.of(BlockEntityModuleCharger::new, BLOCK_WALL_CHARGER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleCharger>> CONTAINER_TYPE_CHARGER = MENU_TYPES.register("container_charger", () -> IMenuTypeExtension.create(ContainerModuleCharger::new));
	
	public static final DeferredBlock<Block> BLOCK_WALL_CRAFTER = BLOCKS.register("block_wall_crafter", () -> new BlockWallCrafter(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_CRAFTER = ITEMS.register("block_wall_crafter", () -> new BlockItem(BLOCK_WALL_CRAFTER.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleCrafter>> BLOCK_ENTITY_TYPE_CRAFTER = BLOCK_ENTITY_TYPES.register("tile_entity_crafter", () -> BlockEntityType.Builder.of(BlockEntityModuleCrafter::new, BLOCK_WALL_CRAFTER.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleCrafter>> CONTAINER_TYPE_CRAFTER = MENU_TYPES.register("container_crafter", () -> IMenuTypeExtension.create(ContainerModuleCrafter::new));

	public static final DeferredBlock<Block> BLOCK_WALL_SMITHING_TABLE = BLOCKS.register("block_wall_smithing_table", () -> new BlockWallSmithingTable(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_SMITHING_TABLE = ITEMS.register("block_wall_smithing_table", () -> new BlockItem(BLOCK_WALL_SMITHING_TABLE.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleSmithingTable>> BLOCK_ENTITY_TYPE_SMITHING_TABLE = BLOCK_ENTITY_TYPES.register("tile_entity_smithing_table", () -> BlockEntityType.Builder.of(BlockEntityModuleSmithingTable::new, BLOCK_WALL_SMITHING_TABLE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleSmithingTable>> CONTAINER_TYPE_SMITHING_TABLE = MENU_TYPES.register("container_smithing_table", () -> IMenuTypeExtension.create(ContainerModuleSmithingTable::new));

	public static final DeferredBlock<Block> BLOCK_WALL_FURNACE = BLOCKS.register("block_wall_furnace", () -> new BlockWallFurnace(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_FURNACE = ITEMS.register("block_wall_furnace", () -> new BlockItem(BLOCK_WALL_FURNACE.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleFurnace>> BLOCK_ENTITY_TYPE_FURNACE = BLOCK_ENTITY_TYPES.register("tile_entity_furnace", () -> BlockEntityType.Builder.of(BlockEntityModuleFurnace::new, BLOCK_WALL_FURNACE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleFurnace>> CONTAINER_TYPE_FURNACE = MENU_TYPES.register("container_furnace", () -> IMenuTypeExtension.create(ContainerModuleFurnace::new));

	public static final DeferredBlock<Block> BLOCK_WALL_BLAST_FURNACE = BLOCKS.register("block_wall_blast_furnace", () -> new BlockWallBlastFurnace(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_BLAST_FURNACE = ITEMS.register("block_wall_blast_furnace", () -> new BlockItem(BLOCK_WALL_BLAST_FURNACE.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleBlastFurnace>> BLOCK_ENTITY_TYPE_BLAST_FURNACE = BLOCK_ENTITY_TYPES.register("tile_entity_blast_furnace", () -> BlockEntityType.Builder.of(BlockEntityModuleBlastFurnace::new, BLOCK_WALL_BLAST_FURNACE.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleBlastFurnace>> CONTAINER_TYPE_BLAST_FURNACE = MENU_TYPES.register("container_blast_furnace", () -> IMenuTypeExtension.create(ContainerModuleBlastFurnace::new));

	public static final DeferredBlock<Block> BLOCK_WALL_ENERGY_DISPLAY = BLOCKS.register("block_wall_energy_display", () -> new BlockWallEnergyDisplay(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks()));
	public static final DeferredItem<Item> ITEM_WALL_ENERGY_DISPLAY = ITEMS.register("block_wall_energy_display", () -> new BlockItem(BLOCK_WALL_ENERGY_DISPLAY.get(), new Item.Properties()));
	
	public static final DeferredBlock<Block> BLOCK_WALL_FLUID_DISPLAY = BLOCKS.register("block_wall_fluid_display", () -> new BlockWallFluidDisplay(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks().dynamicShape().noOcclusion()));
	public static final DeferredItem<Item> ITEM_WALL_FLUID_DISPLAY = ITEMS.register("block_wall_fluid_display", () -> new BlockItem(BLOCK_WALL_FLUID_DISPLAY.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleFluidDisplay>> BLOCK_ENTITY_TYPE_FLUID_DISPLAY = BLOCK_ENTITY_TYPES.register("tile_entity_fluid_display", () -> BlockEntityType.Builder.of(BlockEntityModuleFluidDisplay::new, BLOCK_WALL_FLUID_DISPLAY.get()).build(null));

	public static final DeferredBlock<Block> BLOCK_WALL_ARMOUR_WORKBENCH = BLOCKS.register("block_wall_armour_workbench", () -> new BlockWallArmourWorkbench(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks()));
	public static final DeferredItem<Item> ITEM_WALL_ARMOUR_WORKBENCH = ITEMS.register("block_wall_armour_workbench", () -> new BlockItem(BLOCK_WALL_ARMOUR_WORKBENCH.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleArmourWorkbench>> BLOCK_ENTITY_TYPE_ARMOUR_WORKBENCH = BLOCK_ENTITY_TYPES.register("tile_entity_armour_workbench", () -> BlockEntityType.Builder.of(BlockEntityModuleArmourWorkbench::new, BLOCK_WALL_ARMOUR_WORKBENCH.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleArmourWorkbench>> CONTAINER_TYPE_ARMOUR_WORKBENCH = MENU_TYPES.register("container_armour_workbench", () -> IMenuTypeExtension.create(ContainerModuleArmourWorkbench::new));

	public static final DeferredBlock<Block> BLOCK_WALL_UPGRADE_STATION = BLOCKS.register("block_wall_upgrade_station", () -> new BlockWallUpgradeStation(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; }).randomTicks()));
	public static final DeferredItem<Item> ITEM_WALL_UPGRADE_STATION = ITEMS.register("block_wall_upgrade_station", () -> new BlockItem(BLOCK_WALL_UPGRADE_STATION.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleUpgradeStation>> BLOCK_ENTITY_TYPE_UPGRADE_STATION = BLOCK_ENTITY_TYPES.register("tile_entity_upgrade_station", () -> BlockEntityType.Builder.of(BlockEntityModuleUpgradeStation::new, BLOCK_WALL_UPGRADE_STATION.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleUpgradeStation>> CONTAINER_TYPE_UPGRADE_STATION = MENU_TYPES.register("container_upgrade_station", () -> IMenuTypeExtension.create(ContainerModuleUpgradeStation::new));

	public static final DeferredBlock<Block> BLOCK_WALL_GENERATOR = BLOCKS.register("block_wall_generator", () -> new BlockWallGenerator(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_GENERATOR = ITEMS.register("block_wall_generator", () -> new BlockItem(BLOCK_WALL_GENERATOR.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleGenerator>> BLOCK_ENTITY_TYPE_GENERATOR = BLOCK_ENTITY_TYPES.register("tile_entity_generator", () -> BlockEntityType.Builder.of(BlockEntityModuleGenerator::new, BLOCK_WALL_GENERATOR.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleGenerator>> CONTAINER_TYPE_GENERATOR = MENU_TYPES.register("container_generator", () -> IMenuTypeExtension.create(ContainerModuleGenerator::new));

	public static final DeferredBlock<Block> BLOCK_WALL_ANVIL = BLOCKS.register("block_wall_anvil", () -> new BlockWallAnvil(Block.Properties.of().strength(-1, 3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_ANVIL = ITEMS.register("block_wall_anvil", () -> new BlockItem(BLOCK_WALL_ANVIL.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityModuleAnvil>> BLOCK_ENTITY_TYPE_ANVIL = BLOCK_ENTITY_TYPES.register("tile_entity_anvil", () -> BlockEntityType.Builder.of(BlockEntityModuleAnvil::new, BLOCK_WALL_ANVIL.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerModuleAnvil>> CONTAINER_TYPE_ANVIL = MENU_TYPES.register("container_anvil", () -> IMenuTypeExtension.create(ContainerModuleAnvil::new));

	public static final DeferredBlock<Block> BLOCK_FOCUS = BLOCKS.register("block_dimensional_focus", () -> new BlockFocus(Block.Properties.of().requiresCorrectToolForDrops().strength(-1, 3600000.0F)));
	public static final DeferredItem<Item> BLOCK_ITEM_FOCUS = addToBlockTab(ITEMS.register("block_dimensional_focus", () -> new ItemBlockFocus(BLOCK_FOCUS.get(), new Item.Properties().setNoRepair().rarity(RARITY_POCKET).fireResistant())));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFocus>> BLOCK_ENTITY_TYPE_FOCUS = BLOCK_ENTITY_TYPES.register("tile_entity_focus", () -> BlockEntityType.Builder.of(BlockEntityFocus::new, BLOCK_FOCUS.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFocus>> CONTAINER_TYPE_FOCUS = MENU_TYPES.register("container_focus", () -> IMenuTypeExtension.create(ContainerFocus::new));
	
	public static final DeferredBlock<Block> BLOCK_WALL_CREATIVE_ENERGY = BLOCKS.register("block_wall_creative_energy", () -> new BlockWallZCreativeEnergy(Block.Properties.of().strength(-1,3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_CREATIVE_ENERGY = ITEMS.register("block_wall_creative_energy", () -> new BlockItem(BLOCK_WALL_CREATIVE_ENERGY.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityZModuleCreativeEnergy>> BLOCK_ENTITY_TYPE_CREATIVE_ENERGY = BLOCK_ENTITY_TYPES.register("tile_entity_creative_energy", () -> BlockEntityType.Builder.of(BlockEntityZModuleCreativeEnergy::new, BLOCK_WALL_CREATIVE_ENERGY.get()).build(null));

	public static final DeferredBlock<Block> BLOCK_WALL_CREATIVE_FLUID = BLOCKS.register("block_wall_creative_fluid", () -> new BlockWallZCreativeFluid(Block.Properties.of().strength(-1,3600000.0F).lightLevel((state) -> { return 15; })));
	public static final DeferredItem<Item> ITEM_WALL_CREATIVE_FLUID = ITEMS.register("block_wall_creative_fluid", () -> new BlockItem(BLOCK_WALL_CREATIVE_FLUID.get(), new Item.Properties()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityZModuleCreativeFluid>> BLOCK_ENTITY_TYPE_CREATIVE_FLUID = BLOCK_ENTITY_TYPES.register("tile_entity_creative_fluid", () -> BlockEntityType.Builder.of(BlockEntityZModuleCreativeFluid::new, BLOCK_WALL_CREATIVE_FLUID.get()).build(null));

	public static final DeferredHolder<EntityType<?>, EntityType<DimensionalTridentEntity>> ENTITY_TYPE_TRIDENT = ENTITY_TYPES.register("dimensional_trident_type", () -> EntityType.Builder.<DimensionalTridentEntity>of(DimensionalTridentEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("dimensional_trident_type"));
	public static final DeferredHolder<EntityType<?>, EntityType<DimensionalTridentEnhancedEntity>> ENTITY_TYPE_TRIDENT_ENHANCED = ENTITY_TYPES.register("dimensional_trident_enhanced_type", () -> EntityType.Builder.<DimensionalTridentEnhancedEntity>of(DimensionalTridentEnhancedEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("dimensional_trident_enhanced_type"));

	public static final DeferredHolder<CriterionTrigger<?>, UseShifterTrigger> SHIFTER_TRIGGER = TRIGGERS.register("use_shifter", () -> new UseShifterTrigger());
	
	public static KeyMapping SUIT_SCREEN;
	public static KeyMapping SUIT_SCREEN_ENDER_CHEST;
	public static KeyMapping SUIT_SHIFT;
	public static KeyMapping SUIT_SETTINGS;
	public static KeyMapping SUIT_FIREWORK;
	
	public static void register(IEventBus bus) {
		ITEMS.register(bus);
		BLOCKS.register(bus);
		
		BLOCK_ENTITY_TYPES.register(bus);
		MENU_TYPES.register(bus);
		ENTITY_TYPES.register(bus);
		ARMOUR_MATERIALS.register(bus);
		
		TABS.register(bus);
		TRIGGERS.register(bus);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		CosmosRuntime.Client.registerBERenderer(event, RendererBlockEntityModuleFluidDisplay::new, BLOCK_ENTITY_TYPE_FLUID_DISPLAY.get());
		CosmosRuntime.Client.registerBERenderer(event, RendererBlockEntityModuleCreativeFluid::new, BLOCK_ENTITY_TYPE_CREATIVE_FLUID.get());
		
		DimensionalPockets.CONSOLE.startup("BlockEntityRenderer registration complete.");
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onEntityRenderersAddLayersEvent(final EntityRenderersEvent.AddLayers event) {
		EntityModelSet modelSet = event.getEntityModels();
		Minecraft mc = Minecraft.getInstance();
		
		event.getEntityTypes().forEach((entityType) -> {
			EntityRenderer<?> renderer = event.getRenderer(entityType);
			
			if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer && entityType != EntityType.VEX) {
				ResourceLocation type = ResourceLocation.parse(entityType.getDescriptionId());
				
				if (livingRenderer.getModel() instanceof HumanoidModel<?>) {
					LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>> humanRenderer = (LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>>) renderer;
					ModelLayerLocation[] layers = new ModelLayerLocation[] { ModelLayers.PLAYER_INNER_ARMOR, ModelLayers.PLAYER_OUTER_ARMOR };
					List<ModelLayerLocation> locations = ModelLayers.getKnownLocations().toList();
					
					for (int i = 0; i < locations.size(); i++) {
						ModelLayerLocation location = locations.get(i);
						
						if (location.getModel().equals(type)) {
							if (location.getLayer().equals("inner_armor")) {
								layers[0] = location;
							}
							
							if (location.getLayer().equals("outer_armor")) {
								layers[1] = location;
							}
						}
					}
					
					if (layers[0] != null && layers[1] != null) {
						humanRenderer.addLayer(new CosmosLayerArmourColourable<>(humanRenderer, new HumanoidModel<>(modelSet.bakeLayer(layers[0])), new HumanoidModel<>(modelSet.bakeLayer(layers[1])), mc.getModelManager()));
						DimensionalPockets.CONSOLE.debug("LivingEntityRenderer for: { " + entityType.getDescriptionId() + " } Dimensional Armour Layer added.");
					}
					
					humanRenderer.addLayer(new CosmosLayerElytra<>(humanRenderer, modelSet, ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/entity/dimensional_elytra_base.png")));
					DimensionalPockets.CONSOLE.debug("LivingEntityRenderer for: { " + entityType.getDescriptionId() + " } Elytra Layer added.");
				}
			}
		});
		
		LivingEntityRenderer<Player, PlayerModel<Player>> playerRendererAlt = event.getSkin(PlayerSkin.Model.WIDE);
		LivingEntityRenderer<Player, PlayerModel<Player>> playerRendererSlim = event.getSkin(PlayerSkin.Model.SLIM);

		if (playerRendererAlt != null) {
			playerRendererAlt.addLayer(new CosmosLayerElytra<>(playerRendererAlt, modelSet, ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/entity/dimensional_elytra_base.png")));
			playerRendererAlt.addLayer(new CosmosLayerArmourColourable<>(playerRendererAlt, new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), mc.getModelManager()));
			DimensionalPockets.CONSOLE.debug("Player Renderer {default} Custom Layers added.");
		} else {
			DimensionalPockets.CONSOLE.fatal("Player Renderer {default} NULL!! Report this issue to the Mod Author");
		}
		
		if (playerRendererSlim != null) {
			playerRendererSlim.addLayer(new CosmosLayerElytra<>(playerRendererSlim, modelSet, ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/entity/dimensional_elytra_base.png")));
			playerRendererSlim.addLayer(new CosmosLayerArmourColourable<>(playerRendererSlim, new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), mc.getModelManager()));
			DimensionalPockets.CONSOLE.debug("Player Renderer {slim} Custom Layers added.");
		} else {
			DimensionalPockets.CONSOLE.fatal("Player Renderer {slim} NULL!! Report this issue to the Mod Author");
		}
		
		DimensionalPockets.CONSOLE.startup("EntityRenderer Layer registration complete.");
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onModelRegistryEvent(ModelEvent.RegisterAdditional event) {
		CosmosRuntime.Client.registerStandaloneItemModels(event, DimensionalPockets.MOD_ID, 
			"dimensional_elytraplate_base", "dimensional_elytraplate_shifter", "dimensional_elytraplate_connect",
			"dimensional_elytraplate_visor", "dimensional_elytraplate_solar", "dimensional_elytraplate_battery"
		);
		
		DimensionalPockets.CONSOLE.startup("Additional Model registration complete..");
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRegisterKeyBindings(RegisterKeyMappingsEvent event) {
		SUIT_SCREEN = new KeyMapping("dimensionalpocketsii.keybind.suit_screen", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_BACKSLASH, "dimensionalpocketsii.keybind.category");
		SUIT_SCREEN_ENDER_CHEST = new KeyMapping("dimensionalpocketsii.keybind.suit_ender_chest", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_WORLD_1, "dimensionalpocketsii.keybind.category");
		SUIT_SHIFT = new KeyMapping("dimensionalpocketsii.keybind.suit_shift", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "dimensionalpocketsii.keybind.category");
		SUIT_SETTINGS = new KeyMapping("dimensionalpocketsii.keybind.suit_mode_change", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_APOSTROPHE, "dimensionalpocketsii.keybind.category");
		SUIT_FIREWORK = new KeyMapping("dimensionalpocketsii.keybind.suit_firework", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "dimensionalpocketsii.keybind.category");
		
		event.register(SUIT_SCREEN);
		event.register(SUIT_SCREEN_ENDER_CHEST);
		event.register(SUIT_SHIFT);
		event.register(SUIT_SETTINGS);
		event.register(SUIT_FIREWORK);
		
		DimensionalPockets.CONSOLE.startup("Keybindings registration complete..");
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRegisterColourHandlersEventBlock(RegisterColorHandlersEvent.Block event) {
		CosmosRuntime.Client.registerBlockColours(event, new ColourBlockPocket(), BLOCK_POCKET.get(), BLOCK_POCKET_ENHANCED.get());
		
		CosmosRuntime.Client.registerBlockColours(event, new ColourBlockWall(), 
			BLOCK_WALL.get(), BLOCK_WALL_EDGE.get(), BLOCK_WALL_GLASS.get(), BLOCK_WALL_DOOR.get(),
		
			BLOCK_WALL_CONNECTOR.get(), BLOCK_WALL_CHARGER.get(), BLOCK_WALL_CRAFTER.get(), BLOCK_WALL_SMITHING_TABLE.get(), BLOCK_FOCUS.get(),
			BLOCK_WALL_FURNACE.get(), BLOCK_WALL_BLAST_FURNACE.get(), BLOCK_WALL_ENERGY_DISPLAY.get(), BLOCK_WALL_FLUID_DISPLAY.get(), 
			BLOCK_WALL_ARMOUR_WORKBENCH.get(), BLOCK_WALL_UPGRADE_STATION.get(), BLOCK_WALL_GENERATOR.get(), BLOCK_WALL_ANVIL.get()
		);
		
		DimensionalPockets.CONSOLE.startup("Block Colour registration complete..");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRegisterColourHandlersEventItem(RegisterColorHandlersEvent.Item event) {
		CosmosRuntime.Client.registerItemColours(event, new ColourItem(),
			BLOCK_POCKET.get(), BLOCK_POCKET_ENHANCED.get(), 
			
			BLOCK_DIMENSIONAL_CORE.get(), BLOCK_WALL.get(), BLOCK_WALL_EDGE.get(), BLOCK_WALL_GLASS.get(),
		
			DIMENSIONAL_DEVICE_BASE.get(), DIMENSIONAL_SHIFTER.get(), DIMENSIONAL_SHIFTER_ENHANCED.get(), DIMENSIONAL_EJECTOR.get(),
			DIMENSIONAL_ENERGY_CELL.get(), DIMENSIONAL_ENERGY_CELL_ENHANCED.get(),
		
			DIMENSIONAL_HELMET.get(), DIMENSIONAL_CHESTPLATE.get(), DIMENSIONAL_LEGGINGS.get(), DIMENSIONAL_BOOTS.get(),
			DIMENSIONAL_HELMET_ENHANCED.get(), DIMENSIONAL_CHESTPLATE_ENHANCED.get(), DIMENSIONAL_LEGGINGS_ENHANCED.get(), DIMENSIONAL_BOOTS_ENHANCED.get(),
		
			DIMENSIONAL_ELYTRAPLATE.get(),
		
			BLOCK_WALL_CONNECTOR.get(), BLOCK_WALL_CHARGER.get(), BLOCK_WALL_CRAFTER.get(), BLOCK_WALL_SMITHING_TABLE.get(), BLOCK_FOCUS.get(),
			BLOCK_WALL_FURNACE.get(), BLOCK_WALL_BLAST_FURNACE.get(), BLOCK_WALL_ENERGY_DISPLAY.get(), BLOCK_WALL_FLUID_DISPLAY.get(), 
			BLOCK_WALL_ARMOUR_WORKBENCH.get(), BLOCK_WALL_UPGRADE_STATION.get(), BLOCK_WALL_GENERATOR.get(), BLOCK_WALL_ANVIL.get()		
		);

		DimensionalPockets.CONSOLE.startup("Item Colour registration complete..");
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
		CosmosRuntime.Client.regiserCameraOverlay(event, "elytraplate_screen", new ScreenElytraplateVisor());
		
		DimensionalPockets.CONSOLE.startup("GUI Layer registration complete..");
	}

	@SubscribeEvent
	public static void registerMenuScreensEvent(RegisterMenuScreensEvent event) {
		event.register(CONTAINER_TYPE_POCKET.get(), ScreenPocket::new);
		event.register(CONTAINER_TYPE_POCKET_ENHANCED.get(), ScreenPocketEnhanced::new);
		
		event.register(CONTAINER_TYPE_CONNECTOR.get(), ScreenModuleConnector::new);
		event.register(CONTAINER_TYPE_CHARGER.get(), ScreenModuleCharger::new);
		event.register(CONTAINER_TYPE_CRAFTER.get(), ScreenModuleCrafter::new);
		event.register(CONTAINER_TYPE_SMITHING_TABLE.get(), ScreenModuleSmithingTable::new);
		event.register(CONTAINER_TYPE_FURNACE.get(), ScreenModuleFurnace::new);
		event.register(CONTAINER_TYPE_BLAST_FURNACE.get(), ScreenModuleBlastFurnace::new);
		event.register(CONTAINER_TYPE_ARMOUR_WORKBENCH.get(), ScreenModuleArmourWorkbench::new);
		event.register(CONTAINER_TYPE_UPGRADE_STATION.get(), ScreenModuleUpgradeStation::new);
		event.register(CONTAINER_TYPE_GENERATOR.get(), ScreenModuleGenerator::new);
		event.register(CONTAINER_TYPE_ANVIL.get(), ScreenModuleAnvil::new);
		event.register(CONTAINER_TYPE_FOCUS.get(), ScreenFocus::new);
		
		event.register(CONTAINER_TYPE_ELYTRAPLATE_CONNECTOR.get(), ScreenElytraplateConnector::new);
		event.register(CONTAINER_TYPE_ELYTRAPLATE_SETTINGS.get(), ScreenElytraplateSettings::new);
		event.register(CONTAINER_TYPE_ELYTRAPLATE_ENDER_CHEST.get(), ScreenElytraplateEnderChest::new);
		
		DimensionalPockets.CONSOLE.startup("Menu Screen registration complete..");
	}

	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		CosmosRuntime.Server.registerBlockEnergyCapabilities(event, 
			BLOCK_ENTITY_TYPE_POCKET.get(), BLOCK_ENTITY_TYPE_POCKET_ENHANCED.get(), BLOCK_ENTITY_TYPE_CONNECTOR.get()
		);
		
		CosmosRuntime.Server.registerBlockFluidCapabilities(event, 
			BLOCK_ENTITY_TYPE_POCKET.get(), BLOCK_ENTITY_TYPE_POCKET_ENHANCED.get(), BLOCK_ENTITY_TYPE_CONNECTOR.get()
		);
		
		CosmosRuntime.Server.registerItemEnergyCapabilities(event, 
			DIMENSIONAL_SHIFTER.get(), DIMENSIONAL_SHIFTER_ENHANCED.get(),
			DIMENSIONAL_ENERGY_CELL.get(), DIMENSIONAL_ENERGY_CELL_ENHANCED.get(),
			
			DIMENSIONAL_SWORD.get(), DIMENSIONAL_PICKAXE.get(), DIMENSIONAL_AXE.get(), DIMENSIONAL_SHOVEL.get(), DIMENSIONAL_HOE.get(),
			DIMENSIONAL_BOW.get(), DIMENSIONAL_TRIDENT.get(), DIMENSIONAL_SHIELD.get(),
			DIMENSIONAL_HELMET.get(), DIMENSIONAL_CHESTPLATE.get(), DIMENSIONAL_LEGGINGS.get(), DIMENSIONAL_BOOTS.get(),
			
			DIMENSIONAL_SWORD_ENHANCED.get(), DIMENSIONAL_PICKAXE_ENHANCED.get(), DIMENSIONAL_AXE_ENHANCED.get(), DIMENSIONAL_SHOVEL_ENHANCED.get(), DIMENSIONAL_HOE_ENHANCED.get(),
			DIMENSIONAL_BOW_ENHANCED.get(), DIMENSIONAL_TRIDENT_ENHANCED.get(), DIMENSIONAL_SHIELD_ENHANCED.get(),
			DIMENSIONAL_HELMET_ENHANCED.get(), DIMENSIONAL_CHESTPLATE_ENHANCED.get(), DIMENSIONAL_LEGGINGS_ENHANCED.get(), DIMENSIONAL_BOOTS_ENHANCED.get(),
			
			DIMENSIONAL_ELYTRAPLATE.get()
		);
		
		DimensionalPockets.CONSOLE.startup("Capability registration complete..");
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		CosmosRuntime.Client.setRenderLayers(RenderType.cutoutMipped(),
			BLOCK_POCKET.get(), BLOCK_POCKET_ENHANCED.get(),
		
			BLOCK_WALL_CHARGER.get(), BLOCK_WALL_CONNECTOR.get(), BLOCK_WALL_CRAFTER.get(), BLOCK_WALL_SMITHING_TABLE.get(), BLOCK_WALL_FURNACE.get(), BLOCK_WALL_BLAST_FURNACE.get(), 
			BLOCK_WALL_ENERGY_DISPLAY.get(), BLOCK_WALL_FLUID_DISPLAY.get(), BLOCK_WALL_ARMOUR_WORKBENCH.get(), BLOCK_WALL_UPGRADE_STATION.get(), BLOCK_WALL_GENERATOR.get(),
			BLOCK_DIMENSIONAL_CORE.get(), BLOCK_FOCUS.get(), BLOCK_WALL_ANVIL.get(),
		
			BLOCK_WALL_CREATIVE_ENERGY.get(), BLOCK_WALL_CREATIVE_FLUID.get()
		);
		
		CosmosRuntime.Client.setRenderLayers(RenderType.translucent(),
			BLOCK_WALL_GLASS.get()
		);;
		
		ItemProperties.register(DIMENSIONAL_BOW.get(), ResourceLocation.parse("pull"), (stack, level, entity, seed) -> { if (entity == null) { return 0.0F; } else { return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F; }});
		ItemProperties.register(DIMENSIONAL_BOW.get(), ResourceLocation.parse("pulling"), (stack, level, entity, seed) -> { return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F; });
		
		ItemProperties.register(DIMENSIONAL_BOW_ENHANCED.get(), ResourceLocation.parse("pull"), (stack, level, entity, seed) -> { if (entity == null) { return 0.0F; } else { return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F; }});
		ItemProperties.register(DIMENSIONAL_BOW_ENHANCED.get(), ResourceLocation.parse("pulling"), (stack, level, entity, seed) -> { return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F; });
		
		ItemProperties.register(DIMENSIONAL_TRIDENT.get(), ResourceLocation.parse("throwing"), (stack, level, entity, seed) -> { return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F; });
		ItemProperties.register(DIMENSIONAL_TRIDENT_ENHANCED.get(), ResourceLocation.parse("throwing"), (stack, level, entity, seed) -> { return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F; });
		
		ItemProperties.register(DIMENSIONAL_SHIELD.get(), ResourceLocation.parse("blocking"), (stack, level, entity, seed) -> { return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F; });
		ItemProperties.register(DIMENSIONAL_SHIELD_ENHANCED.get(), ResourceLocation.parse("blocking"), (stack, level, entity, seed) -> { return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F; });

		ItemProperties.register(DIMENSIONAL_ENERGY_CELL.get(), ResourceLocation.parse("energy"), (stack, level, entity, seed) -> { return stack.getItem() instanceof CosmosEnergyItem item ? (float) item.getScaledEnergy(stack, 14) : 0.0F; });
//		ItemProperties.register(DIMENSIONAL_ENERGY_CELL.get(), ResourceLocation.parse("active"), (stack, level, entity, seed) -> { return stack.getItem() instanceof DimensionalEnergyCell item ? (item.isActive(stack) ? 1.0F : 0.0F) : 0.0F; });
		
		ItemProperties.register(DIMENSIONAL_ENERGY_CELL_ENHANCED.get(), ResourceLocation.parse("energy"), (stack, level, entity, seed) -> { return stack.getItem() instanceof CosmosEnergyItem item ? (float) item.getScaledEnergy(stack, 14) : 0.0F; });
//		ItemProperties.register(DIMENSIONAL_ENERGY_CELL_ENHANCED.get(), ResourceLocation.parse("active"), (stack, level, entity, seed) -> { return stack.getItem() instanceof DimensionalEnergyCellEnhanced item ? (item.isActive(stack) ? 1.0F : 0.0F) : 0.0F; });
		
//		ItemProperties.register(NETHER_STAR_SHARD.get(), ResourceLocation.parse("stack"), (stack, level, entity, seed) -> { return (float) stack.getCount() < 5 ? 0.25F : stack.getCount() < 9 ? 0.5F : stack.getCount() < 13 ? 0.75F : 1; });
		
		EntityRenderers.register(ENTITY_TYPE_TRIDENT.get(), RendererDimensionalTrident::new);
		EntityRenderers.register(ENTITY_TYPE_TRIDENT_ENHANCED.get(), RendererDimensionalTridentEnhanced::new);
		
		DimensionalPockets.CONSOLE.startup("FMLClientSetup complete..");
	}

    public static <T extends Item> DeferredItem<T> addToBlockTab(DeferredItem<T> itemLike) {
        TAB_BLOCKS.add(itemLike);
        return itemLike;
    }

    public static <A extends Block> DeferredBlock<A> addToBlockTab_(DeferredBlock<A> itemLike) {
        TAB_BLOCKS.add(itemLike);
        return itemLike;
    }
    

    public static <T extends Item> DeferredItem<T> addToItemTab(DeferredItem<T> itemLike) {
        TAB_ITEMS.add(itemLike);
        return itemLike;
    }

    public static <T extends Item> DeferredItem<T> addToToolsTab(DeferredItem<T> itemLike) {
        TAB_TOOLS.add(itemLike);
        return itemLike;
    }

	private static BlockBehaviour.Properties glassProperties(boolean lightEmit) {
		BlockBehaviour.Properties prop = Block.Properties.of()
			.isValidSpawn(PocketsRegistrationManager::neverAllowSpawn)
			.isRedstoneConductor(PocketsRegistrationManager::isntSolid)
			.isSuffocating(PocketsRegistrationManager::isntSolid)
			.isViewBlocking(PocketsRegistrationManager::isntSolid)
			.noOcclusion().sound(SoundType.GLASS).strength(0.3F);
		
		if (lightEmit) {
			return prop.lightLevel((blockState) -> { return 15; });
		}
		
		return prop;
	}

	private static Boolean neverAllowSpawn(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
		return Boolean.valueOf(false);
	}

	private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
		return false;
	}

}