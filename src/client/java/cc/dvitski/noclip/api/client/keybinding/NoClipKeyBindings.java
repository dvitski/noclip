package cc.dvitski.noclip.api.client.keybinding;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.config.KeyBehavior;
import cc.dvitski.noclip.api.client.config.NoClipConfig;
import cc.dvitski.noclip.impl.client.keybinding.ToggleNoClipKeyBinding;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface NoClipKeyBindings {
    KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(NoClip.MOD_ID, "category"));

    ToggleNoClipKeyBinding ACTIVATE_NOCLIP = (ToggleNoClipKeyBinding) KeyBindingHelper.registerKeyBinding(new ToggleNoClipKeyBinding(
            "key." + NoClip.MOD_ID + ".activate_noclip",
            InputConstants.KEY_GRAVE, CATEGORY, toggles(behaviors -> behaviors.noClip),
            true
    ));

    KeyMapping ACTIVATE_FLIGHT_SPEED_SCROLL = KeyBindingHelper.registerKeyBinding(new ToggleKeyMapping(
            "key." + NoClip.MOD_ID + ".activate_flight_speed_scroll",
            InputConstants.UNKNOWN.getValue(), CATEGORY, toggles(behaviors -> behaviors.flightSpeedActivation),
            true
    ));

    KeyMapping RESET_FLIGHT_SPEED = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key." + NoClip.MOD_ID + ".reset_flight_speed", InputConstants.UNKNOWN.getValue(), CATEGORY
    ));

    private static BooleanSupplier toggles(Function<NoClipConfig.KeyBehaviors, KeyBehavior> getter) {
        return () -> getter.apply(NoClipClient.getConfig().keyBehaviors).toggles();
    }
}
