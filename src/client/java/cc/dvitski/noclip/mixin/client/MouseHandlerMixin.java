package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.config.NoClipConfig;
import cc.dvitski.noclip.api.client.keybinding.NoClipKeyBindings;
import cc.dvitski.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Abilities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Unique private static final String SET_FLIGHT_SPEED_KEY = "text." + NoClip.MOD_ID + ".flight_speed.set";
    @Shadow @Final private Minecraft minecraft;

    /**
     * Enables scrolling to modify fly speed when in not in spectator and a key binding held or toggled.
     */
    @Inject(
        method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"
        ),
        cancellable = true
    )
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (!NoClipKeyBindings.ACTIVATE_FLIGHT_SPEED_SCROLL.isDown()) return;

        NoClipConfig config = NoClipClient.getConfig();
        LocalPlayer player = this.minecraft.player;
        ClippingEntity clippingPlayer = ClippingEntity.cast(player);

        if (config.flight.speedScrolling.onlyInNoClip && !clippingPlayer.isClipping()) return;

        Abilities abilities = player.getAbilities();

        if (!abilities.flying) return;

        float old = abilities.getFlyingSpeed();

        float speed = Mth.clamp(old + ((float)vertical * 0.005f), 0.0f, config.flight.speedScrolling.maxSpeed / 20f);
        abilities.setFlyingSpeed(speed);

        if (old != speed && NoClipClient.getConfig().display.showSpeedUpdatesOnActionBar) {
            Abilities def = new Abilities();
            player.sendOverlayMessage(Component.translatable(SET_FLIGHT_SPEED_KEY, String.format("%.1f", speed / def.getFlyingSpeed())).setStyle(NoClipClient.getTextStyle()));
        }

        ci.cancel();
    }
}
