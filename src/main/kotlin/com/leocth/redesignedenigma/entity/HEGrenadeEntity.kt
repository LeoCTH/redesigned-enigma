package com.leocth.redesignedenigma.entity

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Packet
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.world.World

class HEGrenadeEntity(world: World) : AbstractGrenadeEntity(REEntities.HE_GRENADE, world) {
    override val timeToExplode = 36

    override fun explode() {
        world.addParticle(ParticleTypes.LARGE_SMOKE, true, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
    }

}