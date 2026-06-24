package dev.andante.noclip.impl.client.keybinding;

import dev.andante.noclip.api.NoClip;
import dev.andante.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class ToggleNoClipKeyBinding extends ToggleKeyMapping {
    public static final String ACTIONBAR_KEY = "text." + NoClip.MOD_ID + ".server_noclip_not_present";

    public ToggleNoClipKeyBinding(String id, int code, Category category, BooleanSupplier toggleGetter, boolean restore) {
        super(id, code, category, toggleGetter, restore);
    }

    @Override
    public void setDown(boolean pressed) {
        super.setDown(pressed);

        if (pressed) {
            NoClipManager clipping = NoClipManager.INSTANCE;
            if (!clipping.canClip()) {
                Minecraft client = Minecraft.getInstance();
                client.player.displayClientMessage(Component.translatable(ACTIONBAR_KEY).withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @Override
    protected void release() {
    }
}
