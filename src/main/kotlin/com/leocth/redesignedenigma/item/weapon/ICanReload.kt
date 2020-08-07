package com.leocth.redesignedenigma.item.weapon

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.world.World

interface ICanReload {
    fun reload(world: World, user: PlayerEntity, stack: ItemStack, hand: Hand)
}