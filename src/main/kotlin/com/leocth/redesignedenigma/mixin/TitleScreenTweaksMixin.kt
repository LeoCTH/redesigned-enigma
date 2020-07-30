package com.leocth.redesignedenigma.mixin

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.TitleScreen
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(TitleScreen::class)
abstract class TitleScreenTweaksMixin {
    var disguiseMod = true

    @Redirect(
        method = ["render(IIF)V"],
        at = At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/TitleScreen;isMinceraft:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private fun trickIsMinceraftInRender(
        owner: TitleScreen,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ): Boolean {
        val base = owner.width / 2 - 137
        return mouseY in 30..74 && mouseX-base in 99..154
    }

    @Redirect(
        method = ["render(IIF)V"],
        at = At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;getVersionType()Ljava/lang/String;"
        )
    )
    private fun trickGetVersionTypeInRender(owner: MinecraftClient): String {
        return if (disguiseMod) "release" else owner.versionType
    }

    @Redirect(
        method = ["render(IIF)V"],
        at = At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isModded()Z")
    )
    private fun trickIsModdedInRender(owner: MinecraftClient): Boolean {
        return !disguiseMod && owner.isModded
    }


}