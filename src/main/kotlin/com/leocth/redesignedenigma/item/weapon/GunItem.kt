package com.leocth.redesignedenigma.item.weapon

import com.leocth.redesignedenigma.util.HitscanArgs
import com.leocth.redesignedenigma.util.PhysicsHelper
import com.leocth.redesignedenigma.util.damage.LethalWeaponryDamageSource
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket.Flag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.random.Random

abstract class GunItem(settings: Settings) : Item(settings),
    IFireOnLeftClick,
    IIgnoreEquipProgress,
    ICanReload,
    IHasCustomArmPose {

    abstract val holdAmmoNum: Int
    abstract val reloadTime: Int
    abstract val damagePerShot: Float
    abstract val hitscanArgs: HitscanArgs

    abstract fun getRecoilPattern(inaccuracy: Float): Pair<Float, Float>

    abstract fun playFireSound(world: World, player: PlayerEntity)
    abstract fun playReloadSound(world: World, player: PlayerEntity, reloadProgress: Float, isStartingReload: Boolean)
    abstract fun stopReloadSound(world: World, player: PlayerEntity)

    private data class CustomData(val reloadTicks: Int, val curAmmo: Short) {
        companion object {
            fun loadCustomData(stack: ItemStack): CustomData {
                val tag = stack.orCreateTag
                val reloadTicks = tag.getInt("reloadTicks")
                val curAmmo = tag.getShort("ammo")

                return CustomData(reloadTicks, curAmmo)
            }

            fun saveCustomData(stack: ItemStack, reloadTicks: Int, curAmmo: Short) {
                val tag = stack.orCreateTag
                tag.putInt("reloadTicks", reloadTicks)
                tag.putShort("ammo", curAmmo)
            }
        }
    }

    override fun fire(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack, heldDown: Boolean) {
        if (!world.isClient) {
            var (reloadTicks, curAmmo) = CustomData.loadCustomData(stack)

            if (reloadTicks < 0) {
                if (curAmmo > 0) {
                    --curAmmo
                    PhysicsHelper.calcHitscan(world, user, hitscanArgs)
                    { hitPos, entitiesHit ->
                        playFireSound(world, user)
                        //TODO: base inaccuracy on movement, action, etc.
                        val recoil = getRecoilPattern(1f)
                        user.pitch += recoil.first
                        user.yaw += recoil.second
                        (user as ServerPlayerEntity).networkHandler.sendPacket(
                            PlayerPositionLookS2CPacket(
                                0.0, 0.0, 0.0,
                                recoil.second, recoil.first,
                                setOf(Flag.X, Flag.Y, Flag.Z, Flag.X_ROT, Flag.Y_ROT),
                                Random.nextInt()
                            )
                        )

                        entitiesHit.forEach {
                            it.damage(LethalWeaponryDamageSource(user, stack), damagePerShot)
                        }
                    }
                } else {
                    reloadTicks = 0
                }
            }

            CustomData.saveCustomData(stack, reloadTicks, curAmmo)
        }
    }

    override fun reload(world: World, user: PlayerEntity, stack: ItemStack, hand: Hand) {
        if (!world.isClient) {
            var (reloadTicks, curAmmo) = CustomData.loadCustomData(stack)
            if (reloadTicks < 0 && curAmmo < holdAmmoNum)
                reloadTicks = 0
            CustomData.saveCustomData(stack, reloadTicks, curAmmo)
        }
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        //TODO optimize n simplify
        if (entity is PlayerEntity /*TODO make it work with other entities? idk*/) {
            var (reloadTicks, curAmmo) = CustomData.loadCustomData(stack)
            val reloadProgress = MathHelper.clamp(reloadTicks / reloadTime.toFloat(), 0.0f, 1.0f)
            if (!world.isClient) {
                // that's what you get when emulating csgo behavior, sucker!
                // interrupts yo reload!
                if (selected) {
                    if (reloadTicks >= 0) {
                        playReloadSound(
                            entity.world,
                            entity,
                            reloadProgress,
                            reloadTicks == 0
                        )
                        ++reloadTicks
                        if (reloadTicks >= reloadTime) {
                            curAmmo = holdAmmoNum.toShort()
                            reloadTicks = -1
                        }
                    } else {
                        reloadTicks = if (curAmmo <= 0) 0 else -1
                    }
                } else {
                    reloadTicks = if (curAmmo <= 0) 0 else -1
                    stopReloadSound(world, entity)
                }
                CustomData.saveCustomData(stack, reloadTicks, curAmmo)
            } else {
                if (selected) {
                    val client = MinecraftClient.getInstance()
                    if (reloadProgress > 0.0f) {
                        client.inGameHud.setOverlayMessage(
                            TranslatableText(
                                "text.redesignedenigma.reloading",
                                String.format("%.2f%%", reloadProgress * 100)
                            ),
                            false
                        )
                    } else {
                        client.inGameHud.setOverlayMessage(
                            TranslatableText("text.redesignedenigma.ammo", curAmmo, holdAmmoNum),
                            false
                        )
                    }
                }
            }
        }
    }

    private fun getFlatArmPose(entity: LivingEntity): Pair<Float, Float> =
        ((-90 + entity.pitch) * 0.0174532925f) to 0f

    override fun getRightArmPose(entity: LivingEntity, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float)
            : Pair<Float, Float> = getFlatArmPose(entity)

    override fun getLeftArmPose(entity: LivingEntity, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float)
            : Pair<Float, Float> = getFlatArmPose(entity)
}