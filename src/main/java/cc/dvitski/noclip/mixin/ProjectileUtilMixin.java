package cc.dvitski.noclip.mixin;

import cc.dvitski.noclip.impl.ClippingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
    /**
     * Prevents projectile collision with clipping entities.
     */
    @ModifyArg(
        method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            ordinal = 0
        ),
        index = 2
    )
    private static Predicate<? super Entity> addToPredicate(Predicate<? super Entity> predicate) {
        return predicate.and(o -> !(o instanceof ClippingEntity clippingEntity) || !clippingEntity.isClipping());
    }
}
