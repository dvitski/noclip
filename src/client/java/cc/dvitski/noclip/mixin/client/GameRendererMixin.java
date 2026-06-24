package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(value = GameRenderer.class, priority = 999)
public class GameRendererMixin {
    @Shadow @Final
    private Minecraft minecraft;

    /**
     * Fixes dark hand lighting when clipping and inside a block.
     */
    @ModifyArg(
        method = "renderItemInHand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"
        ),
        index = 4
    )
    private int onRenderHandFixLight(int light) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this.minecraft.player);
        return clippingPlayer.isClippingInsideWall() ? 0xFFFFFF : light;
    }
}
