package com.leocth.redesignedenigma

import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RESounds {
    val DEAGLE_FIRE_ID = getId("item.deagle.fire")
    val DEAGLE_RELOAD_ID = getId("item.deagle.reload")
    val GLOCK_FIRE_ID = getId("item.glock.fire")

    val DEAGLE_FIRE = SoundEvent(DEAGLE_FIRE_ID)
    val DEAGLE_RELOAD = SoundEvent(DEAGLE_RELOAD_ID)
    val GLOCK_FIRE = SoundEvent(GLOCK_FIRE_ID)

    fun getId(str: String) = Identifier(RedesignedEnigma.MODID, str)

    fun register() {
        Registry.register(Registry.SOUND_EVENT, DEAGLE_FIRE_ID, DEAGLE_FIRE)
        Registry.register(Registry.SOUND_EVENT, DEAGLE_RELOAD_ID, DEAGLE_RELOAD)
        Registry.register(Registry.SOUND_EVENT, GLOCK_FIRE_ID, GLOCK_FIRE)
    }
}