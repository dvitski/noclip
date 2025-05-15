package me.andante.noclip.impl.client.keybinding;

import me.andante.noclip.api.NoClip;
import me.andante.noclip.api.client.NoClipClient;
import me.andante.noclip.api.client.NoClipManager;
import me.andante.noclip.api.client.config.NoClipConfig;
import me.andante.noclip.api.client.config.NoClipConfig.AllowIn;
import me.andante.noclip.api.client.keybinding.NoClipKeyBindings;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class NoClipKeyBindingsImpl implements NoClipKeyBindings {
    public static final String RESET_FLIGHT_SPEED_KEY = "text." + NoClip.MOD_ID + ".flight_speed.reset";
    private static List<GameMode> allowedModes = createAllowedModes(NoClipClient.CONFIG.getConfig().allowIn);

    public static void onEndClientTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        NoClipConfig config = NoClipClient.getConfig();
        NoClipConfig.Flight flightConfig = config.flight;
        PlayerAbilities abilities = player.getAbilities();

        /* Clipping */

        NoClipManager clipping = NoClipManager.INSTANCE;
        if (clipping.canClip()) {
            GameMode mode = client.interactionManager.getCurrentGameMode();
            boolean prev = clipping.isClipping();
            boolean curr = ACTIVATE_NOCLIP.isPressed() && allowedModes.contains(mode);
            if (prev != curr) {
                clipping.setClipping(curr);
                clipping.updateClipping();

                if (curr) {
                    if (flightConfig.enableFlightOnClip) abilities.flying = true;
                } else {
                    if (flightConfig.speedScrolling.resetSpeedOnClipOrFlight) {
                        PlayerAbilities def = new PlayerAbilities();
                        abilities.setFlySpeed(def.getFlySpeed());
                    }
                }

                mode.setAbilities(abilities);
            }
        }

        /* Snappy Flight */

        if (abilities.flying) {
            if (flightConfig.snappyFlight.enabled && (!flightConfig.snappyFlight.onlyInNoClip || clipping.isClipping())) {
                player.setVelocity(Vec3d.ZERO);

                int sideways = 0;
                int upward = 0;
                int forward = 0;

                GameOptions options = client.options;
                if (options.leftKey.isPressed()) sideways += 1;
                if (options.rightKey.isPressed()) sideways -= 1;
                if (options.jumpKey.isPressed()) upward += 1;
                if (options.sneakKey.isPressed()) upward -= 1;
                if (options.forwardKey.isPressed()) forward += 1;
                if (options.backKey.isPressed()) forward -= 1;

                player.travel(new Vec3d(sideways, upward, forward));
                player.setVelocity(player.getVelocity().multiply(7.0D));
                player.addVelocity(0.0D, upward * abilities.getFlySpeed() * 2, 0.0D);
            }
        }

        /* Flight Speed */

        if (RESET_FLIGHT_SPEED.wasPressed()) {
            PlayerAbilities def = new PlayerAbilities();
            abilities.setFlySpeed(def.getFlySpeed());
            player.sendMessage(Text.translatable(RESET_FLIGHT_SPEED_KEY, 1.0f).setStyle(NoClipClient.getTextStyle()), true);
        }
    }

    public static ActionResult onConfigSave(ConfigHolder<NoClipConfig> holder, NoClipConfig config) {
        allowedModes = createAllowedModes(config.allowIn);
        return ActionResult.PASS;
    }

    private static List<GameMode> createAllowedModes(AllowIn allow) {
        List<GameMode> list = new ArrayList<>();
        if (allow.creative) list.add(GameMode.CREATIVE);
        if (allow.survival) list.add(GameMode.SURVIVAL);
        if (allow.adventure) list.add(GameMode.ADVENTURE);
        if (allow.spectator) list.add(GameMode.SPECTATOR);
        return list;
    }
}
