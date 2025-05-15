package dev.andante.noclip.mixin;

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
     */
    @Inject(method = "canBePushedBy", at = @At("HEAD"), cancellable = true)
    private static void onCanBePushedBy(Entity entity, CallbackInfoReturnable<Predicate<Entity>> cir) {
        if (entity instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) cir.setReturnValue(e -> false);
    }
}
