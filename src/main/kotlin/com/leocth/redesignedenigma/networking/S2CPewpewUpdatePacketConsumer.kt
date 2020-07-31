package com.leocth.redesignedenigma.networking

import com.leocth.redesignedenigma.eyePos
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.GuardianAttackSoundInstance
import net.minecraft.client.sound.MovingSoundInstance
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

class S2CPewpewUpdatePacketConsumer: PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val player = MinecraftClient.getInstance().player ?: return
        val canFire = buffer.readBoolean()
        val p1 = buffer.readFloat().toDouble()
        val p2 = buffer.readFloat().toDouble()
        val p3 = buffer.readFloat().toDouble()
        val doReload = buffer.readBoolean()
        val ppos = player.eyePos
        context.taskQueue.execute {
            if (canFire) {
                val len = ppos.distanceTo(Vec3d(p1, p2, p3)).times(2).toInt()

                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, SoundCategory.PLAYERS, 0.2f, 1.0f, player.blockPos)
                )

                for (i in 0..len) {
                    val delta = i/len.toDouble()
                    MinecraftClient.getInstance().particleManager.addParticle(
                        ParticleTypes.FLAME,
                        MathHelper.lerp(delta, ppos.x, p1),
                        MathHelper.lerp(delta, ppos.y, p2),
                        MathHelper.lerp(delta, ppos.z, p3),
                        0.0, 0.0, 0.0
                    )
                }
            }
            if (doReload) {
                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2f, 1.5f, player.blockPos)
                )
            }
        }
    }

}