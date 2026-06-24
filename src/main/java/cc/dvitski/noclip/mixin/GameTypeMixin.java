package cc.dvitski.noclip.mixin;

import cc.dvitski.noclip.impl.ClippingEntity;
import cc.dvitski.noclip.impl.PlayerAbilitiesAccess;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(GameType.class)
public abstract class GameTypeMixin {
    @Shadow public abstract void updatePlayerAbilities(Abilities abilities);

    /**
     * Overrides abilities modification if clipping.
     */
    @WrapMethod(method = "updatePlayerAbilities")
    private void onSetAbilities(Abilities abilities, Operation<Void> original) {
        PlayerAbilitiesAccess access = (PlayerAbilitiesAccess) abilities;
        Optional<Player> maybePlayer = access.getPlayer();
        if (maybePlayer.isPresent()) {
            Player player = maybePlayer.get();
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (clippingPlayer.isClipping()) {
                Abilities def = new Abilities();
                this.updatePlayerAbilities(def);

                abilities.mayfly = true;
                abilities.invulnerable = true;
                abilities.instabuild = def.instabuild;
                abilities.mayBuild = def.mayBuild;

                return;
            }
        }

        original.call(abilities);
    }
}
