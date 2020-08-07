package com.leocth.redesignedenigma.client.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.HitResult

/**
 * Called when the player "attacks", aka left-clicks in most key-bindings.
 * This event is called *before* AttackBlockEvent and AttackEntityEvent, and it also captures
 * misses (e.g. punching air) so this is pretty useful for the Pew-Pew2k.
 *
 * CAUTION: NO CHECKS ARE PERFORMED UPON THE INPUTS. SANITIZE THE INPUT FIRST!!! (e.g. game mode check, op check, etc.)
 *
 * Upon return:
 * <ul><li>SUCCESS: skips vanilla behavior, swings the player's arms.</li>
 * <li>PASS: continue with the chain until no callbacks are executed, then vanilla behaviour is enacted instead.</li>
 * <li>FAIL: skips vanilla behavior, DOESN'T swing the player's arms.</li>
 * </ul>
 */
interface AttackAttemptEventCallback {
    companion object {
        /**
         * This is a dirty hack to work around the issue
         * of Kotlin disallowing functional interfaces conveniently
         * expressed in lambda form, which is the apparent norm of style
         * in Fabric documentations. Oh well.
         *
         * NOTE: this actually creates an anonymous object every time you call it. :(
         * NOTE2: this will go away in Kotlin 1.4.
         */
        inline operator fun invoke(crossinline function: (player: ClientPlayerEntity, hitResult: HitResult?, heldDown: Boolean) -> ActionResult) =
            object : AttackAttemptEventCallback {
                override fun interact(
                    player: ClientPlayerEntity,
                    hitResult: HitResult?,
                    heldDown: Boolean
                ): ActionResult = function(player, hitResult, heldDown)
            }

        var EVENT: Event<AttackAttemptEventCallback> =
            EventFactory.createArrayBacked(
                AttackAttemptEventCallback::class.java
            ) { listeners: Array<AttackAttemptEventCallback> ->
                AttackAttemptEventCallback { player: ClientPlayerEntity, hitResult: HitResult?, heldDown: Boolean ->
                    for (listener in listeners) {
                        val result = listener.interact(player, hitResult, heldDown)
                        if (result != ActionResult.PASS) {
                            return@AttackAttemptEventCallback result
                        }
                    }
                    return@AttackAttemptEventCallback ActionResult.PASS
                }
            }
    }

    fun interact(player: ClientPlayerEntity, hitResult: HitResult?, heldDown: Boolean): ActionResult
}