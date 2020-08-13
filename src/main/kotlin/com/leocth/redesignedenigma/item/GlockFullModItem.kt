package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RESounds
import com.leocth.redesignedenigma.addAll
import com.leocth.redesignedenigma.item.weapon.AutomaticGunItem
import com.leocth.redesignedenigma.util.HitscanArgs
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.random.Random

class GlockFullModItem : AutomaticGunItem(REItems.BASIC_WEAPON_SETTINGS) {

    override val holdAmmoNum = 50
    override val reloadTime = 40
    override val damagePerShot = 3.8f
    override val hitscanArgs = HitscanArgs.DEFAULT

    override fun playFireSound(world: World, player: PlayerEntity) {
        if (!world.isClient) {
            world.playSound(
                null,
                player.blockPos,
                RESounds.GLOCK_FIRE,
                SoundCategory.PLAYERS,
                1.0f, 1.0f
            )
        }
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

    override fun getRecoilPattern(inaccuracy: Float): Pair<Float, Float> {
        val randomYaw = Random.nextFloat() * 0.25f * (1.56f - inaccuracy) - 0.176f
        val randomPitch = -0.48f + Random.nextFloat() * 0.16f * (1.237f - inaccuracy)
        return randomPitch to randomYaw
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        tooltip.addAll(
            TranslatableText("tooltip.redesignedenigma.glock_full_mod.desc1"),
            TranslatableText("tooltip.redesignedenigma.glock_full_mod.desc2"),
            TranslatableText("tooltip.redesignedenigma.glock_full_mod.desc3"),
            TranslatableText("tooltip.redesignedenigma.glock_full_mod.witty1")
        )
        super.appendTooltip(stack, world, tooltip, context)
    }
}
