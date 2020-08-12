package com.leocth.redesignedenigma.item.weapon

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

abstract class BurstCapableGunItem(settings: Settings) : AbstractGunItem(settings) {

    private data class CustomData(
        val reloadTicks: Int,
        val curAmmo: Short,
        val isBurstMode: Boolean,
        val burstRoundsLeft: Short
    ) {
        companion object {
            fun loadCustomData(stack: ItemStack): CustomData {
                val tag = stack.orCreateTag
                val reloadTicks = tag.getInt("reloadTicks")
                val curAmmo = tag.getShort("ammo")
                val isBurstMode = tag.getBoolean("isBurstMode")
                val burstRoundsLeft = tag.getShort("burstRoundsLeft")

                return CustomData(reloadTicks, curAmmo, isBurstMode, burstRoundsLeft)
            }

            fun saveCustomData(
                stack: ItemStack,
                reloadTicks: Int,
                curAmmo: Short,
                isBurstMode: Boolean,
                burstRoundsLeft: Short
            ) {
                val tag = stack.orCreateTag
                tag.putInt("reloadTicks", reloadTicks)
                tag.putShort("ammo", curAmmo)
                tag.putBoolean("isBurstMode", isBurstMode)
                tag.putShort("burstRoundsLeft", burstRoundsLeft)
            }
        }
    }

    override fun fire(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack, heldDown: Boolean) {
        if (!world.isClient && !heldDown) {
            val data = CustomData.loadCustomData(stack)
            if (data.burstRoundsLeft <= 0) {
                if (data.isBurstMode)
                    burstFire(world, user, hand, stack, data)
                else
                    super.fire(world, user, hand, stack, heldDown)
            }
        }
    }

    private fun burstFire(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack, data: CustomData) {
        var (reloadTicks, curAmmo, isBurstMode, burstRoundsLeft) = data
        if (reloadTicks < 0) {
            if (burstRoundsLeft <= 0)
                burstRoundsLeft = 3
            if (curAmmo > 0) {
                --curAmmo
                fireSingle(world, user, stack)
                --burstRoundsLeft
            } else {
                reloadTicks = 0
                burstRoundsLeft = 0
            }
        }

        CustomData.saveCustomData(stack, reloadTicks, curAmmo, isBurstMode, burstRoundsLeft)
    }


    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (entity is PlayerEntity) {
            var (reloadTicks, curAmmo, isBurstMode, burstRoundsLeft) = CustomData.loadCustomData(stack)
            if (!world.isClient) {
                val reloadProgress = MathHelper.clamp(reloadTicks / reloadTime.toFloat(), 0.0f, 1.0f)
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
                        reloadTicks = resetReloadTicks(curAmmo)
                        if (burstRoundsLeft > 0) {
                            if (curAmmo > 0) {
                                --curAmmo
                                fireSingle(world, entity, stack)
                                --burstRoundsLeft
                            }
                            else {
                                burstRoundsLeft = 0
                            }
                        }
                    }
                } else {
                    // interrupt reload
                    reloadTicks = resetReloadTicks(curAmmo)
                    stopReloadSound(world, entity)
                }
                CustomData.saveCustomData(stack, reloadTicks, curAmmo, isBurstMode, burstRoundsLeft)
            } else {
                if (selected) {
                    clientDisplay(stack, world, entity, curAmmo, reloadTicks)
                }
            }
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        var (reloadTicks, curAmmo, isBurstMode, burstRoundsLeft) = CustomData.loadCustomData(stack)
        isBurstMode = !isBurstMode
        world.playSound(
            null,
            user.blockPos,
            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
            SoundCategory.PLAYERS,
            0.8f,
            if (isBurstMode) 1.0f else 0.7f
        )
        CustomData.saveCustomData(stack, reloadTicks, curAmmo, isBurstMode, burstRoundsLeft)
        return TypedActionResult.fail(stack)
    }
}