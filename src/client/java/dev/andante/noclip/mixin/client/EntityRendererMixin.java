package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.andante.noclip.api.client.IPlayerClippingState;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    /**
     * Cancels shadow rendering if clipping.
     */
    @Inject(method = "getAndUpdateRenderState", at = @At("RETURN"))
    private void onRender(T entity, float tickProgress, CallbackInfoReturnable<S> cir, @Local EntityRenderState entityRenderState) {
        if (entity instanceof PlayerEntity player) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (entityRenderState instanceof IPlayerClippingState state) {
                state.setIsClipping(clippingPlayer.isClipping());
            }
        }
    }
}
