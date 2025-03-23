package com.tcn.dimensionalpocketsii.client.screen;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionTitle;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionsList;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.dimensionalpocketsii.core.management.ConfigurationManager;

import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;

@OnlyIn(Dist.CLIENT)
public final class ScreenConfiguration extends Screen {

	private final Screen parent;

	private final int TITLE_HEIGHT = 8;

	private final int OPTIONS_LIST_TOP_HEIGHT = 24;
	private final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
	private final int OPTIONS_LIST_ITEM_HEIGHT = 25;

	private final int BUTTON_WIDTH = 200;
	private final int BUTTON_HEIGHT = 20;
	private final int DONE_BUTTON_TOP_OFFSET = 26;

	private CosmosOptionsList optionsRowList;
	
	private static ConfigGuiHandler.ConfigGuiFactory INSTANCE = new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> new ScreenConfiguration(screen));

	public static ConfigGuiFactory getInstance() {
		return INSTANCE;
	}
	
	public ScreenConfiguration(Screen parent) {
		super(ComponentHelper.style(ComponentColour.POCKET_PURPLE_GUI, "bold", "dimensionalpocketsii.gui.config.name"));

		this.parent = parent;
	}

	@Override
	protected void init() {
		this.optionsRowList = new CosmosOptionsList(
			this.minecraft, this.width, this.height,
			OPTIONS_LIST_TOP_HEIGHT,
			this.height - OPTIONS_LIST_BOTTOM_OFFSET,
			OPTIONS_LIST_ITEM_HEIGHT
		);

		this.optionsRowList.addBig(
			new CosmosOptionTitle(ComponentColour.LIGHT_GRAY, true, "dimensionalpocketsii.gui.config.general_title")
		);
		
		this.optionsRowList.addBig(
			new ProgressOption(
				"dimensionalpocketsii.gui.config.height", 15, 255, 1.0F,
				(options) -> (double) ConfigurationManager.getInstance().getInternalHeight(),
				(options, newValue) -> ConfigurationManager.getInstance().setInternalHeight(newValue.intValue()),
				(options, option) -> ComponentHelper.style(ComponentColour.PURPLE, "dimensionalpocketsii.gui.config.height_slide").append(ComponentHelper.style(ComponentColour.GREEN, "bold", " [ " + option.get(options) + " ]"))
			)
		);
		
		this.optionsRowList.addBig(
			new ProgressOption(
				"dimensionalpocketsii.gui.config.focus_jump_range", 4, 32, 1.0F,
				(options) -> (double) ConfigurationManager.getInstance().getFocusJumpRange(),
				(options, newValue) -> ConfigurationManager.getInstance().setFocusJumpRange(newValue.intValue()),
				(options, option) -> ComponentHelper.style(ComponentColour.YELLOW, "dimensionalpocketsii.gui.config.jump_range_slide").append(ComponentHelper.style(ComponentColour.GREEN, "bold", " [ " + option.get(options) + " ]"))
			)
		);
		
		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.use_structures", TYPE.YES_NO,
				(options) ->ConfigurationManager.getInstance().getCanPlaceStructures(),
				(options, newValue) -> ConfigurationManager.getInstance().setCanPlaceStructures(newValue)),
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.use_items", TYPE.YES_NO,
				(options) -> ConfigurationManager.getInstance().getCanUseItems(),
				(options, newValue) -> ConfigurationManager.getInstance().setCanUseItems(newValue)
			)
		);
		
		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.use_commands", TYPE.YES_NO,
				(options) -> ConfigurationManager.getInstance().getCanUseCommands(),
				(options, newValue) -> ConfigurationManager.getInstance().setCanUseCommands(newValue)
			),
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.chunks", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getKeepChunksLoaded(), 
				(options, newValue) -> ConfigurationManager.getInstance().setKeepChunksLoaded(newValue)
			)
			
		);
		
		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.walls", TYPE.YES_NO,
				(options) -> ConfigurationManager.getInstance().getCanDestroyWalls(),
				(options, newValue) -> ConfigurationManager.getInstance().setCanDestroyWalls(newValue)
			),
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.book", TYPE.YES_NO,
				(options) -> ConfigurationManager.getInstance().getSpawnWithTome(),
				(options, newValue) -> ConfigurationManager.getInstance().setSpawnWithTome(newValue)
			)
		);

		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.replace", TYPE.YES_NO,
				(options) -> ConfigurationManager.getInstance().getInternalReplace(),
				(options, newValue) -> ConfigurationManager.getInstance().setInternalReplace(newValue)
			),
			new CosmosOptionBoolean(
				ComponentColour.POCKET_PURPLE_LIGHT, false, "dimensionalpocketsii.gui.config.hostile", TYPE.YES_NO,
				(options) -> ConfigurationManager.getInstance().getStopHostileSpawns(),
				(options, newValue) -> ConfigurationManager.getInstance().setStopHostileSpawns(newValue)
			)
		);
		
		this.optionsRowList.addBig(
			new CosmosOptionTitle(ComponentColour.LIGHT_GRAY, true, "dimensionalpocketsii.gui.config.messages_title")
		);
		
		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.CYAN, false, "dimensionalpocketsii.gui.config.message.info", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getInfoMessage(),
				(options, newValue) -> ConfigurationManager.getInstance().setInfoMessage(newValue)
			),
			new CosmosOptionBoolean(
				ComponentColour.CYAN, false, "dimensionalpocketsii.gui.config.message.debug", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getDebugMessage(),
				(options, newValue) -> ConfigurationManager.getInstance().setDebugMessage(newValue)
			)
		);

		this.optionsRowList.addBig(
			new CosmosOptionTitle(ComponentColour.LIGHT_GRAY, true, "dimensionalpocketsii.gui.config.visual_title")
		);
		
		this.optionsRowList.addBig(
			new CosmosOptionBoolean(
				ComponentColour.LIGHT_BLUE, false, "dimensionalpocketsii.gui.config.textures", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getConnectedTexturesInsidePocket(),
				(options, newValue) -> ConfigurationManager.getInstance().setConnectedTexturesInsidePocket(newValue)
			)
		);
		
		this.addWidget(this.optionsRowList);
		
		this.addRenderableWidget(new Button(
			(this.width - BUTTON_WIDTH) / 2, this.height - DONE_BUTTON_TOP_OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT,
			ComponentHelper.style(ComponentColour.GREEN, "bold", "dimensionalpocketsii.gui.done"), (button) -> { this.onClose(); })
		);
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float ticks) {
		this.renderBackground(matrixStack);
		
		this.optionsRowList.render(matrixStack, mouseX, mouseY, ticks);
		
		drawCenteredString(matrixStack, this.font, this.title, width / 2, TITLE_HEIGHT, 0xFFFFFF);

		this.renderComponentHoverEffect(matrixStack, Style.EMPTY, mouseX, mouseY);
		super.render(matrixStack, mouseX, mouseY, ticks);
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack matrixStack, Style style, int mouseX, int mouseY) {
		if (mouseY > this.OPTIONS_LIST_TOP_HEIGHT && mouseY < this.height - this.OPTIONS_LIST_BOTTOM_OFFSET) {
			if (this.optionsRowList.children().get(0) != null) {
				for (int i = 0; i < this.optionsRowList.children().size(); i++) {
					if (!(i == 0) && !(i == 7) && !(i == 9)) {
						CosmosOptionsList.Entry testRow = this.optionsRowList.children().get(i);
						
						if (testRow.children().size() > 1) {
							GuiEventListener left = testRow.children().get(0);
							GuiEventListener right = testRow.children().get(1);
							
							if (left.isMouseOver(mouseX, mouseY)) {
								this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild((double) i + 0.3D)), mouseX, mouseY + 30);
							} else if (right.isMouseOver(mouseX, mouseY)) {
								this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild((double) i + 0.6D)), mouseX, mouseY + 30);
							}
							
						} else {
							if (testRow.getChildAt(mouseX, mouseY).isPresent()) {
								GuiEventListener list = testRow.getChildAt(mouseX, mouseY).get();
								
								if (list.isMouseOver(mouseX, mouseY)) {
									this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild(i)), mouseX, mouseY + 30);
								}
							}
						}
					}
				}
			}
		}
		super.renderComponentHoverEffect(matrixStack, style, mouseX, mouseY);
	}
	
	public BaseComponent[] getTooltipForChild(double index) {
		ComponentColour desc = ComponentColour.LIGHT_GRAY;
		
		if (index == 1.0D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.height_info"), ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.gui.config.height_info_two") };
		} if (index == 2.0D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.jump_range_info"), ComponentHelper.style(ComponentColour.LIGHT_RED, "dimensionalpocketsii.gui.config.jump_range_info_two") };
		} else if (index == 3.3D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.use_structures_info"), ComponentHelper.style(ComponentColour.LIME, "dimensionalpocketsii.gui.config.use_structures_info_two") };
		} else if (index == 3.6D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.use_items_info"), ComponentHelper.style(ComponentColour.LIME, "dimensionalpocketsii.gui.config.use_items_info_two") };
		} else if (index == 4.3D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.use_commands_info"), ComponentHelper.style(ComponentColour.LIME, "dimensionalpocketsii.gui.config.use_commands_info_two") };
		} else if (index == 4.6D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.chunks_info") };
		} else if (index == 5.3D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.walls_info") };
		} else if (index == 5.6D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.book_info") };
		} else if (index == 6.3D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.replace_info") };
		} else if (index == 6.6D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.hostile_info") };
		} else if (index == 8.3D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.message.info_desc"), ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.config.message.restart") };
		} else if (index == 8.6D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.message.debug_desc"), ComponentHelper.style(ComponentColour.RED, "bold", "dimensionalpocketsii.gui.config.message.restart") };
		} else if (index == 10.0D) {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.textures_info") };
		} else {
			return new BaseComponent[] { ComponentHelper.style(desc, "dimensionalpocketsii.gui.config.missing") };
		}
	}
	
	@Override
	public boolean handleComponentClicked(@Nullable Style styleIn) {
		return super.handleComponentClicked(styleIn);
	}
	
	@Override
	public boolean keyPressed(int mouseX, int mouseY, int ticks) {
		return super.keyPressed(mouseX, mouseY, ticks);
	}
	
    @Override
    public void onClose() {
    	this.minecraft.setScreen(parent);
    	
        ConfigurationManager.save();
    }
}