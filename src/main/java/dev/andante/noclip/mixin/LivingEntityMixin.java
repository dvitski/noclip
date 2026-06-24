package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    /**
     * Makes the player not affected by splash potions when clipping.
     *
     * @return
     */
    @WrapMethod(method = "isAffectedByPotions")
    private boolean onIsAffectedBySplashPotions(Operation<Boolean> original) {
        LivingEntity that = (LivingEntity) (Object) this;
        if (that instanceof Player player) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (clippingPlayer.isClipping()) {
                return false;
            }
        }

        return original.call();
    }

    @WrapMethod(method = "onClimbable")
    private boolean onCanClimb(Operation<Boolean> original) {
        LivingEntity that = (LivingEntity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return false;
        }

        return original.call();
    }
}
