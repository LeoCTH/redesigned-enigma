package com.leocth.redesignedenigma.network

import com.leocth.redesignedenigma.item.weapon.IFireOnLeftClick
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Hand

class C2SWeaponFirePacketConsumer : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val player = context.player
        val mouseHeldDown = buffer.readBoolean()
        context.taskQueue.execute {
            //TODO: Safety checks & final decisions on offhand support
            val stack = player.inventory.mainHandStack
            val item = stack.item
            if (item is IFireOnLeftClick) {
                item.fire(player.world, player, Hand.MAIN_HAND, stack, mouseHeldDown)
            }
        }
    }
}