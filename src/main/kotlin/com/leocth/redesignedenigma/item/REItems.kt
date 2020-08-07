package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RedesignedEnigma
import net.minecraft.util.registry.Registry

object REItems {
    val GLOCK_FULL_MOD = GlockFullModItem()
    val SREAX = SreaxItem()
    val DEAGLE = DeagleItem()

    fun register() {
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:sreax", SREAX)
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:glock_full_mod", GLOCK_FULL_MOD)
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:deagle", DEAGLE)
    }
}