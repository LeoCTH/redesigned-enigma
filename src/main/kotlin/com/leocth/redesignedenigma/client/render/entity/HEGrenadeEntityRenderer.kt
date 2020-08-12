package com.leocth.redesignedenigma.client.render.entity

import com.leocth.redesignedenigma.RedesignedEnigma
import com.leocth.redesignedenigma.client.render.entity.model.HEGrenadeModel
import com.leocth.redesignedenigma.entity.HEGrenadeEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class HEGrenadeEntityRenderer(dispatcher: EntityRenderDispatcher) : EntityRenderer<HEGrenadeEntity>(dispatcher) {

    override fun getTexture(entity: HEGrenadeEntity): Identifier =
        Identifier(RedesignedEnigma.MODID, "textures/entity/he_grenade.png")

    val model = HEGrenadeModel()

    override fun render(
        entity: HEGrenadeEntity,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    ) {
        println("render")
        matrices.push()

        matrices.scale(-1.0f, -1.0f, 1.0f)
        matrices.translate(0.0, -1.5, 0.0)
        val vertexConsumer = vertexConsumers.getBuffer(model.getLayer(getTexture(entity)))
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        matrices.pop()
    }
}