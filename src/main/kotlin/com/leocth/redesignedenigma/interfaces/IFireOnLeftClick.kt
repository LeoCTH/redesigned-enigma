package com.leocth.redesignedenigma.interfaces

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult

interface IFireOnLeftClick {
    fun fire(user: PlayerEntity): ActionResult
}