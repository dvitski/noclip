package cc.dvitski.noclip.impl.client.integration;

import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.config.NoClipConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NoClipModMenuImpl implements NoClipClient, ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return NoClipConfig::createScreen;
    }
}
