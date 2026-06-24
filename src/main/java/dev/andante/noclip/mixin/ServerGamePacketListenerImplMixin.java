package dev.andante.noclip.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Shadow private int aboveGroundTickCount;

    @WrapMethod(method = "getMaximumFlyingTicks")
    private int onGetMaxAllowedFloatingTicks(Entity vehicle, Operation<Integer> original) {
        if (this.player instanceof ClippingEntity entity && entity.isClipping()) {
            this.aboveGroundTickCount = 0;
            return Integer.MAX_VALUE;
        }

        return original.call(vehicle);
    }
}
