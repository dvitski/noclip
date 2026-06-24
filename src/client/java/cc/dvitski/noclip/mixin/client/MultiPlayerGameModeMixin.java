package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.NoClipManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    /**
     * Forces the player to fly if they are clipping.
     *
     * @return
     */
    @WrapMethod(method = "isSpectator")
    private boolean onIsFlyingLocked(Operation<Boolean> original) {
        if (NoClipManager.INSTANCE.isClipping() && NoClipClient.getConfig().flight.flyingLocked) {
            return true;
        }

        return original.call();
    }
}
