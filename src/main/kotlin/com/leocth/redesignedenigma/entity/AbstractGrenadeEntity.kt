package com.leocth.redesignedenigma.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.projectile.thrown.ThrownEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.World

abstract class AbstractGrenadeEntity(
    type: EntityType<out ThrownEntity>,
    world: World
) : ThrownEntity(type, world) {

    var life: Int = 0

    abstract val timeToExplode: Int

    abstract fun explode()

    override fun writeCustomDataToTag(tag: CompoundTag) {
        super.writeCustomDataToTag(tag)
        tag.putShort("life", life.toShort())
    }

    override fun readCustomDataFromTag(tag: CompoundTag) {
        super.readCustomDataFromTag(tag)
        life = tag.getShort("life").toInt()
    }

    override fun tick() {
        super.tick()
        if (!world.isClient) {
            this.age()
        }
    }

    protected fun age() {
        ++life
        if (life >= timeToExplode) {
            explode()
            this.destroy()
        }
    }

    override fun initDataTracker() {}
}