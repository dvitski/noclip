package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.andante.noclip.api.client.IPlayerClippingState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

// TODO: This mixin should take get the entity, and subsequently the ClippingEntity, pass the isClipping state onto PlayerRendererState.
// Another Mixin then hooks into renderShadow which receives the state and returns based on the state.
// Figure out how to access the local variable for entityRenderState and overwrite it. See FogModifierMixin.
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderManager.class)
public class EntityRenderDispatcherMixin {
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitShadowPieces(Lnet/minecraft/client/util/math/MatrixStack;FLjava/util/List;)V"))
    private static <S extends EntityRenderState> boolean onRenderShadow(OrderedRenderCommandQueue instance, MatrixStack matrices, float v, List<EntityRenderState.ShadowPiece> list, @Local(argsOnly = true) S renderState) {
        if (renderState instanceof IPlayerClippingState state) {
            return !state.getIsClipping();
        }

        return true;
    }
}
