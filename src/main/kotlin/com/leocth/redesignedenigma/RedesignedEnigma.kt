@file:JvmName("RedesignedEnigma")
package com.leocth.redesignedenigma
import com.leocth.redesignedenigma.item.PewPewItem
import com.leocth.redesignedenigma.item.REItems
import com.leocth.redesignedenigma.item.SreaxItem
import net.fabricmc.api.ModInitializer
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class RedesignedEnigma: ModInitializer {
    companion object {
        val LOGGER: Logger = LogManager.getLogger("redesigned-enigma")
    }
    override fun onInitialize() {
        LOGGER.info("Initializing...")
        LOGGER.info("Registering items...")
        Registry.register(Registry.ITEM, "redesignedenigma:sreax", REItems.SREAX)
        Registry.register(Registry.ITEM, "redesignedenigma:pewpew", REItems.PEWPEW)
        LOGGER.info("Common class initialized; some common mixins may have already loaded, some have yet.")

    }
}