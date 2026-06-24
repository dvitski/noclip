package cc.dvitski.noclip.api.client.render;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.NoClipManager;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;
import java.util.function.Predicate;

import static net.minecraft.util.Mth.abs;
import static net.minecraft.util.Mth.sin;

/**
 * Responsible for rendering an indicator on the hud of the player's current clipping state.
 */
@Environment(EnvType.CLIENT)
public class NoClipHud implements HudElement {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(NoClip.MOD_ID, "textures/hud/noclip.png");

    private long fade = -1;
    private String activeDebugLine;

    @Override
    public void render(GuiGraphics context, DeltaTracker renderTickCounter) {
        if (!NoClipManager.INSTANCE.isClipping() || !NoClipClient.getConfig().display.hudIcon) {
            this.fade = -1;
            return;
        }

        Minecraft client = Minecraft.getInstance();
        Window window =  client.getWindow();
        int scaledWidth = window.getGuiScaledWidth();

        // calculate effects
        Collection<MobEffectInstance> effects = client.player.getActiveEffects();
        boolean hasStatusEffect = effects.stream().anyMatch(MobEffectInstance::showIcon);
        boolean hasNonBeneficialEffect = effects.stream()
                .filter(MobEffectInstance::showIcon)
                .map(MobEffectInstance::getEffect)
                .map(Holder::value)
                .anyMatch(Predicate.not(MobEffect::isBeneficial));

        // render
        long ms = Util.getMillis();
        float interval = 1000f;
        if (this.fade == -1) this.fade = ms + (long) interval;
        float alpha = abs(sin((ms - this.fade) / interval)) + 0.2F;

        if (client.gui.getDebugOverlay().showDebugScreen()) {
            this.renderIcon(context, scaledWidth - 18 - (client.font.width(this.activeDebugLine) + 4), client.font.lineHeight + 1, ARGB.white(alpha));
        } else {
            this.renderIcon(context, scaledWidth - 18 - 2, (2 + (hasStatusEffect ? 25 + (hasNonBeneficialEffect ? 25 + 1 : 0) : 0)), ARGB.white(alpha));
        }
    }

    public void renderIcon(GuiGraphics context, int x, int y, int color) {
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 18, 18, 18, 18, color);
    }

    public void setActiveDebugLine(String activeDebugLine) {
        this.activeDebugLine = activeDebugLine;
    }
}
