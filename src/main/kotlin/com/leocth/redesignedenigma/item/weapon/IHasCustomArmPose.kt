package com.leocth.redesignedenigma.item.weapon

import net.minecraft.entity.LivingEntity

interface IHasCustomArmPose {
    fun getRightArmPose(entity: LivingEntity, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float): Pair<Float, Float>
    fun getLeftArmPose(entity: LivingEntity, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float): Pair<Float, Float>
}