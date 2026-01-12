package dev.andante.noclip.impl;

public interface ClippingEntity {
    boolean canClip();

    boolean isClipping();

    void setClipping(boolean clipping);

    void setLastCanClip(boolean lastCanClip);

    boolean isClippingInsideWall();

    static ClippingEntity cast(Object player) {
        return (ClippingEntity) player;
    }
}
