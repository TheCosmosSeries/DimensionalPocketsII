package com.tcn.dimensionalpocketsii.pocket.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeListBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.ModReferences.GUI;
import com.tcn.dimensionalpocketsii.ModReferences.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.screen.button.DimensionalButton;
import com.tcn.dimensionalpocketsii.pocket.client.container.ContainerModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.block.entity.BlockEntityModuleConnector;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketAllowedPlayer;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketBlockSideState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketHostileSpawnState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLock;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLockToAllowedPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketTrapPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.connector.PacketConnectionType;
import com.tcn.dimensionalpocketsii.pocket.network.packet.connector.PacketSideState;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings("removal")
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
		
		this.setHasEditBox();
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void removed() {
		super.removed();
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks){
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.textField.render(graphics, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		
		if (this.getBlockEntity() instanceof BlockEntityModuleConnector blockEntity) {
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				ComponentColour colour = ComponentColour.col(pocket.getDisplayColour());
				float[] rgb = colour.equals(ComponentColour.POCKET_PURPLE) ? ComponentColour.rgbFloatArray(ComponentColour.POCKET_PURPLE_LIGHT) : ComponentColour.rgbFloatArray(colour);
				
				CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.CONNECTOR_BASE_NORMAL);
				CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 256, 0, 0, 0, 104, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, blockEntity, GUI.RESOURCE.CONNECTOR_BASE_SIDE);
				
				CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, blockEntity, GUI.RESOURCE.CONNECTOR_OVERLAY_NORMAL);
				CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 256, 0, 0, 0, 104, 256, blockEntity, GUI.RESOURCE.CONNECTOR_OVERLAY_SIDE);
				
				CosmosUISystem.Render.renderFluidTank(graphics, this.getScreenCoords(), this.fluidBarData[0], this.fluidBarData[2], pocket.getFluidTank(), pocket.getFluidLevelScaled(57), 57);

				CosmosUISystem.Render.renderEnergyDisplay(graphics, ComponentColour.RED, pocket, this.getScreenCoords(), this.energyBarData[0], this.energyBarData[1], 16, 58, false);
			}
		}
		
		this.textField.render(graphics, mouseX + 30, mouseY, partialTicks);
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntityModuleConnector blockEntity) {
			graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, blockEntity.getUIMode().getTextColour().dec(), false);
			
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();

				ComponentColour colour = ComponentColour.col(pocket.getDisplayColour());
				ComponentColour textColour = blockEntity.getUIMode().getTextColour();
				
				CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 93, 40, false, ComponentHelper.style(ComponentColour.getCompColourForScreen(colour), "dimensionalpocketsii.gui.header.allowed_players"));
				
				CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 88, 4, false, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.connector.header"));
				CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 8, 4, false, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.config"));
				CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 262, 4, false, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.pocket_inv"));
				CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 8, 169, false, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.storage"));
				CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 8, 110, false, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.settings"));
			}
		}
	}
	
	@Override
	protected void updateWidgetList() {
		if (this.getBlockEntity() instanceof BlockEntityModuleConnector blockEntity) {
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
		
		if (this.getBlockEntity() instanceof BlockEntityModuleConnector blockEntity) {
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				
				int[] sides = new int[] { 0, 0, 0, 0, 0, 0 };
				for (Direction c : Direction.values()) {
					sides[c.get3DDataValue()] = (pocket.getSideArray()[c.get3DDataValue()].getIndex());
				}
				
				this.buttonDown  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + DBI[0], this.getScreenCoords()[1] + DBI[1], 18, true, true, sides[0], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonDown, true);  }, (button) -> { return button.get(); }));
				this.buttonUp    = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + UBI[0], this.getScreenCoords()[1] + UBI[1], 18, true, true, sides[1], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonUp, true);    }, (button) -> { return button.get(); }));
				this.buttonNorth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + NBI[0], this.getScreenCoords()[1] + NBI[1], 18, true, true, sides[2], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonNorth, true); }, (button) -> { return button.get(); }));
				this.buttonSouth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + SBI[0], this.getScreenCoords()[1] + SBI[1], 18, true, true, sides[3], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonSouth, true); }, (button) -> { return button.get(); }));
				this.buttonWest  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + WBI[0], this.getScreenCoords()[1] + WBI[1], 18, true, true, sides[4], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonWest, true);  }, (button) -> { return button.get(); }));
				this.buttonEast  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + EBI[0], this.getScreenCoords()[1] + EBI[1], 18, true, true, sides[5], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonEast, true);  }, (button) -> { return button.get(); }));
				
				this.buttonSideState = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + SIBI[0], this.getScreenCoords()[1] + SIBI[1], 18, true, true, (blockEntity.getSide(Direction.UP).getIndex() + 8), ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonSideState, true); }, (button) -> { return button.get(); }));
				this.buttonType = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + TYBI[0], this.getScreenCoords()[1] + TYBI[1], 18, true, true, (blockEntity.getConnectionType().getIndex() + 4), ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonType, true); }, (button) -> { return button.get(); }));
				this.buttonTrapPlayers = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + TPBI[0], this.getScreenCoords()[1] + TPBI[1], 18, true, true, pocket.getTrapState().getIndex() + 14, ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonTrapPlayers, true); }, (button) -> { return button.get(); }));
				
				this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBCI[0], this.getScreenCoords()[1] + TBCI[1], 18, !pocket.getFluidTank().isEmpty(), true, pocket.getFluidTank().isEmpty() ? 15 : 16, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTankClear, isLeftClick); }));
				this.buttonLock = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + LBI[0], this.getScreenCoords()[1] + LBI[1], 18, true, true, pocket.getLockState().getIndex() + 8, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonLock, isLeftClick); }));
				this.buttonAllowedPlayers = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + APBI[0], this.getScreenCoords()[1] + APBI[1], 18, true, true, pocket.getAllowedPlayerState().getIndex() + 10, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonAllowedPlayers, isLeftClick); }));
				this.buttonHostileSpawn = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + HSBI[0], this.getScreenCoords()[1] + HSBI[1], 18, true, true, pocket.getHostileSpawnState().getIndex() + 15, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonHostileSpawn, isLeftClick); }));
				
				this.buttonTextClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TCBI[0], this.getScreenCoords()[1] + TCBI[1], 18, true, true, 14, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTextClear, isLeftClick); }));
				this.buttonTextPlus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TABI[0], this.getScreenCoords()[1] + TABI[1], 18, true, true, 1, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTextPlus, isLeftClick); }));
				this.buttonTextMinus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TMBI[0], this.getScreenCoords()[1] + TMBI[1], 18, true, true, 2, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTextMinus, isLeftClick); }));
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntityModuleConnector blockEntity) {
			if (blockEntity.getPocket() != null) {
				Pocket pocket = blockEntity.getPocket();
				if (pocket != null) {
					if (this.buttonLock.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.lock_info"),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.lock_value").append(pocket.getLockState().getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonType.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.connector.connection_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.connector.connection_value").append(blockEntity.getConnectionType().getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonSideState.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.connector.side_state_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.connector.side_state_value").append(blockEntity.getSide(Direction.UP).getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonTrapPlayers.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.trap_players_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.trap_players_value").append(pocket.getTrapState().getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonHostileSpawn.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.hostile_spawn_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.hostile_spawn_value").append(pocket.getHostileSpawnState().getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonAllowedPlayers.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.allowed_player_info"), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.allowed_player_value").append(pocket.getAllowedPlayerState().getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					}
					
					else if (this.buttonTextClear.isMouseOver(mouseX, mouseY)) {
						if (this.buttonTextClear.active) {
							graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.button.text.clear"),  mouseX, mouseY);
						}
					} else if (this.buttonTextPlus.isMouseOver(mouseX, mouseY)) {
						if (this.buttonTextPlus.active) {
							graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.button.text.plus"), mouseX, mouseY);
						}
					} else if (this.buttonTextMinus.isMouseOver(mouseX, mouseY)) {
						if (this.buttonTextMinus.active) {
							graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.button.text.minus"), mouseX, mouseY);
						}
					} 
					
					else if (this.buttonDown.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.YELLOW, "bold", " [", "dimensionalpocketsii.gui.button.direction.down", "]")), 
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.DOWN).getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonUp.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.ORANGE, "bold", " [", "dimensionalpocketsii.gui.button.direction.up", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.UP).getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonNorth.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] {
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.BLUE, "bold", " [", "dimensionalpocketsii.gui.button.direction.north", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.NORTH).getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonSouth.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.LIME, "bold", " [", "dimensionalpocketsii.gui.button.direction.south", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.SOUTH).getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonWest.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.WHITE, "bold", " [", "dimensionalpocketsii.gui.button.direction.west", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.WEST).getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (this.buttonEast.isMouseOver(mouseX, mouseY)) {
						Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.GRAY, "bold", " [", "dimensionalpocketsii.gui.button.direction.east", "]")),
								ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(pocket.getSide(Direction.EAST).getColouredComp()) };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					}
					
					else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + this.fluidBarData[0] - 1, this.getScreenCoords()[0] + this.fluidBarData[0] + 16, this.getScreenCoords()[1] + this.fluidBarData[2], this.getScreenCoords()[1] + this.fluidBarData[2] + 57)) {
						FluidTank tank = pocket.getFluidTank();
						
						DecimalFormat formatter = new DecimalFormat("#,###,###,###");
						String amount_string = formatter.format(tank.getFluidAmount());
						String capacity_string = formatter.format(tank.getCapacity());
						String fluid_name = tank.getFluid().getTranslationKey();
						
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.fluid_bar.pre").append(ComponentHelper.style3(ComponentColour.CYAN, "bold", "[ ", fluid_name, " ]")), 
								ComponentHelper.style2(ComponentColour.ORANGE, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.fluid_bar.suff") };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					} else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + this.energyBarData[0] - 1, this.getScreenCoords()[0] + this.energyBarData[0] + 16, this.getScreenCoords()[1] + this.energyBarData[1] - 1, this.getScreenCoords()[1] + this.energyBarData[1] + 58)) {
						DecimalFormat formatter = new DecimalFormat("#,###,###,###");
						String amount_string = formatter.format(pocket.getEnergyStored());
						String capacity_string = formatter.format(pocket.getMaxEnergyStored());
						
						Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"), ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.energy_bar.suff") };
						
						graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					}
					
					else if (this.buttonTankClear != null) {
						if (this.buttonTankClear.isMouseOver(mouseX, mouseY)) {
							if (this.buttonTankClear.active) {
								if (!hasShiftDown()) {
									graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"), mouseX, mouseY);
								} else {
									Component[] comp = new Component[] { 
											ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"),
											ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.button.tank_clear_shift") };
									
									graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
								}
							}
						}
					}
				}
			}
		}
		super.renderStandardHoverEffect(graphics, style, mouseX, mouseY);
	}
	
	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);

		if (isLeftClick) {
			String value = this.textField.getValue();
			
			if (this.getBlockEntity() instanceof BlockEntityModuleConnector blockEntity) {
				if (blockEntity.getPocket() != null) {
					Pocket pocket = blockEntity.getPocket();
	
					if (button.equals(this.buttonType)) {
						PacketDistributor.sendToServer(new PacketConnectionType(this.menu.getBlockPos()));
						blockEntity.cycleConnectionType(true);
						blockEntity.setChanged();;
					} else if (button.equals(this.buttonLock)) {
						PacketDistributor.sendToServer(new PacketLock(pocket.getDominantChunkPos(), !pocket.getLockStateValue()));
						blockEntity.setLockState(!pocket.getLockStateValue(), true);
						blockEntity.setChanged();
					} else if (button.equals(this.buttonSideState)) {
						PacketDistributor.sendToServer(new PacketSideState(this.menu.getBlockPos()));
						blockEntity.cycleSide(Direction.UP, true);
						blockEntity.setChanged();
					} else if (button.equals(this.buttonTrapPlayers)) {
						PacketDistributor.sendToServer(new PacketTrapPlayers(pocket.getDominantChunkPos(), !pocket.getTrapStateValue()));
						pocket.setTrapState(!pocket.getTrapStateValue());
						blockEntity.setChanged();
					} else if (button.equals(this.buttonHostileSpawn)) {
						PacketDistributor.sendToServer(new PacketHostileSpawnState(pocket.getDominantChunkPos(), !pocket.getHostileSpawnStateValue()));
						pocket.setHostileSpawnState(!pocket.getHostileSpawnStateValue());
						blockEntity.setChanged();
					} else if (button.equals(this.buttonAllowedPlayers)) {
						PacketDistributor.sendToServer(new PacketLockToAllowedPlayers(pocket.getDominantChunkPos(), !pocket.getAllowedPlayerStateValue()));
						pocket.setAllowedPlayerState(!pocket.getAllowedPlayerStateValue());
						blockEntity.setChanged();
					}
					
					else if (button.equals(this.buttonTextClear)) {
						this.textField.setValue("");
					} else if (button.equals(this.buttonTextPlus)) {
						if (!value.isEmpty() && value.length() >= 3) {
							PacketDistributor.sendToServer(new PacketAllowedPlayer(pocket.getDominantChunkPos(), value, true));
							pocket.addAllowedPlayerNBT(value);
							blockEntity.setChanged();
							this.textField.setValue("");
						} 
					} else if (button.equals(this.buttonTextMinus)) {
						int selected = this.getSelectedWidgetIndex();
						
						if (selected != 0) {
							String string = this.getWidgetList().get(selected).getDisplayString();
							PacketDistributor.sendToServer(new PacketAllowedPlayer(pocket.getDominantChunkPos(), string, false));
							blockEntity.setChanged();
							
							pocket.removeAllowedPlayerNBT(string);
							this.removeElement();
						}
					}
					
					else if (button.equals(this.buttonTankClear)) {
						if (hasShiftDown()) {
							PacketDistributor.sendToServer(new PacketEmptyTank(pocket.getDominantChunkPos()));
						}
					}
					
					else if (button.equals(this.buttonDown)) {
						PacketDistributor.sendToServer(new PacketBlockSideState(pocket.getDominantChunkPos(), Direction.DOWN.get3DDataValue()));
						pocket.cycleSide(Direction.DOWN, true);
					} else if (button.equals(this.buttonUp)) {
						PacketDistributor.sendToServer(new PacketBlockSideState(pocket.getDominantChunkPos(), Direction.UP.get3DDataValue()));
						pocket.cycleSide(Direction.UP, true);
					} else if (button.equals(this.buttonNorth)) {
						PacketDistributor.sendToServer(new PacketBlockSideState(pocket.getDominantChunkPos(), Direction.NORTH.get3DDataValue()));
						pocket.cycleSide(Direction.NORTH, true);
					} else if (button.equals(this.buttonSouth)) {
						PacketDistributor.sendToServer(new PacketBlockSideState(pocket.getDominantChunkPos(), Direction.SOUTH.get3DDataValue()));
						pocket.cycleSide(Direction.SOUTH, true);
					} else if (button.equals(this.buttonWest)) {
						PacketDistributor.sendToServer(new PacketBlockSideState(pocket.getDominantChunkPos(), Direction.WEST.get3DDataValue()));
						pocket.cycleSide(Direction.WEST, true);
					} else if (button.equals(this.buttonEast)) {
						PacketDistributor.sendToServer(new PacketBlockSideState(pocket.getDominantChunkPos(), Direction.EAST.get3DDataValue()));
						pocket.cycleSide(Direction.EAST, true);
					}
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

		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 40, 66, 20, ComponentHelper.style(ComponentColour.POCKET_PURPLE_LIGHT, "dimensionalpocketsii.gui.help.pocket.surrounding"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 84, 66, 20, ComponentHelper.style(ComponentColour.POCKET_PURPLE_LIGHT, "dimensionalpocketsii.gui.help.pocket.surrounding"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 17, 66, 22, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.gui.help.pocket.surrounding_config"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_config_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_config_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12, 61, 66, 22, ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.gui.help.pocket.surrounding_config"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_config_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.surrounding_config_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 92, 17, 97, 20, ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.help.pocket.allowed_players"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.allowed_players_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.allowed_players_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 190, 17, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.pocket.text_clear"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.text_clear_one")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 211, 17, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.pocket.add_player"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.add_player_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.add_player_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 232, 17, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.pocket.remove_player"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.remove_player_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.remove_player_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 92, 50, 142, 102, ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.help.pocket.allowed_players_list"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.allowed_players_list_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 235, 50, 17, 102, ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.help.pocket.scroll_bar"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.scroll_bar_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.scroll_bar_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12,  123, 20, 20, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.help.pocket.lock"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.lock_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.lock_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 12,  144, 20, 20, ComponentHelper.style(ComponentColour.YELLOW, "dimensionalpocketsii.gui.help.pocket.allow"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.allow_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.allow_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 35,  123, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.gui.help.pocket.trap"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.trap_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.trap_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 35,  144, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.help.pocket.mobs"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.mobs_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.mobs_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 123, 20, 20, ComponentHelper.style(ComponentColour.TURQUOISE, "dimensionalpocketsii.gui.help.pocket.mode"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.mode_one")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 58, 144, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.help.pocket.state"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.state_one")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 264, 15, 74, 182, ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.help.pocket.items"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.items_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.items_two")
		);
		this.addRenderableUIHelpElement(this.getScreenCoords(), 264, 208, 74, 38, ComponentHelper.style(ComponentColour.CYAN, "dimensionalpocketsii.gui.help.pocket.buffer_items"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.buffer_items_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.help.pocket.buffer_items_two")
		);
	}
	
	@Override
	public void initEditBox() {
		super.initEditBox();
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
		boolean textClicked = this.textField.mouseClicked(mouseX, mouseY, mouseButton);
		
		if (!textClicked) {
			this.textField.setFocused(false);
		} else {
			this.textField.setFocused(true);
		}
		
		return this.textField.mouseClicked(mouseX, mouseY, mouseButton) ? true : super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double directionX, double directionY) {
		return this.textField.mouseScrolled(mouseX, mouseY, directionX, directionY) ? true : super.mouseScrolled(mouseX, mouseY, directionX, directionY);
	}

	@Override
	public boolean keyPressed(int keyCode, int mouseX, int mouseY) {
		if (keyCode == 256) {
			if (this.textField.isFocused()) {
				this.textField.setFocused(false);
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