package com.leocth.redesignedenigma.util.damage

import com.leocth.redesignedenigma.RedesignedEnigma
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.EntityDamageSource
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

class LethalWeaponryDamageSource(
    private val culprit: Entity,
    private val weapon: ItemStack = ItemStack.EMPTY
) : EntityDamageSource("lethalweaponry", culprit), IIgnoreRegenCooldown {
    override fun getDeathMessage(victim: LivingEntity): Text {
        return TranslatableText(
            "death.${RedesignedEnigma.MODID}.$name",
            victim.displayName,
            culprit.displayName,
            weapon.toHoverableText()
        )
    }
}