package com.leocth.redesignedenigma.network

import com.leocth.redesignedenigma.item.weapon.ICanReload
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Hand

class C2SWeaponReloadPacketConsumer : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val player = context.player
        context.taskQueue.execute {
            //TODO: Safety checks & final decisions on offhand support
            val stack = player.inventory.mainHandStack
            val item = stack.item
            if (item is ICanReload) {
                item.reload(player.world, player, stack, Hand.MAIN_HAND)
            }
        }
    }
}