package me.andante.noclip.mixin.client;

import me.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BubbleColumnSoundPlayer.class)
public class BubbleColumnSoundPlayerMixin {
    @Shadow @Final private ClientPlayerEntity player;
    @Shadow private boolean hasPlayedForCurrentColumn;
    @Shadow private boolean firstTick;

    /**
     * Cancels bubble column effects when clipping.
     */
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void onTick(CallbackInfo ci) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this.player);
        if (clippingPlayer.isClipping()) {
            this.hasPlayedForCurrentColumn = true;
            this.firstTick = false;
            ci.cancel();
        }
    }
}
