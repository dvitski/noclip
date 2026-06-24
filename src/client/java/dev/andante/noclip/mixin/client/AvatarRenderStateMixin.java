package dev.andante.noclip.mixin.client;

import dev.andante.noclip.api.client.IPlayerClippingState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements IPlayerClippingState {
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
