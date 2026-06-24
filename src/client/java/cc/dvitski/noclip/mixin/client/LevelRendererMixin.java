package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    /**
     * Modifies an argument of setupTerrain to make the world render as if the player is in spectator when clipping.
     */
    @ModifyArg(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;cullTerrain(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;Z)V"
        ),
        index = 2
    )
    private boolean onRenderReplaceSpectator(boolean isSpectator) {
        return isSpectator || NoClipManager.INSTANCE.isClipping();
    }
}
