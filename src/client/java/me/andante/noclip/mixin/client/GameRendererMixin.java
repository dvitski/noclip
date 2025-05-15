package me.andante.noclip.mixin.client;

import me.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(value = GameRenderer.class, priority = 999)
public class GameRendererMixin {
    @Shadow @Final MinecraftClient client;

    /**
     * Fixes dark hand lighting when clipping and inside a block.
     */
    @ModifyArg(
        method = "renderHand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
        ),
        index = 4
    )
    private int onRenderHandFixLight(int light) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this.client.player);
        return clippingPlayer.isClippingInsideWall() ? 0xFFFFFF : light;
    }
}
