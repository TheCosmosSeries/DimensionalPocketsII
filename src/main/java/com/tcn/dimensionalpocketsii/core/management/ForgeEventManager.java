package com.tcn.dimensionalpocketsii.core.management;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyArmourItemColourable;
import com.tcn.dimensionalpocketsii.DimReference;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.client.screen.ScreenElytraplateVisor;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.ElytraSettings;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.network.PacketDimensionChange;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraItemStackTagUpdate;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraShift;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraUseEnergy;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateOpenConnector;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateOpenEnderChest;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateOpenSettings;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@SuppressWarnings({ "unused" })
@Mod.EventBusSubscriber(modid = DimensionalPockets.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class ForgeEventManager {

	private static final ArrayList<PlacedFeature> overworldOres = new ArrayList<>();
	private static final ArrayList<PlacedFeature> netherOres = new ArrayList<>();
	private static final ArrayList<PlacedFeature> endOres = new ArrayList<>();
	
	private static final String GIVEN_INFO_BOOK = "givenInfoBook";
	
	@SubscribeEvent
	public static void onLivingEquipmentChangeEvent(final LivingEquipmentChangeEvent event) {
		EquipmentSlot slot = event.getSlot();
		
		if (!(slot.equals(EquipmentSlot.MAINHAND)) && !(slot.equals(EquipmentSlot.OFFHAND))) {
			LivingEntity entity = event.getEntityLiving();
	
			if (!(entity instanceof Player)) {
				ItemStack stackTo = event.getTo();
				
				if (stackTo.getItem() instanceof CosmosEnergyArmourItemColourable) {
					CosmosEnergyArmourItemColourable item = (CosmosEnergyArmourItemColourable) stackTo.getItem();
					
					item.setDamage(stackTo, 0);
					
					if (!item.hasEnergy(stackTo)) {
						item.setEnergy(stackTo, 1000);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRenderGameOverlayEvent(RenderGameOverlayEvent.Post event) {
		ScreenElytraplateVisor screenSettings = new ScreenElytraplateVisor();
		
		Minecraft mc = Minecraft.getInstance();
		ElementType type = event.getType();
		Player player = mc.player;
		Inventory playerInventory = player.getInventory();
		
		if (type.equals(ElementType.LAYER)) {
			if (playerInventory.getArmor(2).getItem().equals(ModBusManager.DIMENSIONAL_ELYTRAPLATE)) {
				
				if (screenSettings != null) {
					screenSettings.renderOverlay(event.getMatrixStack());
				}
			}
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onKeyPressEvent(KeyInputEvent event) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer playerIn = mc.player;
		
		if (playerIn != null) {
			Level world = playerIn.level;
			if (ModBusManager.SUIT_SCREEN.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					Item armour = armourStack.getItem();
					
					if (armour instanceof DimensionalElytraplate) {
						DimensionalElytraplate elytraplate = (DimensionalElytraplate) armour;
						
						if (DimensionalElytraplate.hasModuleInstalled(armourStack, BaseElytraModule.SCREEN)) {
							if (elytraplate.hasEnergy(armourStack)) {
								NetworkManager.sendToServer(new PacketElytraplateOpenConnector(playerIn.getUUID(), 2));
								NetworkManager.sendToServer(new PacketElytraUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
							} else {
								CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
							}
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_screen"));
						}
					}
				}
			} 
			
			else if (ModBusManager.SUIT_SCREEN_ENDER_CHEST.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					Item armour = armourStack.getItem();
					
					if (armour instanceof DimensionalElytraplate) {
						DimensionalElytraplate elytraplate = (DimensionalElytraplate) armour;
						
						if (DimensionalElytraplate.hasModuleInstalled(armourStack, BaseElytraModule.ENDER_CHEST)) {
							if (elytraplate.hasEnergy(armourStack)) {
								NetworkManager.sendToServer(new PacketElytraplateOpenEnderChest(playerIn.getUUID(), 2));
								NetworkManager.sendToServer(new PacketElytraUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
							} else {
								CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
							}
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_ender_chest"));
						}
					}
				}
			}  
			
			else if (ModBusManager.SUIT_SETTINGS.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					
					if (armourStack.getItem() instanceof DimensionalElytraplate) {
						DimensionalElytraplate elytraplate = (DimensionalElytraplate) armourStack.getItem();
						
						if (elytraplate.hasEnergy(armourStack)) {
							NetworkManager.sendToServer(new PacketElytraplateOpenSettings(playerIn.getUUID(), 2));
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
						}
					}
				}
			}
		
			else if (ModBusManager.SUIT_SHIFT.isDown()) {
				if (playerIn.getInventory().getArmor(2).getItem() != null) {
					ItemStack armourStack = playerIn.getInventory().getArmor(2);
					Item armour = armourStack.getItem();
					BlockPos player_pos_actual = playerIn.blockPosition();
					
					if (armour instanceof DimensionalElytraplate) {
						DimensionalElytraplate elytraplate = (DimensionalElytraplate) armour;
						
						if (DimensionalElytraplate.hasModuleInstalled(armourStack, BaseElytraModule.SHIFTER)) {
							if (armourStack.hasTag()) {
								CompoundTag stack_nbt = armourStack.getTag();
								
								if (stack_nbt.contains("nbt_data")) {
									CompoundTag nbt_data = stack_nbt.getCompound("nbt_data");
									
									if (nbt_data.contains("chunk_pos")) {
										CompoundTag chunk_pos = nbt_data.getCompound("chunk_pos");
										
										if (nbt_data.contains("player_pos")) {
											CompoundTag player_pos = nbt_data.getCompound("player_pos");
											
											int player_x = player_pos.getInt("x");
											int player_y = player_pos.getInt("y");
											int player_z = player_pos.getInt("z");
											float player_pitch = player_pos.getFloat("pitch");
											float player_yaw = player_pos.getFloat("yaw");
											
											boolean tele_to_block = DimensionalElytraplate.getElytraSetting(armourStack, ElytraSettings.TELEPORT_TO_BLOCK)[1];
											
											CompoundTag dim = nbt_data.getCompound("dimension");
											String namespace = dim.getString("namespace");
											String path = dim.getString("path");
											ResourceLocation loc = new ResourceLocation(namespace, path);
											
											ResourceKey<Level> source_dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, loc);
											
											BlockPos teleport_pos = new BlockPos(player_x, player_y, player_z);
											
											int x = chunk_pos.getInt("x");
											int z = chunk_pos.getInt("z");
											
											CosmosChunkPos chunk = new CosmosChunkPos(x, z);
											
											if (elytraplate.hasEnergy(armourStack)) {
												if (PocketUtil.isDimensionEqual(world, DimensionManager.POCKET_WORLD)) {
													if (tele_to_block) {
														NetworkManager.sendToServer(new PacketElytraUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
														NetworkManager.sendToServer(new PacketElytraShift(playerIn.getUUID(), world.dimension(), chunk));
													} else {
														NetworkManager.sendToServer(new PacketElytraUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
														NetworkManager.sendToServer(new PacketDimensionChange(playerIn.getUUID(), source_dimension, EnumShiftDirection.LEAVE, teleport_pos, player_yaw, player_pitch, false, true, true));
													}
												} else {
													CompoundTag pos_tag = new CompoundTag();
													pos_tag.putInt("x", player_pos_actual.getX());
													pos_tag.putInt("y", player_pos_actual.getY());
													pos_tag.putInt("z", player_pos_actual.getZ());
													pos_tag.putFloat("yaw", playerIn.getRotationVector().y);
													pos_tag.putFloat("pitch", playerIn.getRotationVector().x);
													//pos_tag.putBoolean("tele_to_block", tele_to_block);
													
													CompoundTag dimension = new CompoundTag();
													dimension.putString("namespace", world.dimension().location().getNamespace());
													dimension.putString("path", world.dimension().location().getPath());
													
													nbt_data.put("player_pos", pos_tag);
													nbt_data.put("dimension", dimension);
													stack_nbt.put("nbt_data", nbt_data);
													
													NetworkManager.sendToServer(new PacketElytraItemStackTagUpdate(playerIn.getUUID(), 2, stack_nbt));
													NetworkManager.sendToServer(new PacketElytraUseEnergy(playerIn.getUUID(), 2, elytraplate.getMaxUse(armourStack)));
													NetworkManager.sendToServer(new PacketElytraShift(playerIn.getUUID(), world.dimension(), chunk));
												}
											} else {
												CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_energy"));
											}
										}
									}
								} else {
									CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
								}
							} else {
								CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.not_linked"));
							}
						} else {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.elytraplate.no_shifter"));
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
		BiomeGenerationSettingsBuilder generation = event.getGeneration();
		final List<Supplier<PlacedFeature>> features = generation.getFeatures(Decoration.UNDERGROUND_ORES);
		
		switch (event.getCategory()) {
			case THEEND:
				endOres.forEach((ore) -> features.add(() -> ore));
			case NETHER:
				netherOres.forEach((ore) -> features.add(() -> ore));
			default:
				overworldOres.forEach((ore) -> features.add(() -> ore));
		}
	}
	
	@SubscribeEvent
	public static void onServerSaveEvent(final WorldEvent.Save event) {
		LevelAccessor world = event.getWorld();
		DimensionType type = world.dimensionType();
		
		if (!type.ultraWarm() && type.natural() && !type.piglinSafe() && !type.respawnAnchorWorks() && 
				type.bedWorks() && type.hasRaids() && type.hasSkyLight() && !type.hasCeiling() &&
					type.coordinateScale() == 1 && type.logicalHeight() == 384 && type.minY() == -64 &&
						type.effectsLocation().equals(DimensionType.OVERWORLD_EFFECTS)) {
			PocketRegistryManager.saveData();
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedOutEvent(final PlayerEvent.PlayerLoggedOutEvent event) { }
	
	@SubscribeEvent
	public static void onPlayerLoggedInEvent(final PlayerEvent.PlayerLoggedInEvent event) {
		Entity entity = event.getEntity();
		Level world = entity.level;
		
		if (entity instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) entity;
			
			if (!hasHadBook(player)) {
				CosmosChatUtil.sendServerPlayerMessage(player, DimReference.MESSAGES.WELCOME);
			}
			
			if (!world.isClientSide) {
				checkIfSpawnWithBook(player);
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void clearMap() {
		PocketRegistryManager.clearPocketMap();
	}
	
	public static void registerOresForGeneration() {
		final ConfiguredFeature<?, ?> dimensionalOre = FeatureUtils.register("dimensionalpocketsii:block_dimensional_ore",
			Feature.ORE.configured(new OreConfiguration(List.of(
				OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBusManager.BLOCK_DIMENSIONAL_ORE.defaultBlockState()),
				OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBusManager.BLOCK_DEEPSLATE_DIMENSIONAL_ORE.defaultBlockState())
			),
		6)));

		final PlacedFeature placedDimensionalOre = PlacementUtils.register("dimensionalpocketsii:block_dimensional_ore", 
			dimensionalOre.placed(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(80)), InSquarePlacement.spread(), CountPlacement.of(12))
		);
		overworldOres.add(placedDimensionalOre);
		
		final ConfiguredFeature<?, ?> dimensionalOreNether = FeatureUtils.register("dimensionalpocketsii:block_dimensional_ore_nether",
			Feature.ORE.configured(new OreConfiguration(List.of(
				OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, ModBusManager.BLOCK_DIMENSIONAL_ORE_NETHER.defaultBlockState())
			),
		8)));

		final PlacedFeature placedDimensionalOreNether = PlacementUtils.register("dimensionalpocketsii:block_dimensional_ore_nether", 
			dimensionalOre.placed(HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(48)), InSquarePlacement.spread(), CountPlacement.of(16))
		);
		netherOres.add(placedDimensionalOreNether);

		final ConfiguredFeature<?, ?> dimensionalOreEnd = FeatureUtils.register("dimensionalpocketsii:block_dimensional_ore_end",
			Feature.ORE.configured(new OreConfiguration(List.of(
				OreConfiguration.target(new BlockMatchTest(Blocks.END_STONE), ModBusManager.BLOCK_DIMENSIONAL_ORE_END.defaultBlockState())
			),
		10)));

		final PlacedFeature placedDimensionalOreEnd = PlacementUtils.register("dimensionalpocketsii:block_dimensional_ore_end", 
			dimensionalOre.placed(HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(128)), InSquarePlacement.spread(), CountPlacement.of(20))
		);
		endOres.add(placedDimensionalOreEnd);
	}
	
	private static boolean hasHadBook(Player player) {
		CompoundTag pocketTag = PocketUtil.getPlayerPersistTag(player);
		return pocketTag.getBoolean(GIVEN_INFO_BOOK);
	}

	private static void checkIfSpawnWithBook(Player player) {
		CompoundTag pocketTag = PocketUtil.getPlayerPersistTag(player);
		boolean shouldGiveManual = ConfigurationManager.getInstance().getSpawnWithTome() && !pocketTag.getBoolean(GIVEN_INFO_BOOK);
		
		if (shouldGiveManual) {
			ItemStack infoBook = new ItemStack(ModBusManager.DIMENSIONAL_TOME);
			if (!player.getInventory().add(infoBook)) {
				Level playerWorld = player.level;
				ItemEntity entityItem = new ItemEntity(playerWorld, player.getX(), player.getY(), player.getZ(), infoBook);
				entityItem.setPickUpDelay(0);

				playerWorld.addFreshEntity(entityItem);
				pocketTag.putBoolean(GIVEN_INFO_BOOK, true);
			}
			
			pocketTag.putBoolean(GIVEN_INFO_BOOK, true);
			
			player.getPersistentData().put(DimensionalPockets.MOD_ID, pocketTag);
		}
	}
	
	private static <FC extends FeatureConfiguration> ConfiguredFeature<FC, ?> register(String name, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, DimensionalPockets.MOD_ID + ":" + name, configuredFeature);
	}
}