package com.leocth.redesignedenigma.mixin.client

import com.leocth.redesignedenigma.item.weapon.IHasCustomArmPose
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(BipedEntityModel::class)
abstract class ArmPoseHackMixin<T : LivingEntity> {
    @Shadow
    lateinit var rightArm: ModelPart
    @Shadow
    lateinit var leftArm: ModelPart

    /// TODO: 1.16 changed a lot here.
    /// TODO: given that this is not a crucial feature and being the lazy fuck that i am,
    /// TODO: #willfixinthefuture
    @Inject(
        at = [
            At(
                value = "FIELD",
                target = "handSwingProgress:F",
                opcode = Opcodes.GETFIELD,
                ordinal = 0
            )
        ],
        method = ["setAngles"]
    )
    fun hackSetAngles(entity: T, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, ci: CallbackInfo) {
        if (entity is PlayerEntity) {
            val mainItem = entity.mainHandStack.item
            val offItem = entity.offHandStack.item
            if (mainItem is IHasCustomArmPose) {
                val pose = mainItem.getLeftArmPose(entity, f1, f2, f3, f4, f5)
                rightArm.pitch = pose.first
                rightArm.yaw = pose.second
            }
            if (offItem is IHasCustomArmPose) {
                val pose = offItem.getLeftArmPose(entity, f1, f2, f3, f4, f5)
                leftArm.pitch = pose.first
                leftArm.yaw = pose.second
            }
        }
    }
}