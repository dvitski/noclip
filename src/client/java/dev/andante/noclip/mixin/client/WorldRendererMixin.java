package dev.andante.noclip.mixin.client;

import dev.andante.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    /**
     * Modifies an argument of setupTerrain to make the world render as if the player is in spectator when clipping.
     */
    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;updateCamera(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;Z)V"
        ),
        index = 2
    )
    private boolean onRenderReplaceSpectator(boolean isSpectator) {
        return isSpectator || NoClipManager.INSTANCE.isClipping();
    }
}
