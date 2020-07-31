package com.leocth.redesignedenigma.client.event

import com.leocth.redesignedenigma.interfaces.IFireOnLeftClick
import com.leocth.redesignedenigma.util.PhysicsHelper
import net.minecraft.util.ActionResult

object ClientEventManager {
    fun register() {
        AttackAttemptEventCallback.EVENT.register(
            AttackAttemptEventCallback
            { player, _ ->
                val stack = player.inventory.mainHandStack
                return@AttackAttemptEventCallback if (stack.item is IFireOnLeftClick) {
                    PhysicsHelper.sendC2SFirePacket()
                     ActionResult.FAIL
                } else ActionResult.PASS
            }
        )
    }
}