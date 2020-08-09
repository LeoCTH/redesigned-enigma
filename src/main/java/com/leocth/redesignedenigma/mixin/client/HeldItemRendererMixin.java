package com.leocth.redesignedenigma.mixin.client;

import com.google.common.base.MoreObjects;
import com.leocth.redesignedenigma.item.weapon.IIgnoreEquipProgress;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow private ItemStack mainHand;
    @Shadow protected abstract void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(
            at = @At("HEAD"),
            method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;" +
                    "Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
            cancellable = true
    )
    private void onRenderItem(
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider.Immediate vertexConsumers,
            ClientPlayerEntity player,
            int light,
            CallbackInfo ci
    ) {
        boolean ignoreMain = player.getMainHandStack().getItem() instanceof IIgnoreEquipProgress;
        boolean ignoreOff = player.getOffHandStack().getItem() instanceof IIgnoreEquipProgress;

        if (ignoreMain || ignoreOff) {
            float f = player.getHandSwingProgress(tickDelta);
            Hand hand = MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
            float pitch = MathHelper.lerp(tickDelta, player.prevPitch, player.pitch);
            float renderPitch = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch);
            float renderYaw = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((player.getPitch(tickDelta) - renderPitch) * 0.1f));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((player.getYaw(tickDelta) - renderYaw) * 0.1f));

            if (ignoreMain) {
                float swing = hand == Hand.MAIN_HAND ? f : 0.0f;
                renderFirstPersonItem(
                        player,
                        tickDelta,
                        pitch,
                        Hand.MAIN_HAND,
                        swing,
                        mainHand,
                        0.0f,
                        matrices,
                        vertexConsumers,
                        light
                );
            }
            if (ignoreOff) {
                float swing = hand == Hand.OFF_HAND ? f : 0.0f;
                renderFirstPersonItem(
                        player,
                        tickDelta,
                        pitch,
                        Hand.OFF_HAND,
                        swing,
                        mainHand,
                        0.0f,
                        matrices,
                        vertexConsumers,
                        light
                );
            }
            vertexConsumers.draw();
            ci.cancel();
        }
    }

    /* FIXME
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
}
