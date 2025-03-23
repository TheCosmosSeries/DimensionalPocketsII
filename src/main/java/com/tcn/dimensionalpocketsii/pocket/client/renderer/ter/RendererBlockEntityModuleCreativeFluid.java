package com.tcn.dimensionalpocketsii.pocket.client.renderer.ter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tcn.cosmoslibrary.client.renderer.lib.CosmosRendererHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.pocket.core.Pocket;
import com.tcn.dimensionalpocketsii.pocket.core.blockentity.BlockEntityZModuleCreativeFluid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class RendererBlockEntityModuleCreativeFluid implements BlockEntityRenderer<BlockEntityZModuleCreativeFluid> {

	private BlockEntityRendererProvider.Context context;
	
	public RendererBlockEntityModuleCreativeFluid(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void render(BlockEntityZModuleCreativeFluid blockEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (blockEntityIn == null || blockEntityIn.isRemoved()) {
			return;
		}

		if (blockEntityIn.getPocket() != null) {
			Pocket pocket = blockEntityIn.getPocket();
			FluidTank fluidTank = pocket.getFluidTank();

			FluidStack fluid = fluidTank.getFluid();
			if (fluid.isEmpty()) {
				return;
			}

			Fluid renderFluid = fluid.getFluid();
			if (renderFluid == null) {
				return;
			}
			
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(renderFluid.getAttributes().getStillTexture(fluid));
			VertexConsumer builder = bufferIn.getBuffer(RenderType.translucent());

			float[] values = new float[] { 0.0625F, 0.9375F };
			float[] height = new float[] { 0.1F, 0.94F };
			float fillLevel = 16.0F / 16.0F;
			
			height[1] = (float) Mth.map(fillLevel, 0, 1, 0.1F, 0.94F);
			
			float mappedHeight = CosmosRendererHelper.getMappedTextureHeight(sprite, fillLevel * 16);

			Quaternion rotation = Vector3f.YP.rotationDegrees(0);
			int color = renderFluid.getAttributes().getColor();
			
			float a = 1.0F;
			float r = (color >> 16 & 0xFF) / 255.0F;
			float g = (color >> 8 & 0xFF) / 255.0F;
			float b = (color & 0xFF) / 255.0F;
			
			poseStack.pushPose();
			poseStack.mulPose(rotation);
			
			// Top Face
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);
			
			// Bottom Face of Top
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);
			
			
			// Bottom Face
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);

			// Top Face of Bottom
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);
			
			
			// Front Faces [NORTH - SOUTH]
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);

			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

			// Back Faces
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);

			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

			poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
			poseStack.translate(-1f, 0, 0);
			
			// Front Faces [EAST - WEST]
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);

			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

			// Back Faces
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);

			CosmosRendererHelper.addF(builder, poseStack, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
			CosmosRendererHelper.addF(builder, poseStack, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

			poseStack.popPose();
		} else {
			return;
		}
	}
}