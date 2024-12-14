package com.tcn.dimensionalpocketsii.pocket.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.tcn.cosmoslibrary.common.blockentity.CosmosBlockEntityUpdateable;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumGeneralAllowState;
import com.tcn.cosmoslibrary.common.enums.EnumGeneratedState;
import com.tcn.cosmoslibrary.common.enums.EnumLockState;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumTrapState;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IEnergyHolder;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.lib.MathHelper;
import com.tcn.cosmoslibrary.common.nbt.CosmosNBTHelper.Const;
import com.tcn.cosmoslibrary.core.teleport.EnumSafeTeleport;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectBlockPosDimension;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectDestinationInfo;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectPlayerInformation;
import com.tcn.dimensionalpocketsii.ModReferences;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.advancement.CoreTriggers;
import com.tcn.dimensionalpocketsii.core.management.ModConfigManager;
import com.tcn.dimensionalpocketsii.core.management.ModDimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.core.block.AbstractBlockPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallBase;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallEdge;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockWallModule;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityPocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityPocketEnhanced;
import com.tcn.dimensionalpocketsii.pocket.core.gson.PocketChunkInfo;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketEventFactory;
import com.tcn.dimensionalpocketsii.pocket.core.registry.StorageManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.shift.Shifter;
import com.tcn.dimensionalpocketsii.pocket.core.shift.ShifterCore;
import com.tcn.dimensionalpocketsii.pocket.network.packet.system.PacketSystem;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@SuppressWarnings("removal")
public class Pocket implements IEnergyHolder, Container {
	private static final String NBT_DIMENSIONAL_POCKET_KEY = "pocket_data";

	private static final String NBT_OWNER_KEY = "owner";
	private static final String NBT_GENERATED_KEY = "is_generated";
	private static final String NBT_LOCKED_KEY = "is_locked";
	private static final String NBT_ALLOWED_PLAYER_SHIFT_KEY = "allowed_player_shift";
	private static final String NBT_TRAP_KEY = "trap_players";
	private static final String NBT_HOSTILE_SPAWNS_KEY = "allow_hostile_spawns";
	private static final String NBT_COLOUR_KEY = "display_colour";
	private static final String NBT_INTERNAL_HEIGHT_KEY = "internal_height";
	private static final String NBT_ENERGY_STORED_KEY = "energy_stored";
	private static final String NBT_ENERGY_CAPACITY_KEY = "energy_capacity";
	private static final String NBT_ENERGY_RECEIVE_KEY = "energy_max_receive";
	private static final String NBT_ENERGY_EXTRACT_KEY = "energy_max_extract";
	private static final String NBT_BLOCK_DIMENSION_KEY = "source_dimension";
	private static final String NBT_CHUNK_POS_KEY = "chunk_pos";
	private static final String NBT_CHUNK_INFO_KEY = "chunk_info";
	private static final String NBT_LAST_POS_KEY = "last_pos";
	private static final String NBT_SPAWN_POS_KEY = "spawn_pos";
	private static final String NBT_FLUID_TANK_KEY = "fluid_tank";
	private static final String NBT_ALLOWED_PLAYERS_KEY = "allowed_players_array";
	private static final String NBT_BLOCK_ARRAY_KEY = "block_array";
	private static final String NBT_UPDATE_ARRAY_KEY = "update_array";
	private static final String NBT_POCKET_SIDE_KEY = "pocket_side_array";
	private static final String NBT_ITEMS_KEY = "items_array";
	private static final String NBT_SURROUNDING_KEY = "surrounding_array";
	
	@SerializedName(NBT_OWNER_KEY)
	private ObjectPlayerInformation owner;

	@SerializedName(NBT_GENERATED_KEY)
	private EnumGeneratedState is_generated = EnumGeneratedState.UNGENERATED;

	@SerializedName(NBT_LOCKED_KEY)
	private EnumLockState is_locked = EnumLockState.UNLOCKED;

	@SerializedName(NBT_ALLOWED_PLAYER_SHIFT_KEY)
	private EnumGeneralAllowState allowed_players_shift = EnumGeneralAllowState.ALLOWED;
	
	@SerializedName(NBT_TRAP_KEY)
	private EnumTrapState trap_players = EnumTrapState.FREE;
	
	@SerializedName(NBT_HOSTILE_SPAWNS_KEY)
	private EnumGeneralAllowState allow_hostile_spawns = EnumGeneralAllowState.BLOCKED;
	
	@SerializedName(NBT_COLOUR_KEY)
	private int display_colour = ComponentColour.POCKET_PURPLE.dec();
	
	@SerializedName(NBT_INTERNAL_HEIGHT_KEY)
	private int internal_height = ModConfigManager.getInstance().getInternalHeight();
	
	@SerializedName(NBT_ENERGY_STORED_KEY)
	private int energy_stored = 0;
	
	@SerializedName(NBT_ENERGY_CAPACITY_KEY)
	public int energy_capacity = ModReferences.CONSTANT.POCKET_FE_CAP;
	
	@SerializedName(NBT_ENERGY_RECEIVE_KEY)
	public int energy_max_receive = ModReferences.CONSTANT.POCKET_FE_REC;
	
	@SerializedName(NBT_ENERGY_EXTRACT_KEY)
	public int energy_max_extract = ModReferences.CONSTANT.POCKET_FE_EXT;
	
	@SerializedName(NBT_BLOCK_DIMENSION_KEY)
	private ResourceLocation block_dimension = ResourceLocation.parse("");

	@SerializedName(NBT_CHUNK_POS_KEY)
	public CosmosChunkPos chunk_pos = CosmosChunkPos.ZERO;

	@SerializedName(NBT_CHUNK_INFO_KEY)
	public PocketChunkInfo chunk_info = null;

	@SerializedName(NBT_LAST_POS_KEY)
	private ObjectBlockPosDimension last_pos = new ObjectBlockPosDimension(BlockPos.ZERO, ResourceLocation.parse(""));

	@SerializedName(NBT_SPAWN_POS_KEY)
	private ObjectDestinationInfo spawn_pos = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
	
	@SerializedName(NBT_FLUID_TANK_KEY)
	public ObjectFluidTankCustom fluid_tank = new ObjectFluidTankCustom(new FluidTank(ModReferences.CONSTANT.POCKET_FLUID_CAP), 0);

	@SerializedName(NBT_ALLOWED_PLAYERS_KEY)
	private ArrayList<String> allowed_players_array = new ArrayList<String>();
	
	@SerializedName(NBT_UPDATE_ARRAY_KEY)
	private ArrayList<BlockPos> update_array = new ArrayList<BlockPos>();
	
	@SerializedName(NBT_BLOCK_ARRAY_KEY)
	private LinkedHashMap<Integer, ObjectBlockPosDimension> block_array = new LinkedHashMap<>();

	@SerializedName(NBT_POCKET_SIDE_KEY)
	public EnumSideState[] pocket_side_array = EnumSideState.STANDARD.clone();
	
	@SerializedName(NBT_ITEMS_KEY)
	public NonNullList<ItemStack> item_array = NonNullList.<ItemStack>withSize(ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE, ItemStack.EMPTY);
	
	@SerializedName(NBT_SURROUNDING_KEY)
	public NonNullList<ItemStack> surrounding_array = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);
	
	protected Pocket() { }
	
	public Pocket(PocketChunkInfo infoIn) {
		this.chunk_info = infoIn;
	}

	public Pocket(CosmosChunkPos chunkPosIn, boolean isSingleChunk, ResourceKey<Level> blockDimensionIn, BlockPos blockPos) {
		this.setSourceBlockDimension(blockDimensionIn);
		this.addPosToBlockArray(blockPos, blockDimensionIn.location());
		this.setSpawnInPocket(isSingleChunk ? new BlockPos(7, 2, 7) : new BlockPos(15, 2, 15), 0.0F, 0.0F);
		this.chunk_info = new PocketChunkInfo(chunkPosIn, isSingleChunk);
		this.chunk_pos = chunkPosIn;
		
		this.setInternalHeight(isSingleChunk ? ModConfigManager.getInstance().getInternalHeight() : ModConfigManager.getInstance().getInternalHeightEnhanced());
	}
	
	public boolean exists() {
		return this.chunk_info != null;
	}
	
	public boolean isSourceBlockPlaced() {
		return this.getSourceBlock() instanceof AbstractBlockPocket;
	}
	
	public Block getSourceBlock() {
		Level world = this.getSourceBlockLevel();
		
		if (world == null) {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket] <getsourceblock> Dimension: " + this.block_dimension + " returned <null>!! (Mystcraft or GalactiCraft world?). Will return <null>. This will cause a problem.");
			return null;
		}
		
		return world.getBlockState(this.getLastBlockPos()).getBlock();
	
	}
	
	public BlockState getSourceBlockState() {
		Level world = this.getSourceBlockLevel();
		
		if (world == null) {
			DimensionalPockets.CONSOLE.debugWarn("[Pocket] <getsourceblock> Dimension: " + this.block_dimension + " returned <null>!! (Mystcraft or GalactiCraft world?). Will return <null>. This will cause a problem.");
			return null;
		}
		
		return world.getBlockState(this.getLastBlockPos());
	}
	
	public BlockPos getSourceBlockPos() {
		return this.getLastBlockPos();
	}
	
	public void setSourceBlockDimension(ResourceKey<Level> type) {
		this.block_dimension = type.location();
	}
	
	public ResourceKey<Level> getSourceBlockDimension() {
		return ResourceKey.create(Registries.DIMENSION, this.block_dimension);
	}
	
	public Level getSourceBlockLevel() {
		return getLevelFromResource(block_dimension);
	}

	public Level getLevelFromResource(ResourceLocation location) {
		return ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, location));
	}
	
	public void resetSourceBlock() {
		for (int i = 0; i < this.block_array.size(); i++) {
			ObjectBlockPosDimension object = this.block_array.get(i);
			
			Level level = this.getLevelFromResource(object.getDimension());
			BlockPos pos = object.getPos();
			
			if (level.getBlockState(pos).getBlock() instanceof AbstractBlockPocket) {
				this.last_pos = object;
				this.setSourceBlockDimension(ResourceKey.create(Registries.DIMENSION, object.getDimension()));
				
				DimensionalPockets.CONSOLE.debug("[Pocket] <resetsourceblock> Pocket Source Block has been reset to: [" + pos + "]");
				break;
			}
		}
	}

	public EnumGeneratedState getGeneratedState() {
		return this.is_generated;
	}
	
	public boolean getGeneratedStateValue() {
		return this.is_generated.getValue();
	}

	public void setGeneratedState(EnumGeneratedState state) {
		this.is_generated = state;
	}
	
	public void setGeneratedState(boolean change) {
		this.setGeneratedState(EnumGeneratedState.getStateFromValue(change));
	}

	/** - LockState - */
	public EnumLockState getLockState() {
		return this.is_locked;
	}

	public boolean getLockStateValue() {
		return this.is_locked.getValue();
	}
	
	public void setLockState(EnumLockState state) {
		this.is_locked = state;
		
		//this.forceUpdateOutsidePocket();
	}
	
	public void setLockState(boolean change) {
		this.setLockState(EnumLockState.getStateFromValue(change));
	}
	
	/** - AllowedPlayerShift - */
	public EnumGeneralAllowState getAllowedPlayerState() {
		return this.allowed_players_shift;
	}

	public boolean getAllowedPlayerStateValue() {
		return this.allowed_players_shift.getValue();
	}
	
	public void setAllowedPlayerState(EnumGeneralAllowState state) {
		this.allowed_players_shift = state;
	}
	
	public void setAllowedPlayerState(boolean change) {
		this.setAllowedPlayerState(EnumGeneralAllowState.getStateFromValue(change));
	}
	
	/** - Hostile Spawn State - */
	public EnumGeneralAllowState getHostileSpawnState() {
		return this.allow_hostile_spawns;
	}

	public boolean getHostileSpawnStateValue() {
		return this.allow_hostile_spawns.getValue();
	}
	
	public void setHostileSpawnState(EnumGeneralAllowState state) {
		this.allow_hostile_spawns = state;
	}
	
	public void setHostileSpawnState(boolean change) {
		this.setHostileSpawnState(EnumGeneralAllowState.getStateFromValue(change));
	}
	
	public EnumTrapState getTrapState() {
		return this.trap_players;
	}
	
	public boolean getTrapStateValue() {
		return this.trap_players.getValue();
	}
	
	public void setTrapState(EnumTrapState state) {
		this.trap_players = state;
	}
	
	public void setTrapState(boolean change) {
		this.setTrapState(EnumTrapState.getStateFromValue(change));
	}
	
	public int getDisplayColour() {
		return this.display_colour;
	}
	
	public void setDisplayColour(Player playerIn, Level levelIn, int colourIn) {
		if (!(this.display_colour == colourIn)) {
			this.display_colour = colourIn;
			
			//if (this.updateBaseConnectors(levelIn)) {
				this.updateWallBlocks(playerIn, levelIn);
			//}
		}
	}
	
	public void setDisplayColour(ComponentColour colour) {
		this.display_colour = colour.dec();
	}
	
	private void setDisplayColour(int colourIn) {
		this.display_colour = colourIn;
	}

	public int getInternalHeight() {
		return this.internal_height;
	}
	
	public void setInternalHeight(int height) {
		this.internal_height = height;
	}

	public BlockPos getLastBlockPos() {
		return this.last_pos.getPos();
	}
	
	public ObjectBlockPosDimension getLastBlock() {
		return this.last_pos;
	}
	
	public BlockPos getLastBlockPosPlus(BlockPos pos) {
		return MathHelper.addBlockPos(this.getLastBlockPos(), pos);
	}
	
	private BlockPos getSpawnPos() {
		return spawn_pos.getPos();
	}

	public void updatePocketSpawnPos(BlockPos pos) {
		this.spawn_pos.setPos(pos);

		PacketDistributor.sendToServer(new PacketSystem(true));
	}

	public void setSpawnInPocket(BlockPos posIn, float yawIn, float pitchIn) {
		this.spawn_pos = new ObjectDestinationInfo(posIn, yawIn, pitchIn);
	}
	
	public PocketChunkInfo getChunkInfo() {
		return this.chunk_info;
	}
	
	public CosmosChunkPos getDominantChunkPos() {
		return this.chunk_info.getDominantChunk();
	}
	
	public boolean updateOwner(@Nullable ServerPlayer oldPlayerIn, ServerPlayer newPlayerIn) {
		if (oldPlayerIn != null) {
			ObjectPlayerInformation oldInfo = new ObjectPlayerInformation(oldPlayerIn);

			if (this.owner.equals(oldInfo)) {
				CosmosChatUtil.sendPlayerMessageServer(oldPlayerIn, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.command.transfer.success.old").append(ComponentHelper.style(ComponentColour.CYAN, newPlayerIn.getDisplayName().getString())));
				CosmosChatUtil.sendPlayerMessageServer(newPlayerIn, ComponentHelper.style(ComponentColour.CYAN, oldPlayerIn.getDisplayName().getString()).append(ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.command.transfer.success.new")));
				
				this.setOwnerInternal(newPlayerIn);
				
				return true;
			} else {
				CosmosChatUtil.sendPlayerMessageServer(oldPlayerIn, ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.command.transfer.error.not_owner"));
				return false;
			}
		} else {
			CosmosChatUtil.sendPlayerMessageServer(newPlayerIn, ComponentHelper.style(ComponentColour.CYAN, this.getOwnerName()).append(ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.command.transfer.success.new")));
			
			this.setOwnerInternal(newPlayerIn);
			
			return true;
		}
	}
	
	private void setOwnerInternal(Player playerIn) {
		this.owner = new ObjectPlayerInformation(playerIn);
		this.addAllowedPlayer(playerIn, true);
	}

	public void setOwner(Player playerIn) {
		if (playerIn != null) {
			if (!this.getGeneratedStateValue() || this.owner == null) {
				this.owner = new ObjectPlayerInformation(playerIn);
				this.addAllowedPlayer(playerIn, true);
			}
		}
	}
	
	public boolean checkIfOwner(Player playerIn) {
		String player_name = playerIn.getDisplayName().getString();
		UUID player_uuid = playerIn.getUUID();
		
		if (this.owner != null) {
			if (this.owner.getPlayerUUID().equals(player_uuid)) {
				if (!this.owner.getPlayerName().equals(player_name)) {
					this.owner.setPlayerName(player_name);
				}
				return true;
			}
		}
		return false;
	}
	
	public ObjectPlayerInformation getOwner() {
		return this.owner;
	}
	
	public String getOwnerName() {
		return this.owner.getPlayerName();
	}
	
	protected UUID getOwnerUUID() {
		return this.owner.getPlayerUUID();
	}
	
	/** - Block Array Things - */
	public void addPosToBlockArray(BlockPos posIn, ResourceLocation dimensionIn) {
		this.addPosToBlockArray(new ObjectBlockPosDimension(posIn, dimensionIn), true);
	}

	public void addPosToBlockArray(ObjectBlockPosDimension objectIn, boolean updateLastPos) {
		if (!this.doesBlockArrayContain(objectIn.getPos(), objectIn.getDimension())) {
			this.block_array.put(block_array.size(), objectIn);
		}
		
		if (updateLastPos) {
			this.last_pos = objectIn;
		}
	}
	
	public boolean doesBlockArrayContain(BlockPos pos, ResourceLocation dimensionIn) {
		for (int i = 0; i < this.block_array.size(); i++) {
			BlockPos checkPos = this.block_array.get(i).getPos();
			ResourceLocation checkLocation = this.block_array.get(i).getDimension();
			
			if (checkLocation.getNamespace().equals("") || checkLocation.getPath().equals("")) {
				if (checkPos.equals(pos)) {
					return true;
				}
			} else {
				if (checkPos.equals(pos) && checkLocation.equals(dimensionIn)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/** - Updateable Array - */
	public void addUpdateable(BlockPos posIn) {
		if (this.update_array.contains(posIn)) {
			DimensionalPockets.CONSOLE.debug("[Pocket] <addupdateable> Array already contains: [" + posIn + "]. Array will not be updated.");
		} else {
			this.update_array.add(posIn);
			DimensionalPockets.CONSOLE.debug("[Pocket] <addupdateable> BlockPos: [" + posIn + "] added.");
		}
	}
	
	public void removeUpdateable(BlockPos posIn) {
		if (this.update_array.contains(posIn)) {
			this.update_array.remove(posIn);
			DimensionalPockets.CONSOLE.debug("[Pocket] <removeupdateable> BlockPos: [" + posIn + "] removed.");
		} else {
			DimensionalPockets.CONSOLE.debug("[Pocket] <removeupdateable> Array does not contain: [" + posIn + "].");
		}
	}
	
	public void forceUpdateInsidePocket() {
		Level level = StorageManager.getServerLevel();
		
		if (level != null) {
			for (BlockPos pos : this.update_array) {
				BlockEntity entity = level.getBlockEntity(pos);
				
				if (entity != null) {
					if (entity instanceof CosmosBlockEntityUpdateable) {
						CosmosBlockEntityUpdateable blockEntity = (CosmosBlockEntityUpdateable) entity;
						
						blockEntity.sendUpdates(true);
					} else {
						BlockState state = level.getBlockState(pos);
						
						level.sendBlockUpdated(pos, state, state, 3);
						level.markAndNotifyBlock(pos, level.getChunkAt(pos), state, state, 3, 0);
					}
				} else {
					BlockState state = level.getBlockState(pos);
					
					level.sendBlockUpdated(pos, state, state, 3);
					level.markAndNotifyBlock(pos, level.getChunkAt(pos), state, state, 3, 0);
				}
			}
		}
	}
	
	public void forceUpdateOutsidePocket() {
		Level level = this.getSourceBlockLevel();
		
		if (level != null) {
			BlockEntity entity = level.getBlockEntity(this.getLastBlockPos());
			
			if (entity instanceof BlockEntityPocket) {
				BlockEntityPocket blockEntity = (BlockEntityPocket) entity;
				
				blockEntity.sendUpdates(true);
			}
			
			if (entity instanceof BlockEntityPocketEnhanced) {
				BlockEntityPocketEnhanced blockEntity = (BlockEntityPocketEnhanced) entity;
				
				blockEntity.sendUpdates(true);
			}
		}
	}
	
	/** - Allowed Player Things - */
	public ArrayList<String> getAllowedPlayersArray() {
		return this.allowed_players_array;
	}
	
	public void addAllowedPlayer(Player playerIn, boolean replace) {
		if (!this.checkIfAllowedPlayer(playerIn)) {
			String player = playerIn.getDisplayName().getString();
			if (replace) {
				if (this.allowed_players_array.size() > 0) {
					this.allowed_players_array.set(0, player);
				} else {
					this.allowed_players_array.add(player);
				}
			} else {
				this.allowed_players_array.add(player);
			}
		}
	}
	
	public void addAllowedPlayerNBT(String playerNameIn) {
		if (!this.checkIfAllowedPlayerNBT(playerNameIn)) {
			this.allowed_players_array.add(playerNameIn);
		}
	}
	
	private boolean checkIfAllowedPlayerNBT(String playerNameIn) {
		for (int i = 0; i < this.allowed_players_array.size(); i++) {
			String test_player = this.allowed_players_array.get(i);
			
			if (test_player.equals(playerNameIn)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeAllowedPlayer(Player playerIn) {
		String player_name = playerIn.getDisplayName().getString();
		
		for (int i = 0; i < this.allowed_players_array.size(); i++) {
			String test_player = this.allowed_players_array.get(i);
			
			if (!this.checkIfOwner(playerIn)) {
				if (test_player.equals(player_name)) {
					this.allowed_players_array.remove(i);
				}
			}
		}
	}
	
	public void removeAllowedPlayerNBT(String playerNameIn) {
		for (int i = 0; i < this.allowed_players_array.size(); i++) {
			String test_player = this.allowed_players_array.get(i);
			
			if (this.owner != null) {
				if (!this.owner.getPlayerName().equals(playerNameIn)) {
					if (test_player.equals(playerNameIn)) {
						this.allowed_players_array.remove(i);
					}
				}
			} else {
				if (test_player.equals(playerNameIn)) {
					this.allowed_players_array.remove(i);
				}
			}
		}
	}
	
	public boolean checkIfAllowedPlayer(Player playerIn) {
		String player_name = playerIn.getDisplayName().getString();
		
		for (int i = 0; i < this.allowed_players_array.size(); i++) {
			String test_player = this.allowed_players_array.get(i);
			
			if (test_player.equals(player_name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIfPlayerCanShift(Player playerIn, EnumShiftDirection directionIn) {
		if (this.checkIfOwner(playerIn)) {
			return true;
		} else {
			if (this.getAllowedPlayerStateValue()) {
				if (this.getLockStateValue()) {
					return this.checkIfAllowedPlayer(playerIn);
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}
	
	public boolean canPlayerLeave(Player playerIn) {		
		return this.checkIfOwner(playerIn) || this.checkIfAllowedPlayer(playerIn) ? true : !this.getTrapStateValue();
	}
	
	/** 
	 * - Energy Methods 
	 */
	public void setMaxIO(int maxTransfer) {
		this.setMaxReceive(maxTransfer);
		this.setMaxExtract(maxTransfer);
	}

	public void setMaxReceive(int max_receive) {
		this.energy_max_receive = max_receive;
	}

	public void setMaxExtract(int max_extract) {
		this.energy_max_extract = max_extract;
	}

	public int getMaxReceive() {
		return this.energy_max_receive;
	}

	public int getMaxExtract() {
		return this.energy_max_extract;
	}
	
	public void setEnergyStored(int stored) {
		this.energy_stored = stored;

		if (this.energy_stored > energy_capacity) {
			this.energy_stored = energy_capacity;
		} else if (this.energy_stored < 0) {
			this.energy_stored = 0;
		}
	}
	
	public void modifyEnergyStored(int stored) {
		this.energy_stored += stored;

		if (this.energy_stored > this.energy_capacity) {
			this.energy_stored = energy_capacity;
		} else if (this.energy_stored < 0) {
			this.energy_stored = 0;
		}
	}
	
	public int receiveEnergy(int max_receive, boolean simulate) {
		int storedReceived = Math.min(this.getMaxEnergyStored() - energy_stored, Math.min(this.energy_max_receive, max_receive));

		if (!simulate) {
			this.energy_stored += storedReceived;
		}
		
		return storedReceived;
	}

	public int extractEnergy(int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));

		if (!simulate) {
			this.energy_stored -= storedExtracted;
		}
		
		return storedExtracted;
	}

	public int getEnergyStored() {
		return this.energy_stored;
	}

	public int getMaxEnergyStored() {
		return this.energy_capacity;
	}
	
	public boolean hasEnergyStored() {
		return this.energy_stored > 0;
	}
	
	public int getEnergyStoredScaled(int scale) {
		return ((this.getEnergyStored() / 100) * scale) / (this.getMaxEnergyStored() / 100);
	}
	
	public boolean canExtractEnergy() {
		return this.hasEnergyStored();
	}
	
	public boolean canReceiveEnergy() {
		return this.getEnergyStored() < this.getMaxEnergyStored();
	}

	/** - FluidHandler Start */
	
	public int getTanks() {
		return 1;
	}
	
	public FluidStack getFluidInTank() {
		return this.getFluidTank().getFluid();
	}
	
	public int getFluidLevelScaled(int one) {
		float scaled = this.getCurrentFluidAmount() * one / this.getFluidTankCapacity() + 1;
		
		if (scaled == 0 && this.getCurrentFluidAmount() > 0) {
			return 1;
		} else {
			return (int) scaled;
		}
	}

	public int getCurrentFluidAmount() {
		return this.fluid_tank.getFluidTank().getFluidAmount();
	}
	
	public Fluid getCurrentStoredFluid() {
		this.updateFluidFillLevel();
		
		if (!this.isFluidTankEmpty()) {
			return this.fluid_tank.getFluidTank().getFluid().getFluid();
		}
		return null;
	}
	
	public String getCurrentStoredFluidName() {
		if (this.isFluidTankEmpty()) {
			return "Empty";
		}
		return this.fluid_tank.getFluidTank().getFluid().getTranslationKey();
	}

	public boolean isFluidTankEmpty() {
		return this.fluid_tank.getFluidTank().getFluidAmount() == 0;
	}

	public int getFluidTankCapacity() {
		return this.fluid_tank.getFluidTank().getCapacity();
	}
	
	public int getFluidFillLevel() {
		return this.fluid_tank.getFillLevel();
	}

	public void setFluidFillLevel(int set) {
		this.fluid_tank.setFillLevel(set);
	}
	
	public void updateFluidFillLevel() {
		if (!this.isFluidTankEmpty()) {
			if (this.getFluidLevelScaled(16) == 0) {
				this.fluid_tank.setFillLevel(1);
			} else {
				this.fluid_tank.setFillLevel(this.getFluidLevelScaled(16));
			}
		} else {
			this.fluid_tank.setFillLevel(0);
		}
	}
	
	public int fill(FluidStack resource, FluidAction doFill) {
		this.updateFluidFillLevel();
		return this.fluid_tank.getFluidTank().fill(resource, doFill);
	}

	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		this.updateFluidFillLevel();
		
		return this.fluid_tank.getFluidTank().drain(resource.getAmount(), doDrain);
	}

	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		this.updateFluidFillLevel();
		return this.fluid_tank.getFluidTank().drain(maxDrain, doDrain);
	}
	
	public boolean canFill(Direction from, Fluid fluid) {
		return true;
	}
	
	public boolean canDrain(Direction from, Fluid fluid) {
		return true;
	}
	
	public FluidTank getFluidTank() {
		return this.fluid_tank.getFluidTank();
	}
	
	public void setFluidTank(FluidTank tank) {
		this.fluid_tank.setFluidTank(tank);
	}
	
	public void emptyFluidTank() {
		this.fluid_tank.getFluidTank().setFluid(FluidStack.EMPTY);
	}
	
	/** - Side State Things - */
	
	public EnumSideState getSide(Direction facing) {
		return this.pocket_side_array[facing.get3DDataValue()];
	}
	
	public void setSide(Direction facing, EnumSideState side_state, boolean update) {
		this.pocket_side_array[facing.get3DDataValue()] = side_state;
	}
	
	public EnumSideState[] getSideArray() {
		return this.pocket_side_array;
	}

	public void setSideArray(EnumSideState[] new_array, boolean update) {
		this.pocket_side_array = new_array;
	}

	public void cycleSide(Direction facing, boolean update) {
		EnumSideState state = this.pocket_side_array[facing.get3DDataValue()];
		
		this.setSide(facing, state.getNextState(), update);
	}

	public boolean canConnect(Direction direction) {
		EnumSideState state = this.pocket_side_array[direction.get3DDataValue()];
		
		if (state.equals(EnumSideState.DISABLED)) {
			return false;
		}
		
		return true;
	}

	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack stackInSlot = this.getItem(slot);

		int m;
		if (!stackInSlot.isEmpty()) {
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), 64)) {
				return stack;
			}
						
			if (stack.getCount() <= stackInSlot.getMaxStackSize() && ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
				return stack;
			}
				
			if (!this.canPlaceItem(slot, stack)) {
				return stack;
			}

			m = Math.min(stack.getMaxStackSize(), 64) - stackInSlot.getCount();

			if (stack.getCount() <= m) {
				if (!simulate) {
					ItemStack copy = stack.copy();
					copy.grow(stackInSlot.getCount());
					this.setItem(slot, copy);
				}

				return ItemStack.EMPTY;
			} else {
				stack = stack.copy();
				if (!simulate) {
					ItemStack copy = stack.split(m);
					copy.grow(stackInSlot.getCount());
					this.setItem(slot, copy);
					return stack;
				} else {
					stack.shrink(m);
					return stack;
				}
			}
		} else {
			if (!this.canPlaceItem(slot, stack))
				return stack;

			m = Math.min(stack.getMaxStackSize(), 64);
			if (m < stack.getCount()) {
				
				stack = stack.copy();
				if (!simulate) {
					this.setItem(slot, stack.split(m));
					return stack;
				} else {
					stack.shrink(m);
					return stack;
				}
			} else {
				if (!simulate) {
					this.setItem(slot, stack);
				}
				return ItemStack.EMPTY;
			}
		}
	}

	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		ItemStack stackInSlot = this.getItem(slot);

		if (stackInSlot.isEmpty()) {
			return ItemStack.EMPTY;
		}

		if (simulate) {
			if (stackInSlot.getCount() < amount) {
				return stackInSlot.copy();
			} else {
				ItemStack copy = stackInSlot.copy();
				copy.setCount(amount);
				return copy;
			}
		} else {
			int m = Math.min(stackInSlot.getCount(), amount);

			ItemStack decrStackSize = this.removeItem(slot, m);
			return decrStackSize;
		}
	}

	@Override
	public boolean canPlaceItem(int indexIn, ItemStack stackIn) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.item_array) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getContainerSize() {
		return this.item_array.size();
	}

	@Override
	public ItemStack getItem(int index) {
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE) {
			return this.item_array.get(index);
		} else {
			return this.surrounding_array.get(index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE);
		}
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE) {
			return ContainerHelper.removeItem(this.item_array, index, count);
		} else {
			return ContainerHelper.removeItem(this.surrounding_array, index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE, count);
		}
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE) {
			return ContainerHelper.takeItem(this.item_array, index);
		} else {
			return ContainerHelper.takeItem(this.surrounding_array, index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE);
		}
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
		if (index < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE) {
			this.item_array.set(index, stack);
		} else {
			this.surrounding_array.set(index - ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE, stack);
		}
		
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
	}
	
	@Override
	public void setChanged() {
		//PocketRegistryManager.saveData();
	}
	
	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public void clearContent() { }
	
	public void setSurroundingStacks(Level levelIn, BlockPos posIn) {
		if (!levelIn.isClientSide) {
			for (Direction dir : Direction.values()) {
				Block block_other = levelIn.getBlockState(posIn.offset(dir.getNormal())).getBlock();
				this.setItem(dir.get3DDataValue() + ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE, new ItemStack(block_other));
			}
		}
	}
	
	public void updateWallBlocks(Player playerIn, Level levelIn) {
		if (!levelIn.isClientSide) {
			if (playerIn != null) {
				if (playerIn.level().dimension().equals(ModDimensionManager.POCKET_WORLD)) {
					int bottomYOffset = StorageManager.getPocketYOffset() + 2;
					int[] spacing = { 1, 3 };
					
					this.updateColumn(levelIn, this.getDominantChunkPos(), new BlockPos(1, 0, -1), new int[] { bottomYOffset, 0, ThreadLocalRandom.current().nextInt(spacing[0], spacing[1]) });
					this.updateColumn(levelIn, this.getDominantChunkPos(), new BlockPos(-1, 0, 1), new int[] { bottomYOffset, 0, ThreadLocalRandom.current().nextInt(spacing[0], spacing[1]) });
					
					if (!this.getChunkInfo().isSingleChunk()) {
						//this.updateColumn(levelIn, this.getChunkInfo().getChunks().get(1), new BlockPos(13, 0, 01), new int[] { bottomYOffset, 0, ThreadLocalRandom.current().nextInt(spacing[0], spacing[1]) });
						this.updateColumn(levelIn, this.getChunkInfo().getChunks().get(2), new BlockPos(01, 0, 15), new int[] { bottomYOffset, 0, ThreadLocalRandom.current().nextInt(spacing[0], spacing[1]) });
						this.updateColumn(levelIn, this.getChunkInfo().getChunks().get(3), new BlockPos(15, 0, 13), new int[] { bottomYOffset, 0, ThreadLocalRandom.current().nextInt(spacing[0], spacing[1]) });
					}

					StorageManager.saveRegistry(levelIn.registryAccess());
				}
			}
		}
	}
	
	public void updateColumn(Level levelIn, CosmosChunkPos chunkPosIn, BlockPos posIn, int[] argsIn) {
		BlockPos worldPos = new BlockPos(CosmosChunkPos.scaleFromChunkPos(chunkPosIn).getX(), 0, CosmosChunkPos.scaleFromChunkPos(chunkPosIn).getZ());
		
		if (this.updateConnector(levelIn, chunkPosIn)) {
			for (int y = argsIn[0] + argsIn[1]; y < this.getInternalHeight(); y += argsIn[2]) {
				LevelChunk chunkIn = levelIn.getChunkAt(worldPos);
				BlockPos actualPos = MathHelper.addBlockPos(worldPos, new BlockPos(posIn.getX(), y, posIn.getZ()));
				
				BlockState oldState = Blocks.STONE.defaultBlockState();
				BlockState newState = Blocks.AIR.defaultBlockState();
				
				levelIn.setBlockAndUpdate(actualPos, oldState);
				
				levelIn.setBlocksDirty(actualPos, oldState, newState);
				levelIn.markAndNotifyBlock(actualPos, chunkIn, oldState, newState, 3, 3);
				levelIn.updateNeighborsAt(actualPos, newState.getBlock());
				chunkIn.setBlockState(actualPos, newState, true);
				levelIn.sendBlockUpdated(actualPos, oldState, newState, 3);
				
				levelIn.setBlockAndUpdate(actualPos, newState);
			}
		}
	}
	
	public boolean updateBaseConnectors(Level levelIn) {
		boolean[] checks = new boolean[] { false, false, false, false };
		
		checks[0] = this.updateConnector(levelIn, this.getDominantChunkPos());
		
		if (!this.getChunkInfo().isSingleChunk()) {
			ArrayList<CosmosChunkPos> chunks = this.getChunkInfo().getChunks();
			
			for (int i = 1; i < chunks.size(); i++) {
				CosmosChunkPos posToUpdate = chunks.get(i);
				
				checks[i] = this.updateConnector(levelIn, posToUpdate);
			}

			//System.out.println("Multichunk [ " + checks[0] + ", " + checks[1] + ", " + checks[2] + ", " + checks[3] + " ]");
			return checks[0] && checks[1] && checks[2] && checks[3];
		}
		
		//System.out.println("Singlechunk [ " + checks[0] + " ]");
		return checks[0];
	}
	
	public boolean updateConnector(Level levelIn, CosmosChunkPos chunkPosIn) {
		if (!levelIn.isClientSide) {
			Level pocketLevel = StorageManager.getServerLevel();
			
			if (pocketLevel != null) {
				BlockPos pos = CosmosChunkPos.scaleFromChunkPos(chunkPosIn);
				BlockState state = pocketLevel.getBlockState(pos);
				
				if (pocketLevel.getBlockEntity(pos) instanceof BlockEntityModuleConnector) {
					BlockEntityModuleConnector connector = (BlockEntityModuleConnector) pocketLevel.getBlockEntity(pos);
					
					connector.sendUpdates(false);
					pocketLevel.sendBlockUpdated(pos, state, state, 3);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void generatePocket(Player playerIn, Level levelIn) {
		Level level = StorageManager.getServerLevel();
		
		if (this.getInternalHeight() == ModConfigManager.getInstance().getInternalHeightEnhanced()) {
			if (this.chunk_info.isSingleChunk()) {
				this.setInternalHeight(ModConfigManager.getInstance().getInternalHeight());
				this.setGeneratedState(false);
			}
		}
		
		if (!this.getGeneratedStateValue() 
			|| this.chunk_info.isSingleChunk() && (this.getInternalHeight() != ModConfigManager.getInstance().getInternalHeight())
			|| !this.chunk_info.isSingleChunk() && (this.getInternalHeight() != ModConfigManager.getInstance().getInternalHeightEnhanced())) {
			
			if (this.chunk_info.isSingleChunk()) {
				LevelChunk chunk = level.getChunk(this.getDominantChunkPos().getX(), this.getDominantChunkPos().getZ());
				BlockPos worldPos = new BlockPos(CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getX(), 1, CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getZ());				
				int height = ModConfigManager.getInstance().getInternalHeight();
				
				if (height != this.getInternalHeight()) {
					if (this.getInternalHeight() > height) {
						if (ModConfigManager.getInstance().getInternalReplace()) {
							int[] edge = new int[] { 0, 14 };
							
							for (int x = edge[0]; x < edge[1]; x++) {
								for (int z = edge[0]; z < edge[1]; z++) {
									BlockPos pos = new BlockPos(x, this.internal_height, z);
									
									if (chunk.getBlockState(pos).getBlock().equals(ModRegistrationManager.BLOCK_WALL.get())) {
										chunk.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState(), false);
									}
								}
							}
							
							this.generateStructureAndUpdate(level);
							this.setInternalHeight(height);
							
							for (int x = 0; x < StorageManager.getPocketSize(); x++) {
								for (int z = 0; z < StorageManager.getPocketSize(); z++) {
									BlockPos updatePos = new BlockPos(x, height - 1, z);
									
									if (level.getBlockState(updatePos).getBlock() instanceof BlockWallEdge) {
										level.setBlockAndUpdate(updatePos, level.getBlockState(updatePos).updateShape(Direction.UP, level.getBlockState(updatePos), level, updatePos, updatePos.offset(Direction.UP.getNormal())));
									}
								}
							}
						} else {
							DimensionalPockets.CONSOLE.debug("[Pocket] <generatepocket> Pocket internal height is larger than the Config Value. Pocket will not reduce in size.");
						}
					} else {
						int[] upd = new int[] { 0, 1, 13, 14 };
						int[] rep = new int[] { 1, 14 };
						
						for (int x = rep[0]; x < rep[1]; x++) {
							for (int z = rep[0]; z < rep[1]; z++) {
								BlockPos pos = new BlockPos(x, this.getInternalHeight(), z);
								
								chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
							}
						}
						
						this.generateStructureAndUpdate(level);
						
						BlockPos upd1 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[0], this.getInternalHeight() - 1, upd[1]));
						BlockPos upd2 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[1], this.getInternalHeight() - 1, upd[0]));
						BlockPos upd3 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[2], this.getInternalHeight() - 1, upd[3]));
						BlockPos upd4 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[3], this.getInternalHeight() - 1, upd[2]));
	
						BlockPos upd5 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[0], this.getInternalHeight() - 1, upd[2]));
						BlockPos upd6 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[1], this.getInternalHeight() - 1, upd[3]));
						BlockPos upd7 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[2], this.getInternalHeight() - 1, upd[0]));
						BlockPos upd8 = MathHelper.addBlockPos(worldPos, new BlockPos(upd[3], this.getInternalHeight() - 1, upd[1]));
						
						level.setBlockAndUpdate(upd1, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd1), level, upd1, upd1.offset(Direction.UP.getNormal())));
						level.setBlockAndUpdate(upd2, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd2), level, upd2, upd2.offset(Direction.UP.getNormal())));
						level.setBlockAndUpdate(upd3, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd3), level, upd3, upd3.offset(Direction.UP.getNormal())));
						level.setBlockAndUpdate(upd4, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd4), level, upd4, upd4.offset(Direction.UP.getNormal())));
	
						level.setBlockAndUpdate(upd5, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd5), level, upd5, upd5.offset(Direction.UP.getNormal())));
						level.setBlockAndUpdate(upd6, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd6), level, upd6, upd6.offset(Direction.UP.getNormal())));
						level.setBlockAndUpdate(upd7, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd7), level, upd7, upd7.offset(Direction.UP.getNormal())));
						level.setBlockAndUpdate(upd8, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, level.getBlockState(upd8), level, upd8, upd8.offset(Direction.UP.getNormal())));
						
						this.setInternalHeight(height);
					}
					
					chunk.isUnsaved();
				} else {
					this.generateStructureAndUpdate(level);
				}
	
				Block check_block_one = level.getBlockState(MathHelper.addBlockPos(worldPos, new BlockPos(1, 0, 1))).getBlock();
				Block check_block_two = level.getBlockState(MathHelper.addBlockPos(worldPos, new BlockPos(2, 0, 2))).getBlock();
				Block check_block_three = level.getBlockState(MathHelper.addBlockPos(worldPos, new BlockPos(0, -1, 0))).getBlock();
				Block check_block_four = level.getBlockState(MathHelper.addBlockPos(worldPos, new BlockPos(0, this.getInternalHeight() - 1, 0))).getBlock();
				
				DimensionalPockets.CONSOLE.debug(check_block_one + " || " + check_block_two + " || " + check_block_three + " || " + check_block_four);
				
				this.setOwner(playerIn);
				this.setGeneratedState(check_block_one instanceof BlockWallEdge && check_block_two instanceof BlockWallBase && check_block_three.equals(ModRegistrationManager.BLOCK_WALL_CONNECTOR.get()) && check_block_four.equals(Blocks.BEDROCK));
				
				StorageManager.saveRegistry(levelIn.registryAccess());
			}
			
			else if (!this.chunk_info.isSingleChunk()) {
				int height = ModConfigManager.getInstance().getInternalHeightEnhanced();

				//System.out.println("HERE " + height + " " + this.getInternalHeight() + " " + (height != this.getInternalHeight()));
				
				BlockPos[] worldPos = new BlockPos[] { 
					new BlockPos(CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getX(), 1, CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getZ()),
					new BlockPos(CosmosChunkPos.scaleFromChunkPos(this.getChunkInfo().getChunks().get(1)).getX(), 1, CosmosChunkPos.scaleFromChunkPos(this.getChunkInfo().getChunks().get(1)).getZ()),
					new BlockPos(CosmosChunkPos.scaleFromChunkPos(this.getChunkInfo().getChunks().get(2)).getX(), 1, CosmosChunkPos.scaleFromChunkPos(this.getChunkInfo().getChunks().get(2)).getZ()),
					new BlockPos(CosmosChunkPos.scaleFromChunkPos(this.getChunkInfo().getChunks().get(3)).getX(), 1, CosmosChunkPos.scaleFromChunkPos(this.getChunkInfo().getChunks().get(3)).getZ())
				};				
				
				if (height != this.getInternalHeight()) {
					System.out.println("GENERATE");
					
					if (this.getInternalHeight() < ModConfigManager.getInstance().getInternalHeight()) {
						int[] rep = new int[] { 0, 16 };
						
						for (int i = 0; i < this.chunk_info.getChunks().size(); i++) {
							LevelChunk chunk = level.getChunk(this.chunk_info.getChunks().get(i).getX(), this.chunk_info.getChunks().get(i).getZ());
							
							for (int x = rep[0]; x < rep[1]; x++) {
								for (int z = rep[0]; z < rep[1]; z++) {
									BlockPos pos = new BlockPos(x, this.getInternalHeight(), z);
									
									chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
								}
							}
						}
						
						this.setInternalHeight(height);
						this.generateStructureAndUpdate(level);
					}
					
					if (this.getInternalHeight() > height) {
						if (ModConfigManager.getInstance().getInternalReplace()) {
							
						}
					} else {
						
					}
				} else {
					this.generateStructureAndUpdate(level);
				}
				
				Block check_block_one = level.getBlockState(MathHelper.addBlockPos(worldPos[0], new BlockPos(1, 0, 1))).getBlock();
				Block check_block_two = level.getBlockState(MathHelper.addBlockPos(worldPos[0], new BlockPos(2, 0, 2))).getBlock();
				Block check_block_three = level.getBlockState(MathHelper.addBlockPos(worldPos[0], new BlockPos(0, -1, 0))).getBlock();
				Block check_block_four = level.getBlockState(MathHelper.addBlockPos(worldPos[0], new BlockPos(0, this.getInternalHeight() - 1, 0))).getBlock();
				
				Block check_block_second_one = level.getBlockState(MathHelper.addBlockPos(worldPos[1], new BlockPos(14, 1, 0))).getBlock();
				Block check_block_second_three = level.getBlockState(MathHelper.addBlockPos(worldPos[1], new BlockPos(0, -1, 0))).getBlock();
				//Block check_block_second_two = level.getBlockState(MathHelper.addBlockPos(worldPos[1], new BlockPos(13, 1, 1))).getBlock();
				
				Block check_block_third_three = level.getBlockState(MathHelper.addBlockPos(worldPos[2], new BlockPos(0, -1, 0))).getBlock();
				
				//DimensionalPockets.CONSOLE.debug(check_block_second_one + " \\ " + check_block_second_two);
				
				boolean checks = 
					check_block_one instanceof BlockWallEdge && 
					check_block_two instanceof BlockWallBase && 
					check_block_three.equals(ModRegistrationManager.BLOCK_WALL_CONNECTOR.get()) && 
					check_block_second_three.equals(ModRegistrationManager.BLOCK_WALL_CONNECTOR.get()) && 
					check_block_third_three.equals(ModRegistrationManager.BLOCK_WALL_CONNECTOR.get()) && 
					check_block_four.equals(Blocks.BEDROCK) && 
					check_block_second_one.equals(Blocks.BEDROCK);
				
				this.setOwner(playerIn);
				this.setGeneratedState(checks);
				
				StorageManager.saveRegistry(levelIn.registryAccess());
			}			
		} else {
			BlockPos pos = new BlockPos(CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getX(), 0, CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getZ());
			BlockEntity entity = level.getBlockEntity(pos);
			
			if (entity == null) {
				level.setBlockAndUpdate(pos, ModRegistrationManager.BLOCK_WALL_CONNECTOR.get().defaultBlockState());
				level.setBlockAndUpdate(pos.offset(Direction.UP.getNormal()), Blocks.BEDROCK.defaultBlockState());
			}
		}
		
		if (this.chunk_info == PocketChunkInfo.EMPTY) {
			this.chunk_info = new PocketChunkInfo(this.chunk_pos, true);
		}
	}
	
	//Code that actually generates the structure.
	public void generateStructureAndUpdate(Level levelIn) {
		int y_offset = StorageManager.getPocketYOffset();
		int size = StorageManager.getPocketSize();
		
		if (PocketEventFactory.onPocketGenerate(levelIn, this)) {
			if (this.getChunkInfo().isSingleChunk()) {
				int height = ModConfigManager.getInstance().getInternalHeight();
				BlockPos worldPos = new BlockPos(CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getX(), 1, CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos()).getZ());
				LevelChunk levelChunk = levelIn.getChunk(this.getDominantChunkPos().getX(), this.getDominantChunkPos().getZ());
				
				for (int x = 0; x < size; x++) {
					for (int y = y_offset; y < height + y_offset; y++) {
						for (int z = 0; z < size; z++) {
							boolean flagX = x == 0 || x == (size - 1);
							boolean flagY = y == y_offset || y == (height);
							boolean flagZ = z == 0 || z == (size - 1);
							
							BlockPos pos = new BlockPos(x, y, z);
							BlockPos world_pos = new BlockPos(worldPos.getX() + x, y, worldPos.getZ() + z);
							
							//Creates a Bedrock frame around the Pocket to prevent glitching outside
							if (!(levelChunk.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
								if (x == 0 || y == 1 || z == 0) {
									levelChunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
								} else if (x == (size - 1) || y == (height - (1 - y_offset)) || z == (size - 1)) {
									levelChunk.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
								}
							}
							
							if (x == 0 && y == 1 && z == 0) {
								levelIn.setBlockAndUpdate(world_pos.offset(Direction.DOWN.getNormal()), ModRegistrationManager.BLOCK_WALL_CONNECTOR.get().defaultBlockState());
							}
							
							//Added those flags, so I could add these checks, almost halves the time.
							if (!(flagX || flagY || flagZ) || flagX && (flagY || flagZ) || flagY && flagZ) {
								continue;
							}
							
							//Creates the "edge" blocks first. Stylistic choice.
							if (x == 1 || y == (1 + y_offset) || z == 1) {
								levelChunk.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunk.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
							} else if (x == (size - 2) || y == (height - (2 - y_offset)) || z == (size - 2)) {
								levelChunk.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunk.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
							} else {
								if (!(levelChunk.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
									levelChunk.setBlockState(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState().updateShape(Direction.UP, levelChunk.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								}
							}
		
							BlockState worldState = levelIn.getBlockState(world_pos);
							
							levelIn.sendBlockUpdated(world_pos, worldState, worldState.getBlock().defaultBlockState(), 19);
							levelIn.markAndNotifyBlock(world_pos, levelChunk, worldState, worldState.getBlock().defaultBlockState(), 19, 0);
							worldState.updateNeighbourShapes(levelIn, world_pos, 19);
							worldState.updateIndirectNeighbourShapes(levelIn, world_pos, 19);
						}
					}
				}
			} 
			
			else if (!(this.getChunkInfo().isSingleChunk())) {
				int height = ModConfigManager.getInstance().getInternalHeightEnhanced();
				{
					/** - First Corner { 0, 0 } - **/
					CosmosChunkPos chunkPosA = this.getDominantChunkPos();
					BlockPos worldPosA = new BlockPos(CosmosChunkPos.scaleFromChunkPos(chunkPosA).getX(), 1, CosmosChunkPos.scaleFromChunkPos(chunkPosA).getZ());
					LevelChunk levelChunkA = levelIn.getChunk(chunkPosA.getX(), chunkPosA.getZ());
								
					for (int x = 0; x < size + 1; x++) {
						for (int y = y_offset; y < height + y_offset; y++) {
							for (int z = 0; z < size + 1; z++) {
								boolean flagX = x == 0; //|| x == (size - 1);
								boolean flagY = y == y_offset || y == (height);
								boolean flagZ = z == 0; //|| z == (size - 1);
								
								BlockPos pos = new BlockPos(x, y, z);
								BlockPos world_pos = new BlockPos(worldPosA.getX() + x, y, worldPosA.getZ() + z);
								
								//Creates a Bedrock frame around the Pocket to prevent glitching outside
								if (!(levelChunkA.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
									if (x == 0 || y == 1 || z == 0) {
										levelChunkA.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} /*else if (x == (size - 1) || y == (height - (1 - y_offset)) || z == (size - 1)) {
										chunkIn.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} */
								}
								
								if (x == 0 && y == 1 && z == 0) {
									levelIn.setBlockAndUpdate(world_pos.offset(Direction.DOWN.getNormal()), ModRegistrationManager.BLOCK_WALL_CONNECTOR.get().defaultBlockState());
								}
								
								//Added those flags, so I could add these checks, almost halves the time.
								if (!(flagX || flagY || flagZ) || flagX && (flagY || flagZ) || flagY && flagZ) {
									continue;
								}
								
								//Creates the "edge" blocks first. Stylistic choice.
								if (x == 1 || y == (1 + y_offset) || z == 1) {
									levelChunkA.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkA.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								} else if (/*x == (size - 2) ||*/ y == (height - (2 - y_offset)) /*|| z == (size - 2)*/) {
									levelChunkA.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkA.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								}  else {
									if (!(levelChunkA.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
										levelChunkA.setBlockState(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState().updateShape(Direction.UP, levelChunkA.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
									}
								}
			
								BlockState worldState = levelIn.getBlockState(world_pos);
								
								levelIn.sendBlockUpdated(world_pos, worldState, worldState.getBlock().defaultBlockState(), 19);
								levelIn.markAndNotifyBlock(world_pos, levelChunkA, worldState, worldState.getBlock().defaultBlockState(), 19, 0);
								worldState.updateNeighbourShapes(levelIn, world_pos, 19);
								worldState.updateIndirectNeighbourShapes(levelIn, world_pos, 19);
							}
						}
					}
				}
					
				{
					/** - Second Corner { 1, 0 } - **/
					CosmosChunkPos chunkPosB = this.getChunkInfo().getChunks().get(1);
					BlockPos worldPosB = new BlockPos(CosmosChunkPos.scaleFromChunkPos(chunkPosB).getX(), 1, CosmosChunkPos.scaleFromChunkPos(chunkPosB).getZ());
					LevelChunk levelChunkB = levelIn.getChunk(chunkPosB.getX(), chunkPosB.getZ());
								
					for (int x = 0; x < size; x++) {
						for (int y = y_offset; y < height + y_offset; y++) {
							for (int z = 0; z < size + 1; z++) {
								boolean flagX = /*x == 0 ||*/ x == (size - 1);
								boolean flagY = y == y_offset || y == (height);
								boolean flagZ = z == 0 /*|| z == (size - 1)*/;
								
								BlockPos pos = new BlockPos(x, y, z);
								BlockPos world_pos = new BlockPos(worldPosB.getX() + x, y, worldPosB.getZ() + z);
								
								//Creates a Bedrock frame around the Pocket to prevent glitching outside
								if (!(levelChunkB.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
									if (/*x == 0 ||*/ y == 1 || z == 0) {
										levelChunkB.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} else if (x == (size - 1) || y == (height - (1 - y_offset)) /*|| z == (size - 1)*/) {
										levelChunkB.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} 
								}
	
								if (x == 0 && y == 1 && z == 0) {
									levelIn.setBlockAndUpdate(world_pos.offset(Direction.DOWN.getNormal()), ModRegistrationManager.BLOCK_WALL_CONNECTOR.get().defaultBlockState());
								}
								
								//Added those flags, so I could add these checks, almost halves the time.
								if (!(flagX || flagY || flagZ) || flagX && (flagY || flagZ) || flagY && flagZ) {
									continue;
								}
								
								//Creates the "edge" blocks first. Stylistic choice.
								if (/*x == 1 ||*/ y == (1 + y_offset) || z == 1) {
									levelChunkB.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkB.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								} else if (x == (size - 2) || y == (height - (2 - y_offset)) /*|| z == (size - 2)*/) {
									levelChunkB.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkB.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								} else {
									if (!(levelChunkB.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
										levelChunkB.setBlockState(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState().updateShape(Direction.UP, levelChunkB.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
									}
								}
			
								BlockState worldState = levelIn.getBlockState(world_pos);
								
								levelIn.sendBlockUpdated(world_pos, worldState, worldState.getBlock().defaultBlockState(), 19);
								levelIn.markAndNotifyBlock(world_pos, levelChunkB, worldState, worldState.getBlock().defaultBlockState(), 19, 0);
								worldState.updateNeighbourShapes(levelIn, world_pos, 19);
								worldState.updateIndirectNeighbourShapes(levelIn, world_pos, 19);
							}
						}
					}
				}
	
				{
					/** - Third Corner { 0, 1 } - **/
					CosmosChunkPos chunkPosC = this.getChunkInfo().getChunks().get(2);
					BlockPos worldPosC = new BlockPos(CosmosChunkPos.scaleFromChunkPos(chunkPosC).getX(), 1, CosmosChunkPos.scaleFromChunkPos(chunkPosC).getZ());
					LevelChunk levelChunkC = levelIn.getChunk(chunkPosC.getX(), chunkPosC.getZ());
								
					for (int x = 0; x < size + 1; x++) {
						for (int y = y_offset; y < height + y_offset; y++) {
							for (int z = 0; z < size; z++) {
								boolean flagX = x == 0; // || x == (size - 1);
								boolean flagY = y == y_offset || y == (height);
								boolean flagZ = /*z == 0 ||*/ z == (size - 1);
								
								BlockPos pos = new BlockPos(x, y, z);
								BlockPos world_pos = new BlockPos(worldPosC.getX() + x, y, worldPosC.getZ() + z);
								
								//Creates a Bedrock frame around the Pocket to prevent glitching outside
								if (!(levelChunkC.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
									if (x == 0 || y == 1 /*|| z == 0*/) {
										levelChunkC.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} else if (/*x == (size - 1) ||*/ y == (height - (1 - y_offset)) || z == (size - 1)) {
										levelChunkC.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} 
								}
	
								if (x == 0 && y == 1 && z == 0) {
									levelIn.setBlockAndUpdate(world_pos.offset(Direction.DOWN.getNormal()), ModRegistrationManager.BLOCK_WALL_CONNECTOR.get().defaultBlockState());
								}
								
								//Added those flags, so I could add these checks, almost halves the time.
								if (!(flagX || flagY || flagZ) || flagX && (flagY || flagZ) || flagY && flagZ) {
									continue;
								}
								
								//Creates the "edge" blocks first. Stylistic choice.
								if (x == 1 || y == (1 + y_offset) /*|| z == 1*/) {
									levelChunkC.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkC.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								} else if (/*x == (size - 2) ||*/ y == (height - (2 - y_offset)) || z == (size - 2)) {
									levelChunkC.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkC.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								} else {
									if (!(levelChunkC.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
										levelChunkC.setBlockState(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState().updateShape(Direction.UP, levelChunkC.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
									}
								}
			
								BlockState worldState = levelIn.getBlockState(world_pos);
								
								levelIn.sendBlockUpdated(world_pos, worldState, worldState.getBlock().defaultBlockState(), 19);
								levelIn.markAndNotifyBlock(world_pos, levelChunkC, worldState, worldState.getBlock().defaultBlockState(), 19, 0);
								worldState.updateNeighbourShapes(levelIn, world_pos, 19);
								worldState.updateIndirectNeighbourShapes(levelIn, world_pos, 19);
							}
						}
					}
				}
	
				{
					/** - Fourth Corner { 1, 1 } - **/
					CosmosChunkPos chunkPosD = this.getChunkInfo().getChunks().get(3);
					BlockPos worldPosD = new BlockPos(CosmosChunkPos.scaleFromChunkPos(chunkPosD).getX(), 1, CosmosChunkPos.scaleFromChunkPos(chunkPosD).getZ());
					LevelChunk levelChunkD = levelIn.getChunk(chunkPosD.getX(), chunkPosD.getZ());
								
					for (int x = 0; x < size; x++) {
						for (int y = y_offset; y < height + y_offset; y++) {
							for (int z = 0; z < size; z++) {
								boolean flagX = /*x == 0 ||*/ x == (size - 1);
								boolean flagY = y == y_offset || y == (height);
								boolean flagZ = /*z == 0 ||*/ z == (size - 1);
								
								BlockPos pos = new BlockPos(x, y, z);
								BlockPos world_pos = new BlockPos(worldPosD.getX() + x, y, worldPosD.getZ() + z);
								
								//Creates a Bedrock frame around the Pocket to prevent glitching outside
								if (!(levelChunkD.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
									/*if (x == 0 || y == 1 || z == 0) {
										levelChunkD.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} else*/ if (x == (size - 1) || y == (height - (1 - y_offset)) || z == (size - 1)) {
										levelChunkD.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
									} 
								}
	
								if (x == 0 && y == 1 && z == 0) {
									levelIn.setBlockAndUpdate(world_pos.offset(Direction.DOWN.getNormal()), ModRegistrationManager.BLOCK_WALL_CONNECTOR.get().defaultBlockState());
								}
								
								//Added those flags, so I could add these checks, almost halves the time.
								if (!(flagX || flagY || flagZ) || flagX && (flagY || flagZ) || flagY && flagZ) {
									continue;
								}
								
								//Creates the "edge" blocks first. Stylistic choice.
								if (/*x == 1 ||*/ y == (1 + y_offset) /*|| z == 1*/) {
									levelChunkD.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkD.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								} else if (x == (size - 2) || y == (height - (2 - y_offset)) || z == (size - 2)) {
									levelChunkD.setBlockState(pos, ModRegistrationManager.BLOCK_WALL_EDGE.get().defaultBlockState().updateShape(Direction.UP, levelChunkD.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
								} else {
									if (!(levelChunkD.getBlockState(pos).getBlock() instanceof BlockWallModule)) {
										levelChunkD.setBlockState(pos, ModRegistrationManager.BLOCK_WALL.get().defaultBlockState().updateShape(Direction.UP, levelChunkD.getBlockState(pos), levelIn, pos, pos.offset(Direction.UP.getNormal())), false);
									}
								}
			
								BlockState worldState = levelIn.getBlockState(world_pos);
								
								levelIn.sendBlockUpdated(world_pos, worldState, worldState.getBlock().defaultBlockState(), 19);
								levelIn.markAndNotifyBlock(world_pos, levelChunkD, worldState, worldState.getBlock().defaultBlockState(), 19, 0);
								worldState.updateNeighbourShapes(levelIn, world_pos, 19);
								worldState.updateIndirectNeighbourShapes(levelIn, world_pos, 19);
							}
						}
					}
				}
			}
		}
	}
	
	public void shift(Player playerIn, EnumShiftDirection direction, @Nullable BlockPos pocket_pos, @Nullable ResourceKey<Level> dimensionIn, @Nullable ItemStack stack) {
		Level entity_world = playerIn.level();

		if (entity_world.isClientSide || !(playerIn instanceof ServerPlayer)) {
			return;
		}
		
		Level source_world = this.getSourceBlockLevel();
		ServerPlayer server_player = (ServerPlayer) playerIn;
		
		if (this.checkIfPlayerCanShift(playerIn, direction)) {
			if (direction.equals(EnumShiftDirection.ENTER)) {
				
				if (pocket_pos != null && dimensionIn != null) {
					this.addPosToBlockArray(pocket_pos, dimensionIn.location());
				}

				BlockPos chunk = CosmosChunkPos.scaleFromChunkPos(this.getDominantChunkPos());
				EnumSafeTeleport location = EnumSafeTeleport.getValidTeleportLocation(StorageManager.getServerLevel(), MathHelper.addBlockPos(chunk, this.getSpawnPos()));
				
				if (location != EnumSafeTeleport.UNKNOWN) {
					boolean safeLocation = EnumSafeTeleport.isSafeTeleportLocation(StorageManager.getServerLevel(), MathHelper.addBlockPos(chunk, this.getSpawnPos()));
					
					BlockPos test = location.toBlockPos();
					BlockPos spawn = this.getSpawnPos();
					BlockPos shiftPos = MathHelper.addBlockPos(chunk, spawn, test);
					
					Shifter shifter = Shifter.createTeleporter(ModDimensionManager.POCKET_WORLD, direction, shiftPos, this.spawn_pos.getYaw(), this.spawn_pos.getPitch(), false, true, false);
					
					this.generatePocket(server_player, source_world);
					ShifterCore.shiftPlayerToDimension(server_player, shifter, !safeLocation);
					
					if (stack != null) {
						CoreTriggers.triggerUseShifter(server_player, stack);
					}
				} else {
					ShifterCore.sendPlayerToBedWithMessage(server_player, direction, "dimensionalpocketsii.pocket.status.location_blocked");
				}
			} else if (direction.equals(EnumShiftDirection.LEAVE)) {
				if (this.isSourceBlockPlaced()) {
					if (this.canPlayerLeave(playerIn)) {
						EnumSafeTeleport location = EnumSafeTeleport.getValidTeleportLocation(source_world, this.getLastBlockPos());
						boolean safeLcation = EnumSafeTeleport.isSafeTeleportLocation(source_world, this.getLastBlockPos());
						
						if (location != EnumSafeTeleport.UNKNOWN) {
							Shifter shifter = Shifter.createTeleporter(this.getSourceBlockDimension(), direction, MathHelper.addBlockPos(this.getLastBlockPos(), location.toBlockPos()), 0.0F, 0.0F, false, true, false); //player.rotationYaw, player.rotationPitch);
							
							if (stack != null) {
								CoreTriggers.triggerUseShifter(server_player, stack);
							}
							
							ShifterCore.shiftPlayerToDimension(server_player, shifter, !safeLcation);
						} else {
							ShifterCore.sendPlayerToBedWithMessage(server_player, direction, "dimensionalpocketsii.pocket.status.blocked");
							
							Shifter shifter = Shifter.createTeleporter(this.getSourceBlockDimension(), direction, this.getSourceBlockLevel().getSharedSpawnPos(), 0.0F, 0.0F, false, true, false); //player.rotationYaw, player.rotationPitch);
							
							if (stack != null) {
								CoreTriggers.triggerUseShifter(server_player, stack);
							}
							
							ShifterCore.shiftPlayerToDimension(server_player, shifter, !safeLcation);
						}
					} else {
						CosmosChatUtil.sendServerPlayerMessage(server_player, ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.pocket.status.trapped"));
					}
				} else {
					ShifterCore.sendPlayerToBedWithMessage(server_player, direction, "dimensionalpocketsii.pocket.status.broken");
				}
			} else {
				ShifterCore.sendPlayerToBedWithMessage(server_player, direction, "dimensionalpocketsii.pocket.status.direction_unknown");
			}
		} else {
			CosmosChatUtil.sendServerPlayerMessage(server_player, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.locked"));
		}
	}
	
	public void writeToNBT(CompoundTag tag, HolderLookup.Provider provider) {
		tag.put(NBT_DIMENSIONAL_POCKET_KEY, this.getNBT(provider));
	}
	
	private CompoundTag getNBT(HolderLookup.Provider provider) {
		CompoundTag compound_nbt = new CompoundTag();
		
		//Base Info
		if (this.getOwner() != null) {
			this.getOwner().writeToNBT(compound_nbt, NBT_OWNER_KEY);
		}
		
		compound_nbt.putBoolean(NBT_GENERATED_KEY, this.getGeneratedStateValue());
		compound_nbt.putBoolean(NBT_LOCKED_KEY, this.getLockStateValue());
		compound_nbt.putBoolean(NBT_TRAP_KEY, this.getTrapStateValue());
		compound_nbt.putBoolean(NBT_ALLOWED_PLAYER_SHIFT_KEY, this.getAllowedPlayerStateValue());
		compound_nbt.putBoolean(NBT_HOSTILE_SPAWNS_KEY, this.getHostileSpawnStateValue());
		compound_nbt.putInt(NBT_COLOUR_KEY, this.getDisplayColour());
		compound_nbt.putInt(NBT_INTERNAL_HEIGHT_KEY, this.getInternalHeight());
		compound_nbt.putInt(NBT_ENERGY_STORED_KEY, this.getEnergyStored());
		compound_nbt.putInt(NBT_ENERGY_CAPACITY_KEY, this.getMaxEnergyStored());
		compound_nbt.putInt(NBT_ENERGY_RECEIVE_KEY, this.getMaxReceive());
		compound_nbt.putInt(NBT_ENERGY_EXTRACT_KEY, this.getMaxExtract());
		
		//CosmosChunkPos
		CompoundTag chunkPos = new CompoundTag();
		this.chunk_pos.saveRaw(chunkPos);
		compound_nbt.put(NBT_CHUNK_POS_KEY, chunkPos);

		//PocketChunkInfo
		CompoundTag chunk = new CompoundTag();
		this.chunk_info.save(chunk);
		compound_nbt.put(NBT_CHUNK_INFO_KEY, chunk);
		
		//Block Dimension
		CompoundTag blockDimension = new CompoundTag();
		blockDimension.putString(Const.NBT_NAMESPACE_KEY, this.block_dimension.getNamespace());
		blockDimension.putString(Const.NBT_PATH_KEY, this.block_dimension.getPath());
		compound_nbt.put(NBT_BLOCK_DIMENSION_KEY, blockDimension);

		//LastPos
		CompoundTag last = new CompoundTag();
		this.last_pos.save(last);
		
		compound_nbt.put(NBT_LAST_POS_KEY, last);
		
		//SpawnPos
		if (this.getSpawnPos() != null) {
			CompoundTag spawn = new CompoundTag();
			
			this.spawn_pos.writeToNBT(spawn);
			
			compound_nbt.put(NBT_SPAWN_POS_KEY, spawn);
		}
		
		//FluidTank
		CompoundTag tank = new CompoundTag();
		this.fluid_tank.writeToNBT(tank);
		compound_nbt.put(NBT_FLUID_TANK_KEY, tank);
		
		//Player Map
		CompoundTag players = new CompoundTag();
		for (int i = 0; i < this.allowed_players_array.size(); i++){
			String player = this.allowed_players_array.get(i);
			
			players.putString(Integer.toString(i), player);
		}
		players.putInt("length", this.allowed_players_array.size());
		compound_nbt.put(NBT_ALLOWED_PLAYERS_KEY, players);
		
		//Side Map
		CompoundTag sideMapTag = new CompoundTag();
		if (this.block_array.size() > 0) {
			for (int i = 0; i < this.block_array.size(); i++) {
				CompoundTag blockTag = new CompoundTag();
				ObjectBlockPosDimension test = this.block_array.get(i);
				
				test.save(blockTag);
				sideMapTag.put(Integer.toString(i), blockTag);
			}
		}
		sideMapTag.putInt("length", this.block_array.size());
		compound_nbt.put(NBT_BLOCK_ARRAY_KEY, sideMapTag);
		
		//Items
		CompoundTag item_list = new CompoundTag();
		for (int i = 0; i < this.item_array.size(); i++) {
			ItemStack stack = this.item_array.get(i);
			
			CompoundTag itemTag = new CompoundTag();
			if (!stack.isEmpty()) {
                itemTag.putByte("Slot", (byte)i);
                itemTag.merge((CompoundTag) stack.save(provider));
    			item_list.put(Integer.toString(i), itemTag);
			}
		}
		compound_nbt.put(NBT_ITEMS_KEY, item_list);
		
		//Surrounding Items
		CompoundTag surrounding_list = new CompoundTag();
		for (int i = 0; i < this.surrounding_array.size(); i++) {
			ItemStack stack = this.surrounding_array.get(i);

			CompoundTag itemTag = new CompoundTag();
			if (!stack.isEmpty()) {
                itemTag.putByte("Slot", (byte)i);
                itemTag.merge((CompoundTag) stack.save(provider));
    			surrounding_list.put(Integer.toString(i), itemTag);
			}
		}
		compound_nbt.put(NBT_SURROUNDING_KEY, surrounding_list);
		
		//Pocket Side Array
		CompoundTag side_array = new CompoundTag();
		for (int i = 0; i < this.pocket_side_array.length; i++) {
			Integer value = this.pocket_side_array[i].getIndex();
			
			side_array.putInt(Integer.toString(i), value);
		}
		side_array.putInt("length", this.pocket_side_array.length);
		compound_nbt.put(NBT_POCKET_SIDE_KEY, side_array);
		
		return compound_nbt;
	}
	
	public static Pocket readFromNBT(CompoundTag compound, HolderLookup.Provider provider) {
		return readFromNBT(compound, NBT_DIMENSIONAL_POCKET_KEY, provider);
	}

	public static Pocket readFromNBT(CompoundTag compound_nbt, String key, HolderLookup.Provider provider) {
		CompoundTag pocketTag = compound_nbt.getCompound(key);
		Pocket pocket = new Pocket(null);
		
		//BaseInfo
		pocket.owner = ObjectPlayerInformation.readFromNBT(pocketTag, NBT_OWNER_KEY);
		
		pocket.setGeneratedState(pocketTag.getBoolean(NBT_GENERATED_KEY));
		pocket.setLockState(pocketTag.getBoolean(NBT_LOCKED_KEY));
		pocket.setAllowedPlayerState(pocketTag.getBoolean(NBT_ALLOWED_PLAYER_SHIFT_KEY));
		pocket.setTrapState(pocketTag.getBoolean(NBT_TRAP_KEY));
		pocket.setHostileSpawnState(pocketTag.getBoolean(NBT_HOSTILE_SPAWNS_KEY));
		pocket.setDisplayColour(pocketTag.getInt(NBT_COLOUR_KEY));
		pocket.setInternalHeight(pocketTag.getInt(NBT_INTERNAL_HEIGHT_KEY));
		pocket.setEnergyStored(pocketTag.getInt(NBT_ENERGY_STORED_KEY));
		pocket.energy_capacity = pocketTag.getInt(NBT_ENERGY_CAPACITY_KEY);
		pocket.energy_max_receive = pocketTag.getInt(NBT_ENERGY_RECEIVE_KEY);
		pocket.energy_max_extract = pocketTag.getInt(NBT_ENERGY_EXTRACT_KEY);
		
		//CosmosChunkPos
		CompoundTag chunkPos = pocketTag.getCompound(NBT_CHUNK_POS_KEY);
		pocket.chunk_pos = CosmosChunkPos.loadRaw(chunkPos);
		
		//PocketChunkInfo
		CompoundTag chunkTag = pocketTag.getCompound(NBT_CHUNK_INFO_KEY);
		pocket.chunk_info = PocketChunkInfo.load(chunkTag);
		
		//BlockDimension
		CompoundTag blockDimension = pocketTag.getCompound(NBT_BLOCK_DIMENSION_KEY);
		pocket.block_dimension = ResourceLocation.fromNamespaceAndPath(blockDimension.getString(Const.NBT_NAMESPACE_KEY), blockDimension.getString(Const.NBT_PATH_KEY));
		
		//LastPos
		CompoundTag lastTag = pocketTag.getCompound(NBT_LAST_POS_KEY);
		pocket.last_pos = ObjectBlockPosDimension.load(lastTag);		
		
		//SpawnPos
		CompoundTag spawnTag = pocketTag.getCompound(NBT_SPAWN_POS_KEY);
		pocket.spawn_pos = ObjectDestinationInfo.readFromNBT(spawnTag);
		
		//FluidTank
		CompoundTag tankTag = pocketTag.getCompound(NBT_FLUID_TANK_KEY);
		pocket.fluid_tank = ObjectFluidTankCustom.readFromNBT(tankTag);
		
		//Player Map
		CompoundTag playersTag = pocketTag.getCompound(NBT_ALLOWED_PLAYERS_KEY);
		for (int i = 0; i < playersTag.getInt("length"); i++) {
			String player_name = playersTag.getString(Integer.toString(i));
			
			pocket.addAllowedPlayerNBT(player_name);
		}
		
		//Side Map
		CompoundTag sideMapTag = pocketTag.getCompound(NBT_BLOCK_ARRAY_KEY);
		for (int i = 0; i < sideMapTag.getInt("length"); i++) {
			CompoundTag sideTag = sideMapTag.getCompound(Integer.toString(i));
			
			pocket.addPosToBlockArray(ObjectBlockPosDimension.load(sideTag), false);
		}
		
		//Items
		CompoundTag itemsTag = pocketTag.getCompound(NBT_ITEMS_KEY);
		for (int i = 0; i < ModReferences.CONSTANT.POCKET_HELD_ITEMS_SIZE; i++) {
			CompoundTag item = itemsTag.getCompound(Integer.toString(i));
						
			pocket.item_array.set(i, ItemStack.parseOptional(provider, item));
		}

		//Items
		CompoundTag surroundingTag = pocketTag.getCompound(NBT_SURROUNDING_KEY);
		for (int i = 0; i < 6; i++) {
			CompoundTag item = surroundingTag.getCompound(Integer.toString(i));
			
			pocket.surrounding_array.set(i, ItemStack.parseOptional(provider, item));
		}
		
		//Pocket Side Array
		CompoundTag sideArrayTag = pocketTag.getCompound(NBT_POCKET_SIDE_KEY);
		for (int i = 0; i < sideArrayTag.getInt("length"); i++) {
			int side = sideArrayTag.getInt(Integer.toString(i));
			
			pocket.pocket_side_array[i] = EnumSideState.getStateFromIndex(side);
		}
		
		return pocket;
	}
	
	public ItemStack generateItemStackWithNBT() {
		ItemStack item_stack = new ItemStack(ModRegistrationManager.BLOCK_POCKET.get());
		CompoundTag stackTag = new CompoundTag();

		if (!item_stack.has(DataComponents.CUSTOM_DATA)) {
			item_stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
		}

		CompoundTag compound = new CompoundTag();
		CompoundTag chunk_tag = new CompoundTag();
		
		int x = this.chunk_info.getDominantChunk().getX();
		int z = this.chunk_info.getDominantChunk().getZ();
		
		chunk_tag.putInt("X", x);
		chunk_tag.putInt("Z", z);

		compound.put("chunk_set", chunk_tag);
		
		compound.putInt("colour", this.getDisplayColour());
		
		stackTag.put("nbt_data", compound);
		
		item_stack.set(DataComponents.CUSTOM_DATA, CustomData.of(stackTag));
		
		return item_stack;
	}

}