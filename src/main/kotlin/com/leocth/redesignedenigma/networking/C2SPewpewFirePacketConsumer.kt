package com.leocth.redesignedenigma.networking

import com.leocth.redesignedenigma.interfaces.IFireOnLeftClick
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.PacketByteBuf

class C2SPewpewFirePacketConsumer: PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val player = context.player
        context.taskQueue.execute {
            //TODO: Safety checks
            val stack = player.inventory.mainHandStack
            if (stack.item is IFireOnLeftClick) {
                (stack.item as IFireOnLeftClick).fire(player)
            }
        }
    }
}