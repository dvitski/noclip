package me.andante.noclip.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import me.andante.noclip.impl.client.NoClipClientImpl;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci, @Local(ordinal = 0) LayeredDrawer baseDrawer) {
        baseDrawer.addLayer(NoClipClientImpl.NOCLIP_HUD_RENDERER);
    }
}
