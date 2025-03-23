package com.tcn.dimensionalpocketsii.core.item.armour;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyArmourItemElytra;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityPocket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DimensionalElytraplateScreen extends CosmosEnergyArmourItemElytra {

	public DimensionalElytraplateScreen(ArmorMaterial materialIn, EquipmentSlot slot, Item.Properties builderIn, boolean damageableIn, CosmosEnergyItem.Properties energyProperties) {
		super(materialIn, slot, builderIn, damageableIn, energyProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.item.info.elytraplate_screen"));
		tooltip.add(ComponentHelper.getTooltipTwo("dimensionalpocketsii.item.info.elytraplate_screen_one"));
		
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			
			if (tag.contains("nbt_data") || tag.contains("moduleList")) {
				CompoundTag nbt_data = tag.getCompound("nbt_data");
				
				if (!ComponentHelper.isControlKeyDown(Minecraft.getInstance())) {
					tooltip.add(ComponentHelper.ctrlForMoreDetails());
				} else {
					if (nbt_data.contains("chunk_pos")) {
						CompoundTag pos_tag = nbt_data.getCompound("chunk_pos");
						
						int x = pos_tag.getInt("x");
						int z = pos_tag.getInt("z");
						
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter.pocket").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_BLUE + x + Value.LIGHT_GRAY + ", " + Value.BRIGHT_BLUE + z + Value.LIGHT_GRAY + " ]")));
					}
					
					if (nbt_data.contains("dimension")) {
						CompoundTag dimension = nbt_data.getCompound("dimension");
						
						String namespace = dimension.getString("namespace");
						String path = dimension.getString("path");
						
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.info.shifter_source_dimension").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.BRIGHT_GREEN + namespace + Value.LIGHT_GRAY + ": " + Value.BRIGHT_GREEN + path + Value.LIGHT_GRAY + " ]")));
					}
					
					tooltip.add(ComponentHelper.ctrlForLessDetails());
				}
			}
		}
		
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public int getMaxEnergyStored(ItemStack stackIn) {
		Item item = stackIn.getItem();
		return !(item instanceof DimensionalElytraplateScreen) ? 0 : ((DimensionalElytraplateScreen)item).maxEnergyStored;
	}
	
	@Override
	public boolean isFlyEnabled(ItemStack stackIn) {
		return false;
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return isFlyEnabled(stack);
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
			if (entity instanceof BlockEntityPocket) {
				Pocket pocket = ((BlockEntityPocket) entity).getPocket();
				
				if (this.addOrUpdateShifterInformation(stack, pocket, world, playerIn)) {
					return InteractionResult.SUCCESS;
				}
			} else {
				return InteractionResult.PASS;
			}
		}
		
		return InteractionResult.FAIL;
	}
	
	public boolean addOrUpdateShifterInformation(ItemStack stackIn, Pocket pocketIn, Level levelIn, Player playerIn) {
		if (pocketIn != null) {
			if (pocketIn.checkIfOwner(playerIn)) {
				CosmosChunkPos chunk_pos = pocketIn.getChunkPos();

				int x = chunk_pos.getX();
				int z = chunk_pos.getZ();

				if (playerIn.isShiftKeyDown()) {
					CompoundTag stack_tag = stackIn.getOrCreateTag();
					CompoundTag nbt_data = new CompoundTag();
					
					CompoundTag chunk_tag = new CompoundTag();
					chunk_tag.putInt("x", x);
					chunk_tag.putInt("z", z);
					nbt_data.put("chunk_pos", chunk_tag);
					
					nbt_data.putInt("colour", pocketIn.getDisplayColour());
					
					CompoundTag dimension = new CompoundTag();
					dimension.putString("namespace", levelIn.dimension().location().getNamespace());
					dimension.putString("path", levelIn.dimension().location().getPath());
					nbt_data.put("dimension", dimension);
					
					stack_tag.put("nbt_data", nbt_data);
					
					stackIn.setTag(stack_tag);
					CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.item.message.elytraplate.linked").append(ComponentHelper.comp(Value.LIGHT_GRAY + " {" + x + ", " + z + "}")));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
}