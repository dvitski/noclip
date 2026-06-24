package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.client.IPlayerClippingState;
import cc.dvitski.noclip.impl.ClippingEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    /**
     * Cancels shadow rendering if clipping.
     */
    @Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("RETURN"))
    private void onRender(T entity, float tickProgress, CallbackInfoReturnable<S> cir, @Local EntityRenderState entityRenderState) {
        if (entity instanceof Player player) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (entityRenderState instanceof IPlayerClippingState state) {
                state.setIsClipping(clippingPlayer.isClipping());
            }
        }
    }
}
