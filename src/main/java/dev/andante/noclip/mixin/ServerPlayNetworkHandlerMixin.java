package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Shadow private int floatingTicks;

    @WrapMethod(method = "getMaxAllowedFloatingTicks")
    private int onGetMaxAllowedFloatingTicks(Entity vehicle, Operation<Integer> original) {
        if (this.player instanceof ClippingEntity entity && entity.isClipping()) {
            this.floatingTicks = 0;
            return Integer.MAX_VALUE;
        }

        return original.call(vehicle);
    }
}
