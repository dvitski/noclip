package dev.andante.noclip.mixin.client;

import dev.andante.noclip.api.NoClip;
import dev.andante.noclip.api.client.NoClipClient;
import dev.andante.noclip.api.client.config.NoClipConfig;
import dev.andante.noclip.api.client.keybinding.NoClipKeyBindings;
import dev.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public class MouseMixin {
    @Unique private static final String SET_FLIGHT_SPEED_KEY = "text." + NoClip.MOD_ID + ".flight_speed.set";
    @Shadow @Final private MinecraftClient client;

    /**
     * Enables scrolling to modify fly speed when in not in spectator and a key binding held or toggled.
     */
    @Inject(
        method = "onMouseScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci, boolean discreteMouseScroll, double sensitivity, double e, double f, int deltaHorizontal, int deltaVertical, int delta) {
        if (!NoClipKeyBindings.ACTIVATE_FLIGHT_SPEED_SCROLL.isPressed()) return;

        NoClipConfig config = NoClipClient.getConfig();
        ClientPlayerEntity player = this.client.player;
        ClippingEntity clippingPlayer = ClippingEntity.cast(player);

        if (config.flight.speedScrolling.onlyInNoClip && !clippingPlayer.isClipping()) return;

        PlayerAbilities abilities = player.getAbilities();

        if (!abilities.flying) return;

        float old = abilities.getFlySpeed();

        float speed = MathHelper.clamp(old + (delta * 0.005f), 0.0f, config.flight.speedScrolling.maxSpeed / 20f);
        abilities.setFlySpeed(speed);

        if (old != speed && NoClipClient.getConfig().display.showSpeedUpdatesOnActionBar) {
            PlayerAbilities def = new PlayerAbilities();
            player.sendMessage(Text.translatable(SET_FLIGHT_SPEED_KEY, String.format("%.1f", speed / def.getFlySpeed())).setStyle(NoClipClient.getTextStyle()), true);
        }

        ci.cancel();
    }
}
