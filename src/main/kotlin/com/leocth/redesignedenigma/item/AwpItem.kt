package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RESounds
import com.leocth.redesignedenigma.item.weapon.SemiGunItem
import com.leocth.redesignedenigma.util.HitscanArgs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.world.World

class AwpItem: SemiGunItem(REItems.BASIC_WEAPON_SETTINGS) {
    override val holdAmmoNum = 10
    override val reloadTime = 74
    override val damagePerShot = 22.7f
    override val hitscanArgs = HitscanArgs.DEFAULT

    override fun getRecoilPattern(inaccuracy: Float): Pair<Float, Float> = 0.0f to -0.5f

    override fun playFireSound(world: World, player: PlayerEntity) {
        if (!world.isClient) {
            world.playSound(
                null,
                player.blockPos,
                RESounds.AWP_FIRE,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
            )
        }
    }

    override fun playReloadSound(world: World, player: PlayerEntity) {
        world.playSound(
            null,
            player.blockPos,
            RESounds.AWP_RELOAD,
            SoundCategory.PLAYERS,
            1.0f,
            1.0f
        )
    }

    override fun stopReloadSound(world: World, player: PlayerEntity) {
        (player as ServerPlayerEntity).networkHandler.sendPacket(
            StopSoundS2CPacket(RESounds.AWP_RELOAD_ID, SoundCategory.PLAYERS)
        )
    }
}