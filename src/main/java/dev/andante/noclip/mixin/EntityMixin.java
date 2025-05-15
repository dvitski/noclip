package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * Makes pistons and shulker boxes ignore clipping entities.
     */
    @WrapMethod(method = "getPistonBehavior")
    private PistonBehavior onGetPistonBehavior(Operation<PistonBehavior> original) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return PistonBehavior.IGNORE;
        }

        return original.call();
    }

    /**
     * Cancels fire rendering when clipping.
     */
    @WrapMethod(method = "doesRenderOnFire")
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
    @WrapMethod(method = "isInSneakingPose")
    private boolean onIsInSneakingPose(Operation<Boolean> original) {
        Entity that = (Entity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return false;
        }

        return original.call();
    }
}
