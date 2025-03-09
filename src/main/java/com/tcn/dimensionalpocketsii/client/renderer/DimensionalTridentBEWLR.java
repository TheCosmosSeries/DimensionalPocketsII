package com.tcn.dimensionalpocketsii.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.client.renderer.model.DimensionalTridentModel;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DimensionalTridentBEWLR extends BlockEntityWithoutLevelRenderer {
	
	public final static BlockEntityWithoutLevelRenderer INSTANCE = new DimensionalTridentBEWLR();

	private final DimensionalTridentModel tridentModel = new DimensionalTridentModel();

	public DimensionalTridentBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderByItem(ItemStack stackIn, ItemDisplayContext transformIn, PoseStack poseStack, MultiBufferSource typeBuffer, int combinedLight, int combinedOverlay) {
		Item item = stackIn.getItem();
		BakedModel model = null;
		Minecraft mc = Minecraft.getInstance();
		ItemRenderer renderer = mc.getItemRenderer();

		boolean normal = item.equals(PocketsRegistrationManager.DIMENSIONAL_TRIDENT.get());
		
		String modelLocation = normal ? "dimensional_trident" : "dimensional_trident_enhanced";
		ResourceLocation texture = normal ? DimensionalTridentModel.TEXTURE : DimensionalTridentModel.TEXTURE_ENHANCED;
		
		boolean flag = transformIn == ItemDisplayContext.GUI || transformIn == ItemDisplayContext.GROUND || transformIn == ItemDisplayContext.FIXED;
		
		if (flag) {
			model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, modelLocation), "inventory"));

			poseStack.pushPose();
			renderer.render(stackIn, transformIn, true, poseStack, typeBuffer, combinedLight, combinedOverlay, model);
			poseStack.popPose();
		} else {
			poseStack.pushPose();
			poseStack.scale(1.0F, -1.0F, -1.0F);
			VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(typeBuffer, this.tridentModel.renderType(texture), false, stackIn.hasFoil());
			this.tridentModel.renderToBuffer(poseStack, ivertexbuilder1, combinedLight, combinedOverlay, ComponentColour.WHITE.decOpaque());
			poseStack.popPose();
		}
	}
}