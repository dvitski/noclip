package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Makes the player not affected by splash potions when clipping.
     *
     * @return
     */
    @WrapMethod(method = "isAffectedBySplashPotions")
    private boolean onIsAffectedBySplashPotions(Operation<Boolean> original) {
        LivingEntity that = (LivingEntity) (Object) this;
        if (that instanceof PlayerEntity player) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (clippingPlayer.isClipping()) {
                return false;
            }
        }

        return original.call();
    }

    @WrapMethod(method = "isClimbing")
    private boolean onCanClimb(Operation<Boolean> original) {
        LivingEntity that = (LivingEntity) (Object) this;
        if (that instanceof ClippingEntity clippingEntity && clippingEntity.isClipping()) {
            return false;
        }

        return original.call();
    }
}
