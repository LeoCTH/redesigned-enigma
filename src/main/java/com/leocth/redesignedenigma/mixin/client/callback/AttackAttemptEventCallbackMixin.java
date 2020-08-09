package com.leocth.redesignedenigma.mixin.client.callback;

import com.leocth.redesignedenigma.client.event.AttackAttemptEventCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class AttackAttemptEventCallbackMixin {

    @Shadow public ClientPlayerEntity player;

    @Shadow public HitResult crosshairTarget;

    @Shadow @Final public GameOptions options;
    private boolean shouldSwing = false;

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;doAttack()V"
        ),
        method = "handleInputEvents",
        cancellable = true
    )
    private void handleInputEventsBeforeDoAttack(CallbackInfo ci) {
        ActionResult result = AttackAttemptEventCallback.EVENT.invoker().interact(this.player, this.crosshairTarget, false);
        shouldSwing = result == ActionResult.SUCCESS;
        if (result != ActionResult.PASS) {
            //todo
            ci.cancel();
        }
    }

    @Inject(
        at = @At("HEAD"),
        method = "handleInputEvents",
        cancellable = true
    )
    private void handleInputEventsGlobalHook(CallbackInfo ci) {
        if (this.options.keyAttack.isPressed()) {
            ActionResult result = AttackAttemptEventCallback.EVENT.invoker().interact(player, crosshairTarget, true);
            shouldSwing = result != ActionResult.FAIL;
        }
    }

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"
        ),
        method = "doAttack"
    )
    private void redirectSwingHand(ClientPlayerEntity client, Hand hand) {
        if (shouldSwing) {
            client.swingHand(hand);
        }
    }
}
