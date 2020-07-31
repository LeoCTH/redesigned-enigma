package com.leocth.redesignedenigma.event

import com.leocth.redesignedenigma.interfaces.IFireOnLeftClick
import net.fabricmc.fabric.api.event.server.ServerTickCallback

object EventManager {
    fun register() {
        ServerTickCallback.EVENT.register(ServerTickCallback { server ->
            server.playerManager.playerList.forEach {
                val stack = it.mainHandStack
                // TODO separate interface
                if (stack.item is IFireOnLeftClick) {
                    val tag = stack.orCreateTag


                }
            }
        })
    }
}