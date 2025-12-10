package dev.andante.noclip.mixin.client;

import dev.andante.noclip.api.client.IPlayerClippingState;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity> {
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
            at = @At("HEAD")
    )
    private void noclip$storeClippingState(AvatarlikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (!(playerEntityRenderState instanceof IPlayerClippingState state)) {
            return;
        }

        ClippingEntity clippingPlayer = ClippingEntity.cast(playerLikeEntity);
        state.setIsClipping(clippingPlayer.isClipping());
    }
}
