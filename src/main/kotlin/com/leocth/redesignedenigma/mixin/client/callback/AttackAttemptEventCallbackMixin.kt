package com.leocth.redesignedenigma.mixin.client.callback

import com.leocth.redesignedenigma.client.event.AttackAttemptEventCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MinecraftClient::class)
abstract class AttackAttemptEventCallbackMixin {

    @Shadow private lateinit var player: ClientPlayerEntity
    @Shadow private lateinit var crosshairTarget: HitResult
    @Shadow private lateinit var interactionManager: ClientPlayerInteractionManager
    @Shadow private lateinit var world: ClientWorld
    @Shadow protected var attackCooldown = 0

    @Inject(
        at = [
            At(value = "INVOKE",
               target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult\$Type;",
               ordinal = 0)
            ],
        method = ["doAttack"],
        cancellable = true
    )
    fun doAttack(ci: CallbackInfo) {
        val result = AttackAttemptEventCallback.EVENT.invoker().interact(player, crosshairTarget)

        if (result == ActionResult.PASS) {
            // Vanilla Behavior

            // so, due to some kotlin issues, we are actually creating
            // a new subclass AttackAttemptEventCallbackMixin.WhenMappings
            // behind the scene to handle indexing and other things,
            // which the mixin system can't access. oof.
            // as far as i know, this hidden overhead only happens with
            // the when statement paired with enums. burp

            if (crosshairTarget.type == HitResult.Type.ENTITY) {
                this.interactionManager.attackEntity(
                    player,
                    (crosshairTarget as EntityHitResult).entity
                )
            }
            else if (crosshairTarget.type == HitResult.Type.BLOCK) {
                val blockHitResult = crosshairTarget as BlockHitResult
                val blockPos = blockHitResult.blockPos
                if (!this.world.getBlockState(blockPos).isAir) {
                    this.interactionManager.attackBlock(blockPos, blockHitResult.side)
                }
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10
                }
                player.resetLastAttackedTicks()
            }
            else if (crosshairTarget.type == HitResult.Type.MISS) {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10
                }
                player.resetLastAttackedTicks()
            }
        }
        if (result == ActionResult.SUCCESS || result == ActionResult.PASS)
            player.swingHand(Hand.MAIN_HAND)
        ci.cancel()
    }
}