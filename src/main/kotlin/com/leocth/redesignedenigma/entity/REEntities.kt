package com.leocth.redesignedenigma.entity

import com.leocth.redesignedenigma.RedesignedEnigma
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object REEntities {
    val HE_GRENADE = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(RedesignedEnigma.MODID, "he_grenade"),
        FabricEntityTypeBuilder.create<HEGrenadeEntity>(SpawnGroup.MISC)
        { _, world -> HEGrenadeEntity(world)}
            .dimensions(EntityDimensions.fixed(0.3f, 0.3f))
            .build()
    )

    fun register() {
    }
}