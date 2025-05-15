package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
    @Shadow private PlayerEntity target;

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/ExperienceOrbEntity;target:Lnet/minecraft/entity/player/PlayerEntity;",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void onTick(CallbackInfo ci) {
        if (this.target instanceof ClippingEntity entity && entity.isClipping()) {
            this.target = null;
        }
    }
}
