package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * Makes pistons and shulker boxes ignore clipping entities.
     */
    @Inject(method = "getPistonBehavior", at = @At("HEAD"), cancellable = true)
    private void onGetPistonBehavior(CallbackInfoReturnable<PistonBehavior> cir) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) cir.setReturnValue(PistonBehavior.IGNORE);
    }

    /**
     * Cancels fire rendering when clipping.
     */
    @Inject(method = "doesRenderOnFire", at = @At("HEAD"), cancellable = true)
    private void onDoesRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) cir.setReturnValue(false);
    }

    /**
     * Cancels enabling sneak when clipping.
     */
    @Inject(method = "isInSneakingPose", at = @At("HEAD"), cancellable = true)
    private void onIsInSneakingPose(CallbackInfoReturnable<Boolean> cir) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) cir.setReturnValue(false);
    }
}
