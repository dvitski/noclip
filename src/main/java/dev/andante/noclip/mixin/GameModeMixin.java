package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import dev.andante.noclip.impl.PlayerAbilitiesAccess;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMode.class)
public abstract class GameModeMixin {
    @Shadow public abstract void setAbilities(PlayerAbilities abilities);

    /**
     * Overrides abilities modification if clipping.
     */
    @Inject(method = "setAbilities", at = @At("HEAD"), cancellable = true)
    private void onSetAbilities(PlayerAbilities abilities, CallbackInfo ci) {
        PlayerAbilitiesAccess access = (PlayerAbilitiesAccess) abilities;
        access.getPlayer().ifPresent(player -> {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (clippingPlayer.isClipping()) {
                PlayerAbilities def = new PlayerAbilities();
                this.setAbilities(def);

                abilities.allowFlying = true;
                abilities.invulnerable = true;
                abilities.creativeMode = def.creativeMode;
                abilities.allowModifyWorld = def.allowModifyWorld;

                ci.cancel();
            }
        });
    }
}
