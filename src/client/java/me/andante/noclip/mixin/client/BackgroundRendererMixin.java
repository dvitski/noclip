package me.andante.noclip.mixin.client;

import me.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    /**
     * Improves vision when clipping.
     */
    @Inject(
        method = "applyFog",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, CameraSubmersionType submersion, Entity entity, BackgroundRenderer.FogData data) {
        if (entity instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            if (submersion == CameraSubmersionType.LAVA || submersion == CameraSubmersionType.POWDER_SNOW) {
                data.fogStart = -8.0f;
                data.fogEnd = viewDistance * 0.5f;
            }
        }
    }
}
