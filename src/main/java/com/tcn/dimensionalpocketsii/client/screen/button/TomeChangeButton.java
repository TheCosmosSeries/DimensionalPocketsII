package com.tcn.dimensionalpocketsii.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TomeChangeButton extends Button {
	private final boolean isForward;
	private final boolean playTurnSound;

	private ResourceLocation texture;

	public TomeChangeButton(int posX, int posY, boolean isForwardIn, boolean playTurnSoundIn, ResourceLocation textureIn, Button.OnPress onPressIn) {
		super(posX, posY, 25, 13, ComponentHelper.empty(), onPressIn);
		this.isForward = isForwardIn;
		this.playTurnSound = playTurnSoundIn;

		this.texture = textureIn;
		this.visible = true;
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

		RenderSystem.enableBlend();
	    RenderSystem.defaultBlendFunc();
	    RenderSystem.enableDepthTest();
	    
		int i = 0;
		int j = 230;

		if (this.isMouseOver(mouseX, mouseY)) {
			i += 25;
		}

		if (!this.isForward) {
			j += 13;
		}

		this.blit(matrixStack, this.x, this.y, i, j, 25, 13);
	}

	public void playDownSound(SoundManager handler) {
		if (this.playTurnSound) {
			handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 5F));
		}
	}
}
