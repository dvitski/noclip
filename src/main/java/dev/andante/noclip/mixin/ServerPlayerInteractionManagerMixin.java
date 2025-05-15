package dev.andante.noclip.mixin;

import dev.andante.noclip.impl.ClippingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(method = "setGameMode", at = @At("TAIL"))
    private void onSetGameMode(GameMode gameMode, GameMode previousGameMode, CallbackInfo ci) {
        if (this.player instanceof ClippingEntity entity && entity.isClipping()) {
            PlayerAbilities abilities = this.player.getAbilities();
            abilities.flying = true;
            abilities.allowFlying = true;
            abilities.invulnerable = true;
            this.player.sendAbilitiesUpdate();
        }
    }
}
