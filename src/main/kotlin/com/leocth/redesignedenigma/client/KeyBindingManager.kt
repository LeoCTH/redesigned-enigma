package com.leocth.redesignedenigma.client

import com.leocth.redesignedenigma.network.C2SPacketManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object KeyBindingManager {
    val RELOAD = KeyBinding(
        "key.redesignedenigma.reload",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "category.redesignedenigma.action"
    )

    fun register() {
        KeyBindingHelper.registerKeyBinding(RELOAD)
        ClientTickEvents.END_CLIENT_TICK.register(
            ClientTickEvents.EndTick {
                while (RELOAD.wasPressed()) C2SPacketManager.sendC2SReloadPacket()
            }
        )
    }
}