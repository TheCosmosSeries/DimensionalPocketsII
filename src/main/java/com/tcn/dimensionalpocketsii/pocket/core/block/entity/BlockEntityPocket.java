package com.tcn.dimensionalpocketsii.pocket.core.block.entity;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerPocket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityPocket extends AbstractBlockEntityPocket {

	public BlockEntityPocket(BlockPos posIn, BlockState stateIn) {
		super(PocketsRegistrationManager.BLOCK_ENTITY_TYPE_POCKET.get(), posIn, stateIn, true);
	}

	@Override
	public AbstractContainerMenu createMenu(int indexIn, Inventory playerInventoryIn, Player playerIn) {
		return ContainerPocket.createContainerServerSide(indexIn, playerInventoryIn, this.getPocket(), this, this.getBlockPos());
	}
	
	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.pocket.header");
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("dimensionalpocketsii.gui.pocket.header");
	}
}