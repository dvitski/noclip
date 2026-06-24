package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BubbleColumnAmbientSoundHandler.class)
public class BubbleColumnAmbientSoundHandlerMixin {
    @Shadow @Final private LocalPlayer player;
    @Shadow private boolean wasInBubbleColumn;
    @Shadow private boolean firstTick;

    /**
     * Cancels bubble column effects when clipping.
     */
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void onTick(CallbackInfo ci) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this.player);
        if (clippingPlayer.isClipping()) {
            this.wasInBubbleColumn = true;
            this.firstTick = false;
            ci.cancel();
        }
    }
}
