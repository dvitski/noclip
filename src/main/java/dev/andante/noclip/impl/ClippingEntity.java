package dev.andante.noclip.impl;

import net.minecraft.entity.player.PlayerEntity;

public interface ClippingEntity {
    boolean isClipping();
    void setClipping(boolean clipping);

    boolean isClippingInsideWall();

    static ClippingEntity cast(PlayerEntity player) {
        return (ClippingEntity) player;
    }
}
