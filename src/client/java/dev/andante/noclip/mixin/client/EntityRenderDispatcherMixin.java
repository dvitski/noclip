package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.andante.noclip.api.client.IPlayerClippingState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

// TODO: This mixin should take get the entity, and subsequently the ClippingEntity, pass the isClipping state onto PlayerRendererState.
// Another Mixin then hooks into renderShadow which receives the state and returns based on the state.
// Figure out how to access the local variable for entityRenderState and overwrite it. See FogModifierMixin.
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @WrapWithCondition(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShadow(Lcom/mojang/blaze3d/vertex/PoseStack;FLjava/util/List;)V"))
    private static <S extends EntityRenderState> boolean onRenderShadow(SubmitNodeCollector instance, PoseStack matrices, float v, List<EntityRenderState.ShadowPiece> list, @Local(argsOnly = true) S renderState) {
        if (renderState instanceof IPlayerClippingState state) {
            return !state.getIsClipping();
        }

        return true;
    }
}
