package com.leocth.redesignedenigma.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public abstract class TitleScreenTweaksMixin extends Screen {

    public boolean disguiseMod = true;

    protected TitleScreenTweaksMixin(Text title) {
        super(title);
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/TitleScreen;isMinceraft:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private boolean trickIsMinceraftInRender(TitleScreen owner, MatrixStack matrixStack, int mouseX, int mouseY, float delta)
    {
        int base = owner.width / 2 - 137;
        int x = mouseX - base;
        return mouseY >= 30 && mouseY <= 74 && x >= 99 && x <= 154;
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;getVersionType()Ljava/lang/String;"
        )
    )
    private String trickGetVersionTypeInRender(MinecraftClient owner) {
        return disguiseMod ? "release" : owner.getVersionType();
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;isModded()Z"
        )
    )
    private boolean trickIsModdedInRender(MinecraftClient owner) {
        return !disguiseMod && owner.isModded();
    }

}