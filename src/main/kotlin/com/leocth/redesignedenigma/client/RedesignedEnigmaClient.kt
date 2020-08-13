@file:JvmName("RedesignedEnigmaClient")
package com.leocth.redesignedenigma.client

import com.leocth.redesignedenigma.RedesignedEnigma
import com.leocth.redesignedenigma.client.event.ClientEventManager
import com.leocth.redesignedenigma.client.render.CompakModelProvider
import com.leocth.redesignedenigma.client.render.entity.HEGrenadeEntityRenderer
import com.leocth.redesignedenigma.client.render.item.BuiltinItemRendererManager
import com.leocth.redesignedenigma.client.render.model.CompakModel
import com.leocth.redesignedenigma.entity.REEntities
import com.leocth.redesignedenigma.network.S2CPacketManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class RedesignedEnigmaClient : ClientModInitializer {
    companion object {
        val LOGGER: Logger = LogManager.getLogger("redesigned-enigma-client")
    }
    override fun onInitializeClient() {
        LOGGER.info("Initializing client...")

        LOGGER.info("Registering custom models...")

        ModelLoadingRegistry.INSTANCE.registerResourceProvider {
            val mp = CompakModelProvider(RedesignedEnigma.MODID, it)
            mp.registerBlockModel("test")
            mp
        }

        LOGGER.info("Registering builtin item renderers...")
        BuiltinItemRendererManager.register()
        LOGGER.info("Registering keybindings...")
        KeyBindingManager.register()
        LOGGER.info("Registering client-side events...")
        ClientEventManager.register()
        LOGGER.info("Registering S2C packets...")
        S2CPacketManager.register()

        EntityRendererRegistry.INSTANCE.register(REEntities.HE_GRENADE)
        { dispatcher, _ -> HEGrenadeEntityRenderer(dispatcher) }
        LOGGER.info("Client class initialized; some client-side mixins may have already loaded, some have yet.")
    }
}