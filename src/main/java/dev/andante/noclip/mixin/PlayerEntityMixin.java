package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import dev.andante.noclip.api.NoClip;
import dev.andante.noclip.impl.ClippingEntity;
import dev.andante.noclip.impl.ClippingUpdatePacket;
import dev.andante.noclip.impl.PlayerAbilitiesAccess;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ClippingEntity {
    @Shadow
    @Final
    private PlayerAbilities abilities;
    @Unique
    private boolean clipping;
    @Unique
    private boolean lastCanClip;

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Unique
    @Override
    public boolean canClip() {
        return Permissions.check(this, "noclip", 2);
    }

    @Unique
    @Override
    public boolean isClipping() {
        return this.clipping;
    }

    @Unique
    @Override
    public void setClipping(boolean clipping) {
        this.clipping = clipping;
        this.intersectionChecked = !clipping;
    }

    @Unique
    @Override
    public void setLastCanClip(boolean lastCanClip) {
        this.lastCanClip = lastCanClip;
    }

    @Unique
    @Override
    public boolean isClippingInsideWall() {
        if (!this.isClipping())
            return false;

        // love this.
        this.noClip = false;
        boolean insideWall = this.isInsideWall();
        this.noClip = true;
        return insideWall;
    }

    /**
     * Attaches the player to their {@link #abilities}.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(World world, GameProfile profile, CallbackInfo ci) {
        PlayerEntity that = (PlayerEntity) (Object) this;
        ((PlayerAbilitiesAccess) this.abilities).setPlayer(that);
    }

    /**
     * Updates the player's clipping value based on our custom parameters.
     */
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;noClip:Z", shift = At.Shift.AFTER))
    private void onTickAfterNoClip(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            boolean canClip = this.canClip();
            boolean forceSync = this.lastCanClip != canClip;

            if (this.isClipping() && !canClip) {
                this.setClipping(false);
                forceSync = true;

                GameMode mode = player.interactionManager.getGameMode();
                mode.setAbilities(player.getAbilities());
                player.sendAbilitiesUpdate();
            }

            if (forceSync) {
                this.lastCanClip = canClip;
                ServerPlayNetworking.send(player, new ClippingUpdatePacket(this.isClipping(), canClip));
            }

            if (this.isClipping() && !canClip) {
                return;
            }
        }

        if (this.isClipping()) {
            this.noClip = true;
            this.setOnGround(false);
            this.fallDistance = 0;
        }
    }

    /**
     * Prevents the player's pose from updating when clipping.
     */
    @WrapMethod(method = "updatePose")
    private void onUpdatePose(Operation<Void> original) {
        if (this.isClipping()) {
            this.setPose(EntityPose.STANDING);
            return;
        }

        original.call();
    }

    /**
     * Ignores ground checks for block breaking speed when clipping.
     */
    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir, float speed) {
        if (this.isClipping())
            cir.setReturnValue(speed);
    }

    /**
     * Cancels any player collision code when clipping.
     */
    @WrapMethod(method = "collideWithEntity")
    private void onCollideWithEntity(Entity entity, Operation<Void> original) {
        if (this.isClipping()) {
            return;
        }

        original.call(entity);
    }

    /**
     * Cancels water interaction when clipping.
     */
    @WrapMethod(method = "onSwimmingStart")
    private void onOnSwimmingStart(Operation<Void> original) {
        if (this.isClipping()) {
            return;
        }

        original.call();
    }

    /* NBT */

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void onWriteCustomDataToNbt(WriteView view, CallbackInfo ci) {
        view.putBoolean(NoClip.NBT_KEY, this.isClipping());
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void onReadCustomDataFromNbt(ReadView view, CallbackInfo ci) {
        this.setClipping(view.getBoolean(NoClip.NBT_KEY, false));
    }
}
