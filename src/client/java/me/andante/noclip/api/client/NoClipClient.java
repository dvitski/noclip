package me.andante.noclip.api.client;

import me.andante.noclip.api.NoClip;
import me.andante.noclip.api.client.config.NoClipConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface NoClipClient extends NoClip {
    ConfigHolder<NoClipConfig> CONFIG = NoClipConfig.initialize();
    Supplier<Style> TEXT_STYLE = () -> Style.EMPTY.withColor(getConfig().display.textColor);

    static NoClipConfig getConfig() {
        return CONFIG.getConfig();
    }

    static Style getTextStyle() {
        return TEXT_STYLE.get();
    }
}
