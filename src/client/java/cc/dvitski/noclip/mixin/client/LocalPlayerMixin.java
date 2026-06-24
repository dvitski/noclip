package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.NoClipManager;
import cc.dvitski.noclip.impl.ClippingEntity;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow public ClientInput input;

    private LocalPlayerMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    /**
     * Updates player clipping value based on set/received client value.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(Minecraft client, ClientLevel world, ClientPacketListener handler, StatsCounter stats, ClientRecipeBook recipeBook, Input lastPlayerInput, boolean lastSprinting, CallbackInfo ci) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this);
        clippingPlayer.setClipping(NoClipManager.INSTANCE.isClipping());
    }

    /**
     * Cancels water submersion effects when clipping.
     */
    @Inject(
        method = "updateIsUnderwater",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/AbstractClientPlayer;updateIsUnderwater()Z",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void onUpdateWaterSubmersionState(CallbackInfoReturnable<Boolean> cir) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this);
        if (clippingPlayer.isClipping()) cir.setReturnValue(this.wasUnderwater);
    }

    /**
     * Prevents the player from having their sprinting stopped when clipping through water.
     */
    @Inject(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;setSprinting(Z)V",
            ordinal = 3,
            shift = At.Shift.AFTER
        )
    )
    private void preventStopSprinting(CallbackInfo ci) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this);
        if (clippingPlayer.isClipping() && this.input.hasForwardImpulse()) this.setSprinting(true);
    }

    /**
     * Fixes underwater vision when clipping to be that of spectator's.
     */
    @ModifyArg(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(III)I",
            ordinal = 0
        ),
        index = 0
    )
    private int fixUnderwaterVision(int perTick) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this);
        return clippingPlayer.isClipping() ? perTick + (this.isSpectator() ? 0 : 10 - 1) : perTick;
    }

    /**
     * Resets flight speed when disabling flight, if configured.
     */
    @Inject(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;onUpdateAbilities()V",
            ordinal = 1,
            shift = At.Shift.BEFORE
        )
    )
    private void disableFlightIfConfigured(CallbackInfo ci) {
        if (NoClipClient.getConfig().flight.speedScrolling.resetSpeedOnClipOrFlight) {
            Abilities def = new Abilities();
            this.getAbilities().setFlyingSpeed(def.getFlyingSpeed());
        }
    }
}
