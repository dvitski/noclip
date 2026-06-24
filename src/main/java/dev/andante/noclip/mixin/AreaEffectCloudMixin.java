package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.world.entity.AreaEffectCloud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AreaEffectCloud.class)
public class AreaEffectCloudMixin {
    /**
     * Prevents clipping players from being affected by AECs.
     */
    @ModifyArg(
    method = "serverTick",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"
        ),
        index = 0
    )
    private <K> K onAECContainsKey(K key) {
        if (key instanceof ClippingEntity clipping) {
            return clipping.isClipping() ? null : key;
        }

        return key;
    }
}
