package me.andante.noclip.mixin.client;

import me.andante.noclip.api.client.NoClipClient;
import me.andante.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    /**
     * Forces the player to fly if they are clipping.
     */
    @Inject(method = "isFlyingLocked", at = @At("HEAD"), cancellable = true)
    private void onIsFlyingLocked(CallbackInfoReturnable<Boolean> cir) {
        if (NoClipManager.INSTANCE.isClipping() && NoClipClient.getConfig().flight.flyingLocked) cir.setReturnValue(true);
    }
}
