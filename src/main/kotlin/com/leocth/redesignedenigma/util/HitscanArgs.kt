package com.leocth.redesignedenigma.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.LeavesBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

data class HitscanArgs(
    val entitySelectPredicate: (Entity) -> Boolean,
    // if true passes through
    val ignoreBlockCollisionPredicate: (BlockState) -> Boolean,
    val maxRange: Int,
    val ignoreShooter: Boolean,
    val penetrateEntitiesNum: Int
)
{
    companion object {
        val DEFAULT =
            HitscanArgs(
                { it is LivingEntity },
                { it.block is LeavesBlock },
                128,
                true,
                1
            )
    }
}