package com.leocth.redesignedenigma.interfaces

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult

interface IScrollableItem {
    fun onScroll(player: PlayerEntity, stack: ItemStack, dwheel: Float): ActionResult
}