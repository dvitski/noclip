package dev.andante.noclip.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Shadow @Final private Font font;

/*    *//**
     * Captures the first line of the debug hud.
     *//*
    @ModifyArg(
            method = "getRightText",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;",
                    ordinal = 0,
                    remap = false
            ),
            index = 0
    )
    private <E> E[] onGetLeftTextCaptureFirstDebugLine(E[] elements) {
        if (elements instanceof String[] strings) {
            String str1 = strings[1];
            String str2 = strings[2];
            NoClipClientImpl.NOCLIP_HUD_RENDERER.setActiveDebugLine(this.textRenderer.getWidth(str2) > this.textRenderer.getWidth(str1) ? str2 : str1);
        }
        return elements;
    }*/
}
