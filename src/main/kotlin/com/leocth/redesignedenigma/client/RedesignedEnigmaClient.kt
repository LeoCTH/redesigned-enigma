@file:JvmName("RedesignedEnigmaClient")
package com.leocth.redesignedenigma.client

import com.leocth.redesignedenigma.client.event.ClientEventManager
import com.leocth.redesignedenigma.networking.S2CPacketManager
import com.leocth.redesignedenigma.item.REItems
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.math.BlockPos
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

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
            matrixStack.push(); run {
                matrixStack.scale(0.75f, 0.75f, 0.75f)
                MinecraftClient.getInstance().blockRenderManager.renderBlock(
                    Blocks.JUKEBOX.defaultState,
                    BlockPos(0, 0, 0),
                    MinecraftClient.getInstance().world,
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayer.getSolid()),
                    true,
                    MinecraftClient.getInstance().world?.random ?: java.util.Random()
                )
            }; matrixStack.pop()

        }
        LOGGER.info("Registering client-side events...")
        ClientEventManager.register()
        LOGGER.info("Registering S2C packets...")
        S2CPacketManager.register()
        LOGGER.info("Client class initialized; some client-side mixins may have already loaded, some have yet.")
    }
}