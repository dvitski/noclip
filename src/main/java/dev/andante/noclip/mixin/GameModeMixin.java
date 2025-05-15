package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import dev.andante.noclip.impl.PlayerAbilitiesAccess;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(GameMode.class)
public abstract class GameModeMixin {
    @Shadow public abstract void setAbilities(PlayerAbilities abilities);

    /**
     * Overrides abilities modification if clipping.
     */
    @WrapMethod(method = "setAbilities")
    private void onSetAbilities(PlayerAbilities abilities, Operation<Void> original) {
        PlayerAbilitiesAccess access = (PlayerAbilitiesAccess) abilities;
        Optional<PlayerEntity> maybePlayer = access.getPlayer();
        if (maybePlayer.isPresent()) {
            PlayerEntity player = maybePlayer.get();
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (clippingPlayer.isClipping()) {
                PlayerAbilities def = new PlayerAbilities();
                this.setAbilities(def);

                abilities.allowFlying = true;
                abilities.invulnerable = true;
                abilities.creativeMode = def.creativeMode;
                abilities.allowModifyWorld = def.allowModifyWorld;

                return;
            }
        }

        original.call(abilities);
    }
}
