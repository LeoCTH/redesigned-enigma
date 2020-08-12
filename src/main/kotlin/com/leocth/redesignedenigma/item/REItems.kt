package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RedesignedEnigma
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

object REItems {
    val BASIC_WEAPON_SETTINGS = Item.Settings().maxCount(1)

    val GLOCK_FULL_MOD = GlockFullModItem()
    val SREAX = SreaxItem()
    val DEAGLE = DeagleItem()
    val GLOCK_18 = Glock18Item()

    val HE_GRENADE = HEGrenadeItem()

    fun register() {
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:sreax", SREAX)
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:glock_full_mod", GLOCK_FULL_MOD)
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:deagle", DEAGLE)
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:glock_18", GLOCK_18)
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:he_grenade", HE_GRENADE)
    }
}