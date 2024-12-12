package com.tcn.dimensionalpocketsii.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.client.renderer.model.DimensionalTridentModel;
import com.tcn.dimensionalpocketsii.core.entity.DimensionalTridentEnhancedEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererDimensionalTridentEnhanced extends EntityRenderer<DimensionalTridentEnhancedEntity> {
	public static final ResourceLocation TRIDENT_LOCATION = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/entity/dimensional_trident_enhanced.png");
	private final DimensionalTridentModel model = new DimensionalTridentModel();

	public RendererDimensionalTridentEnhanced(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void render(DimensionalTridentEnhancedEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource typeBuffer, int packedLightIn) {
		matrixStack.pushPose();
		matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()) + 90.0F));
		VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(typeBuffer, this.model.renderType(this.getTextureLocation(entityIn)), false, entityIn.isFoil());
		this.model.renderToBuffer(matrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, ComponentColour.WHITE.decOpaque());
		matrixStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, matrixStack, typeBuffer, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(DimensionalTridentEnhancedEntity entityIn) {
		return TRIDENT_LOCATION;
	}
}
