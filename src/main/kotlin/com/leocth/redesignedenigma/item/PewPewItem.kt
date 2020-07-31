package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RedesignedEnigma
import com.leocth.redesignedenigma.addAll
import com.leocth.redesignedenigma.interfaces.IFireOnLeftClick
import com.leocth.redesignedenigma.interfaces.IReloadable
import com.leocth.redesignedenigma.util.HitscanArgs
import com.leocth.redesignedenigma.util.PhysicsHelper
import com.leocth.redesignedenigma.util.damage.LethalWeaponryDamageSource
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.world.World

class PewPewItem: Item(Settings().maxCount(1)), IFireOnLeftClick, IReloadable {

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        tooltip.addAll(
            TranslatableText("tooltip.redesignedenigma.pewpew.desc1"),
            TranslatableText("tooltip.redesignedenigma.pewpew.desc2"),
            TranslatableText("tooltip.redesignedenigma.pewpew.desc3"),
            TranslatableText("tooltip.redesignedenigma.pewpew.warn1"),
            TranslatableText("tooltip.redesignedenigma.pewpew.warn2"),
            TranslatableText("tooltip.redesignedenigma.pewpew.warn3")
        )
        super.appendTooltip(stack, world, tooltip, context)
    }

    override fun fire(
        user: PlayerEntity
    ): ActionResult {
        val world = user.world
        if (!world.isClient) {
            val stack = user.mainHandStack
            val tag = stack.orCreateTag

            var resammo = tag.getShort("debug_resammo")
            var reloadstarted = tag.getInt("debug_reloadstarted")
            val curTick = user.server?.ticks ?: 0
            var reload = false
            val canFire: Boolean

            println(curTick)
            if (resammo <= 0) {
                canFire = false
                if (reloadstarted == 0) {
                    println("start reload")
                    // start reload
                    reloadstarted = curTick
                    reload = true
                }
                else if (reloadstarted + 50 < curTick){
                    println("reload ended")
                    // reload ended
                    resammo = 10
                    reloadstarted = 0
                    reload = false
                }
                /*
                else {
                    // progress reload
                }
                */
            }
            else {
                println("normal fire")
                // calculate ammo and issue reload
                canFire = true
                --resammo
            }
            tag.putInt("debug_reloadstarted", reloadstarted)
            tag.putShort("debug_resammo", resammo)


            val k = PhysicsHelper.calcHitscan(world, user, HitscanArgs.DEFAULT)
            { hitPos, entitiesHit ->
                PhysicsHelper.sendS2CUpdatePacket(canFire, hitPos, user, reload)
                // TODO optimize reduce waste calculations
                if (canFire) {
                    entitiesHit.forEach {
                        it.damage(LethalWeaponryDamageSource(it, stack), 2.0f)
                    }
                }
            }
            return if (k) ActionResult.SUCCESS else ActionResult.PASS
        } else {
            //TODO: What the fuck?
            RedesignedEnigma.LOGGER.error("wtf? you shouldnt be here, client!")
            return ActionResult.PASS
        }
    }

    override fun reloadStart(player: PlayerEntity, stack: ItemStack, tag: CompoundTag) {
        tag.putInt("debug_reloadstarted", player.server?.ticks ?: 0)
        //TODO send reload s2c packet
    }

    override fun reloadEnd(player: PlayerEntity, stack: ItemStack, tag: CompoundTag) {
        tag.putInt("debug_reloadstarted", -1)

    }
}
