package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    /**
     * Cancels all overlays when clipping.
     */
    @WrapMethod(method = "renderScreenEffect")
    private void onRenderOverlays(boolean sleeping, float tickProgress, SubmitNodeCollector queue, Operation<Void> original) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(minecraft.player);
        if (clippingPlayer.isClipping()) {
            return;
        }

        original.call(sleeping, tickProgress, queue);
    }
}
