package com.leocth.redesignedenigma.item.weapon

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.world.World

abstract class AutomaticGunItem(settings: Settings) : GunItem(settings) {
    override fun fire(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack, heldDown: Boolean) {
        if (!world.isClient && heldDown) {
            super.fire(world, user, hand, stack, heldDown)
        }
    }
}