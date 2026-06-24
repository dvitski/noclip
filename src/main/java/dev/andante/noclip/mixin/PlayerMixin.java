package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import dev.andante.noclip.api.NoClip;
import dev.andante.noclip.impl.ClippingEntity;
import dev.andante.noclip.impl.PlayerAbilitiesAccess;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ClippingEntity {
    @Shadow @Final private Abilities abilities;
    @Unique private boolean clipping;

    private PlayerMixin(EntityType<? extends LivingEntity> type, Level world) {
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
        this.blocksBuilding = !clipping;
    }

    @Unique
    @Override
    public boolean isClippingInsideWall() {
        if (!this.isClipping()) return false;

        // love this.
        this.noPhysics = false;
        boolean insideWall = this.isInWall();
        this.noPhysics = true;
        return insideWall;
    }

    /**
     * Attaches the player to their {@link #abilities}.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Level world, GameProfile profile, CallbackInfo ci) {
        Player that = (Player) (Object) this;
        ((PlayerAbilitiesAccess) this.abilities).setPlayer(that);
    }

    /**
     * Updates the player's clipping value based on our custom parameters.
     */
    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Player;noPhysics:Z",
                    shift = At.Shift.AFTER,
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void onTickAfterNoClip(CallbackInfo ci) {
        if (this.isClipping()) {
            this.noPhysics = true;
            this.setOnGround(false);
            this.fallDistance = 0;
        }
    }

    /**
     * Prevents the player's pose from updating when clipping.
     */
    @WrapMethod(method = "updatePlayerPose")
    private void onUpdatePose(Operation<Void> original) {
        if (this.isClipping()) {
            this.setPose(Pose.STANDING);
            return;
        }

        original.call();
    }

    /**
     * Ignores ground checks for block breaking speed when clipping.
     */
    @Inject(
            method = "getDestroySpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir, @Local(ordinal = 0) float speed) {
        if (this.isClipping()) cir.setReturnValue(speed);
    }

    /**
     * Cancels any player collision code when clipping.
     */
    @WrapMethod(method = "touch")
    private void onCollideWithEntity(Entity entity, Operation<Void> original) {
        if (this.isClipping()) {
            return;
        }

        original.call(entity);
    }

    /**
     * Cancels water interaction when clipping.
     */
    @WrapMethod(method = "doWaterSplashEffect")
    private void onOnSwimmingStart(Operation<Void> original) {
        if (this.isClipping()) {
            return;
        }

        original.call();
    }

    /* NBT */

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onWriteCustomDataToNbt(ValueOutput view, CallbackInfo ci) {
        view.putBoolean(NoClip.NBT_KEY, this.isClipping());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadCustomDataFromNbt(ValueInput view, CallbackInfo ci) {
        this.setClipping(view.getBooleanOr(NoClip.NBT_KEY, false));
    }
}
