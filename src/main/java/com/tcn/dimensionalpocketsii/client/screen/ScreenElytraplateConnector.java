package com.tcn.dimensionalpocketsii.client.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.CosmosReference.RESOURCE.BASE;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.FONT;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonUIMode;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosListWidget;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.DimReference.GUI;
import com.tcn.dimensionalpocketsii.DimReference.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateConnector;
import com.tcn.dimensionalpocketsii.client.screen.button.DimensionalButton;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.management.NetworkManager;
import com.tcn.dimensionalpocketsii.core.network.elytraplate.PacketElytraplateUpdateUIMode;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.management.PocketNetworkManager;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketAllowedPlayer;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketBlockSideState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketHostileSpawnState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLock;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLockToAllowedPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketTrapPlayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class ScreenElytraplateConnector extends AbstractContainerScreen<ContainerElytraplateConnector> {

	protected ResourceLocation WIDGET_TEXTURE;
	protected ArrayList<CosmosListWidget> widgetList = new ArrayList<CosmosListWidget>();
	protected ArrayList<String> fromList = new ArrayList<String>();

	private boolean scrollEnabled = false;
	private int currentScroll;

	private int topIndex = 0;
	protected int selectedIndex = -1;
	
	private int[] listIndex;
	private int[] scrollElementIndex;
	
	private int[] energyBarData = new int[] { 14, 184 };
	private int[] fluidBarData = new int[] { 37, 52, 185, 184, 18, 57 };
	
	private int[] screenCoords;
	
	private UUID playerUUID;
	private ItemStack stack;
	private Pocket pocket;

	protected CosmosButtonUIMode uiModeButton;
	
	private CosmosButtonWithType buttonTextClear; private int[] TCBI = new int[] { 191, 18, 18 };
	private CosmosButtonWithType buttonTextPlus;  private int[] TABI = new int[] { 212, 18, 18 };
	private CosmosButtonWithType buttonTextMinus; private int[] TMBI = new int[] { 233, 18, 18 };

	private CosmosButtonWithType buttonTankClear; private int[] TBCI = new int[] { 59, 225, 20 };

	private DimensionalButton buttonNorth; private int[] NBI = new int[] { 57, 20, 18 };
	private DimensionalButton buttonSouth; private int[] SBI = new int[] { 15, 63, 18 };
	private DimensionalButton buttonUp;    private int[] UBI = new int[] { 15, 20, 18 };
	private DimensionalButton buttonDown;  private int[] DBI = new int[] { 36, 20, 18 };
	private DimensionalButton buttonEast;  private int[] EBI = new int[] { 57, 63, 18 };
	private DimensionalButton buttonWest;  private int[] WBI = new int[] { 36, 63, 18 };

	private EditBox textField; private int[] textFieldI = new int[] { 98, 23, 93, 16 };

	private CosmosButtonWithType buttonLock; private int[] LBI = new int[] { 13, 124 };
	private CosmosButtonWithType buttonAllowedPlayers; private int[] APBI = new int[] { 13, 145 };
	private DimensionalButton buttonTrapPlayers; private int[] TPBI = new int[] { 36, 124 };
	private CosmosButtonWithType buttonHostileSpawn; private int[] HSBI = new int[] { 36, 145 };
	
	private CosmosButtonWithType buttonPlaceHolder0; private int[] PB0I = new int[] { 59, 124 };
	private CosmosButtonWithType buttonPlaceHolder1; private int[] PB1I = new int[] { 59, 145 };
	
	public ScreenElytraplateConnector(ContainerElytraplateConnector containerIn, Inventory inventoryIn, Component componentIn) {
		super(containerIn, inventoryIn, componentIn);
		
		this.pocket = containerIn.getPocket();
		this.playerUUID = this.getMenu().getPlayer().getUUID();
		this.stack = this.getMenu().getStack();
		
		this.setImageDims(362, 256);
		this.setTitleLabelDims(88, 4);
		this.setInventoryLabelDims(88, 157);
		
		this.setListDims(94, 52, 138, 98, 14, 0);
		this.setScrollElementDims(237, 52);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		this.setScreenCoords(CosmosUISystem.getScreenCoords(this, this.imageWidth, this.imageHeight));
		this.initTextField();
		this.addButtons();
		super.init();
	}

	@Override
	public void containerTick() {
		super.containerTick();
		this.textField.tick();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);

		this.addButtons();
		
		if (this.pocket != null) {
			ComponentColour colour = ComponentColour.col(this.pocket.getDisplayColour());
			ComponentColour textColour = this.getUIMode().equals(EnumUIMode.LIGHT) ? ComponentColour.BLACK : ComponentColour.SCREEN_LIGHT;
			
			FONT.drawString(poseStack, font, this.getScreenCoords(), 93, 40, true, ComponentHelper.style(ComponentColour.getCompColourForScreen(colour), "dimensionalpocketsii.gui.header.allowed_players"));
			FONT.drawString(poseStack, font, this.getScreenCoords(), 8,   4, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.config"));
			FONT.drawString(poseStack, font, this.getScreenCoords(), 262, 4, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.pocket_inv"));
			FONT.drawString(poseStack, font, this.getScreenCoords(), 8, 169, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.storage"));
			FONT.drawString(poseStack, font, this.getScreenCoords(), 8, 110, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.settings"));
		}
		
		this.textField.render(poseStack, mouseX, mouseY, partialTicks);
		
		this.renderComponents(poseStack, mouseX, mouseY, partialTicks);
		this.renderComponentHoverEffect(poseStack, Style.EMPTY, mouseX, mouseY);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}
	
	public void renderComponents(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderScrollElement(poseStack);
		this.updateWidgetList();
		this.renderWidgetList(poseStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, Mth.clamp(this.imageWidth, 0, 256), this.imageHeight, this.getUIMode(), RESOURCE.CONNECTOR);
		CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 256, 0, 0, 0, 106, this.imageHeight, this.getUIMode(), RESOURCE.CONNECTOR_SIDE);
		
		if (this.menu.getPocket() != null) {
			ComponentColour colour = ComponentColour.col(this.pocket.getDisplayColour());
			float[] rgb = colour.equals(ComponentColour.POCKET_PURPLE) ? ComponentColour.rgbFloatArray(ComponentColour.POCKET_PURPLE_LIGHT) : ComponentColour.rgbFloatArray(colour);
			
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, this.getUIMode(), GUI.RESOURCE.CONNECTOR_BASE_NORMAL);
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 256, 0, 0, 0, 106, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, this.getUIMode(), GUI.RESOURCE.CONNECTOR_BASE_SIDE);

			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, this.getUIMode(), GUI.RESOURCE.CONNECTOR_OVERLAY_NORMAL);
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 256, 0, 0, 0, 106, 256, this.getUIMode(), GUI.RESOURCE.CONNECTOR_OVERLAY_SIDE);

			CosmosUISystem.renderFluidTank(this, poseStack, this.getScreenCoords(), this.fluidBarData[0], this.fluidBarData[2], this.pocket.getFluidTank(), this.pocket.getFluidLevelScaled(57), 57);
			CosmosUISystem.renderEnergyDisplay(this, poseStack, ComponentColour.RED, this.pocket, this.getScreenCoords(), this.energyBarData[0], this.energyBarData[1], 16, 58, false);
		}
		this.textField.render(poseStack, mouseX + 30, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec());
		
		this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec());
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		if (this.uiModeButton.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.ui_mode.info"),
				ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.ui_mode.value").append(this.getUIMode().getColouredComp())
			};
			
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
		} 
		
		else if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + this.energyBarData[0] - 1, this.getScreenCoords()[0] + this.energyBarData[0] + 16, this.getScreenCoords()[1] + this.energyBarData[1] - 1, this.getScreenCoords()[1] + this.energyBarData[1] + 58)) {
			DecimalFormat formatter = new DecimalFormat("#,###,###,###");
			String amount_string = formatter.format(this.pocket.getEnergyStored());
			String capacity_string = formatter.format(this.pocket.getMaxEnergyStored());
			
			Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"), ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.energy_bar.suff") };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		}
		
		else if (this.buttonLock.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.lock_info"),
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.lock_value").append(this.pocket.getLockState().getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonTrapPlayers.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.trap_players_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.trap_players_value").append(this.pocket.getTrapState().getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonHostileSpawn.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.hostile_spawn_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.hostile_spawn_value").append(this.pocket.getHostileSpawnState().getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonAllowedPlayers.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.allowed_player_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.allowed_player_value").append(this.pocket.getAllowedPlayerState().getColouredComp()) };
			
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
		
		if (this.buttonTankClear != null) {
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

		if (this.buttonDown.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
				ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.YELLOW, "bold", " [", "dimensionalpocketsii.gui.button.direction.down", "]")), 
				ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.DOWN).getColouredComp()) 
			};
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonUp.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.ORANGE, "bold", " [", "dimensionalpocketsii.gui.button.direction.up", "]")),
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.UP).getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonNorth.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.BLUE, "bold", " [", "dimensionalpocketsii.gui.button.direction.north", "]")),
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.NORTH).getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonSouth.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.LIME, "bold", " [", "dimensionalpocketsii.gui.button.direction.south", "]")),
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.SOUTH).getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonWest.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.WHITE, "bold", " [", "dimensionalpocketsii.gui.button.direction.west", "]")),
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.WEST).getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		} else if (this.buttonEast.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.GRAY, "bold", " [", "dimensionalpocketsii.gui.button.direction.east", "]")),
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.EAST).getColouredComp()) };
			
			this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		}
	}

	protected void updateWidgetList() {
		if (this.pocket != null) {
			this.updateFromList(this.pocket.getAllowedPlayersArray());
			this.updateFromStringList();
		}
	}
	
	protected void addButtons() {
		this.clearWidgets();
		
		this.uiModeButton = this.addRenderableWidget(new CosmosButtonUIMode(this.getUIMode(), this.getScreenCoords()[0] + 345, this.getScreenCoords()[1] + 5, true, true, ComponentHelper.empty(), (button) -> { this.changeUIMode(); } ));

		int[] sides = new int[] { 0, 0, 0, 0, 0, 0 };
		for (Direction c : Direction.values()) {
			sides[c.get3DDataValue()] = (this.pocket.getSideArray()[c.get3DDataValue()].getIndex());
		}
		
		this.buttonDown  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + DBI[0], this.getScreenCoords()[1] + DBI[1], 18, true, true, sides[0], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonDown);  }));
		this.buttonUp    = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + UBI[0], this.getScreenCoords()[1] + UBI[1], 18, true, true, sides[1], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonUp);    }));
		this.buttonNorth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + NBI[0], this.getScreenCoords()[1] + NBI[1], 18, true, true, sides[2], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonNorth); }));
		this.buttonSouth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + SBI[0], this.getScreenCoords()[1] + SBI[1], 18, true, true, sides[3], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonSouth); }));
		this.buttonWest  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + WBI[0], this.getScreenCoords()[1] + WBI[1], 18, true, true, sides[4], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonWest);  }));
		this.buttonEast  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + EBI[0], this.getScreenCoords()[1] + EBI[1], 18, true, true, sides[5], ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonEast);  }));
		
		this.buttonTrapPlayers = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + TPBI[0], this.getScreenCoords()[1] + TPBI[1], 18, true, true, pocket.getTrapState().getIndex() + 14, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTrapPlayers); }));
		
		this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBCI[0], this.getScreenCoords()[1] + TBCI[1], 18, !pocket.getFluidTank().isEmpty(), true, pocket.getFluidTank().isEmpty() ? 15 : 16, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTankClear); }));
		this.buttonLock = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + LBI[0], this.getScreenCoords()[1] + LBI[1], 18, true, true, pocket.getLockState().getIndex() + 8, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonLock); }));
		this.buttonAllowedPlayers = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + APBI[0], this.getScreenCoords()[1] + APBI[1], 18, true, true, pocket.getAllowedPlayerState().getIndex() + 10, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonAllowedPlayers); }));
		this.buttonHostileSpawn = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + HSBI[0], this.getScreenCoords()[1] + HSBI[1], 18, true, true, pocket.getHostileSpawnState().getIndex() + 15, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonHostileSpawn); }));
		
		this.buttonTextClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TCBI[0], this.getScreenCoords()[1] + TCBI[1], 18, !(this.textField.getValue().isEmpty()), true, 14, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTextClear); }));
		this.buttonTextPlus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TABI[0], this.getScreenCoords()[1] + TABI[1], 18, !(this.textField.getValue().isEmpty()), true, 1, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTextPlus); }));
		this.buttonTextMinus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TMBI[0], this.getScreenCoords()[1] + TMBI[1], 18, true, true, 2, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonTextMinus); }));
		
		this.buttonPlaceHolder0 = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + PB0I[0], this.getScreenCoords()[1] + PB0I[1], 18, true, true, 0, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonPlaceHolder0); }));
		this.buttonPlaceHolder1 = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + PB1I[0], this.getScreenCoords()[1] + PB1I[1], 18, true, true, 0, ComponentHelper.empty(), (button) -> { this.pushButton(this.buttonPlaceHolder1); }));
	}
	
	private void pushButton(Button button) {
		if (button.equals(this.buttonLock)) {
			PocketNetworkManager.sendToServer(new PacketLock(this.pocket.getChunkPos(), !pocket.getLockStateValue()));
			pocket.setLockState(!pocket.getLockStateValue());
		} else if (button.equals(this.buttonTrapPlayers)) {
			PocketNetworkManager.sendToServer(new PacketTrapPlayers(this.pocket.getChunkPos(), !pocket.getTrapStateValue()));
			pocket.setTrapState(!pocket.getTrapStateValue());
		} else if (button.equals(this.buttonHostileSpawn)) {
			PocketNetworkManager.sendToServer(new PacketHostileSpawnState(this.pocket.getChunkPos(), !pocket.getHostileSpawnStateValue()));
			pocket.setHostileSpawnState(!pocket.getHostileSpawnStateValue());
		} else if (button.equals(this.buttonAllowedPlayers)) {
			PocketNetworkManager.sendToServer(new PacketLockToAllowedPlayers(this.pocket.getChunkPos(), !pocket.getAllowedPlayerStateValue()));
			pocket.setAllowedPlayerState(!pocket.getAllowedPlayerStateValue());
		}
		
		if (this.textField != null) {
			String value = this.textField.getValue();
			
			if (button.equals(this.buttonTextClear)) {
				this.textField.setValue("");
			} else if (button.equals(this.buttonTextPlus)) {
				if (!value.isEmpty() && value.length() >= 3) {
					PocketNetworkManager.sendToServer(new PacketAllowedPlayer(pocket.getChunkPos(), value, true));
					pocket.addAllowedPlayerNBT(value);
					this.textField.setValue("");
				} 
			} else if (button.equals(this.buttonTextMinus)) {
				int selected = this.getSelectedWidgetIndex();
				
				if (selected != 0) {
					String string = this.getWidgetList().get(selected).getDisplayString();
					PocketNetworkManager.sendToServer(new PacketAllowedPlayer(pocket.getChunkPos(), string, false));
					
					pocket.removeAllowedPlayerNBT(string);
					this.removeElement();
				}
			}
		}
		
		if (button.equals(this.buttonTankClear)) {
			if (hasShiftDown()) {
				PocketNetworkManager.sendToServer(new PacketEmptyTank(this.pocket.getChunkPos()));
				pocket.emptyFluidTank();
			}
		}
		
		else if (button.equals(this.buttonDown)) {
			PocketNetworkManager.sendToServer(new PacketBlockSideState(this.pocket.getChunkPos(), Direction.DOWN));
			pocket.cycleSide(Direction.DOWN, true);
		} else if (button.equals(this.buttonUp)) {
			PocketNetworkManager.sendToServer(new PacketBlockSideState(this.pocket.getChunkPos(), Direction.UP));
			pocket.cycleSide(Direction.UP, true);
		} else if (button.equals(this.buttonNorth)) {
			PocketNetworkManager.sendToServer(new PacketBlockSideState(this.pocket.getChunkPos(), Direction.NORTH));
			pocket.cycleSide(Direction.NORTH, true);
		} else if (button.equals(this.buttonSouth)) {
			PocketNetworkManager.sendToServer(new PacketBlockSideState(this.pocket.getChunkPos(), Direction.SOUTH));
			pocket.cycleSide(Direction.SOUTH, true);
		} else if (button.equals(this.buttonWest)) {
			PocketNetworkManager.sendToServer(new PacketBlockSideState(this.pocket.getChunkPos(), Direction.WEST));
			pocket.cycleSide(Direction.WEST, true);
		} else if (button.equals(this.buttonEast)) {
			PocketNetworkManager.sendToServer(new PacketBlockSideState(this.pocket.getChunkPos(), Direction.EAST));
			pocket.cycleSide(Direction.EAST, true);
		}
	}

	public void initTextField() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.textField = new EditBox(this.font, this.getScreenCoords()[0] + this.textFieldI[0], this.getScreenCoords()[1] + this.textFieldI[1], this.textFieldI[2], this.textFieldI[3], ComponentHelper.title("Allowed Player Entry"));
		this.textField.setMaxLength(16);
		this.textField.setVisible(true);
		this.textField.setTextColor(CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
		this.textField.setBordered(false);
		this.textField.setCanLoseFocus(true);
		this.textField.setEditable(true);
		
		this.addWidget(this.textField);
	}

	protected void setImageDims(int widthIn, int heightIn) {
		this.imageWidth = widthIn;
		this.imageHeight = heightIn;
	}
	
	protected void setTitleLabelDims(int posX, int posY) {
		this.titleLabelX = posX;
		this.titleLabelY = posY;
	}
	
	protected void setInventoryLabelDims(int posX, int posY) {
		this.inventoryLabelX = posX;
		this.inventoryLabelY = posY;
	}
	
	protected void setScreenCoords(int[] coordsIn) {
		this.screenCoords = coordsIn;
	}
	
	protected int[] getScreenCoords() {
		return this.screenCoords;
	}

	public EnumUIMode getUIMode() {
		if (this.stack != null) {
			return DimensionalElytraplate.getUIMode(this.stack);
		}
		
		return EnumUIMode.DARK;
	}

	private void changeUIMode() {
		NetworkManager.sendToServer(new PacketElytraplateUpdateUIMode(this.playerUUID, 2, this.getUIMode().getNextState()));
		DimensionalElytraplate.setUIMode(this.stack, this.getUIMode().getNextState());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		for (int i = 0; i < widgetList.size(); i++) {
			CosmosListWidget widget = this.getListWidget(i);
			
			if (widget.isMouseOver() && i != 0) {
				widget.mousePressed(this.minecraft, mouseX, mouseY);
				this.selectWidget(i);
				this.selectedIndex = i;
			} else {
				if (mouseX < this.listIndex[0] || mouseX > this.listIndex[0] + this.listIndex[2] || mouseY < this.listIndex[1] || mouseY > this.listIndex[1] + this.listIndex[3]) {
					//this.deselectWidget(i);
					//this.selectedIndex = -1;
				}
			}
		}
		
		return ((this.textField != null) && this.textField.mouseClicked(mouseX, mouseY, mouseButton)) ? true : super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
		if (this.scrollEnabled()) {
			int maxScroll = this.widgetList.size() - this.widgetCount();
			this.currentScroll += -direction;
			
			if (this.currentScroll >= 0) {
				if (this.currentScroll <= maxScroll) {
					if (direction == -1.0F) {
						if (this.topIndex < this.fromList.size()) {
							this.topIndex += 1;
						}
					} else {
						if (this.topIndex > 0) {
							this.topIndex -= 1;
						}
					}
					
					return true;
				} else {
					this.currentScroll = maxScroll;
					return false;
				}
			} else {
				this.currentScroll = 0;
				return false;
			}
		}
		
		return ((this.textField != null) && this.textField.mouseScrolled(mouseX, mouseY, direction)) ? true : super.mouseScrolled(mouseX, mouseY, direction);
	}

	@Override
	public boolean keyPressed(int keyCode, int mouseX, int mouseY) {
		if (keyCode == 256) {
			this.minecraft.player.closeContainer();
		}
		
		return ((this.textField != null) && !this.textField.keyPressed(keyCode, mouseX, mouseY) && !this.textField.canConsumeInput()) ? super.keyPressed(keyCode, mouseX, mouseY) : true;
	}

	@Override
	public boolean charTyped(char charIn, int p_98522_) {
		return ((this.textField != null) && !this.textField.charTyped(charIn, p_98522_) && !this.textField.canConsumeInput()) ? super.charTyped(charIn, p_98522_) : true;
	}
	
	@Override
	public void resize(Minecraft mc, int width, int height) {
		this.clearWidgetList();
		super.resize(mc, width, height);
	}
	
	protected void setListDims(int x, int y, int width, int height, int widgetHeightIn, int widgetSpacingIn) {
		this.listIndex = new int[] { x, y, width, height, widgetHeightIn, widgetSpacingIn};
	}
	
	protected void setScrollElementDims(int x, int y) {
		this.scrollEnabled = true;
		this.scrollElementIndex = new int[] { x, y };
	}

	protected void setWidgetTexture(ResourceLocation textureIn) {
		this.WIDGET_TEXTURE = textureIn;
	}
	
	protected void updateFromList(ArrayList<String> fromListIn) {
		this.fromList = fromListIn;
	}
	
	public CosmosListWidget addListWidget(CosmosListWidget widget) {
		this.widgetList.add(widget);
		return widget;
	}

	protected CosmosListWidget getListWidget(int index) {
		return this.widgetList.get(index);
	}

	protected ArrayList<CosmosListWidget> getWidgetList() {
		return this.widgetList;
	}
	
	protected void removeElement() {
		if (this.topIndex > 0) {
			this.topIndex = this.topIndex - 1;
		}
		this.selectedIndex = -1;
	}
	
	protected void selectWidget(int index) {
		for (int i = 0; i < this.widgetList.size(); i++) {
			this.widgetList.get(i).deselect();
		}
		
		this.widgetList.get(index).setSelectedState(true);
	}

	protected void deselectWidget(int index) {
		this.widgetList.get(index).setSelectedState(false);
	}

	public int getSelectedWidgetIndex() {
		if (this.selectedIndex > 0) {
			return this.selectedIndex;
		}
		
		return 0;
	}

	public int widgetCount() {
		return (int) Math.floor(this.listIndex[3] / (this.listIndex[4] + this.listIndex[5]));
	}
	
	public boolean scrollEnabled() {
		if (this.widgetList.size() > this.widgetCount()) {
			return true;
		} else {
			return false;
		}
	}

	protected void clearWidgetList() {
		this.widgetList.clear();
	}
	
	private void renderWidgetList(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		for (int i = 0; i < this.widgetList.size(); i++) {
			CosmosListWidget widget = this.getListWidget(i);
			
			if (i >= this.topIndex && i <= this.topIndex + this.widgetCount() && i < this.widgetList.size()) {
				widget.renderWidget(poseStack, font, this.getScreenCoords(), this.listIndex[0], this.listIndex[1] + ((this.listIndex[4] + this.listIndex[5]) * (i - this.topIndex)), mouseX, mouseY, i, (this.listIndex[1] + this.listIndex[3]));
			}
		}
	}

	protected void updateFromStringList() {
		this.clearWidgetList();
		
		int spacing_y = this.listIndex[4] + this.listIndex[5];
		
		for (int i = 0; i < this.fromList.size(); i++) {
			CosmosListWidget widget = new CosmosListWidget(this.listIndex[0], this.listIndex[1] + (spacing_y * i), this.listIndex[2], this.listIndex[4], this.WIDGET_TEXTURE, this.fromList.get(i), ComponentColour.WHITE);
			
			this.widgetList.add(widget);
			
			if (i == this.selectedIndex) {
				this.getListWidget(i).setSelectedState(true);
			}
		}
	}
	
	protected void renderScrollElement(PoseStack poseStack) {
		int[] scrollType = new int[] { 15, 0, 15 };
		
		CosmosUISystem.setTextureWithColour(poseStack, BASE.GUI_ELEMENT_MISC_LOC, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
		
		if (this.widgetList.size() > 0) {
			int div;
			
			if (this.widgetList.size() == this.widgetCount()) {
				div = 1;
			} else {
				div = this.widgetList.size() - this.widgetCount();
			}

			int increment = (this.listIndex[3]) / div;

			int posX = this.scrollElementIndex[0];
			int posY = this.scrollElementIndex[1];
			
			int posYUpdated = Mth.clamp((posY + (this.currentScroll * increment)), posY, (this.listIndex[1] + this.listIndex[3] - 1) - this.listIndex[4]);
			
			int type = this.getUIMode().equals(EnumUIMode.DARK) ? 0 : 1;
			
			CosmosUISystem.renderStaticElementToggled(this, poseStack, this.getScreenCoords(), posX, this.scrollEnabled ? posYUpdated : posY, scrollType[0], scrollType[type + 1], 13, 15, true);
		}
	}
}