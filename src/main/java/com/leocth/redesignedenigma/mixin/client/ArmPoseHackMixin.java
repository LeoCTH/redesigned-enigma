package com.leocth.redesignedenigma.mixin.client;

import com.leocth.redesignedenigma.item.weapon.IHasCustomArmPose;
import kotlin.Pair;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class ArmPoseHackMixin<T extends LivingEntity> {

    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;

    @Inject(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;handSwingProgress:F",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        ),
        method = "setAngles"
    )
    //FIXME: 1.16
    private void hackSetAngles(T entity, float f1, float f2, float f3, float f4, float f5, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            Item mainItem = player.getMainHandStack().getItem();
            Item offItem = entity.getOffHandStack().getItem();
            if (mainItem instanceof IHasCustomArmPose) {
                // interop *lenny*
                Pair<Float, Float> pose = ((IHasCustomArmPose) mainItem).getLeftArmPose(entity, f1, f2, f3, f4, f5);
                rightArm.pitch = pose.getFirst();
                rightArm.yaw = pose.getSecond();
            }
            if (offItem instanceof IHasCustomArmPose) {
                Pair<Float, Float> pose = ((IHasCustomArmPose) offItem).getLeftArmPose(entity, f1, f2, f3, f4, f5);
                leftArm.pitch = pose.getFirst();
                leftArm.yaw = pose.getSecond();
            }
        }
    }
}
