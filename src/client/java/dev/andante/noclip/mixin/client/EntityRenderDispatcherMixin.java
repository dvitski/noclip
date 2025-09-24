package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.andante.noclip.api.client.IPlayerClippingState;
import dev.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: This mixin should take get the entity, and subsequently the ClippingEntity, pass the isClipping state onto PlayerRendererState.
// Another Mixin then hooks into renderShadow which receives the state and returns based on the state.
// Figure out how to access the local variable for entityRenderState and overwrite it. See FogModifierMixin.
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    /**
     * Cancels shadow rendering if clipping.
     */
    @Inject(
        method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
        at = @At(
            target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            value = "INVOKE"
        )
    )
    private <E extends Entity, S extends EntityRenderState> void onRender(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo ci, @Local EntityRenderState entityRenderState) {
        if (entity instanceof PlayerEntity player) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (entityRenderState instanceof IPlayerClippingState state) {
                state.setIsClipping(clippingPlayer.isClipping());
            }
        }
    }

    @WrapMethod(method = "renderShadow")
    private static void onRenderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius, Operation<Void> original) {
        if (renderState instanceof IPlayerClippingState state) {
            if(state.getIsClipping()) {
                return;
            }
        }

        original.call(matrices, vertexConsumers, renderState, opacity, world, radius);
    }
}
