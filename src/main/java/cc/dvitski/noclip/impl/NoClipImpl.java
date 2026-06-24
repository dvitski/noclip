package cc.dvitski.noclip.impl;

import cc.dvitski.noclip.api.NoClip;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;

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
        ServerPlayerEvents.AFTER_RESPAWN.register(this::afterRespawn);
    }

    /**
     * Updates the client player on server join.
     */
    private void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(handler.player);
        ServerPlayNetworking.send(handler.player, new ClippingUpdatePacket(clippingPlayer.isClipping(), clippingPlayer.canClip()));
    }

    /**
     * Receives a clipping update from the client.
     */
    private void receiveUpdate(ClippingUpdatePacket packet, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        boolean clipping = packet.clipping();
        ClippingEntity clippingPlayer = ClippingEntity.cast(player);
        clippingPlayer.setClipping(clipping);

        GameType mode = player.gameMode.getGameModeForPlayer();
        mode.updatePlayerAbilities(player.getAbilities());
    }

    /**
     * Copies data from a dead player to a new player.
     */
    private void copyFrom(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        ClippingEntity clippingOldPlayer = ClippingEntity.cast(oldPlayer);
        ClippingEntity clippingNewPlayer = ClippingEntity.cast(newPlayer);
        clippingNewPlayer.setClipping(clippingOldPlayer.isClipping());
    }

    private void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        ClippingEntity clippingNewPlayer = ClippingEntity.cast(newPlayer);
        if (clippingNewPlayer.isClipping()) {
            ServerPlayNetworking.send(newPlayer, new ClippingUpdatePacket(false, false));
            ServerPlayNetworking.send(newPlayer, new ClippingUpdatePacket(true, true));
        }
    }
}
