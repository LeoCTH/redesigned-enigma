package com.leocth.redesignedenigma.mixin.client.callback

import com.leocth.redesignedenigma.client.event.AttackAttemptEventCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.options.GameOptions
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.Redirect
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MinecraftClient::class)
abstract class AttackAttemptEventCallbackMixin {

    @Shadow
    private lateinit var player: ClientPlayerEntity
    @Shadow
    private lateinit var crosshairTarget: HitResult
    @Shadow
    lateinit var options: GameOptions
    private var shouldSwing = false

    @Inject(
        at = [
            At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/MinecraftClient;doAttack()V"
            )
        ],
        method = ["handleInputEvents"],
        cancellable = true
    )
    fun handleInputEventsBeforeDoAttack(ci: CallbackInfo) {
        val result = AttackAttemptEventCallback.EVENT.invoker().interact(player, crosshairTarget, false)
        shouldSwing = result == ActionResult.SUCCESS
        if (result != ActionResult.PASS) {
            //todo
            ci.cancel()
        }
    }

    @Inject(
        at = [
            At("HEAD")
        ],
        method = ["handleInputEvents"],
        cancellable = true
    )
    fun handleInputEventsGlobalHook(ci: CallbackInfo) {
        if (options.keyAttack.isPressed) {
            val result = AttackAttemptEventCallback.EVENT.invoker().interact(player, crosshairTarget, true)
            shouldSwing = result != ActionResult.FAIL
        }
    }

    @Redirect(
        at = At(
            "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"
        ), method = ["doAttack"]
    )
    fun redirectSwingHand(client: ClientPlayerEntity, hand: Hand) {
        if (shouldSwing) {
            client.swingHand(hand)
        }
    }
}

