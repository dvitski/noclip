package dev.andante.noclip.api.client;

public interface IPlayerClippingState {
    default void setIsClipping(boolean value) {}
    default boolean getIsClipping() {
        return false;
    }
}
