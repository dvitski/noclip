package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public class EntitySelectorMixin {
    /**
     * Adds an extra check to {@link EntitySelector#LIVING_ENTITY_STILL_ALIVE} for clipping.
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
    @WrapMethod(method = "pushableBy")
    private static Predicate<Entity> onCanBePushedBy(Entity entity, Operation<Predicate<Entity>> original) {
        if (entity instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return e -> false;
        }

        return original.call(entity);
    }
}
