package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    /**
     * Cancels all overlays when clipping.
     */
    @WrapMethod(method = "renderOverlays")
    private static void onRenderOverlays(MinecraftClient client, MatrixStack matrices, Operation<Void> original) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(client.player);
        if (clippingPlayer.isClipping()) {
            return;
        }

        original.call(client, matrices);
    }
}
