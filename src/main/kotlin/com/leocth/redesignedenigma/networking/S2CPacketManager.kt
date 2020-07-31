package com.leocth.redesignedenigma.networking

import com.leocth.redesignedenigma.RedesignedEnigma
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.util.Identifier

object S2CPacketManager {
    val PEWPEW_S2C_UPDATE_ID = Identifier(RedesignedEnigma.MODID, "pewpew_s2c_update")

    fun register() {
        ClientSidePacketRegistry.INSTANCE.register(PEWPEW_S2C_UPDATE_ID, S2CUpdatePewpewPacketConsumer())
    }
}