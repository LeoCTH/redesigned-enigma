package com.leocth.redesignedenigma.client.event

import com.leocth.redesignedenigma.item.weapon.IFireOnLeftClick
import com.leocth.redesignedenigma.network.C2SPacketManager
import net.minecraft.util.ActionResult

object ClientEventManager {
    fun register() {
        AttackAttemptEventCallback.EVENT.register(
            AttackAttemptEventCallback
            { player, _, heldDown ->
                val stack = player.inventory.mainHandStack
                return@AttackAttemptEventCallback if (stack.item is IFireOnLeftClick) {
                    C2SPacketManager.sendC2SFirePacket(heldDown)
                    ActionResult.FAIL
                } else ActionResult.PASS
            }
        )
    }
}