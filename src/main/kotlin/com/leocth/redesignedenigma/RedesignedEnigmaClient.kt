@file:JvmName("RedesignedEnigmaClient")
package com.leocth.redesignedenigma

import com.leocth.redesignedenigma.event.MouseScrollCallback
import com.leocth.redesignedenigma.interfaces.IScrollableItem
import com.leocth.redesignedenigma.item.REItems
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.block.Blocks
import net.minecraft.block.JukeboxBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.random.Random

class RedesignedEnigmaClient : ClientModInitializer {
    companion object {
        val LOGGER: Logger = LogManager.getLogger("redesigned-enigma-client")
    }
    override fun onInitializeClient() {
        LOGGER.info("Initializing client...")
        LOGGER.info("Registering client-side renderers...")
        // so i suppose this is the fabric version of ISTERs... ye
        BuiltinItemRendererRegistry.INSTANCE.register(REItems.PEWPEW)
        { itemStack, matrixStack, vertexConsumerProvider, light, overlay ->
            MinecraftClient.getInstance().blockRenderManager.renderBlock(
                Blocks.JUKEBOX.defaultState,
                BlockPos(0, 0, 0),
                MinecraftClient.getInstance().world,
                matrixStack,
                vertexConsumerProvider.getBuffer(RenderLayer.getSolid()),
                true,
                MinecraftClient.getInstance().world?.random ?: java.util.Random()
            )

        }
        LOGGER.info("Registering client-side events...")
        //TODO: S/C sync.. now i know why
        MouseScrollCallback.EVENT.register(
            MouseScrollCallback
            { player: ClientPlayerEntity, dwheel: Float ->
                val stack = player.inventory.mainHandStack
                if (stack.item is IScrollableItem) {
                    return@MouseScrollCallback (stack.item as IScrollableItem).onScroll(player, stack, dwheel)
                }
                return@MouseScrollCallback ActionResult.PASS
            }
        )
        LOGGER.info("Client class initialized; some client-side mixins may have already loaded, some have yet.")
    }
}