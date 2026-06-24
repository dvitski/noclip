package cc.dvitski.noclip.mixin;

import cc.dvitski.noclip.impl.PlayerAbilitiesAccess;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(Abilities.class)
public class AbilitiesMixin implements PlayerAbilitiesAccess {
    @Unique private Player player;

    @Unique
    @Override
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    @Unique
    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }
}
