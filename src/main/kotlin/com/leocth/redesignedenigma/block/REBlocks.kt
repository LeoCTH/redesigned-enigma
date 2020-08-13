package com.leocth.redesignedenigma.block

import com.leocth.redesignedenigma.RedesignedEnigma
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

object REBlocks {
    val TEST = Block(FabricBlockSettings.copy(Blocks.STONE))

    fun register() {
        Registry.register(Registry.BLOCK, "${RedesignedEnigma.MODID}:test", TEST)
        Registry.register(Registry.ITEM, "${RedesignedEnigma.MODID}:test", BlockItem(TEST, Item.Settings()))
    }
}