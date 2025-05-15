package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.PlayerAbilitiesAccess;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(PlayerAbilities.class)
public class PlayerAbilitiesMixin implements PlayerAbilitiesAccess {
    @Unique private PlayerEntity player;

    @Unique
    @Override
    public Optional<PlayerEntity> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    @Unique
    @Override
    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }
}
