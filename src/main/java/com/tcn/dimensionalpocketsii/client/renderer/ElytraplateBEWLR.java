package com.tcn.dimensionalpocketsii.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmoslibrary.client.renderer.CosmosRendererHelper;
import com.tcn.dimensionalpocketsii.core.item.armour.DimensionalElytraplate;
import com.tcn.dimensionalpocketsii.core.item.armour.module.BaseElytraModule;
import com.tcn.dimensionalpocketsii.core.management.PocketsRegistrationManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraplateBEWLR extends BlockEntityWithoutLevelRenderer {
	
	public final static BlockEntityWithoutLevelRenderer INSTANCE = new ElytraplateBEWLR();

	public ElytraplateBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderByItem(ItemStack stackIn, ItemDisplayContext transformIn, PoseStack poseStack, MultiBufferSource typeBuffer, int combinedLight, int combinedOverlay) {
		Item item = stackIn.getItem();
		ResourceLocation itemLocation = BuiltInRegistries.ITEM.getKey(item);
		Minecraft mc = Minecraft.getInstance();
		ItemRenderer renderer = mc.getItemRenderer();
		ModelManager manager = mc.getModelManager();
		
		boolean flag = transformIn == ItemDisplayContext.GUI;
		
		if (flag) {
			if (item.equals(PocketsRegistrationManager.DIMENSIONAL_ELYTRAPLATE.get())) {
				poseStack.pushPose();
				ResourceLocation resBase = ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_base");
				BakedModel itemModelBase = manager.getModel(new ModelResourceLocation(resBase, "standalone"));
				
				boolean foil = true;
				
				poseStack.translate(0.5F, 0.5F, 0.0F);
				if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.SCREEN)) {
					BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_connect"), "standalone"));
					
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, 0.001F);
					CosmosRendererHelper.render(renderer, stackIn, transformIn, false, poseStack, typeBuffer, combinedLight, combinedOverlay, itemModel, foil);
					poseStack.popPose();
					
					foil = false;
				}

				if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.SHIFTER)) {
					BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_shifter"), "standalone"));
					
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, 0.001F);
					CosmosRendererHelper.render(renderer, stackIn, transformIn, false, poseStack, typeBuffer, combinedLight, combinedOverlay, itemModel, foil);
					poseStack.popPose();
					
					foil = false;
				}

				if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.VISOR)) {
					BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_visor"), "standalone"));
					
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, 0.001F);
					CosmosRendererHelper.render(renderer, stackIn, transformIn, false, poseStack, typeBuffer, combinedLight, combinedOverlay, itemModel, foil);
					poseStack.popPose();
					
					foil = false;
				}
				
				if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.SOLAR)) {
					BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_solar"), "standalone"));
					
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, 0.001F);
					CosmosRendererHelper.render(renderer, stackIn, transformIn, false, poseStack, typeBuffer, combinedLight, combinedOverlay, itemModel, foil);
					poseStack.popPose();
					
					foil = false;
				}
				
				if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.BATTERY)) {
					BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_battery"), "standalone"));
					
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, 0.001F);
					CosmosRendererHelper.render(renderer, stackIn, transformIn, false, poseStack, typeBuffer, combinedLight, combinedOverlay, itemModel, foil);
					poseStack.popPose();
					
					foil = false;
				}
				
				poseStack.pushPose();
				CosmosRendererHelper.render(renderer, stackIn, transformIn, false, poseStack, typeBuffer, combinedLight, combinedOverlay, itemModelBase, true);
				poseStack.popPose();
				
				poseStack.popPose();
			}
		} else {
			poseStack.pushPose();
			BakedModel model = mc.getModelManager().getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_base"), "standalone"));
			
			boolean flag1 = true;
			
			RenderType rendertype = model.getRenderTypes(stackIn, flag1).get(0);
			VertexConsumer ivertexbuilder;
			
			boolean foil = true;
			ivertexbuilder = ItemRenderer.getFoilBufferDirect(typeBuffer, rendertype, true, stackIn.hasFoil());
			
			if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.SCREEN)) {
				BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_connect"), "standalone"));

				VertexConsumer consumer = transformIn.firstPerson() ? ItemRenderer.getFoilBuffer(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false) : ItemRenderer.getFoilBufferDirect(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false);
				
				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, 0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, -0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				foil = false;
			}

			if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.SHIFTER)) {
				BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_shifter"), "standalone"));

				VertexConsumer consumer = transformIn.firstPerson() ? ItemRenderer.getFoilBuffer(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false) : ItemRenderer.getFoilBufferDirect(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false);
				
				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, 0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, -0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				foil = false;
			}

			if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.VISOR)) {
				BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_visor"), "standalone"));

				VertexConsumer consumer = transformIn.firstPerson() ? ItemRenderer.getFoilBuffer(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false) : ItemRenderer.getFoilBufferDirect(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false);
				
				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, 0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, -0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				foil = false;
			}
			
			if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.SOLAR)) {
				BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_solar"), "standalone"));

				VertexConsumer consumer = transformIn.firstPerson() ? ItemRenderer.getFoilBuffer(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false) : ItemRenderer.getFoilBufferDirect(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false);
				
				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, 0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, -0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				foil = false;
			}
			
			if (DimensionalElytraplate.hasModuleInstalled(stackIn, BaseElytraModule.BATTERY)) {
				BakedModel itemModel = manager.getModel(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(itemLocation.getNamespace(), "item/" + itemLocation.getPath() + "_battery"), "standalone"));
				
				VertexConsumer consumer = transformIn.firstPerson() ? ItemRenderer.getFoilBuffer(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false) : ItemRenderer.getFoilBufferDirect(typeBuffer, rendertype, true, foil ? stackIn.hasFoil() : false);
				
				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, 0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				poseStack.pushPose();
				poseStack.translate(0.0F, 0.0F, -0.001F);
				renderer.renderModelLists(itemModel, stackIn, combinedLight, combinedOverlay, poseStack, consumer);
				poseStack.popPose();

				foil = false;
			}
			
			renderer.renderModelLists(model, stackIn, combinedLight, combinedOverlay, poseStack, ivertexbuilder);
			poseStack.popPose();
		}
	}
}