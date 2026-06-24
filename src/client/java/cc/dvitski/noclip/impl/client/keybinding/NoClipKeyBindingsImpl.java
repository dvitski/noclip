package cc.dvitski.noclip.impl.client.keybinding;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.NoClipManager;
import cc.dvitski.noclip.api.client.config.KeyBehavior;
import cc.dvitski.noclip.api.client.config.NoClipConfig;
import cc.dvitski.noclip.api.client.config.NoClipConfig.AllowIn;
import cc.dvitski.noclip.api.client.keybinding.NoClipKeyBindings;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class NoClipKeyBindingsImpl implements NoClipKeyBindings {
    public static final String RESET_FLIGHT_SPEED_KEY = "text." + NoClip.MOD_ID + ".flight_speed.reset";
    public static final String TOGGLE_NOCLIP_ON_KEY = "text." + NoClip.MOD_ID + ".toggle_noclip.on";
    public static final String TOGGLE_NOCLIP_OFF_KEY = "text." + NoClip.MOD_ID + ".toggle_noclip.off";
    private static List<GameType> allowedModes = createAllowedModes(NoClipClient.CONFIG.getConfig().allowIn);

    public static void onEndClientTick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) return;

        NoClipConfig config = NoClipClient.getConfig();
        NoClipConfig.Flight flightConfig = config.flight;
        Abilities abilities = player.getAbilities();

        /* Clipping */

        NoClipManager clipping = NoClipManager.INSTANCE;
        if (clipping.canClip()) {
            GameType mode = client.gameMode.getPlayerMode();
            boolean prev = clipping.isClipping();
            boolean curr = ACTIVATE_NOCLIP.isDown() && allowedModes.contains(mode);
            if (prev != curr) {
                clipping.setClipping(curr);
                clipping.updateClipping();

                if (curr) {
                    if (flightConfig.enableFlightOnClip) {
                        abilities.flying = true;
                    }
                } else {
                    if (flightConfig.speedScrolling.resetSpeedOnClipOrFlight) {
                        Abilities def = new Abilities();
                        abilities.setFlyingSpeed(def.getFlyingSpeed());
                    }
                }

                mode.updatePlayerAbilities(abilities);
                player.onUpdateAbilities();

                if (config.keyBehaviors.noClip == KeyBehavior.TOGGLE) {
                    String key = curr ? TOGGLE_NOCLIP_ON_KEY : TOGGLE_NOCLIP_OFF_KEY;
                    player.sendOverlayMessage(Component.translatable(key));
                }
            }
        }

        /* Snappy Flight */

        if (abilities.flying) {
            if (flightConfig.snappyFlight.enabled && (!flightConfig.snappyFlight.onlyInNoClip || clipping.isClipping())) {
                player.setDeltaMovement(Vec3.ZERO);

                int sideways = 0;
                int upward = 0;
                int forward = 0;

                Options options = client.options;
                if (options.keyLeft.isDown()) sideways += 1;
                if (options.keyRight.isDown()) sideways -= 1;
                if (options.keyJump.isDown()) upward += 1;
                if (options.keyShift.isDown()) upward -= 1;
                if (options.keyUp.isDown()) forward += 1;
                if (options.keyDown.isDown()) forward -= 1;

                player.travel(new Vec3(sideways, upward, forward));
                player.setDeltaMovement(player.getDeltaMovement().scale(7.0D));
                player.push(0.0D, upward * abilities.getFlyingSpeed() * 2, 0.0D);
            }
        }

        /* Flight Speed */

        if (RESET_FLIGHT_SPEED.consumeClick()) {
            Abilities def = new Abilities();
            abilities.setFlyingSpeed(def.getFlyingSpeed());
            player.sendOverlayMessage(Component.translatable(RESET_FLIGHT_SPEED_KEY, 1.0f).setStyle(NoClipClient.getTextStyle()));
        }
    }

    public static InteractionResult onConfigSave(ConfigHolder<NoClipConfig> holder, NoClipConfig config) {
        allowedModes = createAllowedModes(config.allowIn);
        return InteractionResult.PASS;
    }

    private static List<GameType> createAllowedModes(AllowIn allow) {
        List<GameType> list = new ArrayList<>();
        if (allow.creative) list.add(GameType.CREATIVE);
        if (allow.survival) list.add(GameType.SURVIVAL);
        if (allow.adventure) list.add(GameType.ADVENTURE);
        if (allow.spectator) list.add(GameType.SPECTATOR);
        return list;
    }
}
