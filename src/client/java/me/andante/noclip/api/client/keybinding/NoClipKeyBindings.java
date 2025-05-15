package me.andante.noclip.api.client.keybinding;

import me.andante.noclip.api.NoClip;
import me.andante.noclip.api.client.NoClipClient;
import me.andante.noclip.api.client.config.KeyBehavior;
import me.andante.noclip.api.client.config.NoClipConfig;
import me.andante.noclip.impl.client.keybinding.ToggleNoClipKeyBinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface NoClipKeyBindings {
    String CATEGORY = "key.category." + NoClip.MOD_ID;

    ToggleNoClipKeyBinding ACTIVATE_NOCLIP = (ToggleNoClipKeyBinding) KeyBindingHelper.registerKeyBinding(new ToggleNoClipKeyBinding(
        "key." + NoClip.MOD_ID + ".activate_noclip",
        InputUtil.GLFW_KEY_GRAVE_ACCENT, CATEGORY, toggles(behaviors -> behaviors.noClip)
    ));

    KeyBinding ACTIVATE_FLIGHT_SPEED_SCROLL = KeyBindingHelper.registerKeyBinding(new StickyKeyBinding(
        "key." + NoClip.MOD_ID + ".activate_flight_speed_scroll",
        InputUtil.UNKNOWN_KEY.getCode(), CATEGORY, toggles(behaviors -> behaviors.flightSpeedActivation)
    ));

    KeyBinding RESET_FLIGHT_SPEED = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key." + NoClip.MOD_ID + ".reset_flight_speed", InputUtil.UNKNOWN_KEY.getCode(), CATEGORY
    ));

    private static BooleanSupplier toggles(Function<NoClipConfig.KeyBehaviors, KeyBehavior> getter) {
        return () -> getter.apply(NoClipClient.getConfig().keyBehaviors).toggles();
    }
}
