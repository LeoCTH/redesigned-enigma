package com.leocth.redesignedenigma.mixin.client

import com.leocth.redesignedenigma.item.weapon.IIgnoreEquipProgress
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.client.render.item.HeldItemRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.entity.LivingEntity
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(HeldItemRenderer::class)
abstract class HeldItemRendererMixin {

    @Shadow
    private val mainHand: ItemStack = ItemStack.EMPTY
    @Shadow
    private val offHand: ItemStack = ItemStack.EMPTY

    @Shadow
    private val equipProgressMainHand = 0f
    @Shadow
    private val prevEquipProgressMainHand = 0f
    @Shadow
    private val equipProgressOffHand = 0f
    @Shadow
    private val prevEquipProgressOffHand = 0f
    /*
    @Inject(
        at = [
            At(
                value = "INVOKE",
                target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                ordinal = 0
            )
        ],
        method = ["renderFirstPersonItem"],
        cancellable = true
    )
    fun onRenderFirstPersonItem(
        player: AbstractClientPlayerEntity,
        tickDelta: Float,
        pitch: Float,
        hand: Hand,
        swingProgress: Float,
        itemStack: ItemStack,
        equipProgress: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        ci: CallbackInfo
    ) {
        //TODO
        if (itemStack.item is GunItem) {
            val arm = if (hand == Hand.MAIN_HAND) player.mainArm else player.mainArm.opposite
            val bl4 = arm == Arm.RIGHT
            if (!player.isInvisible)
                this.renderArmHoldingItem(matrices, vertexConsumers, light, 0.0f, swingProgress, arm)
            this.applyEquipOffset(matrices, arm, 0.0f)
            this.renderItem(
                player,
                ItemStack(Blocks.JUKEBOX),
                ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND,
                !bl4,
                matrices,
                vertexConsumers,
                light
            )
            ci.cancel()
        }
    }
    */

    @Inject(
        at = [At("HEAD")],
        method = [
            "renderItem(FLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider\$Immediate;" +
                    "Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
        ],
        cancellable = true
    )
    fun onRenderItem(
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: Immediate,
        player: ClientPlayerEntity,
        light: Int,
        ci: CallbackInfo
    ) {
        val f = player.getHandSwingProgress(tickDelta)
        val hand = player.preferredHand ?: Hand.MAIN_HAND
        val g = MathHelper.lerp(tickDelta, player.prevPitch, player.pitch)
        var bl = true
        var bl2 = true
        val itemStack3: ItemStack
        if (player.isUsingItem) {
            itemStack3 = player.activeItem
            if (itemStack3.item === Items.BOW || itemStack3.item === Items.CROSSBOW) {
                bl = player.activeHand == Hand.MAIN_HAND
                bl2 = !bl
            }
            val hand2 = player.activeHand
            if (hand2 == Hand.MAIN_HAND) {
                val itemStack2 = player.offHandStack
                if (itemStack2.item === Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
                    bl2 = false
                }
            }
        } else {
            itemStack3 = player.mainHandStack
            val itemStack4 = player.offHandStack
            if (itemStack3.item === Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
                bl2 = !bl
            }
            if (itemStack4.item === Items.CROSSBOW && CrossbowItem.isCharged(itemStack4)) {
                bl = !itemStack3.isEmpty
                bl2 = !bl
            }
        }

        val h = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch)
        val i = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw)
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((player.getPitch(tickDelta) - h) * 0.1f))
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((player.getYaw(tickDelta) - i) * 0.1f))
        var m: Float
        var l: Float
        if (bl) {
            l = if (hand == Hand.MAIN_HAND) f else 0.0f
            m = if (player.mainHandStack.item is IIgnoreEquipProgress)
                0.0f
            else
                1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand)
            renderFirstPersonItem(
                player,
                tickDelta,
                g,
                Hand.MAIN_HAND,
                l,
                mainHand,
                m,
                matrices,
                vertexConsumers,
                light
            )
        }

        if (bl2) {
            l = if (hand == Hand.OFF_HAND) f else 0.0f
            m = if (player.offHandStack.item is IIgnoreEquipProgress)
                0.0f
            else
                1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand)
            renderFirstPersonItem(
                player,
                tickDelta,
                g,
                Hand.OFF_HAND,
                l,
                offHand,
                m,
                matrices,
                vertexConsumers,
                light
            )
        }

        vertexConsumers.draw()
        ci.cancel()
    }

    @Shadow
    abstract fun renderItem(
        entity: LivingEntity?,
        stack: ItemStack,
        renderMode: ModelTransformation.Mode,
        leftHanded: Boolean,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    )

    @Shadow
    abstract fun applyEquipOffset(matrices: MatrixStack, arm: Arm, equipProgress: Float)

    @Shadow
    abstract fun renderArmHoldingItem(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        equipProgress: Float,
        swingProgress: Float,
        arm: Arm
    )

    @Shadow
    abstract fun renderFirstPersonItem(
        player: AbstractClientPlayerEntity,
        tickDelta: Float,
        pitch: Float,
        hand: Hand,
        swingProgress: Float,
        itemStack: ItemStack,
        equipProgress: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    )
}