package com.tcn.dimensionalpocketsii.client.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tcn.cosmoslibrary.CosmosReference.RESOURCE.BASE;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonUIHelp;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonUIMode;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosListWidget;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosUIHelpElement;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.ModReferences.GUI;
import com.tcn.dimensionalpocketsii.ModReferences.GUI.RESOURCE;
import com.tcn.dimensionalpocketsii.client.container.ContainerElytraplateConnector;
import com.tcn.dimensionalpocketsii.client.screen.button.DimensionalButton;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIHelp;
import com.tcn.dimensionalpocketsii.core.network.packet.elytraplate.PacketElytraplateUpdateUIMode;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketAllowedPlayer;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketBlockSideState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketEmptyTank;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketHostileSpawnState;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLock;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketLockToAllowedPlayers;
import com.tcn.dimensionalpocketsii.pocket.network.packet.PacketTrapPlayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("removal")
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
	protected CosmosButtonUIHelp uiHelpButton; private int[] uiHelpButtonIndex;

	protected List<CosmosUIHelpElement> uiHelpElements = Lists.newArrayList();

	private boolean hasUIHelp = false;
	private boolean hasUIHelpElementDeadzone = false;
	private int[] uiHelpElementDeadzone;
	private int uiHelpTitleYOffset = 0;
	
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

		this.setUIHelpButtonIndex(345, 19);
		this.setUIHelpTitleOffset(5);
		this.setUIHelpElementDeadzone(0, 0, 360, 256);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		this.setScreenCoords(CosmosUISystem.Init.getScreenCoords(this, this.imageWidth, this.imageHeight));
		this.initTextField();
		this.addButtons();
		super.init();
		this.addUIHelpElements();
	}

	@Override
	public void containerTick() {
		super.containerTick();
		//this.textField.tick();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(graphics, mouseX, mouseY, partialTicks);
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		if (this.pocket != null) {
			ComponentColour colour = ComponentColour.col(this.pocket.getDisplayColour());
			ComponentColour textColour = this.getUIMode().equals(EnumUIMode.LIGHT) ? ComponentColour.BLACK : ComponentColour.SCREEN_LIGHT;
			
			CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 93, 40, true, ComponentHelper.style(ComponentColour.getCompColourForScreen(colour), "dimensionalpocketsii.gui.header.allowed_players"));
			CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 8,   4, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.config"));
			CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 262, 4, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.pocket_inv"));
			CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 8, 169, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.storage"));
			CosmosUISystem.FontRenderer.drawString(graphics, font, this.getScreenCoords(), 8, 110, true, ComponentHelper.style(textColour, "dimensionalpocketsii.gui.header.settings"));
		}
		
		this.textField.render(graphics, mouseX, mouseY, partialTicks);
		
		this.renderComponents(graphics, mouseX, mouseY, partialTicks);
		this.renderComponentHoverEffect(graphics, Style.EMPTY, mouseX, mouseY);
		this.renderUIHelpElements(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
		if (this.getHasUIHelpShow()) {
			if (this.hasUIHelpElementDeadzone) {
				if (!(mouseX > (this.getScreenCoords()[0] + this.uiHelpElementDeadzone[0]) && mouseX < (this.getScreenCoords()[0] + this.uiHelpElementDeadzone[2]) 
						&& mouseY > (this.getScreenCoords()[1] + this.uiHelpElementDeadzone[1]) && mouseY < (this.getScreenCoords()[1] + this.uiHelpElementDeadzone[3]))) {
					super.renderTooltip(graphics, mouseX, mouseY);
				}
			} else {
				super.renderTooltip(graphics, mouseX, mouseY);
			}
		} else {
			super.renderTooltip(graphics, mouseX, mouseY);
		}
	}
	
	public void renderComponents(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderScrollElement(graphics);
		this.updateWidgetList();
		this.renderWidgetList(graphics, mouseX, mouseY, partialTicks);
		this.addUIHelpElements();
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, Mth.clamp(this.imageWidth, 0, 256), this.imageHeight, this.getUIMode(), RESOURCE.CONNECTOR);
		CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 256, 0, 0, 0, 106, this.imageHeight, this.getUIMode(), RESOURCE.CONNECTOR_SIDE);
		
		if (this.menu.getPocket() != null) {
			ComponentColour colour = ComponentColour.col(this.pocket.getDisplayColour());
			float[] rgb = colour.equals(ComponentColour.POCKET_PURPLE) ? ComponentColour.rgbFloatArray(ComponentColour.POCKET_PURPLE_LIGHT) : ComponentColour.rgbFloatArray(colour);
			
			CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, this.getUIMode(), GUI.RESOURCE.CONNECTOR_BASE_NORMAL);
			CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 256, 0, 0, 0, 106, 256, new float[] { rgb[0], rgb[1], rgb[2], 1.0F }, this.getUIMode(), GUI.RESOURCE.CONNECTOR_BASE_SIDE);

			CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, 256, 256, this.getUIMode(), GUI.RESOURCE.CONNECTOR_OVERLAY_NORMAL);
			CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 256, 0, 0, 0, 106, 256, this.getUIMode(), GUI.RESOURCE.CONNECTOR_OVERLAY_SIDE);

			CosmosUISystem.Render.renderFluidTank(graphics, this.getScreenCoords(), this.fluidBarData[0], this.fluidBarData[2], this.pocket.getFluidTank(), this.pocket.getFluidLevelScaled(57), 57);
			CosmosUISystem.Render.renderEnergyDisplay(graphics, ComponentColour.RED, this.pocket, this.getScreenCoords(), this.energyBarData[0], this.energyBarData[1], 16, 58, false);
		}
		this.textField.render(graphics, mouseX + 30, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec(), false);
		
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, this.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec(), false);
	}
	
	public void renderComponentHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {
		if (getUIHelp().equals(EnumUIHelp.SHOWN)) {
			this.renderHelpElementHoverEffect(graphics, mouseX, mouseY);
		} else {
			if (this.getHasUIHelp()) {
				if (this.uiHelpButton.isMouseOver(mouseX, mouseY)) {
					Component[] comp = new Component[] { 
						ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.ui_help.info"),
						ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.ui_help.value").append(this.getUIHelp().getColouredComp())
					};
					
					graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
				}
			}
			
			if (this.uiModeButton.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.ui_mode.info"),
					ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.ui_mode.value").append(this.getUIMode().getColouredComp())
				};
				
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
			} 
			
			else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + this.energyBarData[0] - 1, this.getScreenCoords()[0] + this.energyBarData[0] + 16, this.getScreenCoords()[1] + this.energyBarData[1] - 1, this.getScreenCoords()[1] + this.energyBarData[1] + 58)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(this.pocket.getEnergyStored());
				String capacity_string = formatter.format(this.pocket.getMaxEnergyStored());
				
				Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.energy_bar.pre"), ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "dimensionalpocketsii.gui.energy_bar.suff") };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			
			else if (this.buttonLock.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.lock_info"),
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.lock_value").append(this.pocket.getLockState().getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonTrapPlayers.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.trap_players_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.trap_players_value").append(this.pocket.getTrapState().getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonHostileSpawn.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.hostile_spawn_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.hostile_spawn_value").append(this.pocket.getHostileSpawnState().getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonAllowedPlayers.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.allowed_player_info"), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.allowed_player_value").append(this.pocket.getAllowedPlayerState().getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			
			else if (this.buttonTextClear.isMouseOver(mouseX, mouseY)) {
				graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "dimensionalpocketsii.gui.button.text.clear"),  mouseX, mouseY);
			} else if (this.buttonTextPlus.isMouseOver(mouseX, mouseY)) {
				graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.GREEN, "dimensionalpocketsii.gui.button.text.plus"), mouseX, mouseY);
			} else if (this.buttonTextMinus.isMouseOver(mouseX, mouseY)) {
				graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.button.text.minus"), mouseX, mouseY);
			}
			
			else if (this.buttonTankClear.isMouseOver(mouseX, mouseY)) {
				if (!hasShiftDown()) {
					graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"), mouseX, mouseY);
				} else {
					Component[] comp = new Component[] { 
							ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.tank_clear"),
							ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.button.tank_clear_shift") };
					
					graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
				}
			}
			
			else if (this.buttonDown.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.YELLOW, "bold", " [", "dimensionalpocketsii.gui.button.direction.down", "]")), 
					ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.DOWN).getColouredComp()) 
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonUp.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { 
						ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.ORANGE, "bold", " [", "dimensionalpocketsii.gui.button.direction.up", "]")),
						ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.UP).getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonNorth.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] {
						ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.BLUE, "bold", " [", "dimensionalpocketsii.gui.button.direction.north", "]")),
						ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.NORTH).getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonSouth.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { 
						ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.LIME, "bold", " [", "dimensionalpocketsii.gui.button.direction.south", "]")),
						ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.SOUTH).getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonWest.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { 
						ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.WHITE, "bold", " [", "dimensionalpocketsii.gui.button.direction.west", "]")),
						ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.WEST).getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (this.buttonEast.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { 
						ComponentHelper.style(ComponentColour.WHITE, "dimensionalpocketsii.gui.button.direction.prefix").append(ComponentHelper.style3(ComponentColour.GRAY, "bold", " [", "dimensionalpocketsii.gui.button.direction.east", "]")),
						ComponentHelper.style(ComponentColour.GRAY, "dimensionalpocketsii.gui.button.direction.suffix").append(this.pocket.getSide(Direction.EAST).getColouredComp()) };
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
		}
	}

	protected void renderUIHelpElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (this.getHasUIHelpShow()) {
			for (CosmosUIHelpElement element : this.uiHelpElements) {
				element.render(graphics, mouseX, mouseY, partialTicks);
			}
			
			for (Renderable widget : this.renderables) {
				if (!(widget instanceof CosmosUIHelpElement) && !(widget instanceof CosmosButtonUIHelp) && !(widget instanceof CosmosButtonUIMode)) {
					if (widget instanceof Button) {
						Button button = (Button) widget;
						
						button.active = false;
					}
				}
			}
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
		int[] screen_coords = CosmosUISystem.Init.getScreenCoords(this, this.imageWidth, this.imageHeight);
		
		this.uiModeButton = this.addRenderableWidget(new CosmosButtonUIMode(this.getUIMode(), this.getScreenCoords()[0] + 345, this.getScreenCoords()[1] + 5, false, true, true, ComponentHelper.empty(), (button) -> { this.changeUIMode(); } ));

		if (this.getHasUIHelp()) {
			this.addUIHelpButton(screen_coords, uiHelpButtonIndex, (button) -> { this.changeUIHelp(); });
		}
		
		int[] sides = new int[] { 0, 0, 0, 0, 0, 0 };
		for (Direction c : Direction.values()) {
			sides[c.get3DDataValue()] = (this.pocket.getSideArray()[c.get3DDataValue()].getIndex());
		}
		
		this.buttonDown  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + DBI[0], this.getScreenCoords()[1] + DBI[1], 18, true, true, sides[0], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonDown, true);  }, (button) -> { return button.get(); }));
		this.buttonUp    = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + UBI[0], this.getScreenCoords()[1] + UBI[1], 18, true, true, sides[1], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonUp, true);    }, (button) -> { return button.get(); }));
		this.buttonNorth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + NBI[0], this.getScreenCoords()[1] + NBI[1], 18, true, true, sides[2], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonNorth, true); }, (button) -> { return button.get(); }));
		this.buttonSouth = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + SBI[0], this.getScreenCoords()[1] + SBI[1], 18, true, true, sides[3], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonSouth, true); }, (button) -> { return button.get(); }));
		this.buttonWest  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + WBI[0], this.getScreenCoords()[1] + WBI[1], 18, true, true, sides[4], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonWest, true);  }, (button) -> { return button.get(); }));
		this.buttonEast  = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + EBI[0], this.getScreenCoords()[1] + EBI[1], 18, true, true, sides[5], ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonEast, true);  }, (button) -> { return button.get(); }));
		
		this.buttonTrapPlayers = this.addRenderableWidget(new DimensionalButton(this.getScreenCoords()[0] + TPBI[0], this.getScreenCoords()[1] + TPBI[1], 18, true, true, pocket.getTrapState().getIndex() + 14, ComponentHelper.empty(), (button) -> { this.clickButton(this.buttonTrapPlayers, true); }, (button) -> { return button.get(); }));
		
		this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBCI[0], this.getScreenCoords()[1] + TBCI[1], 18, !pocket.getFluidTank().isEmpty(), true, pocket.getFluidTank().isEmpty() ? 15 : 16, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTankClear, isLeftClick); }));
		this.buttonLock = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + LBI[0], this.getScreenCoords()[1] + LBI[1], 18, true, true, pocket.getLockState().getIndex() + 8, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonLock, isLeftClick); }));
		this.buttonAllowedPlayers = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + APBI[0], this.getScreenCoords()[1] + APBI[1], 18, true, true, pocket.getAllowedPlayerState().getIndex() + 10, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonAllowedPlayers, isLeftClick); }));
		this.buttonHostileSpawn = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + HSBI[0], this.getScreenCoords()[1] + HSBI[1], 18, true, true, pocket.getHostileSpawnState().getIndex() + 15, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonHostileSpawn, isLeftClick); }));
		
		this.buttonTextClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TCBI[0], this.getScreenCoords()[1] + TCBI[1], 18, true, true, 14, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTextClear, isLeftClick); }));
		this.buttonTextPlus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TABI[0], this.getScreenCoords()[1] + TABI[1], 18, true, true, 1, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTextPlus, isLeftClick); }));
		this.buttonTextMinus = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + TMBI[0], this.getScreenCoords()[1] + TMBI[1], 18, true, true, 2, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTextMinus, isLeftClick); }));
		
		this.buttonPlaceHolder0 = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + PB0I[0], this.getScreenCoords()[1] + PB0I[1], 18, true, true, 0, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonPlaceHolder0, isLeftClick); }));
		this.buttonPlaceHolder1 = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + PB1I[0], this.getScreenCoords()[1] + PB1I[1], 18, true, true, 0, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonPlaceHolder1, isLeftClick); }));
	}
	
	private void clickButton(Button button, boolean isLeftClick) {
		if (isLeftClick) {
			if (button.equals(this.buttonLock)) {
				PacketDistributor.sendToServer(new PacketLock(this.pocket.getDominantChunkPos(), !pocket.getLockStateValue()));
				pocket.setLockState(!pocket.getLockStateValue());
			} else if (button.equals(this.buttonTrapPlayers)) {
				PacketDistributor.sendToServer(new PacketTrapPlayers(this.pocket.getDominantChunkPos(), !pocket.getTrapStateValue()));
				pocket.setTrapState(!pocket.getTrapStateValue());
			} else if (button.equals(this.buttonHostileSpawn)) {
				PacketDistributor.sendToServer(new PacketHostileSpawnState(this.pocket.getDominantChunkPos(), !pocket.getHostileSpawnStateValue()));
				pocket.setHostileSpawnState(!pocket.getHostileSpawnStateValue());
			} else if (button.equals(this.buttonAllowedPlayers)) {
				PacketDistributor.sendToServer(new PacketLockToAllowedPlayers(this.pocket.getDominantChunkPos(), !pocket.getAllowedPlayerStateValue()));
				pocket.setAllowedPlayerState(!pocket.getAllowedPlayerStateValue());
			}
			
			if (this.textField != null) {
				String value = this.textField.getValue();
				
				if (button.equals(this.buttonTextClear)) {
					this.textField.setValue("");
				} else if (button.equals(this.buttonTextPlus)) {
					if (!value.isEmpty() && value.length() >= 3) {
						PacketDistributor.sendToServer(new PacketAllowedPlayer(pocket.getDominantChunkPos(), value, true));
						pocket.addAllowedPlayerNBT(value);
						this.textField.setValue("");
					} 
				} else if (button.equals(this.buttonTextMinus)) {
					int selected = this.getSelectedWidgetIndex();
					
					if (selected != 0) {
						String string = this.getWidgetList().get(selected).getDisplayString();
						PacketDistributor.sendToServer(new PacketAllowedPlayer(pocket.getDominantChunkPos(), string, false));
						
						pocket.removeAllowedPlayerNBT(string);
						this.removeElement();
					}
				}
			}
			
			if (button.equals(this.buttonTankClear)) {
				if (hasShiftDown()) {
					System.out.println("TEST");
					PacketDistributor.sendToServer(new PacketEmptyTank(this.pocket.getDominantChunkPos()));
					pocket.emptyFluidTank();
				}
			}
			
			else if (button.equals(this.buttonDown)) {
				PacketDistributor.sendToServer(new PacketBlockSideState(this.pocket.getDominantChunkPos(), Direction.DOWN.get3DDataValue()));
				pocket.cycleSide(Direction.DOWN, true);
			} else if (button.equals(this.buttonUp)) {
				PacketDistributor.sendToServer(new PacketBlockSideState(this.pocket.getDominantChunkPos(), Direction.UP.get3DDataValue()));
				pocket.cycleSide(Direction.UP, true);
			} else if (button.equals(this.buttonNorth)) {
				PacketDistributor.sendToServer(new PacketBlockSideState(this.pocket.getDominantChunkPos(), Direction.NORTH.get3DDataValue()));
				pocket.cycleSide(Direction.NORTH, true);
			} else if (button.equals(this.buttonSouth)) {
				PacketDistributor.sendToServer(new PacketBlockSideState(this.pocket.getDominantChunkPos(), Direction.SOUTH.get3DDataValue()));
				pocket.cycleSide(Direction.SOUTH, true);
			} else if (button.equals(this.buttonWest)) {
				PacketDistributor.sendToServer(new PacketBlockSideState(this.pocket.getDominantChunkPos(), Direction.WEST.get3DDataValue()));
				pocket.cycleSide(Direction.WEST, true);
			} else if (button.equals(this.buttonEast)) {
				PacketDistributor.sendToServer(new PacketBlockSideState(this.pocket.getDominantChunkPos(), Direction.EAST.get3DDataValue()));
				pocket.cycleSide(Direction.EAST, true);
			}
		}
		
		this.addButtons();
	}

	protected void addUIHelpElements() { 
		this.clearUIHelpElementList();

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
	
	public void initTextField() {
		//this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
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

	public EnumUIHelp getUIHelp() {
		if (this.stack != null) {
			return DimensionalElytraplate.getUIHelp(this.stack);
		}
		
		return EnumUIHelp.HIDDEN;
	}

	private void changeUIMode() {
		PacketDistributor.sendToServer(new PacketElytraplateUpdateUIMode(this.playerUUID, 2, this.getUIMode().getNextState()));
		DimensionalElytraplate.setUIMode(this.stack, this.getUIMode().getNextState());
	}

	private void changeUIHelp() {
		PacketDistributor.sendToServer(new PacketElytraplateUpdateUIHelp(this.playerUUID, 2, this.getUIHelp().getNextState()));
		DimensionalElytraplate.setUIHelp(this.stack, this.getUIHelp().getNextState());
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double directionX, double directionY) {
		if (this.scrollEnabled()) {
			int maxScroll = this.widgetList.size() - this.widgetCount();
			this.currentScroll += -directionY;
			
			if (this.currentScroll >= 0) {
				if (this.currentScroll <= maxScroll) {
					if (directionY == -1.0F) {
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
		
		return ((this.textField != null) && this.textField.mouseScrolled(mouseX, mouseY, directionX, directionY)) ? true : super.mouseScrolled(mouseX, mouseY, directionX, directionY);
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
	
	private void renderWidgetList(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		for (int i = 0; i < this.widgetList.size(); i++) {
			CosmosListWidget widget = this.getListWidget(i);
			
			if (i >= this.topIndex && i <= this.topIndex + this.widgetCount() && i < this.widgetList.size()) {
				widget.renderWidget(graphics, font, this.getScreenCoords(), this.listIndex[0], this.listIndex[1] + ((this.listIndex[4] + this.listIndex[5]) * (i - this.topIndex)), mouseX, mouseY, i, (this.listIndex[1] + this.listIndex[3]));
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
	
	protected void renderScrollElement(GuiGraphics graphics) {
		int[] scrollType = new int[] { 15, 0, 15 };
		
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
			
			CosmosUISystem.Render.renderStaticElementToggled(graphics, this.getScreenCoords(), posX, this.scrollEnabled ? posYUpdated : posY, scrollType[0], scrollType[type + 1], 13, 15, true, BASE.GUI_ELEMENT_MISC_LOC);
		}
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

		if (!this.textField.mouseClicked(mouseX, mouseY, mouseButton)) { 
			this.textField.setFocused(false);
		} else {
			this.textField.setFocused(true);
		}

		if (buttonLock.isMouseOver(mouseX, mouseY) && buttonLock.isActive() && buttonLock.visible) {
			if (mouseButton == 1) {
				buttonLock.onClick(false);
			} else if (mouseButton == 0) {
				buttonLock.onClick(true);
			}
		}

		if (buttonAllowedPlayers.isMouseOver(mouseX, mouseY) && buttonAllowedPlayers.isActive() && buttonAllowedPlayers.visible) {
			if (mouseButton == 1) {
				buttonAllowedPlayers.onClick(false);
			} else if (mouseButton == 0) {
				buttonAllowedPlayers.onClick(true);
			}
		}

		if (buttonHostileSpawn.isMouseOver(mouseX, mouseY) && buttonHostileSpawn.isActive() && buttonHostileSpawn.visible) {
			if (mouseButton == 1) {
				buttonHostileSpawn.onClick(false);
			} else if (mouseButton == 0) {
				buttonHostileSpawn.onClick(true);
			}
		}

		if (buttonTextClear.isMouseOver(mouseX, mouseY) && buttonTextClear.isActive() && buttonTextClear.visible) {
			if (mouseButton == 1) {
				buttonTextClear.onClick(false);
			} else if (mouseButton == 0) {
				buttonTextClear.onClick(true);
			}
		}

		if (buttonTextMinus.isMouseOver(mouseX, mouseY) && buttonTextMinus.isActive() && buttonTextMinus.visible) {
			if (mouseButton == 1) {
				buttonTextMinus.onClick(false);
			} else if (mouseButton == 0) {
				buttonTextMinus.onClick(true);
			}
		}

		if (buttonTextPlus.isMouseOver(mouseX, mouseY) && buttonTextPlus.isActive() && buttonTextPlus.visible) {
			if (mouseButton == 1) {
				buttonTextPlus.onClick(false);
			} else if (mouseButton == 0) {
				buttonTextPlus.onClick(true);
			}
		}

		if (buttonTankClear.isMouseOver(mouseX, mouseY) && buttonTankClear.isActive() && buttonTankClear.visible) {
			if (mouseButton == 1) {
				buttonTankClear.onClick(false);
			} else if (mouseButton == 0) {
				buttonTankClear.onClick(true);
			}
		}

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void addUIHelpButton(int[] screen_coords, int[] indexIn, Button.OnPress pressAction) {
		this.uiHelpButton = this.addRenderableWidget(new CosmosButtonUIHelp(this.getUIHelp(), screen_coords[0] + indexIn[0], screen_coords[1] + indexIn[1], true, true, ComponentHelper.empty(), pressAction));
	}

	protected void clearUIHelpElementList() {
		this.uiHelpElements.clear();
	}

	protected void setHasUIElementDeadzone() {
		this.hasUIHelpElementDeadzone = true;
	}

	protected void addRenderableUIHelpElement(int[] screenCoords, int xIn, int yIn, int widthIn, int heightIn, Component... descIn) {
		this.addRenderableUIHelpElement(screenCoords, xIn, yIn, widthIn, heightIn, true, descIn);
	}
	
	protected void addRenderableUIHelpElement(int[] screenCoords, int xIn, int yIn, int widthIn, int heightIn, boolean isVisible, Component... descIn) {
		this.addUIHelpElement(new CosmosUIHelpElement(screenCoords[0] + xIn, screenCoords[1] + yIn, widthIn, heightIn, descIn).setVisible(isVisible));
	}

	private CosmosUIHelpElement addUIHelpElement(CosmosUIHelpElement elementIn) {
		this.uiHelpElements.add(elementIn);
		//this.renderables.add(elementIn);
		return elementIn;
	}
	
	protected void setUIHelpElementDeadzone(int minX, int minY, int maxX, int maxY) {
		this.setHasUIElementDeadzone();
		this.uiHelpElementDeadzone = new int[] { minX, minY, maxX, maxY };
	}

	protected boolean getHasUIHelp() {
		return this.hasUIHelp;
	}

	protected boolean getHasUIHelpShow() {
		return getHasUIHelp() && getUIHelp().equals(EnumUIHelp.SHOWN);
	}

	protected void setHasUIHelp() {
		this.hasUIHelp = true;
	}

	protected void setUIHelpButtonIndex(int posX, int posY) {
		this.setHasUIHelp();
		this.uiHelpButtonIndex = new int[] { posX, posY };
	}

	protected void setUIHelpTitleOffset(int yOffset) {
		this.uiHelpTitleYOffset = yOffset;
	}

	protected void renderHelpElementHoverEffect(GuiGraphics graphics, int mouseX, int mouseY) {
		if (this.getHasUIHelpShow()) {
			if (this.getUIHelp().equals(EnumUIHelp.SHOWN)) {
				for (CosmosUIHelpElement element : this.uiHelpElements) {
					if (element.isMouseOver(mouseX, mouseY)) {
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
						
						graphics.renderComponentTooltip(this.font, element.getHoverElement(), mouseX, mouseY);
					}
				}
				
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				
				Component title = ComponentHelper.style(ComponentColour.GREEN, "cosmoslibrary.gui_help_title");
				//this.font.draw(poseStack, title, ((this.getScreenCoords()[0] * 2) / 2) + (this.imageWidth / 2) - (this.font.width(title) / 2), this.getScreenCoords()[1] - 8, CosmosColour.WHITE.dec());
				graphics.renderComponentTooltip(this.font, Arrays.asList(title), ((this.getScreenCoords()[0] * 2) / 2) + (this.imageWidth / 2) - (this.font.width(title) / 2) - 13, this.getScreenCoords()[1] - 2 + this.uiHelpTitleYOffset);
			}
		}
	}
}