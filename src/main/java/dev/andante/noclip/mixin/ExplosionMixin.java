package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Explosion.class)
public class ExplosionMixin {
    /**
     * Prevents clipping players from being added to the knockback map.
     */
    @ModifyArg(
        method = "collectBlocksAndDamageEntities",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        index = 0
    )
    private <K> K onPlayerKnockbackPut(K key) {
        PlayerEntity player = (PlayerEntity) key;
        ClippingEntity clippingPlayer = ClippingEntity.cast(player);
        return clippingPlayer.isClipping() ? null : key;
    }
}
