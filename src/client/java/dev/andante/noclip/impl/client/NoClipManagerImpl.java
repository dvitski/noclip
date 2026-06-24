package dev.andante.noclip.impl.client;

import dev.andante.noclip.api.client.NoClipClient;
import dev.andante.noclip.api.client.NoClipManager;
import dev.andante.noclip.api.client.keybinding.NoClipKeyBindings;
import dev.andante.noclip.impl.ClippingEntity;
import dev.andante.noclip.impl.ClippingUpdatePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

@Environment(EnvType.CLIENT)
public final class NoClipManagerImpl implements NoClipManager {
    private boolean clipping;
    private boolean canClip;

    @Override
    public boolean isClipping() {
        return this.canClip && this.clipping;
    }

    @Override
    public boolean setClipping(boolean clipping) {
        this.clipping = clipping;
        return this.clipping;
    }

    @Override
    public boolean canClip() {
        return this.canClip;
    }

    @Override
    public void setCanClip(boolean canClip) {
        this.canClip = canClip;
        if (!this.canClip) this.updateClipping();
    }

    @Override
    public void updateClipping(boolean sendToServer) {
        boolean clipping = this.isClipping();

        // update client player
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(client.player);
            clippingPlayer.setClipping(clipping);
        }

        if (sendToServer) {
            ClientPlayNetworking.send(new ClippingUpdatePacket(clipping, false));
        }
    }

    /* Events */

    /**
     * Receives a clipping update from the server.
     */
    public static void onServerUpdate(ClippingUpdatePacket packet, ClientPlayNetworking.Context context) {
        NoClipManager clipManager = NoClipManager.INSTANCE;
        clipManager.setCanClip(packet.canClip());

        if (NoClipClient.getConfig().keyBehaviors.noClip.toggles()) {
            boolean clipping = packet.clipping();

            clipManager.setClipping(clipping);
            if (NoClipKeyBindings.ACTIVATE_NOCLIP.isDown() != clipping) NoClipKeyBindings.ACTIVATE_NOCLIP.setDown(true);
        }

        clipManager.updateClipping();
    }

    /**
     * Resets clipping value upon disconnecting a server.
     */
    public static void onDisconnect(ClientPacketListener handler, Minecraft client) {
        NoClipManager clipManager = NoClipManager.INSTANCE;
        clipManager.setCanClip(false);
        clipManager.setClipping(false);
        NoClipKeyBindings.ACTIVATE_NOCLIP.setDown(false);
    }
}
