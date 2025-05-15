package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class EntityPredicatesMixin {
    /**
     * Adds an extra check to {@link EntityPredicates#VALID_LIVING_ENTITY} for clipping.
     * <p>This predicate is used when checking for players near spawners and dripstone landing.</p>
     */
    @Inject(method = "method_32878", at = @At("TAIL"), cancellable = true, remap = false)
    private static void onValidLivingEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            if (entity instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) cir.setReturnValue(false);
        }
    }

    @Inject(method = "method_24517", at = @At("TAIL"), cancellable = true, remap = false)
    private static void onExceptSpectator(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            if (entity instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) cir.setReturnValue(false);
        }
    }

    /**
     * Removes collision entirely from clipping players.
     *
     * @return
     */
    @WrapMethod(method = "canBePushedBy")
    private static Predicate<Entity> onCanBePushedBy(Entity entity, Operation<Predicate<Entity>> original) {
        if (entity instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return e -> false;
        }

        return original.call(entity);
    }
}
