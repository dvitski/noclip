package me.andante.noclip.mixin.client;

import me.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    /**
     * Cancels all overlays when clipping.
     */
    @Inject(method = "renderOverlays", at = @At("HEAD"), cancellable = true)
    private static void onRenderOverlays(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(client.player);
        if (clippingPlayer.isClipping()) ci.cancel();
    }
}
