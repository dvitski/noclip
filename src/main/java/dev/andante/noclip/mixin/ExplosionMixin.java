package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerExplosion.class)
public class ExplosionMixin {
    /**
     * Prevents clipping players from being added to the knockback map.
     */
    @ModifyArg(
        method = "hurtEntities",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        index = 0
    )
    private <K> K onPlayerKnockbackPut(K key) {
        Player player = (Player) key;
        ClippingEntity clippingPlayer = ClippingEntity.cast(player);
        return clippingPlayer.isClipping() ? null : key;
    }
}
