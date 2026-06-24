package cc.dvitski.noclip.api.client;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.config.NoClipConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Style;

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
