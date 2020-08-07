package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RESounds
import com.leocth.redesignedenigma.addAll
import com.leocth.redesignedenigma.item.weapon.SemiGunItem
import com.leocth.redesignedenigma.util.HitscanArgs
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.world.World
import kotlin.random.Random

class DeagleItem : SemiGunItem(Settings().maxCount(1)) {
    override val holdAmmoNum = 7
    override val reloadTime = 44
    override val damagePerShot = 11.3f
    override val hitscanArgs = HitscanArgs.DEFAULT

    override fun playFireSound(world: World, player: PlayerEntity) {
        if (!world.isClient) {
            world.playSound(
                null,
                player.blockPos,
                RESounds.DEAGLE_FIRE,
                SoundCategory.PLAYERS,
                0.2f,
                1.0f
            )
        }
    }

    override fun playReloadSound(world: World, player: PlayerEntity, reloadProgress: Float, isStartingReload: Boolean) {
        if (isStartingReload && !world.isClient) {
            world.playSound(
                null,
                player.blockPos,
                RESounds.DEAGLE_RELOAD,
                SoundCategory.PLAYERS,
                0.8f,
                1.0f
            )
        }
    }

    override fun stopReloadSound(world: World, player: PlayerEntity) {
        (player as ServerPlayerEntity).networkHandler.sendPacket(
            StopSoundS2CPacket(RESounds.DEAGLE_RELOAD_ID, SoundCategory.PLAYERS)
        )
    }

    override fun getRecoilPattern(inaccuracy: Float): Pair<Float, Float> {
        val randomPitch = -10f + Random.nextFloat() * 3.863f * (1.63f - inaccuracy)
        val randomYaw = 2.23f + (Random.nextFloat() * 5.593f * (1.29f - inaccuracy) - 2.783f)
        return randomPitch to randomYaw
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        tooltip.addAll(
            TranslatableText("tooltip.redesignedenigma.deagle.desc1"),
            TranslatableText("tooltip.redesignedenigma.deagle.desc2"),
            TranslatableText("tooltip.redesignedenigma.deagle.desc3"),
            TranslatableText("tooltip.redesignedenigma.deagle.desc4"),
            TranslatableText("tooltip.redesignedenigma.deagle.witty1")
        )
        super.appendTooltip(stack, world, tooltip, context)
    }
}