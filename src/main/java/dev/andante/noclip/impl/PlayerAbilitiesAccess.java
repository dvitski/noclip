package dev.andante.noclip.impl;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

/**
 * An interface used to attach a {@link PlayerEntity} to their {@link PlayerAbilities}.
 */
public interface PlayerAbilitiesAccess {
    Optional<PlayerEntity> getPlayer();
    void setPlayer(PlayerEntity player);
}
