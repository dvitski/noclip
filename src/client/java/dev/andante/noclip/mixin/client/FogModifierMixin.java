package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.andante.noclip.impl.ClippingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.client.renderer.fog.environment.PowderedSnowFogEnvironment;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin({LavaFogEnvironment.class, PowderedSnowFogEnvironment.class})
public class FogModifierMixin {
    @WrapOperation(method = "setupFog", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;isSpectator()Z"
    ))
    private boolean onIsSpectator(Entity instance, Operation<Boolean> original) {
        return original.call(instance) || (instance instanceof ClippingEntity clippingEntity && clippingEntity.isClipping());
    }
}
