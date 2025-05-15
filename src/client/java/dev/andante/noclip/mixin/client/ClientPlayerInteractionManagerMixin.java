package dev.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.andante.noclip.api.client.NoClipClient;
import dev.andante.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    /**
     * Forces the player to fly if they are clipping.
     *
     * @return
     */
    @WrapMethod(method = "isFlyingLocked")
    private boolean onIsFlyingLocked(Operation<Boolean> original) {
        if (NoClipManager.INSTANCE.isClipping() && NoClipClient.getConfig().flight.flyingLocked) {
            return true;
        }

        return original.call();
    }
}
