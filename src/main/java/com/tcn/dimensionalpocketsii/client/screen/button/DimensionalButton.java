package com.tcn.dimensionalpocketsii.client.screen.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.CosmosReference;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.dimensionalpocketsii.DimReference;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class DimensionalButton extends Button {
	protected int width;
	protected int height;
	public int x;
	public int y;
	private Component message;
	private boolean wasHovered;
	protected boolean isHovered;
	public boolean active = true;
	public boolean visible = true;
	protected float alpha = 1.0F;
	protected long nextNarration = Long.MAX_VALUE;
	private boolean focused;
	private int identifier;

	public DimensionalButton(int x, int y, int size, boolean enabled, boolean visible, int identifier, Component title, Button.OnPress pressedAction) {
		super(x, y, size, size, title, pressedAction);
		this.x = x;
		this.y = y;
		this.width = size;
		this.height = size;
		this.message = title;

		this.active = enabled;
		this.visible = visible;
		this.identifier = identifier;
	}

	public DimensionalButton(int x, int y, boolean enabled, boolean visible, int identifier, Component title, Button.OnPress pressedAction) {
		this(x, y, 20, enabled, visible, identifier, title, pressedAction);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.renderButton(poseStack, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void onPress() {
		if (this.active) {
			this.onPress.onPress(this);
		}
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		if (!this.active) {
			return false;
		}
		return super.isMouseOver(mouseX, mouseY);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		CosmosUISystem.setTextureWithColourAlpha(poseStack, DimReference.GUI.RESOURCE.GUI_DIMENSIONAL_BUTTON, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
		
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		int i = this.getHoverState(this.isHovered);

		if (this.identifier >= 0 && this.identifier <= 23) {
			if (this.identifier >= 0 && this.identifier <= 5) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i], this.width, this.height);
				}
			} else if (this.identifier > 5 && this.identifier <= 11) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier - 6], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i + 3], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier - 6], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i + 3], this.width, this.height);
				}
			} else if (this.identifier > 11 && this.identifier <= 17) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier - 12], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i + 6], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier - 12], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i + 6], this.width, this.height);
				}
			} else if (this.identifier > 17 && this.identifier <= 23) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier - 18], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i + 9], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier - 18], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i + 9], this.width, this.height);
				}
			} 
		}
		
		else if (this.identifier > 23 && this.identifier <= 47) {
			CosmosUISystem.setTexture(poseStack, DimReference.GUI.RESOURCE.GUI_DIMENSIONAL_BUTTON_0);
			
			if (this.identifier > 23 && this.identifier <= 29) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier - 24], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier - 24], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i], this.width, this.height);
				}
			} else if (this.identifier > 29 && this.identifier <= 35) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier - 30], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i + 3], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier - 30], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i + 3], this.width, this.height);
				}
			} else if (this.identifier > 35 && this.identifier <= 41) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier - 36], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i + 6], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier - 36], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i + 6], this.width, this.height);
				}
			} else if (this.identifier > 41 && this.identifier <= 47) {
				if (this.width == 20 && this.height == 20) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X[identifier - 42], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y[i + 9], this.width, this.height);
				} else if (this.width == 18 && this.height == 18) {
					this.blit(poseStack, this.x, this.y, CosmosReference.RESOURCE.INFO.BUTTON_STATE_X_SMALL[identifier - 42], CosmosReference.RESOURCE.INFO.BUTTON_STATE_Y_SMALL[i + 9], this.width, this.height);
				}
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