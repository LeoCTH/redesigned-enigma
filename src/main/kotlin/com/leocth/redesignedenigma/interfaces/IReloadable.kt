package com.leocth.redesignedenigma.interfaces

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

interface IReloadable {
    fun reloadStart(player: PlayerEntity, stack: ItemStack, tag: CompoundTag)
    fun reloadEnd(player: PlayerEntity, stack: ItemStack, tag: CompoundTag)
}