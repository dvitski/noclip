package me.andante.noclip.mixin.client;

import me.andante.noclip.impl.client.NoClipClientImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Shadow @Final private TextRenderer textRenderer;

    /**
     * Captures the first line of the debug hud.
     */
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
    }
}
