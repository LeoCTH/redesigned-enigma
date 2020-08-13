package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RESounds
import com.leocth.redesignedenigma.item.weapon.BurstCapableGunItem
import com.leocth.redesignedenigma.util.HitscanArgs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.random.Random

class Glock18Item: BurstCapableGunItem(REItems.BASIC_WEAPON_SETTINGS) {


    override val holdAmmoNum = 20
    override val reloadTime = 45
    override val damagePerShot = 5.85f
    override val hitscanArgs = HitscanArgs.DEFAULT

    override fun getRecoilPattern(inaccuracy: Float): Pair<Float, Float> {
        //TODO
        val randomPitch = -3.67f + Random.nextFloat() * 5.863f * (1.53f - inaccuracy)
        val randomYaw = Random.nextFloat() * 2.593f * (1.29f - inaccuracy) - 1.573f
        return randomPitch to randomYaw
    }

    override fun playFireSound(world: World, player: PlayerEntity) {
        world.playSound(
            null,
            player.blockPos,
            RESounds.GLOCK_FIRE,
            SoundCategory.PLAYERS,
            1.0f, 1.0f
        )
    }

    override fun playReloadSound(world: World, player: PlayerEntity) {
        world.playSound(
            null,
            player.blockPos,
            RESounds.GLOCK_RELOAD,
            SoundCategory.PLAYERS,
            1.0f, 1.0f
        )
    }

    override fun stopReloadSound(world: World, player: PlayerEntity) {
        (player as ServerPlayerEntity).networkHandler.sendPacket(
            StopSoundS2CPacket(RESounds.GLOCK_RELOAD_ID, SoundCategory.PLAYERS)
        )
    }

    override fun playSelectFireSound(world: World, player: PlayerEntity) {
        world.playSound(
            null,
            player.blockPos,
            RESounds.GLOCK_SELECTFIRE,
            SoundCategory.PLAYERS,
            1.0f, 1.0f
        )
    }
}