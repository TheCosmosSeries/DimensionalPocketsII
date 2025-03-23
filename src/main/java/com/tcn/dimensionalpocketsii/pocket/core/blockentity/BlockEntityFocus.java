package com.tcn.dimensionalpocketsii.pocket.core.blockentity;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumGeneralEnableState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.CosmosChunkPos;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.core.management.ConfigurationManager;
import com.tcn.dimensionalpocketsii.core.management.DimensionManager;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerFocus;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.BlockFocus;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketRegistryManager;
import com.tcn.dimensionalpocketsii.pocket.core.shift.EnumShiftDirection;
import com.tcn.dimensionalpocketsii.pocket.core.util.PocketUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class BlockEntityFocus extends BlockEntity implements IBlockInteract, MenuProvider, Nameable, IBlockEntityUIMode {
	
	private Pocket pocket;
	private EnumGeneralEnableState jumpEnabled = EnumGeneralEnableState.ENABLED;
	private EnumGeneralEnableState shiftEnabled = EnumGeneralEnableState.ENABLED;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityFocus(BlockPos posIn, BlockState stateIn) {
		super(ModBusManager.BLOCK_ENTITY_TYPE_FOCUS, posIn, stateIn);
	}
	
	public Pocket getPocket() {
		if (level.isClientSide) {
			return this.pocket;
		}
		
		return PocketRegistryManager.getPocketFromChunkPosition(CosmosChunkPos.scaleToChunkPos(this.getBlockPos()));
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockFocus block = (BlockFocus) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state,  block.updateState(state, this.getBlockPos(), level), 3);
			
			if (update) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), block.updateState(state, this.getBlockPos(), level));
					
					if (this.getPocket() != null) {
						this.getPocket().updateBaseConnector(this.getLevel());
					}
				}
			} else {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), block.updateState(state, this.getBlockPos(), level));
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		
		if (this.getPocket() != null) {
			this.getPocket().writeToNBT(compound);
		}

		compound.putInt("jumpEnabled", this.jumpEnabled.getIndex());
		compound.putInt("shiftEnabled", this.shiftEnabled.getIndex());
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		if (PocketUtil.hasPocketKey(compound)) {
			this.pocket = Pocket.readFromNBT(compound);
		}
		
		this.jumpEnabled = EnumGeneralEnableState.getStateFromIndex(compound.getInt("jumpEnabled"));
		this.shiftEnabled = EnumGeneralEnableState.getStateFromIndex(compound.getInt("shiftEnabled"));
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}
	
	/**
	 * Set the data once it has been received. [NBT > TE] (READ)
	 */
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	/**
	 * Retrieve the data to be stored. [TE > NBT] (WRITE)
	 */
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag);
		return tag;
	}
	
	/**
	 * Actually sends the data to the server. [NBT > SER]
	 */
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	/**
	 * Method is called once packet has been received by the client. [SER > CLT]
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag_ = pkt.getTag();
		this.handleUpdateTag(tag_);
		this.sendUpdates(true);
	}
	
	@Override
	public void onLoad() { }
	
	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player playerIn) { }

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		this.setChanged();

		if (CosmosUtil.getStackItem(playerIn) instanceof BlockItem) {
			return InteractionResult.FAIL;
		}
		
		if (PocketUtil.isDimensionEqual(levelIn, DimensionManager.POCKET_WORLD)) {
			Pocket pocket = this.getPocket();
			
			if (pocket != null) {
				if (!playerIn.isShiftKeyDown()) {
					if (levelIn.isClientSide) {
						return InteractionResult.SUCCESS;
					} else {
						if (playerIn instanceof ServerPlayer) {
							if (this.canPlayerAccess(playerIn)) {
								NetworkHooks.openGui((ServerPlayer)playerIn, this, (packetBuffer)->{ packetBuffer.writeBlockPos(pos); });
								return InteractionResult.SUCCESS;
							}
						}
					}
				} else {
					if(!levelIn.isClientSide) {
						CosmosChunkPos chunkPos = CosmosChunkPos.scaleToChunkPos(pos);
						Pocket pocketIn = PocketRegistryManager.getPocketFromChunkPosition(chunkPos);
						
						if (CosmosUtil.holdingWrench(playerIn)) {
							if (pocketIn.checkIfOwner(playerIn)) {
								if (this.getBlockPos().getY() == 1 || this.getBlockPos().getY() == this.getPocket().getInternalHeight() || this.getBlockPos().getY() == ConfigurationManager.getInstance().getInternalHeight()) {
									ItemStack stack = new ItemStack(ModBusManager.MODULE_FOCUS);
									
									levelIn.setBlockAndUpdate(pos, ModBusManager.BLOCK_WALL.defaultBlockState());
									levelIn.removeBlockEntity(pos);
									
									CosmosUtil.addStack(levelIn, playerIn, stack);
									
									return InteractionResult.SUCCESS;
								} else {
									ItemStack stack = new ItemStack(ModBusManager.BLOCK_FOCUS);
									levelIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
									CosmosUtil.addStack(levelIn, playerIn, stack);
									
									return InteractionResult.SUCCESS;
								}
							} else {
								CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.no_access"));
								return InteractionResult.FAIL;
							}
						}
						
						else if (pocketIn.exists()) {
							if (CosmosUtil.handEmpty(playerIn)) {
								if (this.getShiftEnabled()) {
									pocketIn.shift(playerIn, EnumShiftDirection.LEAVE, null, null, null);
									return InteractionResult.SUCCESS;
								}
							}
						} else {
							CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.getErrorText("dimensionalpocketsii.pocket.status.action.null"));
							return InteractionResult.FAIL;
						}
					}
				}
			} 
		}
		
		return InteractionResult.FAIL;
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.focus");
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.focus");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerFocus(idIn, playerInventoryIn, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	public EnumGeneralEnableState getJump() {
		return this.jumpEnabled;
	}
	
	public boolean getJumpEnabled() {
		return this.jumpEnabled.getValue();
	}
	
	public void setJumpEnabled(EnumGeneralEnableState state) {
		this.jumpEnabled = state;
		this.sendUpdates(true);
	}

	public void setJumpEnabled(Boolean state) {
		this.jumpEnabled = EnumGeneralEnableState.getStateFromValue(state);
		this.sendUpdates(true);
	}

	public EnumGeneralEnableState getShift() {
		return this.shiftEnabled;
	}
	
	public boolean getShiftEnabled() {
		return this.shiftEnabled.getValue();
	}

	public void setShiftEnabled(EnumGeneralEnableState state) {
		this.shiftEnabled = state;
		this.sendUpdates(true);
	}

	public void setShiftEnabled(Boolean state) {
		this.shiftEnabled = EnumGeneralEnableState.getStateFromValue(state);
		this.sendUpdates(true);
	}
	
	@Override
	public EnumUIMode getUIMode() {
		return this.uiMode;
	}

	@Override
	public void setUIMode(EnumUIMode modeIn) {
		this.uiMode = modeIn;
	}

	@Override
	public void cycleUIMode() {
		this.uiMode = EnumUIMode.getNextStateFromState(this.uiMode);
	}

	@Override
	public EnumUIHelp getUIHelp() {
		return this.uiHelp;
	}

	@Override
	public void setUIHelp(EnumUIHelp modeIn) {
		this.uiHelp = modeIn;
	}

	@Override
	public void cycleUIHelp() {
		this.uiHelp = EnumUIHelp.getNextStateFromState(this.uiHelp);
	}

	@Override
	public EnumUILock getUILock() {
		return this.uiLock;
	}

	@Override
	public void setUILock(EnumUILock modeIn) {
		this.uiLock = modeIn;
	}

	@Override
	public void cycleUILock() {
		this.uiLock = EnumUILock.getNextStateFromState(this.uiLock);
	}

	@Override
	public void setOwner(Player playerIn) { }

	@Override
	public boolean canPlayerAccess(Player playerIn) {
		if (this.getUILock().equals(EnumUILock.PUBLIC)) {
			return true;
		} else {
			if (this.getPocket().checkIfOwner(playerIn)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean checkIfOwner(Player playerIn) {
		if (this.getPocket().checkIfOwner(playerIn)) {
			return true;
		}
		return false;
	}
}