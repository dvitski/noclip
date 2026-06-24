package cc.dvitski.noclip.impl.client.keybinding;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class ToggleNoClipKeyMapping extends ToggleKeyMapping {
    public static final String ACTIONBAR_KEY = "text." + NoClip.MOD_ID + ".server_noclip_not_present";

    public ToggleNoClipKeyMapping(String id, int code, Category category, BooleanSupplier toggleGetter, boolean restore) {
        super(id, code, category, toggleGetter, restore);
    }

    @Override
    public void setDown(boolean pressed) {
        super.setDown(pressed);

        if (pressed) {
            NoClipManager clipping = NoClipManager.INSTANCE;
            if (!clipping.canClip()) {
                Minecraft client = Minecraft.getInstance();
                client.player.sendOverlayMessage(Component.translatable(ACTIONBAR_KEY).withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    protected void release() {
    }
}
