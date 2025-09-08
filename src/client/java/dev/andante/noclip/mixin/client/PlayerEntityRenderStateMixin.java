package dev.andante.noclip.mixin.client;

import dev.andante.noclip.api.client.IPlayerClippingState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements IPlayerClippingState {
    @Unique
    private boolean isClipping = false;

    @Override
    public void setIsClipping(boolean value) {
        this.isClipping = value;
    }

    @Override
    public boolean getIsClipping() {
        return this.isClipping;
    }
}
