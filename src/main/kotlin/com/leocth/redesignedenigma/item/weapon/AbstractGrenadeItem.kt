package com.leocth.redesignedenigma.item.weapon

import net.minecraft.client.gui.screen.world.WorldListWidget
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.world.World

abstract class AbstractGrenadeItem(settings: Settings) : Item(settings),
    IFireOnLeftClick,
    IIgnoreEquipProgress,
    IHasCustomArmPose {

}