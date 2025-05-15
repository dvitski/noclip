package dev.andante.noclip.mixin;

import com.mojang.authlib.GameProfile;
import dev.andante.noclip.api.NoClip;
import dev.andante.noclip.impl.ClippingEntity;
import dev.andante.noclip.impl.PlayerAbilitiesAccess;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
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
    @Shadow @Final private PlayerAbilities abilities;
    @Unique private boolean clipping;

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
    public boolean isClippingInsideWall() {
        if (!this.isClipping()) return false;

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
    private void onInit(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci) {
        PlayerEntity that = (PlayerEntity) (Object) this;
        ((PlayerAbilitiesAccess) this.abilities).setPlayer(that);
    }

    /**
     * Updates the player's clipping value based on our custom parameters.
     */
    @Inject(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/PlayerEntity;noClip:Z",
            shift = At.Shift.AFTER
        )
    )
    private void onTickAfterNoClip(CallbackInfo ci) {
        if (this.isClipping()) {
            this.noClip = true;
            this.setOnGround(false);
            this.fallDistance = 0;
        }
    }

    /**
     * Prevents the player's pose from updating when clipping.
     */
    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        if (this.isClipping()) {
            this.setPose(EntityPose.STANDING);
            ci.cancel();
        }
    }

    /**
     * Ignores ground checks for block breaking speed when clipping.
     */
    @Inject(
        method = "getBlockBreakingSpeed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir, float speed) {
        if (this.isClipping()) cir.setReturnValue(speed);
    }

    /**
     * Cancels any player collision code when clipping.
     */
    @Inject(method = "collideWithEntity", at = @At("HEAD"), cancellable = true)
    private void onCollideWithEntity(Entity entity, CallbackInfo ci) {
        if (this.isClipping()) ci.cancel();
    }

    /**
     * Cancels water interaction when clipping.
     */
    @Inject(method = "onSwimmingStart", at = @At("HEAD"), cancellable = true)
    private void onOnSwimmingStart(CallbackInfo ci) {
        if (this.isClipping()) ci.cancel();
    }

    /* NBT */

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(NoClip.NBT_KEY, this.isClipping());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setClipping(nbt.getBoolean(NoClip.NBT_KEY));
    }
}
