package cc.dvitski.noclip.mixin.client;

import cc.dvitski.noclip.api.client.IPlayerClippingState;
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
