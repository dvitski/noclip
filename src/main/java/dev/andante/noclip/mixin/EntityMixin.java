package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * Makes pistons and shulker boxes ignore clipping entities.
     */
    @WrapMethod(method = "getPistonPushReaction")
    private PushReaction onGetPistonBehavior(Operation<PushReaction> original) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return PushReaction.IGNORE;
        }

        return original.call();
    }

    /**
     * Cancels fire rendering when clipping.
     */
    @WrapMethod(method = "displayFireAnimation")
    private boolean onDoesRenderOnFire(Operation<Boolean> original) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return false;
        }

        return original.call();
    }

    /**
     * Cancels enabling sneak when clipping.
     */
    @WrapMethod(method = "isCrouching")
    private boolean onIsInSneakingPose(Operation<Boolean> original) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return false;
        }

        return original.call();
    }

    @WrapMethod(method = "getGravity")
    private double onGetFinalGravity(Operation<Double> original) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return 0.0;
        }

        return original.call();
    }
}
