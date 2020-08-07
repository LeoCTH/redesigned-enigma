package com.leocth.redesignedenigma.util

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

data class HitscanArgs(
    val entitySelectPredicate: (Entity) -> Boolean,
    val maxRange: Int,
    val ignoreShooter: Boolean,
    val penetrateEntitiesNum: Int
)
{
    companion object {
        val DEFAULT = HitscanArgs({ it is LivingEntity }, 128, true, 1)
    }
}