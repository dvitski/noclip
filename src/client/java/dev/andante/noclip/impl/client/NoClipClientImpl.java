package dev.andante.noclip.impl.client;

import com.google.common.reflect.Reflection;
import dev.andante.noclip.api.client.NoClipClient;
import dev.andante.noclip.api.client.NoClipManager;
import dev.andante.noclip.api.client.command.NoClipClientCommand;
import dev.andante.noclip.api.client.keybinding.NoClipKeyBindings;
import dev.andante.noclip.api.client.render.NoClipHudLayer;
import dev.andante.noclip.impl.ClippingUpdatePacket;
import dev.andante.noclip.impl.client.keybinding.NoClipKeyBindingsImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public final class NoClipClientImpl implements NoClipClient, ClientModInitializer {
    public static final NoClipHudLayer NOCLIP_HUD_RENDERER = new NoClipHudLayer();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}-CLIENT", MOD_NAME);

        Reflection.initialize(NoClipClient.class, NoClipKeyBindings.class, NoClipManager.class);

        // networking
        ClientPlayNetworking.registerGlobalReceiver(ClippingUpdatePacket.ID, NoClipManagerImpl::onServerUpdate);
        ClientPlayConnectionEvents.DISCONNECT.register(NoClipManagerImpl::onDisconnect);

        // keybinding
        ClientTickEvents.END_CLIENT_TICK.register(NoClipKeyBindingsImpl::onEndClientTick);

        // command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
            NoClipClientCommand.register(dispatcher);
        });
    }
}
