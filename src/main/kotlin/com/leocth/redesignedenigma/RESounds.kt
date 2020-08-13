package com.leocth.redesignedenigma

import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RESounds {
    private val DEAGLE_EQUIP_ID     = getId("item.deagle.equip")
    private val DEAGLE_FIRE_ID      = getId("item.deagle.fire")
            val DEAGLE_RELOAD_ID    = getId("item.deagle.reload")

    private val GLOCK_EQUIP_ID      = getId("item.glock.equip")
    private val GLOCK_FIRE_ID       = getId("item.glock.fire")
            val GLOCK_RELOAD_ID     = getId("item.glock.reload")
    private val GLOCK_BURST_ID      = getId("item.glock.burst")
    private val GLOCK_SELECTFIRE_ID = getId("item.glock.selectfire")

    private val AWP_EQUIP_ID        = getId("item.awp.equip")
    private val AWP_FIRE_ID         = getId("item.awp.fire")
            val AWP_RELOAD_ID       = getId("item.awp.reload")
    private val AWP_SCOPE_ID        = getId("item.awp.scope")

    val DEAGLE_EQUIP        = SoundEvent(DEAGLE_EQUIP_ID)
    val DEAGLE_FIRE         = SoundEvent(DEAGLE_FIRE_ID)
    val DEAGLE_RELOAD       = SoundEvent(DEAGLE_RELOAD_ID)

    val GLOCK_EQUIP         = SoundEvent(GLOCK_EQUIP_ID)
    val GLOCK_FIRE          = SoundEvent(GLOCK_FIRE_ID)
    val GLOCK_RELOAD        = SoundEvent(GLOCK_RELOAD_ID)
    val GLOCK_BURST         = SoundEvent(GLOCK_BURST_ID)
    val GLOCK_SELECTFIRE    = SoundEvent(GLOCK_SELECTFIRE_ID)

    val AWP_EQUIP           = SoundEvent(AWP_EQUIP_ID)
    val AWP_FIRE            = SoundEvent(AWP_FIRE_ID)
    val AWP_RELOAD          = SoundEvent(AWP_RELOAD_ID)
    val AWP_SCOPE           = SoundEvent(AWP_SCOPE_ID)

    private fun getId(str: String) = Identifier(RedesignedEnigma.MODID, str)

    fun register() {
        Registry.register(Registry.SOUND_EVENT, DEAGLE_EQUIP_ID     , DEAGLE_EQUIP)
        Registry.register(Registry.SOUND_EVENT, DEAGLE_FIRE_ID      , DEAGLE_FIRE)
        Registry.register(Registry.SOUND_EVENT, DEAGLE_RELOAD_ID    , DEAGLE_RELOAD)

        Registry.register(Registry.SOUND_EVENT, GLOCK_EQUIP_ID      , GLOCK_EQUIP)
        Registry.register(Registry.SOUND_EVENT, GLOCK_FIRE_ID       , GLOCK_FIRE)
        Registry.register(Registry.SOUND_EVENT, GLOCK_RELOAD_ID     , GLOCK_RELOAD)
        Registry.register(Registry.SOUND_EVENT, GLOCK_BURST_ID      , GLOCK_BURST)
        Registry.register(Registry.SOUND_EVENT, GLOCK_SELECTFIRE_ID , GLOCK_SELECTFIRE)

        Registry.register(Registry.SOUND_EVENT, AWP_EQUIP_ID        , AWP_EQUIP)
        Registry.register(Registry.SOUND_EVENT, AWP_FIRE_ID         , AWP_FIRE)
        Registry.register(Registry.SOUND_EVENT, AWP_RELOAD_ID       , AWP_RELOAD)
        Registry.register(Registry.SOUND_EVENT, AWP_SCOPE_ID        , AWP_SCOPE)
    }
}