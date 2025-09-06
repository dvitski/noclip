package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Shadow @Final private MinecraftClient client;

    /**
     * Cancels all overlays when clipping.
     */
    @WrapMethod(method = "renderOverlays")
    private void onRenderOverlays(boolean sleeping, float tickProgress, Operation<Void> original) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(client.player);
        if (clippingPlayer.isClipping()) {
            return;
        }

        original.call(sleeping, tickProgress);
    }
}
