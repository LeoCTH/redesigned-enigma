package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.entity.HEGrenadeEntity
import com.leocth.redesignedenigma.item.weapon.AbstractGrenadeItem
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.world.World

class HEGrenadeItem: AbstractGrenadeItem(REItems.BASIC_WEAPON_SETTINGS) {
    override fun fire(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack, heldDown: Boolean) {
        val grenade = HEGrenadeEntity(world)
        grenade.setProperties(user, user.pitch, user.yaw, 0.0f, 1.5f, 1.0f)
        world.spawnEntity(grenade)
    }

    override fun getRightArmPose(
        entity: LivingEntity,
        f1: Float,
        f2: Float,
        f3: Float,
        f4: Float,
        f5: Float
    ): Pair<Float, Float> {
        return 0f to 0f
    }

    override fun getLeftArmPose(
        entity: LivingEntity,
        f1: Float,
        f2: Float,
        f3: Float,
        f4: Float,
        f5: Float
    ): Pair<Float, Float> {
        return 0f to 0f
    }
}