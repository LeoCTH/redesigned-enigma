package com.leocth.redesignedenigma.network

import com.leocth.redesignedenigma.RedesignedEnigma
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object C2SPacketManager {
    val PEWPEW_C2S_FIRE_ID = Identifier(RedesignedEnigma.MODID, "pewpew_c2s_fire")
    val PEWPEW_C2S_RELOAD_ID = Identifier(RedesignedEnigma.MODID, "pewpew_c2s_reload")

    fun register() {
        ServerSidePacketRegistry.INSTANCE.register(PEWPEW_C2S_FIRE_ID, C2SWeaponFirePacketConsumer())
        ServerSidePacketRegistry.INSTANCE.register(PEWPEW_C2S_RELOAD_ID, C2SWeaponReloadPacketConsumer())
    }

    @Environment(EnvType.CLIENT)
    fun sendC2SFirePacket(mouseHeldDown: Boolean) {
        val packetData = PacketByteBuf(Unpooled.buffer())

        packetData.writeBoolean(mouseHeldDown)

        ClientSidePacketRegistry.INSTANCE.sendToServer(PEWPEW_C2S_FIRE_ID, packetData)
    }

    @Environment(EnvType.CLIENT)
    fun sendC2SReloadPacket() {
        val packetData = PacketByteBuf(Unpooled.buffer())

        ClientSidePacketRegistry.INSTANCE.sendToServer(PEWPEW_C2S_RELOAD_ID, packetData)
    }
}