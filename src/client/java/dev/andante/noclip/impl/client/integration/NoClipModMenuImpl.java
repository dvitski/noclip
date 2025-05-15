package dev.andante.noclip.impl.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.andante.noclip.api.client.NoClipClient;
import dev.andante.noclip.api.client.config.NoClipConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NoClipModMenuImpl implements NoClipClient, ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return NoClipConfig::createScreen;
    }
}
