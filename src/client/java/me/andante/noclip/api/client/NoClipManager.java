package me.andante.noclip.api.client;

import me.andante.noclip.impl.client.NoClipManagerImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface NoClipManager {
    NoClipManager INSTANCE = new NoClipManagerImpl();

    /**
     * @return if the player is in a clipping state
     */
    boolean isClipping();

    /**
     * Sets the player's clipping state.
     * @return the new clipping value
     */
    boolean setClipping(boolean clipping);

    /**
     * Sets whether the client can clip.
     */
    void setCanClip(boolean canClip);

    /**
     * @return whether the client can clip
     */
    boolean canClip();

    /**
     * Updates the client's player and notifies the server of the current clipping state.
     */
    void updateClipping(boolean sendToServer);

    default void updateClipping() {
        updateClipping(true);
    }
}
