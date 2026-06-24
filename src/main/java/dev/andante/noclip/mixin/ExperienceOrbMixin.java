package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
    @Shadow private Player followingPlayer;

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/ExperienceOrb;followingPlayer:Lnet/minecraft/world/entity/player/Player;",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void onTick(CallbackInfo ci) {
        if (this.followingPlayer instanceof ClippingEntity entity && entity.isClipping()) {
            this.followingPlayer = null;
        }
    }
}
