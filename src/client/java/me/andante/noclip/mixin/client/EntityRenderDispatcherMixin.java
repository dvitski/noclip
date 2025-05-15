package me.andante.noclip.mixin.client;

import me.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    /**
     * Cancels shadow rendering if clipping.
     */
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void onRenderShadow(MatrixStack matrices, VertexConsumerProvider vertices, Entity entity, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (clippingPlayer.isClipping()) ci.cancel();
        }
    }
}
