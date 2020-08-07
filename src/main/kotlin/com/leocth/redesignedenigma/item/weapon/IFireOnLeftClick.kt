package com.leocth.redesignedenigma.item.weapon

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.world.World

interface IFireOnLeftClick {
    fun fire(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack, heldDown: Boolean)
}