package com.leocth.redesignedenigma.networking

import com.leocth.redesignedenigma.RedesignedEnigma
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.util.Identifier

object C2SPacketManager {
    val PEWPEW_C2S_FIRE_ID = Identifier(RedesignedEnigma.MODID, "pewpew_c2s_fire")

    fun register() {
        ServerSidePacketRegistry.INSTANCE.register(PEWPEW_C2S_FIRE_ID, C2SPewpewFirePacketConsumer())
    }
}