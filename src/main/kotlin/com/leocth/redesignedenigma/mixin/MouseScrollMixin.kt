package com.leocth.redesignedenigma.mixin

import com.leocth.redesignedenigma.event.MouseScrollCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.Mouse
import net.minecraft.util.ActionResult
import net.minecraft.util.math.MathHelper
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.sign

@Mixin(Mouse::class)
class MouseScrollMixin {

    @Shadow private var eventDeltaWheel = 0.0
    @Shadow private lateinit var client: MinecraftClient
    @Shadow private val x = 0.0
    @Shadow private val y = 0.0

    @Inject(at = [
        At(
            value = "RETURN",
            ordinal = 1
        )
    ], method = ["onMouseScroll(LDD)V"], cancellable = true)
    fun injectMouseScrollEventCallback(window: Long, d: Double, e: Double, ci: CallbackInfo) {
        if (window == MinecraftClient.getInstance().window.handle) {
            val f =
                (if (client.options.discreteMouseScroll) sign(e) else e) * client.options.mouseWheelSensitivity
            if (client.overlay == null) {
                if (client.currentScreen != null) {
                    val g: Double =
                        this.x * client.window.scaledWidth.toDouble() / client.window.width.toDouble()
                    val h: Double =
                        this.y * client.window.scaledHeight.toDouble() / client.window.height.toDouble()
                    client.currentScreen!!.mouseScrolled(g, h, f)
                } else if (client.player != null) {
                    val player = client.player ?: return
                    if (eventDeltaWheel != 0.0 && sign(f) != sign(eventDeltaWheel)) {
                        eventDeltaWheel = 0.0
                    }
                    eventDeltaWheel += f
                    val i = eventDeltaWheel.toInt().toFloat()
                    if (i == 0.0f) {
                        ci.cancel()
                        return
                    }

                    val result = MouseScrollCallback.EVENT.invoker().interact(player, eventDeltaWheel.toFloat())
                    if (result != ActionResult.FAIL) {
                        eventDeltaWheel -= i.toDouble()
                        if (player.isSpectator) {
                            if (client.inGameHud.spectatorHud.isOpen) {
                                client.inGameHud.spectatorHud.cycleSlot((-i).toDouble())
                            } else {
                                val j =
                                    MathHelper.clamp(player.abilities.flySpeed + i * 0.005f, 0.0f, 0.2f)
                                player.abilities.flySpeed = j
                            }
                        } else {
                            player.inventory.scrollInHotbar(i.toDouble())
                        }
                    }
                }
            }
        }
        ci.cancel()
    }
}