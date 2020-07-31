@file:JvmName("RedesignedEnigma")
package com.leocth.redesignedenigma
import com.leocth.redesignedenigma.event.EventManager
import com.leocth.redesignedenigma.item.REItems
import com.leocth.redesignedenigma.networking.C2SPacketManager
import net.fabricmc.api.ModInitializer
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class RedesignedEnigma: ModInitializer {
    companion object {
        val MODID = "redesignedenigma"
        val LOGGER: Logger = LogManager.getLogger("redesigned-enigma")
    }
    override fun onInitialize() {
        LOGGER.info("Initializing...")
        LOGGER.info("Registering items...")
        Registry.register(Registry.ITEM, "$MODID:sreax", REItems.SREAX)
        Registry.register(Registry.ITEM, "$MODID:pewpew", REItems.PEWPEW)

        LOGGER.info("Registering events...")
        EventManager.register()

        LOGGER.info("Registering C2S packets...")
        C2SPacketManager.register()
        LOGGER.info("Common class initialized; some common mixins may have already loaded, some have yet.")

    }
}