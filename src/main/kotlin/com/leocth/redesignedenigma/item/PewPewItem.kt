package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.RedesignedEnigma
import com.leocth.redesignedenigma.addAll
import com.leocth.redesignedenigma.interfaces.IFireOnLeftClick
import com.leocth.redesignedenigma.util.HitscanArgs
import com.leocth.redesignedenigma.util.PhysicsHelper
import com.leocth.redesignedenigma.util.damage.LethalWeaponryDamageSource
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.EntityDamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class PewPewItem: Item(Settings().maxCount(1)), IFireOnLeftClick {

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
        val stack = user.mainHandStack
        val tag = user.mainHandStack.orCreateTag
        println(tag.getShort("debug_resammo"))
        return if (!world.isClient) {
            val k = PhysicsHelper.calcHitscan(world, user, HitscanArgs.DEFAULT)
            { hitPos, entitiesHit ->
                PhysicsHelper.sendS2CUpdatePacket(hitPos, user)
                entitiesHit.forEach {
                    it.damage(LethalWeaponryDamageSource(it, stack), 2.0f)
                }
            }
            if (k) ActionResult.SUCCESS else ActionResult.PASS
        } else {
            //TODO: What the fuck?
            RedesignedEnigma.LOGGER.error("wtf? you shouldnt be here, client!")
            ActionResult.PASS
        }
    }
}
