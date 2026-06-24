package cc.dvitski.noclip.api.client.keybinding;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.config.KeyBehavior;
import cc.dvitski.noclip.api.client.config.NoClipConfig;
import cc.dvitski.noclip.impl.client.keybinding.ToggleNoClipKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface NoClipKeyBindings {
    KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(NoClip.MOD_ID, "category"));

    ToggleNoClipKeyMapping ACTIVATE_NOCLIP = (ToggleNoClipKeyMapping) KeyMappingHelper.registerKeyMapping(new ToggleNoClipKeyMapping(
            "key." + NoClip.MOD_ID + ".activate_noclip",
            InputConstants.KEY_GRAVE, CATEGORY, toggles(behaviors -> behaviors.noClip),
            true
    ));

    KeyMapping ACTIVATE_FLIGHT_SPEED_SCROLL = KeyMappingHelper.registerKeyMapping(new ToggleKeyMapping(
            "key." + NoClip.MOD_ID + ".activate_flight_speed_scroll",
            InputConstants.UNKNOWN.getValue(), CATEGORY, toggles(behaviors -> behaviors.flightSpeedActivation),
            true
    ));

    KeyMapping RESET_FLIGHT_SPEED = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key." + NoClip.MOD_ID + ".reset_flight_speed", InputConstants.UNKNOWN.getValue(), CATEGORY
    ));

    private static BooleanSupplier toggles(Function<NoClipConfig.KeyBehaviors, KeyBehavior> getter) {
        return () -> getter.apply(NoClipClient.getConfig().keyBehaviors).toggles();
    }
}
