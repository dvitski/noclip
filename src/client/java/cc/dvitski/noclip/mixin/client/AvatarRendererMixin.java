package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.client.IPlayerClippingState;
import cc.dvitski.noclip.impl.ClippingEntity;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("HEAD")
    )
    private void noclip$storeClippingState(AvatarlikeEntity playerLikeEntity, AvatarRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (!(playerEntityRenderState instanceof IPlayerClippingState state)) {
            return;
        }

        ClippingEntity clippingPlayer = ClippingEntity.cast(playerLikeEntity);
        state.setIsClipping(clippingPlayer.isClipping());
    }
}
