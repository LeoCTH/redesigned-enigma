package com.leocth.redesignedenigma.item

import com.leocth.redesignedenigma.addAll
import com.leocth.redesignedenigma.interfaces.IScrollableItem
import com.leocth.redesignedenigma.util.I18nHelper
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.sign

/**
 * This is a Java file created by leoc200 on 2020/7/24 in project redesigned-enigma-
 * All sources that are released publicly on GitHub are licensed under the MIT license.
 * Please do not redistribute this file to other platforms before acknowledging the author.
 */
class SreaxItem : Item(Settings()), IScrollableItem {

    companion object {
        val conversionTable = mapOf(
            Blocks.EMERALD_BLOCK to 9216,
            Blocks.DIAMOND_BLOCK to 73728,
            Blocks.IRON_BLOCK to 2304
        )
        val debugIdTable = listOf(
            Blocks.EMERALD_BLOCK,
            Blocks.DIAMOND_BLOCK,
            Blocks.IRON_BLOCK
        )
    }

    @Environment(EnvType.CLIENT)
    override fun appendTooltip(
        stack: ItemStack, world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        val tag = stack.orCreateTag
        val fuel = tag.getLong("energy")
        val energyString = I18nHelper.getNumericShortForm(fuel)
        val maxString = I18nHelper.getNumericShortForm(Long.MAX_VALUE)

        tooltip.addAll(
            TranslatableText("tooltip.redesignedenigma.sreax.desc1"),
            TranslatableText("tooltip.redesignedenigma.sreax.desc2"),
            TranslatableText("tooltip.redesignedenigma.sreax.energy", energyString, maxString),
            LiteralText(""),
            TranslatableText("tooltip.redesignedenigma.sreax.pr1"),
            TranslatableText("tooltip.redesignedenigma.sreax.pr2")
        )
    }



    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val tag = context.stack.orCreateTag
        var energy = tag.getLong("energy")
        if (context.player?.isSneaking == false) {
            val bs = context.world.getBlockState(context.blockPos)
            val value = conversionTable[bs.block] ?: -1
            return if (value > 0) {
                energy += value
                context.world.setBlockState(context.blockPos, Blocks.AIR.defaultState)
                tag.putLong("energy", energy)
                ActionResult.SUCCESS
            } else {
                ActionResult.PASS
            }

        } else {
            val pos = context.blockPos.offset(context.side)
            //val id = tag.getString("selectedBlock")
            val id = tag.getInt("debug_selectId")
            /*
            val block = if (id == "") {
                Blocks.EMERALD_BLOCK
            } else {
                Registry.BLOCK[Identifier(id)]
            }
             */
            val block = debugIdTable[id % debugIdTable.size] ?: Blocks.EMERALD_BLOCK
            val cost = conversionTable[block] ?: -1
            return if (cost in 0..energy) {
                energy -= cost
                context.world.setBlockState(pos, block.defaultState)
                tag.putLong("energy", energy)
                //tag.putString("selectedBlock", Registry.BLOCK.getId(block).toString())
                tag.putInt("debug_selectId", debugIdTable.indexOf(block))
                ActionResult.SUCCESS
            } else {
                ActionResult.PASS
            }
        }
    }

    override fun onScroll(player: PlayerEntity, stack: ItemStack, dwheel: Float): ActionResult {
        val tag = stack.orCreateTag
        var selectId = tag.getInt("debug_selectId")
        selectId += dwheel.sign.toInt()
        if (selectId < 0)
            selectId += debugIdTable.size
        selectId %= debugIdTable.size
        println(selectId)
        player.addChatMessage(LiteralText("DEBUG: Selected #$selectId: ${debugIdTable[selectId] ?: Blocks.EMERALD_BLOCK}"), true)
        tag.putInt("debug_selectId", selectId)
        return ActionResult.FAIL
    }

}
