package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.FONT;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeListBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.screen.button.DimensionalButton;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketNetworkManager;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketHostileSpawnState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLock;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLockToAllowedPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketTrapPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketAllowedPlayer;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketBlockSideState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.connector.PacketConnectionType;
import com.tcn.dimensionalpocketsii.pocket.network.packet.connector.PacketSideState;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class ScreenModuleConnector extends CosmosScreenUIModeListBE<ContainerModuleConnector> {
	
	private int[] energyBarData = new int[] { 14, 184 };
	private int[] fluidBarData = new int[] { 37, 52, 185, 184, 18, 57 };
	
	private CosmosButtonWithType buttonTextClear; private int[] TCBI = new int[] { 191, 18, 18 };
	private CosmosButtonWithType buttonTextPlus; private int[] TABI = new int[] { 212, 18, 18 };
	private CosmosButtonWithType buttonTextMinus; private int[] TMBI = new int[] { 233, 18, 18 };
	
	private CosmosButtonWithType buttonTankClear; private int[] TBCI = new int[] { 59, 225, 20 };

	private EditBox textField; private int[] textFieldI = new int[] { 98, 23, 93, 16 };
	
	private DimensionalButton buttonNorth; private int[] NBI = new int[] { 57, 20, 18 };
	private DimensionalButton buttonSouth; private int[] SBI = new int[] { 15, 63, 18 };
	private DimensionalButton buttonUp;    private int[] UBI = new int[] { 15, 20, 18 };
	private DimensionalButton buttonDown;  private int[] DBI = new int[] { 36, 20, 18 };
	private DimensionalButton buttonEast;  private int[] EBI = new int[] { 57, 63, 18 };
	private DimensionalButton buttonWest;  private int[] WBI = new int[] { 36, 63, 18 };

	private CosmosButtonWithType buttonLock; private int[] LBI = new int[] { 13, 124 };
	private CosmosButtonWithType buttonAllowedPlayers; private int[] APBI = new int[] { 13, 145 };
	private DimensionalButton buttonTrapPlayers; private int[] TPBI = new int[] { 36, 124 };
	private CosmosButtonWithType buttonHostileSpawn; private int[] HSBI = new int[] { 36, 145 };
	
	private DimensionalButton buttonType; private int[] TYBI = new int[] { 59, 124 };
	private DimensionalButton buttonSideState; private int[] SIBI = new int[] { 59, 145 };
	
	public ScreenModuleConnector(ContainerModuleConnector containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(362, 256);
		
		this.setLight(RESOURCE.CONNECTOR[0]);
		this.setDark(RESOURCE.CONNECTOR[1]);
		
		this.setDualScreen();
		this.setDualScreenIndex(256, 0, 104, 256);
		
		this.setDualLight(RESOURCE.CONNECTOR_SIDE[0]);
		this.setDualDark(RESOURCE.CONNECTOR_SIDE[1]);
		
		this.setUIModeButtonIndex(345, 5);
		this.setUIHelpButtonIndex(345, 19);
		this.setUIHelpTitleOffset(5);
		this.setUIHelpElementDeadzone(0, 0, 360, 256);
		
		this.setTitleLabelDims(23, 4);
		this.setInventoryLabelDims(88, 157);

		this.setScrollElementDims(237, 52);
		this.setListDims(94, 52, 138, 98, 14, 0);
	}

	@Override
	protected void init() {
		this.setScreenCoords(CosmosUISystem.getScreenCoords(this, this.imageWidth, this.imageHeight));
		this.initTextField();
		super.init();
	}

	@Override
	public void containerTick() {
		super.containerTick();
		this.textField.tick();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks){
		super.render(poseStack, mouseX, mouseY, partialTicks);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector blockEntity = (BlockEntityModuleConnector) entity;
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();

				ComponentColour colour = ComponentColour.col(pocket.getDisplayColour());
				ComponentColour textColour = blockEntity.getUIMode().equals(EnumUIMode.LIGHT) ? ComponentColour.BLACK : ComponentColour.SCREEN_LIGHT;
				
				FONT.drawString(poseStack, font, this.getScreenCoords(), 93, 40, true, ComponentHelper.style(ComponentColour.getCompColourForScreen(colour), "dimensionalpocketsii.gui.header.allowed_players"));
				FONT.drawString(poseStack, font, this.getScreenCoords(), 88, 4, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.connector.header"));
				FONT.drawString(poseStack, font, this.getScreenCoords(), 8, 4, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.config"));
				FONT.drawString(poseStack, font, this.getScreenCoords(), 262, 4, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.pocket_inv"));
				FONT.drawString(poseStack, font, this.getScreenCoords(), 8, 169, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.storage"));
				FONT.drawString(poseStack, font, this.getScreenCoords(), 8, 110, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.settings"));
			}
		}
		
		this.textField.render(poseStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector blockEntity = (BlockEntityModuleConnector) entity;
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				ComponentColour colour = ComponentColour.col(pocket.getDisplayColour());
				float[] rgb = colour.equals(ComponentColour.POCKET_PURPLE) ? ComponentColour.rgbFloatArray(ComponentColour.POCKET_PURPLE_LIGHT) : ComponentColour.rgbFloatArray(colour);
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.CONNECTOR_BASE_NORMAL);
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 256, 0, 0, 0, 104, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.CONNECTOR_BASE_SIDE);
				
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, blockEntity, GUI.RESOURCE.CONNECTOR_OVERLAY_NORMAL);
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 256, 0, 0, 0, 104, 256, blockEntity, GUI.RESOURCE.CONNECTOR_OVERLAY_SIDE);
				
				CosmosUISystem.renderFluidTank(this, poseStack, this.getScreenCoords(), this.fluidBarData[0], this.fluidBarData[2], pocket.getFluidTank(), pocket.getFluidLevelScaled(57), 57);

				CosmosUISystem.renderEnergyDisplay(this, poseStack, ComponentColour.RED, pocket, this.getScreenCoords(), this.energyBarData[0], this.energyBarData[1], 16, 58, false);
			}
		}
		
		this.textField.render(poseStack, mouseX + 30, mouseY, partialTicks);
	}
	
	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector blockEntity = (BlockEntityModuleConnector) entity;
			
			this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, blockEntity.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec());
		}
	}
	
	@Override
	protected void updateWidgetList() {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector blockEntity = (BlockEntityModuleConnector) entity;
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				this.updateFromList(pocket.getAllowedPlayersArray());
				super.updateWidgetList();
			}
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector blockEntity = (BlockEntityModuleConnector) entity;
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				int[] sides = new int[] { 0, 0, 0, 0, 0, 0 };
				for (Direction c : Direction.values()) {
					sides[c.get3DDataValue()] = (pocket.getSideArray()[c.get3DDataValue()].getIndex());
				}
				
				this.buttonDown  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + DBI[0], this.getScreenCoords()[1] + DBI[1], 18, true, true, sides[0], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonDown);  }));
				this.buttonUp    = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + UBI[0], this.getScreenCoords()[1] + UBI[1], 18, true, true, sides[1], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonUp);    }));
				this.buttonNorth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + NBI[0], this.getScreenCoords()[1] + NBI[1], 18, true, true, sides[2], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonNorth); }));
				this.buttonSouth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + SBI[0], this.getScreenCoords()[1] + SBI[1], 18, true, true, sides[3], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonSouth); }));
				this.buttonWest  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + WBI[0], this.getScreenCoords()[1] + WBI[1], 18, true, true, sides[4], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonWest);  }));
				this.buttonEast  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + EBI[0], this.getScreenCoords()[1] + EBI[1], 18, true, true, sides[5], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonEast);  }));
				
				this.buttonSideState = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + SIBI[0], this.getScreenCoords()[1] + SIBI[1], 18, true, true, (blockEntity.getSide(Direction.UP).getIndex() + 8), ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonSideState); }));
				this.buttonType = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + TYBI[0], this.getScreenCoords()[1] + TYBI[1], 18, true, true, (blockEntity.getConnectionType().getIndex() + 4), ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonType); }));
				this.buttonTrapPlayers = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + TPBI[0], this.getScreenCoords()[1] + TPBI[1], 18, true, true, pocket.getTrapState().getIndex() + 14, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTrapPlayers); }));
				
				this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBCI[0], this.getScreenCoords()[1] + TBCI[1], 18, !pocket.getFluidTank().isEmpty(), true, pocket.getFluidTank().isEmpty() ? 15 : 16, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTankClear); }));
				this.buttonLock = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + LBI[0], this.getScreenCoords()[1] + LBI[1], 18, true, true, pocket.getLockState().getIndex() + 8, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonLock); }));
				this.buttonAllowedPlayers = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + APBI[0], this.getScreenCoords()[1] + APBI[1], 18, true, true, pocket.getAllowedPlayerState().getIndex() + 10, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonAllowedPlayers); }));
				this.buttonHostileSpawn = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + HSBI[0], this.getScreenCoords()[1] + HSBI[1], 18, true, true, pocket.getHostileSpawnState().getIndex() + 15, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonHostileSpawn); }));
				
				this.buttonTextClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TCBI[0], this.getScreenCoords()[1] + TCBI[1], 18, !(this.textField.getValue().isEmpty()), true, 14, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTextClear); }));
				this.buttonTextPlus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TABI[0], this.getScreenCoords()[1] + TABI[1], 18, !(this.textField.getValue().isEmpty()), true, 1, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTextPlus); }));
				this.buttonTextMinus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TMBI[0], this.getScreenCoords()[1] + TMBI[1], 18, true, true, 2, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTextMinus); }));
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector blockEntity = (BlockEntityModuleConnector) entity;
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				if (pocket != null) {
					if (this.buttonLock.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.lock_info"),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.lock_value").append(pocket.getLockState().getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonType.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.connector.connection_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.connector.connection_value").append(blockEntity.getConnectionType().getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonSideState.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.connector.side_state_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.connector.side_state_value").append(blockEntity.getSide(Direction.UP).getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonTrapPlayers.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.trap_players_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.trap_players_value").append(pocket.getTrapState().getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonHostileSpawn.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.hostile_spawn_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.hostile_spawn_value").append(pocket.getHostileSpawnState().getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonAllowedPlayers.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.allowed_player_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.allowed_player_value").append(pocket.getAllowedPlayerState().getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					}
					
					else if (this.buttonTextClear.isMouseOver(mouseX, mouseY)) {
						if (this.buttonTextClear.active) {
							this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.button.text.clear"),  mouseX, mouseY);
						}
					} else if (this.buttonTextPlus.isMouseOver(mouseX, mouseY)) {
						if (this.buttonTextPlus.active) {
							this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.button.text.plus"), mouseX, mouseY);
						}
					} else if (this.buttonTextMinus.isMouseOver(mouseX, mouseY)) {
						if (this.buttonTextMinus.active) {
							this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.button.text.minus"), mouseX, mouseY);
						}
					} 
					
					else if (this.buttonDown.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.YELLOW, "bold", " [", "dimensionalpocketsii.gui.button.direction.down", "]")), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.DOWN).getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonUp.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.ORANGE, "bold", " [", "dimensionalpocketsii.gui.button.direction.up", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.UP).getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonNorth.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] {
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.BLUE, "bold", " [", "dimensionalpocketsii.gui.button.direction.north", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.NORTH).getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonSouth.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.LIME, "bold", " [", "dimensionalpocketsii.gui.button.direction.south", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.SOUTH).getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonWest.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.WHITE, "bold", " [", "dimensionalpocketsii.gui.button.direction.west", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.WEST).getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonEast.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.GRAY, "bold", " [", "dimensionalpocketsii.gui.button.direction.east", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.EAST).getColouredComp()) };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					}
					
					else if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + this.fluidBarData[0] - 1, this.getScreenCoords()[0] + this.fluidBarData[0] + 16, this.getScreenCoords()[1] + this.fluidBarData[2], this.getScreenCoords()[1] + this.fluidBarData[2] + 57)) {
						FluidTank tank = pocket.getFluidTank();
						
						DecimalFormat formatter = new DecimalFormat("#,###,###,###");
						String amount_string = formatter.format(tank.getFluidAmount());
						String capacity_string = formatter.format(tank.getCapacity());
						String fluid_name = tank.getFluid().getTranslationKey();
						
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.fluid_bar.pre").append(ComponentHelper.style3(ComponentColour.CYAN, "bold", "[ ", fluid_name, " ]")), 
								ComponentHelper.style2(ComponentColour.ORANGE, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.fluid_bar.suff") };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					} else if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + this.energyBarData[0] - 1, this.getScreenCoords()[0] + this.energyBarData[0] + 16, this.getScreenCoords()[1] + this.energyBarData[1] - 1, this.getScreenCoords()[1] + this.energyBarData[1] + 58)) {
						DecimalFormat formatter = new DecimalFormat("#,###,###,###");
						String amount_string = formatter.format(pocket.getEnergyStored());
						String capacity_string = formatter.format(pocket.getMaxEnergyStored());
						
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"), ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.energy_bar.suff") };
						
						this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
					}
					
					else if (this.buttonTankClear != null) {
						if (this.buttonTankClear.isMouseOver(mouseX, mouseY)) {
							if (this.buttonTankClear.active) {
								if (!hasShiftDown()) {
									this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"), mouseX, mouseY);
								} else {
									Component[] comp = new Component[] { 
											ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"),
											ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.button.tank_clear_shift") };
									
									this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
								}
							}
						}
					}
				}
			}
		}
		super.renderStandardHoverEffect(poseStack, style, mouseX, mouseY);
	}
	
	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
		BlockEntity entity = this.getBlockEntity();
		String value = this.textField.getValue();
		
		if (entity instanceof BlockEntityModuleConnector) {
			BlockEntityModuleConnector blockEntity = (BlockEntityModuleConnector) entity;
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();

				if (button.equals(this.buttonType)) {
					PocketNetworkManager.sendToServer(new PacketConnectionType(this.menu.getBlockPos()));
					blockEntity.cycleConnectionType(true);
					blockEntity.setChanged();;
				} else if (button.equals(this.buttonLock)) {
					PocketNetworkManager.sendToServer(new PacketLock(pocket.getChunkPos(), !pocket.getLockStateValue()));
					blockEntity.setLockState(!pocket.getLockStateValue(), true);
					blockEntity.setChanged();
				} else if (button.equals(this.buttonSideState)) {
					PocketNetworkManager.sendToServer(new PacketSideState(this.menu.getBlockPos()));
					blockEntity.cycleSide(Direction.UP, true);
					blockEntity.setChanged();
				} else if (button.equals(this.buttonTrapPlayers)) {
					PocketNetworkManager.sendToServer(new PacketTrapPlayers(pocket.getChunkPos(), !pocket.getTrapStateValue()));
					pocket.setTrapState(!pocket.getTrapStateValue());
					blockEntity.setChanged();
				} else if (button.equals(this.buttonHostileSpawn)) {
					PocketNetworkManager.sendToServer(new PacketHostileSpawnState(pocket.getChunkPos(), !pocket.getHostileSpawnStateValue()));
					pocket.setHostileSpawnState(!pocket.getHostileSpawnStateValue());
					blockEntity.setChanged();
				} else if (button.equals(this.buttonAllowedPlayers)) {
					PocketNetworkManager.sendToServer(new PacketLockToAllowedPlayers(pocket.getChunkPos(), !pocket.getAllowedPlayerStateValue()));
					pocket.setAllowedPlayerState(!pocket.getAllowedPlayerStateValue());
					blockEntity.setChanged();
				}
				
				else if (button.equals(this.buttonTextClear)) {
					this.textField.setValue("");
				} else if (button.equals(this.buttonTextPlus)) {
					if (!value.isEmpty() && value.length() >= 3) {
						PocketNetworkManager.sendToServer(new PacketAllowedPlayer(pocket.getChunkPos(), value, true));
						pocket.addAllowedPlayerNBT(value);
						blockEntity.setChanged();
						this.textField.setValue("");
					} 
				} else if (button.equals(this.buttonTextMinus)) {
					int selected = this.getSelectedWidgetIndex();
					
					if (selected != 0) {
						String string = this.getWidgetList().get(selected).getDisplayString();
						PocketNetworkManager.sendToServer(new PacketAllowedPlayer(pocket.getChunkPos(), string, false));
						blockEntity.setChanged();
						
						pocket.removeAllowedPlayerNBT(string);
						this.removeElement();
					}
				}
				
				else if (button.equals(this.buttonTankClear)) {
					if (hasShiftDown()) {
						PocketNetworkManager.sendToServer(new PacketEmptyTank(pocket.getChunkPos()));
					}
				}
				
				else if (button.equals(this.buttonDown)) {
					PocketNetworkManager.sendToServer(new PacketBlockSideState(pocket.getChunkPos(), Direction.DOWN));
					pocket.cycleSide(Direction.DOWN, true);
				} else if (button.equals(this.buttonUp)) {
					PocketNetworkManager.sendToServer(new PacketBlockSideState(pocket.getChunkPos(), Direction.UP));
					pocket.cycleSide(Direction.UP, true);
				} else if (button.equals(this.buttonNorth)) {
					PocketNetworkManager.sendToServer(new PacketBlockSideState(pocket.getChunkPos(), Direction.NORTH));
					pocket.cycleSide(Direction.NORTH, true);
				} else if (button.equals(this.buttonSouth)) {
					PocketNetworkManager.sendToServer(new PacketBlockSideState(pocket.getChunkPos(), Direction.SOUTH));
					pocket.cycleSide(Direction.SOUTH, true);
				} else if (button.equals(this.buttonWest)) {
					PocketNetworkManager.sendToServer(new PacketBlockSideState(pocket.getChunkPos(), Direction.WEST));
					pocket.cycleSide(Direction.WEST, true);
				} else if (button.equals(this.buttonEast)) {
					PocketNetworkManager.sendToServer(new PacketBlockSideState(pocket.getChunkPos(), Direction.EAST));
					pocket.cycleSide(Direction.EAST, true);
				}
			}
		}
	}
	
	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();

		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 182, 20, 62, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.power_display"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.power_display_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.power_display_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 35, 182, 20, 62, ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.help.fluid_display"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.fluid_display_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.fluid_display_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 182, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_BLUE, "dimensionalpocketsii.gui.help.slot.bucket_input"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_three"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_input_four")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 203, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.slot.bucket_output"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_output_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_output_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.slot.bucket_output_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 224, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.fluid_clear_button"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.fluid_clear_button_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.fluid_clear_button_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 40, 66, 20, ComponentHelper.style(ComponentColour.POCKET_PURPLE_LIGHT, "Pocket Surrounding Blocks"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 84, 66, 20, ComponentHelper.style(ComponentColour.POCKET_PURPLE_LIGHT, "Pocket Surrounding Blocks"), ComponentHelper.comp("DESC"));
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 17, 66, 22, ComponentHelper.style(ComponentColour.PURPLE, "Pocket Side State Configuration"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 61, 66, 22, ComponentHelper.style(ComponentColour.PURPLE, "Pocket Side State Configuration"), ComponentHelper.comp("DESC"));

		this.addRenderableUIHelpElement(this.getScreenCoords(), 92, 17, 97, 20, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "Allowed Player Entry"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 190, 17, 20, 20, ComponentHelper.style(ComponentColour.RED, "Text Clear Button"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 211, 17, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "Add Allowed Player Button"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 232, 17, 20, 20, ComponentHelper.style(ComponentColour.RED, "Remove Allowed Player Button"), ComponentHelper.comp("DESC"));
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 92, 50, 142, 102, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "Allowed Player List"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 235, 50, 17, 102, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "Scroll Bar"), ComponentHelper.comp("DESC"));
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12,  123, 20, 20, ComponentHelper.style(ComponentColour.RED, "Lock Button"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12,  144, 20, 20, ComponentHelper.style(ComponentColour.YELLOW, "Allowed Player Lock Button"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 35,  123, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_RED, "Trap Players Button"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 35,  144, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "Mob Spawning Button"), ComponentHelper.comp("DESC"));
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 123, 20, 20, ComponentHelper.style(ComponentColour.TURQUOISE, "Connector Mode Button"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 144, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "Side State Button"), ComponentHelper.comp("DESC"));
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 264, 15, 74, 182, ComponentHelper.style(ComponentColour.GRAY, "Pocket Items"), ComponentHelper.comp("DESC"));
		this.addRenderableUIHelpElement(this.getScreenCoords(), 264, 208, 74, 38, ComponentHelper.style(ComponentColour.CYAN, "Pocket Buffer Items"), ComponentHelper.comp("DESC"));
	}
	
	public void initTextField() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.textField = new EditBox(this.font, this.getScreenCoords()[0] + this.textFieldI[0], this.getScreenCoords()[1] + this.textFieldI[1], this.textFieldI[2], this.textFieldI[3], ComponentHelper.comp("Allowed Player Entry"));
		this.textField.setMaxLength(16);
		this.textField.setVisible(true);
		this.textField.setTextColor(CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
		this.textField.setBordered(false);
		this.textField.setCanLoseFocus(true);
		this.textField.setEditable(true);
		
		this.addWidget(this.textField);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return this.textField.mouseClicked(mouseX, mouseY, mouseButton) ? true : super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double p_97659_, double p_97660_, double p_97661_) {
		return this.textField.mouseScrolled(p_97659_, p_97660_, p_97661_) ? true : super.mouseScrolled(p_97659_, p_97660_, p_97661_);
	}

	@Override
	public boolean keyPressed(int keyCode, int mouseX, int mouseY) {
		if (keyCode == 256) {
			if (this.textField.isFocused()) {
				this.textField.setFocus(false);
			} else {
				this.minecraft.player.closeContainer();
			}
		}
		
		return !this.textField.keyPressed(keyCode, mouseX, mouseY) && !this.textField.canConsumeInput() ? super.keyPressed(keyCode, mouseX, mouseY) : true;
	}

	@Override
	public boolean charTyped(char charIn, int p_98522_) {
		return !this.textField.charTyped(charIn, p_98522_) && !this.textField.canConsumeInput() ? super.charTyped(charIn, p_98522_) : true;
	}
	
}