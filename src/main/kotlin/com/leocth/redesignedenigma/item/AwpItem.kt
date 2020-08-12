package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.item.weapon.SemiGunItem
import com.leocth.redesignedenigma.util.HitscanArgs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World

class AwpItem: SemiGunItem(REItems.BASIC_WEAPON_SETTINGS) {
    override val holdAmmoNum = 10
    override val reloadTime: Int
        get() = TODO("Not yet implemented")
    override val damagePerShot = 22.7f
    override val hitscanArgs = HitscanArgs.DEFAULT

    override fun getRecoilPattern(inaccuracy: Float): Pair<Float, Float> {
        TODO("Not yet implemented")
    }

    override fun playFireSound(world: World, player: PlayerEntity) {
        TODO("Not yet implemented")
    }

    override fun playReloadSound(world: World, player: PlayerEntity, reloadProgress: Float, isStartingReload: Boolean) {
        TODO("Not yet implemented")
    }

    override fun stopReloadSound(world: World, player: PlayerEntity) {
        TODO("Not yet implemented")
    }
}