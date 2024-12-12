package com.tcn.dimensionalpocketsii.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.dimensionalpocketsii.DimensionalPockets;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DimensionalTridentModel extends Model {
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/entity/dimensional_trident.png");
	public static final ResourceLocation TEXTURE_ENHANCED = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "textures/entity/dimensional_trident_enhanced.png");
	
	public final static ModelLayerLocation loc = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "dimensional_trident"), "main");
	private final ModelPart root;

	public DimensionalTridentModel() {
		super(RenderType::entitySolid);
		
		this.root = createLayer().bakeRoot();
	}
	
	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 3.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.ZERO);

		partdefinition1.addOrReplaceChild("base", CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 3.0F, 1.0F), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("left_spike", CubeListBuilder.create().texOffs(4, 4).addBox(-2.5F, -3.0F, -0.5F, 1.0F, 5.0F, 1.0F), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("middle_spike", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("right_spike", CubeListBuilder.create().texOffs(8, 4).addBox(1.5F, -3.0F, -0.5F, 1.0F, 5.0F, 1.0F), PartPose.ZERO);
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, int colourIn) {
		this.root.render(matrixStack, vertexBuilder, packedLightIn, packedOverlayIn, colourIn);
	}
}