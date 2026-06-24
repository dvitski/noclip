package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public class CameraMixin {
    /**
     * Modifies an argument of setupTerrain to make the world render as if the player is in spectator when clipping.
     */
    @Inject(
        method = "extractRenderState",
        at = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/renderer/state/level/CameraRenderState;smartCull:Z",
                ordinal = 0,
                shift = At.Shift.AFTER
        )
    )
    private void onRenderReplaceSpectator(CameraRenderState cameraState, float cameraEntityPartialTicks, CallbackInfo ci) {
        if (NoClipManager.INSTANCE.isClipping()) {
            cameraState.smartCull = false;
        }
    }
}
