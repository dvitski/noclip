package cc.dvitski.noclip.impl.client;

import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.NoClipManager;
import cc.dvitski.noclip.api.client.command.NoClipClientCommand;
import cc.dvitski.noclip.api.client.keybinding.NoClipKeyBindings;
import cc.dvitski.noclip.api.client.render.NoClipHud;
import cc.dvitski.noclip.impl.ClippingUpdatePacket;
import cc.dvitski.noclip.impl.client.keybinding.NoClipKeyBindingsImpl;
import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public final class NoClipClientImpl implements NoClipClient, ClientModInitializer {
    public static NoClipHud NOCLIP_HUD_RENDERER = new NoClipHud();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}-CLIENT", MOD_NAME);

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "hud"), NOCLIP_HUD_RENDERER);

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
