package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Makes the player not affected by splash potions when clipping.
     */
    @Inject(method = "isAffectedBySplashPotions", at = @At("HEAD"), cancellable = true)
    private void onIsAffectedBySplashPotions(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity that = (LivingEntity) (Object) this;
        if (that instanceof PlayerEntity player) {
            ClippingEntity clippingPlayer = ClippingEntity.cast(player);
            if (clippingPlayer.isClipping()) cir.setReturnValue(false);
        }
    }
}
