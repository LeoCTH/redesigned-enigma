package com.leocth.redesignedenigma.client.render.entity.model

import net.minecraft.client.model.Model
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack

class HEGrenadeModel: Model(RenderLayer::getEntityCutoutNoCull) {

    private val body: ModelPart

    init {
        textureWidth = 32
        textureHeight = 32

        body = ModelPart(this)

        body.setPivot(0f, 4f, 0f)
        body.addCuboid("v", -2f, -7f, -2f, 4, 7, 4, 0f, 0, 0)
        body.addCuboid("h", -3f, -6f, -3f, 6, 5, 6, 0f, 0, 10)
        body.addCuboid("t", -1f, -8f, -0.5f, 2, 1, 1, 0f, 0, 22)
    }

    override fun render(
        matrices: MatrixStack,
        vertices: VertexConsumer,
        light: Int,
        overlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        body.render(matrices, vertices, light, overlay, red, green, blue, alpha)
    }

}