package cc.dvitski.noclip.impl;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * An interface used to attach a {@link Player} to their {@link Abilities}.
 */
public interface PlayerAbilitiesAccess {
    Optional<Player> getPlayer();
    void setPlayer(Player player);
}
