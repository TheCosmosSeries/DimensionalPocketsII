package com.tcn.dimensionalpocketsii.core.item.device;

import java.util.List;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.lib.MathHelper;
import com.tcn.cosmoslibrary.core.teleport.EnumSafeTeleport;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.AbstractBlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.shift.Shifter;
import com.tcn.dimensionalpocketsii.pocket.core.shift.ShifterCore;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class DimensionalShifterEnhanced extends CosmosEnergyItem {

	public String info = "Allows access to a Pocket from anywhere!";
	public String shift_desc_one = "To link: Shift-Right click this item on a Pocket Block!";
	public String shift_desc_two = "To use: Shift-Right click this item facing air!";
	public String shift_desc_three = "To change mode: Right click this item facing air!";
	public String limitation = "Unlimited Charges.";
	
	public DimensionalShifterEnhanced(Item.Properties properties, CosmosEnergyItem.Properties energyProperties) {
		super(properties, energyProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.shifter_info"));

			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {

			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.shifter_shift_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.info.shifter_shift_two"));
			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.info.shifter_shift_three"));
			tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.info.shifter_enhanced"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}

		if (stack.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag stackTag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			
			if (!ComponentHelper.isControlKeyDown(Minecraft.getInstance())) {
				tooltip.add(ComponentHelper.ctrlForMoreDetails());
			} else {
				if (stackTag.contains("nbt_data")) {
					CompoundTag nbtData = stackTag.getCompound("nbt_data");
					
					if (nbtData.contains("chunk_pos")) {
						CompoundTag pos_tag = nbtData.getCompound("chunk_pos");
						
						int x = pos_tag.getInt("x");
						int z = pos_tag.getInt("z");
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter.pocket").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_BLUE + x + Value.LIGHT_GRAY + ", " + Value.BRIGHT_BLUE + z + Value.LIGHT_GRAY + " ]")));
					}
					
					if (nbtData.contains("player_pos")) {
						CompoundTag playerData = nbtData.getCompound("player_pos");
						
						int x = playerData.getInt("x");
						int y = playerData.getInt("y");
						int z = playerData.getInt("z");
						boolean tele_to_block = playerData.getBoolean("tele_to_block");
						
						if (tele_to_block) {
							tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.item.message.shifter.mode_change_prefix").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ ")
									.append(ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.item.message.shifter.mode_change_true")).append(ComponentHelper.comp(Value.LIGHT_GRAY + " ]"))));
						} else {
							tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.item.message.shifter.mode_change_prefix").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ ")
									.append(ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.item.message.shifter.mode_change_false")).append(ComponentHelper.comp(Value.LIGHT_GRAY + " ]"))));
						}
						
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter_player_pos").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + x + Value.LIGHT_GRAY + ", " + Value.CYAN + y + Value.LIGHT_GRAY + ", " + Value.CYAN + z + Value.LIGHT_GRAY + " ]")));
					}
					
					if (nbtData.contains("dimension_data")) {
						CompoundTag dimension = nbtData.getCompound("dimension_data");
						
						String namespace = dimension.getString("namespace");
						String path = dimension.getString("path");
						
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter_source_dimension").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_GREEN + namespace + Value.LIGHT_GRAY + ": " + Value.BRIGHT_GREEN + path + Value.LIGHT_GRAY + " ]")));
					}
				}
				
				tooltip.add(ComponentHelper.ctrlForLessDetails());
			}
		}

		super.appendHoverText(stack, context, tooltip, flagIn);
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
		return false;
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player playerIn = context.getPlayer();
		
		BlockPos pos = context.getClickedPos();
		Level world = context.getLevel();
		BlockEntity entity = world.getBlockEntity(pos);
		
		if (entity != null) {
			if (entity instanceof AbstractBlockEntityPocket) {
				Pocket pocket = ((AbstractBlockEntityPocket) entity).getPocket();

				//playerIn.swing(context.getHand());
				if (this.addOrUpdateShifterInformation(stack, pocket, world, playerIn)) {
					return InteractionResult.SUCCESS;
				}
			}
		}
		
		return InteractionResult.PASS;
	}

	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		BlockPos player_pos_actual = playerIn.blockPosition();

		if (stack.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag stack_tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
			if (stack_tag.contains("nbt_data")) {
				CompoundTag nbtData = stack_tag.getCompound("nbt_data");
				
				if (playerIn.isShiftKeyDown()) {
					if (nbtData.contains("chunk_pos")) {
						CompoundTag chunkPos = nbtData.getCompound("chunk_pos");

						if (nbtData.contains("player_pos")) {
							CompoundTag playerData = nbtData.getCompound("player_pos");

							if (nbtData.contains("dimension_data")) {
								CompoundTag dimensionData = nbtData.getCompound("dimension_data");

								int[] chunk = new int[] { chunkPos.getInt("x"), chunkPos.getInt("z") };
								CosmosChunkPos savedChunkPos = new CosmosChunkPos(chunk[0], chunk[1]);
								Pocket pocket = StorageManager.getPocketFromChunkPosition(worldIn, savedChunkPos);
								
								int player_x = playerData.getInt("x");
								int player_y = playerData.getInt("y");
								int player_z = playerData.getInt("z");
								float player_pitch = playerData.getFloat("pitch");
								float player_yaw = playerData.getFloat("yaw");
								boolean tele_to_block = playerData.getBoolean("tele_to_block");
								BlockPos teleport_pos = new BlockPos(player_x, player_y, player_z);

								String namespace = dimensionData.getString("namespace");
								String path = dimensionData.getString("path");
								ResourceLocation dimension = ResourceLocation.fromNamespaceAndPath(namespace, path);
								
								ResourceKey<Level> saved_dimension = ResourceKey.create(Registries.DIMENSION, dimension);
								
								if (this.hasEnergy(stack)) {
									if (pocket.exists()) {
										if (PocketUtil.isDimensionEqual(worldIn, ModDimensionManager.POCKET_WORLD)) {
											CosmosChunkPos playerChunkPos = CosmosChunkPos.scaleToChunkPos(playerIn.blockPosition());
											
											if (savedChunkPos.equals(playerChunkPos)) {
												if (tele_to_block) {
													pocket.shift(playerIn, EnumShiftDirection.LEAVE, null, null, stack);
													
													this.extractEnergy(stack, this.getMaxUse(stack), false);
												} else {
													ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
													
													EnumSafeTeleport teleport = EnumSafeTeleport.getValidTeleportLocation(level, teleport_pos);
													boolean teleportSafe = EnumSafeTeleport.isSafeTeleportLocation(level, teleport_pos);
													
													Shifter shifter = Shifter.createTeleporter(saved_dimension, EnumShiftDirection.LEAVE, MathHelper.addBlockPos(teleport_pos, teleport.toBlockPos()), player_yaw, player_pitch, false, true, !teleportSafe);
													
													ShifterCore.shiftPlayerToDimension(playerIn, shifter, shifter.getSafeSpawn());
													
													this.extractEnergy(stack, this.getMaxUse(stack), false);
													return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
												}
											}
										} else {
											CompoundTag playerDataNew = new CompoundTag();
											playerDataNew.putInt("x", player_pos_actual.getX());
											playerDataNew.putInt("y", player_pos_actual.getY());
											playerDataNew.putInt("z", player_pos_actual.getZ());
											playerDataNew.putFloat("yaw", playerIn.getRotationVector().y);
											playerDataNew.putFloat("pitch", playerIn.getRotationVector().x);
											playerDataNew.putBoolean("tele_to_block", tele_to_block);
											
											CompoundTag dimensionDataNew = new CompoundTag();
											dimensionDataNew.putString("namespace", worldIn.dimension().location().getNamespace());
											dimensionDataNew.putString("path", worldIn.dimension().location().getPath());
	
											nbtData.put("player_pos", playerDataNew);
											nbtData.put("dimension_data", dimensionDataNew);
											
											stack_tag.put("nbt_data", nbtData);
											stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
											
											pocket.shift(playerIn, EnumShiftDirection.ENTER, null, null, stack);
											
											this.extractEnergy(stack, this.getMaxUse(stack), false);
											return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
										}
									} else {
										CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
									}
								} else {
									CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.shifter.no_energy"));
								}
							}
						}
					}
				} else {
					if (nbtData.contains("player_pos")) {
						CompoundTag playerData = nbtData.getCompound("player_pos");
						boolean change = playerData.getBoolean("tele_to_block");
						
						playerData.putBoolean("tele_to_block", !change);
						nbtData.put("player_pos", playerData);
						stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
						
						if (!change) {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.item.message.shifter.mode_change")
								.append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.item.message.shifter.mode_change_prefix")
								.append(ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.item.message.shifter.mode_change_true").append(ComponentHelper.comp(Value.LIGHT_GRAY + " ]"))))
							);
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.item.message.shifter.mode_change")
								.append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.item.message.shifter.mode_change_prefix")
								.append(ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.item.message.shifter.mode_change_false").append(ComponentHelper.comp(Value.LIGHT_GRAY + " ]"))))
							);
						}
					}
				}
			}
		} else {
			CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.shifter.not_linked"));
		}
		
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}
	
	public boolean addOrUpdateShifterInformation(ItemStack stackIn, Pocket pocketIn, Level levelIn, Player playerIn) {
		BlockPos player_pos = playerIn.blockPosition();
		
		if (pocketIn != null) {
			if (pocketIn.checkIfOwner(playerIn)) {
				CosmosChunkPos pocket_get_pos = pocketIn.getDominantChunkPos();
		
				CompoundTag stack_tag = new CompoundTag();
				CompoundTag nbt_data = new CompoundTag();
				
				if (playerIn.isShiftKeyDown()) {
					int x = pocket_get_pos.getX();
					int z = pocket_get_pos.getZ();

					CompoundTag chunk_tag = new CompoundTag();
					chunk_tag.putInt("x", x);
					chunk_tag.putInt("z", z);
					nbt_data.put("chunk_pos", chunk_tag);
					
					CompoundTag playerData = new CompoundTag();
					playerData.putInt("x", player_pos.getX());
					playerData.putInt("y", player_pos.getY());
					playerData.putInt("z", player_pos.getZ());
					playerData.putFloat("yaw", playerIn.getRotationVector().y);
					playerData.putFloat("pitch", playerIn.getRotationVector().x);
					playerData.putBoolean("tele_to_block", true);
					nbt_data.put("player_pos", playerData);
					
					CompoundTag dimensionData = new CompoundTag();
					dimensionData.putString("namespace", levelIn.dimension().location().getNamespace());
					dimensionData.putString("path", levelIn.dimension().location().getPath());
					nbt_data.put("dimension_data", dimensionData);

					if (stackIn.has(DataComponents.CUSTOM_DATA)) {
						CompoundTag stackTag = stackIn.get(DataComponents.CUSTOM_DATA).copyTag();
						
						if (stackTag.contains("energy")) {
							stack_tag.putInt("energy", stackTag.getInt("energy"));
							stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
						}
					}

					nbt_data.putInt("colour", pocketIn.getDisplayColour());
					
					stack_tag.put("nbt_data", nbt_data);
					stackIn.set(DataComponents.CUSTOM_DATA, CustomData.of(stack_tag));
					
					CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.item.message.shifter.linked").append(ComponentHelper.comp(Value.LIGHT_GRAY + " {" + x + ", " + z + "}")));
					
					return true;
				}
			} else {
				CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.item.message.shifter.not_owner"));
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

}