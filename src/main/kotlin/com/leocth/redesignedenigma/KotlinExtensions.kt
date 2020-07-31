package com.leocth.redesignedenigma

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d

fun <E> MutableList<E>.addAll(vararg elements: E) {
    this.addAll(elements)
}

val PlayerEntity.eyePos: Vec3d
    get() = Vec3d(x, eyeY, z)