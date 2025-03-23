package com.tcn.dimensionalpocketsii.client.screen;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionInstance;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionListElement;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionListTextEntry;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionTitle;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionTitleReset;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionsList;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.management.PocketsConfigManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public final class PocketsConfigScreen extends OptionsSubScreen {

	private final Screen parent;
	
	private final int OPTIONS_LIST_TOP_HEIGHT = 24;
	private final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
	private final int OPTIONS_LIST_ITEM_HEIGHT = 25;
	private final int OPTIONS_LIST_BUTTON_HEIGHT = 20;
	private final int OPTIONS_LIST_WIDTH = 335;

	private final int BIG_WIDTH = 310;
	
	private CosmosOptionsList OPTIONS_ROW_LIST;
	private CurrentScreen CURRENT_SCREEN = CurrentScreen.HOME;

	private final ComponentColour DESC_COLOUR = ComponentColour.LIGHT_GRAY;
	
	private CosmosOptionListTextEntry EDIT_BOX_BLOCKS;
	private CosmosOptionListTextEntry EDIT_BOX_ITEMS;
	private CosmosOptionListTextEntry EDIT_BOX_COMMANDS;
	
	private Button closeButton;
	
	private enum CurrentScreen {
		HOME,
		BLOCKS,
		ITEMS,
		COMMANDS;
		
		public boolean home() {
			return this == HOME;
		}

		public boolean blocks() {
			return this == BLOCKS;
		}

		public boolean items() {
			return this == ITEMS;
		}

		public boolean commands() {
			return this == COMMANDS;
		}
	}

    @SuppressWarnings("resource")
	public PocketsConfigScreen(ModContainer container, Screen parent) {
		super(parent, Minecraft.getInstance().options, ComponentHelper.style(ComponentColour.POCKET_PURPLE_GUI, "boldunderline", "dimensionalpocketsii.gui.config.name"));

		this.parent = parent;
		
		this.EDIT_BOX_BLOCKS = new CosmosOptionListTextEntry(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "", ""), true, ComponentHelper.style(ComponentColour.GREEN, "bold", "+"), ComponentHelper.style(ComponentColour.GREEN, "", "dimensionalpocketsii.gui.config.add"), (button) -> {  }, (button) -> { return button.get(); });
		this.EDIT_BOX_ITEMS = new CosmosOptionListTextEntry(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "", ""), true, ComponentHelper.style(ComponentColour.GREEN, "bold", "+"), ComponentHelper.style(ComponentColour.GREEN, "", "dimensionalpocketsii.gui.config.add"), (button) -> {  }, (button) -> { return button.get(); });
		this.EDIT_BOX_COMMANDS = new CosmosOptionListTextEntry(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "", ""), true, ComponentHelper.style(ComponentColour.GREEN, "bold", "+"), ComponentHelper.style(ComponentColour.GREEN, "", "dimensionalpocketsii.gui.config.add"), (button) -> {  }, (button) -> { return button.get(); });
	}
	
	@Override
	protected void init() {
		super.init();
		this.initRowList();
		this.addWidget(this.OPTIONS_ROW_LIST);
	}

	public void initRowList() {
		if (this.CURRENT_SCREEN.home()) {
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionTitle(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "dimensionalpocketsii.gui.config.general_title"))
			);
			
			this.OPTIONS_ROW_LIST.addBig(
				CosmosOptionInstance.createIntSlider(ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.config.height"),
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.height_info"), ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.config.height_info_two")), 
					PocketsConfigManager.getInstance().getInternalHeight(), 15, 255, 15,
					ComponentColour.WHITE, ComponentHelper.style(ComponentColour.GREEN, "15 (Default)"), ComponentHelper.style(ComponentColour.WHITE, "Blocks"), ComponentHelper.style(ComponentColour.RED, "255 (Max)"), (intValue) -> {
					PocketsConfigManager.getInstance().setInternalHeight(intValue);
					this.updateWidgets();
				})
			);

			this.OPTIONS_ROW_LIST.addBig(
				CosmosOptionInstance.createIntSlider(ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.config.height_enhanced"),
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.height_enhanced_info"), ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.config.height_enhanced_info_two")), 
					PocketsConfigManager.getInstance().getInternalHeightEnhanced(), 31, 255, 31,
					ComponentColour.WHITE, ComponentHelper.style(ComponentColour.GREEN, "31 (Default)"), ComponentHelper.style(ComponentColour.WHITE, "Blocks"), ComponentHelper.style(ComponentColour.RED, "255 (Max)"), (intValue) -> {
					PocketsConfigManager.getInstance().setInternalHeightEnhanced(intValue);
					this.updateWidgets();
				})
			);
			
			this.OPTIONS_ROW_LIST.addBig(
				CosmosOptionInstance.createIntSlider(ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.config.jump_range"),
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.jump_range_info"), ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.config.jump_range_info_two")), 
					PocketsConfigManager.getInstance().getFocusJumpRange(), 4, 32, 12,
					ComponentColour.WHITE, ComponentHelper.style(ComponentColour.GREEN, "4 (Min)"), ComponentHelper.style(ComponentColour.WHITE, "Blocks"), ComponentHelper.style(ComponentColour.LIGHT_RED, "32 (Max)"), (intValue) -> {
					PocketsConfigManager.getInstance().setFocusJumpRange(intValue);
					this.updateWidgets();
				})
			);
			
			this.OPTIONS_ROW_LIST.addSmall(
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.use_structures", TYPE.YES_NO,
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.use_structures_info"), ComponentHelper.style(ComponentColour.LIME, "dimensionalpocketsii.gui.config.use_structures_info_two")),
					PocketsConfigManager.getInstance().getCanPlaceStructures(),
					(newValue) -> {
						PocketsConfigManager.getInstance().setCanPlaceStructures(newValue);
						this.updateWidgets();
					}, ":"
				),
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.use_items", TYPE.YES_NO,
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.use_items_info"), ComponentHelper.style(ComponentColour.LIME, "dimensionalpocketsii.gui.config.use_items_info_two")),
					PocketsConfigManager.getInstance().getCanUseItems(),
					(newValue) -> {
						PocketsConfigManager.getInstance().setCanUseItems(newValue);
						this.updateWidgets();
					}, ":"
				)
			);
			
			this.OPTIONS_ROW_LIST.addSmall(
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.use_commands", TYPE.YES_NO,
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.use_commands_info"), ComponentHelper.style(ComponentColour.LIME, "dimensionalpocketsii.gui.config.use_commands_info_two")),
					PocketsConfigManager.getInstance().getCanUseCommands(),
					(newValue) -> PocketsConfigManager.getInstance().setCanUseCommands(newValue), ":"
				),
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.chunks", TYPE.ON_OFF,
					CosmosOptionInstance.getTooltipSplitComponent( ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.chunks_info")),
					PocketsConfigManager.getInstance().getKeepChunksLoaded(), 
					(newValue) -> PocketsConfigManager.getInstance().setKeepChunksLoaded(newValue), ":"
				)
			);
	
			this.OPTIONS_ROW_LIST.addSmall(
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.replace", TYPE.YES_NO, 
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.replace_info")),
					PocketsConfigManager.getInstance().getInternalReplace(),
					(newValue) -> PocketsConfigManager.getInstance().setInternalReplace(newValue), ":"
				),
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.hostile", TYPE.YES_NO, 
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.hostile_info")),
					PocketsConfigManager.getInstance().getStopHostileSpawns(),
					(newValue) -> PocketsConfigManager.getInstance().setStopHostileSpawns(newValue), ":"
				)
			);
			
			this.OPTIONS_ROW_LIST.addSmall(
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.walls", TYPE.YES_NO,
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.walls_info")),
					PocketsConfigManager.getInstance().getCanDestroyWalls(),
					(newValue) -> PocketsConfigManager.getInstance().setCanDestroyWalls(newValue), ":"
				),
				new CosmosOptionBoolean(
					ComponentColour.ORANGE, "", "dimensionalpocketsii.gui.config.backups", TYPE.YES_NO,
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.backups_info")),
					PocketsConfigManager.getInstance().getCreateBackups(),
					(newValue) -> PocketsConfigManager.getInstance().setCreateBackups(newValue), ":"
				)
			);
	
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionTitle(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "dimensionalpocketsii.gui.config.messages_title"))
			);
			
			this.OPTIONS_ROW_LIST.addSmall(
				new CosmosOptionBoolean(
					ComponentColour.YELLOW, "", "dimensionalpocketsii.gui.config.message.info", TYPE.ON_OFF, 
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.message.info_DESC_COLOUR"), ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.config.message.restart")),
					PocketsConfigManager.getInstance().getInfoMessage(),
					(newValue) -> PocketsConfigManager.getInstance().setInfoMessage(newValue), ":"
				),
				new CosmosOptionBoolean(
					ComponentColour.YELLOW, "", "dimensionalpocketsii.gui.config.message.debug", TYPE.ON_OFF, 
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.message.debug_DESC_COLOUR"), ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.config.message.restart")),
					PocketsConfigManager.getInstance().getDebugMessage(),
					(newValue) -> PocketsConfigManager.getInstance().setDebugMessage(newValue), ":"
				)
			);
	
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionTitle(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "dimensionalpocketsii.gui.config.visual_title"))
			);
			
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionBoolean(
					ComponentColour.MAGENTA, "", "dimensionalpocketsii.gui.config.textures", TYPE.ON_OFF, 
					CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.textures_info")),
					PocketsConfigManager.getInstance().getConnectedTexturesInsidePocket(),
					(newValue) -> PocketsConfigManager.getInstance().setConnectedTexturesInsidePocket(newValue), ":"
				) 
			);
			
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionTitle(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "dimensionalpocketsii.gui.config.blocked_title"))
			);

			this.OPTIONS_ROW_LIST.addSmall(
				CosmosOptionInstance.createScreenSwitchOption(ComponentHelper.style(ComponentColour.LIGHT_RED, "", "dimensionalpocketsii.gui.config.blocked_structures"), (button) -> { 
					this.switchScreen(CurrentScreen.BLOCKS);
					this.updateWidgets();
				}, ""),
				CosmosOptionInstance.createScreenSwitchOption(ComponentHelper.style(ComponentColour.LIGHT_RED, "", "dimensionalpocketsii.gui.config.blocked_items"), (button) -> { 
					this.switchScreen(CurrentScreen.ITEMS);
					this.updateWidgets();
				}, "")
			);

			this.OPTIONS_ROW_LIST.addSmall(
				CosmosOptionInstance.createScreenSwitchOption(ComponentHelper.style(ComponentColour.LIGHT_RED, "", "dimensionalpocketsii.gui.config.blocked_commands"), (button) -> { 
					this.switchScreen(CurrentScreen.COMMANDS);
					this.updateWidgets();
				}, ""),
				null
			); 
		}
		
		
		
		else if (this.CURRENT_SCREEN.blocks()) {
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionTitleReset(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "dimensionalpocketsii.gui.config.blocked_structures"), 
				(button) -> {
					PocketsConfigManager.getInstance().resetBlockedStructures();
					this.updateWidgets();
				})
			);
			
			this.EDIT_BOX_BLOCKS.setOnPressFunction((button) -> { 
				PocketsConfigManager.getInstance().addBlockedStructure(this.EDIT_BOX_BLOCKS.getEditBox().getValue());
				this.EDIT_BOX_BLOCKS.getEditBox().setValue("");
				this.updateWidgets();
			});
			
			this.OPTIONS_ROW_LIST.addBig(EDIT_BOX_BLOCKS);
			
			for (int i = 0; i < PocketsConfigManager.getInstance().getBlockedStructures().size(); i++) {
				String object = PocketsConfigManager.getInstance().getBlockedStructures().get(i);
				
				this.OPTIONS_ROW_LIST.addBig(
					new CosmosOptionListElement(ComponentHelper.style(ComponentColour.WHITE, "", object), true, 
						ComponentHelper.style(ComponentColour.RED, "bold", "-"), 
						ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.config.remove"),
					(button) -> { 
						PocketsConfigManager.getInstance().removeBlockedStructure(object);
						this.updateWidgets();
					}, 
					(button) -> {
						return button.get();
					})
				);
			}
		}
		
		
		
		else if (this.CURRENT_SCREEN.items()) {
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionTitleReset(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "dimensionalpocketsii.gui.config.blocked_items"), 
				(button) -> {
					PocketsConfigManager.getInstance().resetBlockedItems();
					this.updateWidgets();
				})
			);

			this.EDIT_BOX_ITEMS.setOnPressFunction((button) -> { 
				PocketsConfigManager.getInstance().addBlockedItem(this.EDIT_BOX_ITEMS.getEditBox().getValue());
				this.EDIT_BOX_ITEMS.getEditBox().setValue("");
				this.updateWidgets();
			});
			
			this.OPTIONS_ROW_LIST.addBig(EDIT_BOX_ITEMS);
			
			for (int i = 0; i < PocketsConfigManager.getInstance().getBlockedItems().size(); i++) {
				String object = PocketsConfigManager.getInstance().getBlockedItems().get(i);
				
				this.OPTIONS_ROW_LIST.addBig(
					new CosmosOptionListElement(ComponentHelper.style(ComponentColour.WHITE, "", object), true, 
					ComponentHelper.style(ComponentColour.RED, "bold", "-"), 
					ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.config.remove"), 
					(button) -> { 
						PocketsConfigManager.getInstance().removeBlockedItem(object);
						this.updateWidgets();
					},
					(button) -> {
						return button.get();
					})
				);
			}
		} 
		
		else if (this.CURRENT_SCREEN.commands()) {
			this.OPTIONS_ROW_LIST.addBig(
				new CosmosOptionTitleReset(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "dimensionalpocketsii.gui.config.blocked_commands"), 
				(button) -> {
					PocketsConfigManager.getInstance().resetBlockedCommands();
					this.updateWidgets();
				})
			);

			this.OPTIONS_ROW_LIST.addBig(
				CosmosOptionInstance.createIntSlider(ComponentHelper.style(ComponentColour.ORANGE, "dimensionalpocketsii.gui.config.op_level"),
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC_COLOUR, "dimensionalpocketsii.gui.config.op_level_info"), 
				ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.config.op_level_info_two")), 
				PocketsConfigManager.getInstance().getOPLevel(), 0, 4, 4, ComponentColour.WHITE, ComponentHelper.style(ComponentColour.RED, "0 (Not Recommended)"), 
				ComponentHelper.empty(), ComponentHelper.style(ComponentColour.GREEN, "Default (4)"), 
				(intValue) -> {
					PocketsConfigManager.getInstance().setOPLevel(intValue);
				})
			);

			this.EDIT_BOX_COMMANDS.setOnPressFunction((button) -> {
				if (!this.EDIT_BOX_COMMANDS.getEditBox().getValue().isBlank()) {
					PocketsConfigManager.getInstance().addBlockedCommand(this.EDIT_BOX_COMMANDS.getEditBox().getValue());
					this.EDIT_BOX_COMMANDS.getEditBox().setValue("");
					//this.updateWidgets();
			        PocketsConfigManager.save();
				}
			});
			
			this.OPTIONS_ROW_LIST.addBig(EDIT_BOX_COMMANDS);
			
			for (int i = 0; i < PocketsConfigManager.getInstance().getBlockedCommands().size(); i++) {
				String object = PocketsConfigManager.getInstance().getBlockedCommands().get(i);
				
				this.OPTIONS_ROW_LIST.addBig(
					new CosmosOptionListElement(ComponentHelper.style(ComponentColour.WHITE, "", object), true, 
					ComponentHelper.style(ComponentColour.RED, "bold", "-"), 
					ComponentHelper.style(ComponentColour.RED, "dimensionalpocketsii.gui.config.remove"), 
					(button) -> { 
						PocketsConfigManager.getInstance().removeBlockedCommand(object);
						this.updateWidgets();
				        PocketsConfigManager.save();
					}, (button) -> {
						return button.get();
					})
				);
			}
		}
	}
	
	@Override
	public void renderBackground(GuiGraphics graphicsIn, int mouseX, int mouseY, float ticks) {
		super.renderBackground(graphicsIn, mouseX, mouseY, ticks);
	}

	@Override
	public void render(GuiGraphics graphicsIn, int mouseX, int mouseY, float ticks) {
		super.render(graphicsIn, mouseX, mouseY, ticks);
		this.OPTIONS_ROW_LIST.render(graphicsIn, mouseX, mouseY, ticks);
	}

	public void updateWidgets() {
		double scroll = this.OPTIONS_ROW_LIST.getScrollAmount();
		this.OPTIONS_ROW_LIST.clear();
		this.initRowList();
		this.OPTIONS_ROW_LIST.setScrollAmount(scroll);
	}

	public boolean switchScreen(CurrentScreen screen) {
		this.CURRENT_SCREEN = screen;
		this.updateWidgets();
        PocketsConfigManager.save();
        return false;
	}
	
	@Override
	public void onClose() {
		if (this.CURRENT_SCREEN.home()) {
			this.minecraft.setScreen(this.parent);
	        PocketsConfigManager.save();
		} else {
			this.CURRENT_SCREEN = CurrentScreen.HOME;
			this.updateWidgets();
		}
	}

	@Override
	protected void addOptions() {
		this.OPTIONS_ROW_LIST = new CosmosOptionsList(
			this.minecraft, this.width, this.height, 40, 33,
			OPTIONS_LIST_ITEM_HEIGHT, OPTIONS_LIST_BUTTON_HEIGHT, 310, 26
		);
	}
	
	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		this.OPTIONS_ROW_LIST.resize(minecraft, width, height);
		this.updateWidgets();
		super.resize(minecraft, width, height);
	}

	@SuppressWarnings("unchecked")
	public static List<FormattedCharSequence> tooltipAt(CosmosOptionsList listIn, int mouseX, int mouseY) {
		Optional<AbstractWidget> optional = listIn.getMouseOver((double)  mouseX, (double) mouseY);
		return (List<FormattedCharSequence>) (optional.isPresent() && optional.get() instanceof AbstractWidget ? ((AbstractWidget) optional.get()).getTooltip() : ImmutableList.of());
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.CURRENT_SCREEN.blocks()) {
			if (keyCode == 256) {
				return this.switchScreen(CurrentScreen.HOME);
			}
		} else if (this.CURRENT_SCREEN.items()) {
			if (keyCode == 256) {
				return this.switchScreen(CurrentScreen.HOME);
			}
		} else if (this.CURRENT_SCREEN.commands()) {
			if (keyCode == 256) {
				return this.switchScreen(CurrentScreen.HOME);
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char charIn, int charCode) {
		if (this.CURRENT_SCREEN.blocks()) {
			return !this.EDIT_BOX_BLOCKS.getEditBox().charTyped(charIn, charCode) && !this.EDIT_BOX_BLOCKS.getEditBox().canConsumeInput();
		} else if (this.CURRENT_SCREEN.items()) {
			return !this.EDIT_BOX_ITEMS.getEditBox().charTyped(charIn, charCode) && !this.EDIT_BOX_ITEMS.getEditBox().canConsumeInput();
		} else if (this.CURRENT_SCREEN.commands()) {
			return !this.EDIT_BOX_COMMANDS.getEditBox().charTyped(charIn, charCode) && !this.EDIT_BOX_COMMANDS.getEditBox().canConsumeInput();
		}
		return super.charTyped(charIn, charCode);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int ticks, double dragX, double dragY) {
		if (this.getChildAt(mouseX, mouseY).isPresent()) {
			for (GuiEventListener listener : this.OPTIONS_ROW_LIST.children()) {
				if (listener.isMouseOver(mouseX, mouseY)) {
					this.updateWidgets();
				}
			}
		}
		return super.mouseDragged(mouseX, mouseY, ticks, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int ticks) {
		boolean clicked = super.mouseClicked(mouseX, mouseY, ticks);
		
		if (this.CURRENT_SCREEN.blocks()) {
			if (this.EDIT_BOX_BLOCKS.getEditBox().isMouseOver(mouseX, mouseY)) {
				this.EDIT_BOX_BLOCKS.getEditBox().setFocused(true);
				return this.EDIT_BOX_BLOCKS.getEditBox().mouseClicked(mouseX, mouseY, ticks);
			}
		} else if (this.CURRENT_SCREEN.items()) {
			if (this.EDIT_BOX_ITEMS.getEditBox().isMouseOver(mouseX, mouseY)) {
				this.EDIT_BOX_ITEMS.getEditBox().setFocused(true);
				return this.EDIT_BOX_ITEMS.getEditBox().mouseClicked(mouseX, mouseY, ticks);
			}
		} else if (this.CURRENT_SCREEN.commands()) {
			if (this.EDIT_BOX_COMMANDS.getEditBox().isMouseOver(mouseX, mouseY)) {
				this.EDIT_BOX_COMMANDS.getEditBox().setFocused(true);
				return this.EDIT_BOX_COMMANDS.getEditBox().mouseClicked(mouseX, mouseY, ticks);
			}
		}
		
		if (this.getChildAt(mouseX, mouseY).isPresent()) {
			for (GuiEventListener listener : this.OPTIONS_ROW_LIST.children()) {
				if (!listener.equals(this.closeButton)) {
					if (listener.isMouseOver(mouseX, mouseY)) {
						//listener.mouseClicked(mouseX, mouseY, ticks);
						this.updateWidgets();
					}
				}
			}
		}		
		return clicked;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (this.OPTIONS_ROW_LIST.isMouseOver(mouseX, mouseY)) {
			return this.OPTIONS_ROW_LIST.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}
}