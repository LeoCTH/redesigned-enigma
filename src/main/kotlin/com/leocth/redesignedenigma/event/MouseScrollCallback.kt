package com.leocth.redesignedenigma.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.ActionResult

interface MouseScrollCallback {
    companion object {
        // kotlin 5w33tn355
        inline operator fun invoke(crossinline function: (player: ClientPlayerEntity, dwheel: Float) -> ActionResult) =
            object : MouseScrollCallback {
                override fun interact(player: ClientPlayerEntity, dwheel: Float): ActionResult = function(player, dwheel)
            }

        var EVENT: Event<MouseScrollCallback> =
            EventFactory.createArrayBacked(
                MouseScrollCallback::class.java
            ) { listeners: Array<MouseScrollCallback> ->
                /* TODO:
                    waiting for Kotlin 1.4 to fix this
                    otherwise we are stuck with this mess
                */
                MouseScrollCallback {
                    player: ClientPlayerEntity, dwheel: Float ->
                    for (listener in listeners) {
                        val result = listener.interact(player, dwheel)
                        if (result != ActionResult.PASS) {
                            return@MouseScrollCallback result
                        }
                    }
                    return@MouseScrollCallback ActionResult.PASS
                }
            }
    }

    fun interact(player: ClientPlayerEntity, dwheel: Float): ActionResult
}