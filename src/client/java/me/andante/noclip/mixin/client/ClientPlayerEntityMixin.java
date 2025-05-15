package me.andante.noclip.mixin.client;

import com.mojang.authlib.GameProfile;
import me.andante.noclip.api.client.NoClipClient;
import me.andante.noclip.api.client.NoClipManager;
import me.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow public Input input;

    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    /**
     * Updates player clipping value based on set/received client value.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler handler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo ci) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this);
        clippingPlayer.setClipping(NoClipManager.INSTANCE.isClipping());
    }

    /**
     * Cancels water submersion effects when clipping.
     */
    @Inject(
        method = "updateWaterSubmersionState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;updateWaterSubmersionState()Z",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void onUpdateWaterSubmersionState(CallbackInfoReturnable<Boolean> cir) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this);
        if (clippingPlayer.isClipping()) cir.setReturnValue(this.isSubmergedInWater);
    }

    /**
     * Prevents the player from having their sprinting stopped when clipping through water.
     */
    @Inject(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V",
            ordinal = 3,
            shift = At.Shift.AFTER
        )
    )
    private void preventStopSprinting(CallbackInfo ci) {
        ClippingEntity clippingPlayer = ClippingEntity.cast(this);
        if (clippingPlayer.isClipping() && this.input.hasForwardMovement()) this.setSprinting(true);
    }

    /**
     * Fixes underwater vision when clipping to be that of spectator's.
     */
    @ModifyArg(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I",
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
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendAbilitiesUpdate()V",
            ordinal = 1,
            shift = At.Shift.BEFORE
        )
    )
    private void disableFlightIfConfigured(CallbackInfo ci) {
        if (NoClipClient.getConfig().flight.speedScrolling.resetSpeedOnClipOrFlight) {
            PlayerAbilities def = new PlayerAbilities();
            this.getAbilities().setFlySpeed(def.getFlySpeed());
        }
    }
}
