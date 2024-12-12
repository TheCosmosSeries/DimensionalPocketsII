package com.tcn.dimensionalpocketsii.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TomeButton extends Button {
	private ResourceLocation TEXTURE;
	
	protected int width;
	protected int height;
	public int x;
	public int y;
	private int colour;
	protected boolean isHovered;
	protected float alpha = 1.0F;
	protected long nextNarration = Long.MAX_VALUE;
	private int identifier;

	public TomeButton(int x, int y, int size, int identifier, int colour, ResourceLocation location, Button.OnPress pressedAction, Button.CreateNarration createNarr) {
		super(x, y, size, size, ComponentHelper.empty(), pressedAction, createNarr);
		this.x = x;
		this.y = y;
		this.width = size;
		this.height = size;
		
		this.colour = colour;
		this.TEXTURE = location;
		this.identifier = identifier;
	}
	
	public TomeButton(int x, int y, int colour, ResourceLocation location, Button.OnPress pressedAction, Button.CreateNarration createNarr) {
		super(x, y, 15, 25, ComponentHelper.empty(), pressedAction, createNarr);
		this.x = x;
		this.y = y;
		this.width = 15;
		this.height = 25;
		this.colour = colour;
		this.TEXTURE = location;
		this.identifier = 2;
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}
	
	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.setFGColor(this.colour);
		
		float[] rgb = ComponentColour.rgbFloatArray(this.colour);
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.TEXTURE);
		RenderSystem.setShaderColor(rgb[0], rgb[1], rgb[2], 1.0F);
		
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

		RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    RenderSystem.enableDepthTest();
	    
	    int id = this.identifier;
	    boolean hovered = this.isHovered;
	    
	    if (this.width > 10 && this.height > 10) {
		    if (id < 2) {
		    	graphics.blit(this.TEXTURE, this.x, this.y, id == 0 ? 50 : 63, hovered ? 243 : 230, this.width, this.height);
		    } else {
		    	graphics.blit(this.TEXTURE, this.x, this.y, hovered ? 76 : 91, 231, this.width, this.height);
		    }
	    }
	}

	protected int getHoverState(boolean mouseOver) {
		int i = 0;

		if (!this.active) {
			i = 2;
		} else if (mouseOver) {
			i = 1;
		}
		return i;
	}
}