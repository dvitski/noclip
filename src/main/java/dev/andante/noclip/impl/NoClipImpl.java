package dev.andante.noclip.impl;

import dev.andante.noclip.api.NoClip;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public final class NoClipImpl implements NoClip, ModInitializer {
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {}", MOD_NAME);

        // networking
        PayloadTypeRegistry.playC2S().register(ClippingUpdatePacket.ID, ClippingUpdatePacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ClippingUpdatePacket.ID, ClippingUpdatePacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ClippingUpdatePacket.ID, this::receiveUpdate);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);

        // death
        ServerPlayerEvents.COPY_FROM.register(this::copyFrom);
    }

    /**
     * Updates the client player on server join.
     */
    private void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(handler.player);
        ServerPlayNetworking.send(handler.player, new ClippingUpdatePacket(clippingPlayer.isClipping()));
    }

    /**
     * Receives a clipping update from the client.
     */
    private void receiveUpdate(ClippingUpdatePacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        boolean clipping = packet.clipping();
        ClippingEntity clippingPlayer = ClippingEntity.cast(player);
        clippingPlayer.setClipping(clipping);

        GameMode mode = player.interactionManager.getGameMode();
        mode.setAbilities(player.getAbilities());
    }

    /**
     * Copies data from a dead player to a new player.
     */
    private void copyFrom(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        ClippingEntity clippingOldPlayer = ClippingEntity.cast(oldPlayer);
        ClippingEntity clippingNewPlayer = ClippingEntity.cast(newPlayer);
        clippingNewPlayer.setClipping(clippingOldPlayer.isClipping());
    }
}
