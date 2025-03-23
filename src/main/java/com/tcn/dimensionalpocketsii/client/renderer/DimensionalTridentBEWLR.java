package com.tcn.dimensionalpocketsii.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.dimensionalpocketsii.client.renderer.model.DimensionalTridentModel;
import com.tcn.dimensionalpocketsii.core.management.ModBusManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DimensionalTridentBEWLR extends BlockEntityWithoutLevelRenderer {
	
	public final static BlockEntityWithoutLevelRenderer INSTANCE = new DimensionalTridentBEWLR();

	private final DimensionalTridentModel tridentModel = new DimensionalTridentModel();

	public DimensionalTridentBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderByItem(ItemStack stackIn, ItemTransforms.TransformType transformIn, PoseStack poseStack, MultiBufferSource typeBuffer, int combinedLight, int combinedOverlay) {
		Item item = stackIn.getItem();
		BakedModel model = null;
		Minecraft mc = Minecraft.getInstance();
		ItemRenderer renderer = mc.getItemRenderer();

		if (item == ModBusManager.DIMENSIONAL_TRIDENT) {
			boolean flag = transformIn == ItemTransforms.TransformType.GUI || transformIn == ItemTransforms.TransformType.GROUND || transformIn == ItemTransforms.TransformType.FIXED;
			
			if (flag) {
				model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("dimensionalpocketsii:dimensional_trident#inventory"));

				poseStack.pushPose();
				renderer.render(stackIn, transformIn, true, poseStack, typeBuffer, combinedLight, combinedOverlay, model);
				poseStack.popPose();
			} else {
				poseStack.pushPose();
				poseStack.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(typeBuffer, this.tridentModel.renderType(DimensionalTridentModel.TEXTURE), false, stackIn.hasFoil());
				this.tridentModel.renderToBuffer(poseStack, ivertexbuilder1, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
				poseStack.popPose();
			}
		} else if (item == ModBusManager.DIMENSIONAL_TRIDENT_ENHANCED) {
			boolean flag = transformIn == ItemTransforms.TransformType.GUI || transformIn == ItemTransforms.TransformType.GROUND || transformIn == ItemTransforms.TransformType.FIXED;
			
			if (flag) {
				model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("dimensionalpocketsii:dimensional_trident_enhanced#inventory"));

				poseStack.pushPose();
				renderer.render(stackIn, transformIn, true, poseStack, typeBuffer, combinedLight, combinedOverlay, model);
				poseStack.popPose();
			} else {
				poseStack.pushPose();
				poseStack.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(typeBuffer, this.tridentModel.renderType(DimensionalTridentModel.TEXTURE_ENHANCED), false, stackIn.hasFoil());
				this.tridentModel.renderToBuffer(poseStack, ivertexbuilder1, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
				poseStack.popPose();
			}
		}
	}
}