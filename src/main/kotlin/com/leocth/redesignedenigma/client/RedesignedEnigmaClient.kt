@file:JvmName("RedesignedEnigmaClient")
package com.leocth.redesignedenigma.client

import com.leocth.redesignedenigma.client.event.ClientEventManager
import com.leocth.redesignedenigma.client.render.item.BuiltinItemRendererManager
import com.leocth.redesignedenigma.network.S2CPacketManager
import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class RedesignedEnigmaClient : ClientModInitializer {
    companion object {
        val LOGGER: Logger = LogManager.getLogger("redesigned-enigma-client")
    }
    override fun onInitializeClient() {
        LOGGER.info("Initializing client...")

        //OBJLoader.INSTANCE.registerDomain(RedesignedEnigma.MODID)

        LOGGER.info("Registering builtin item renderers...")
        BuiltinItemRendererManager.register()
        LOGGER.info("Registering keybindings...")
        KeyBindingManager.register()
        LOGGER.info("Registering client-side events...")
        ClientEventManager.register()
        LOGGER.info("Registering S2C packets...")
        S2CPacketManager.register()
        LOGGER.info("Client class initialized; some client-side mixins may have already loaded, some have yet.")
    }
}