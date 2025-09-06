package dev.andante.noclip.api.client.render;

import dev.andante.noclip.api.NoClip;
import dev.andante.noclip.api.client.NoClipClient;
import dev.andante.noclip.api.client.NoClipManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

import java.util.Collection;
import java.util.function.Predicate;

import static net.minecraft.util.math.MathHelper.abs;
import static net.minecraft.util.math.MathHelper.sin;

/**
 * Responsible for rendering an indicator on the hud of the player's current clipping state.
 */
@Environment(EnvType.CLIENT)
public class NoClipHud implements HudElement {
    public static final Identifier TEXTURE = Identifier.of(NoClip.MOD_ID, "textures/hud/noclip.png");

    private long fade = -1;
    private String activeDebugLine;

    @Override
    public void render(DrawContext context, RenderTickCounter renderTickCounter) {
        if (!NoClipManager.INSTANCE.isClipping() || !NoClipClient.getConfig().display.hudIcon) {
            this.fade = -1;
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Window window =  client.getWindow();
        int scaledWidth = window.getScaledWidth();

        // calculate effects
        Collection<StatusEffectInstance> effects = client.player.getStatusEffects();
        boolean hasStatusEffect = effects.stream().anyMatch(StatusEffectInstance::shouldShowIcon);
        boolean hasNonBeneficialEffect = effects.stream()
                .filter(StatusEffectInstance::shouldShowIcon)
                .map(StatusEffectInstance::getEffectType)
                .map(RegistryEntry::value)
                .anyMatch(Predicate.not(StatusEffect::isBeneficial));

        // render
        long ms = Util.getMeasuringTimeMs();
        float interval = 1000f;
        if (this.fade == -1) this.fade = ms + (long) interval;
        float alpha = abs(sin((ms - this.fade) / interval)) + 0.2F;

        if (client.inGameHud.getDebugHud().shouldShowDebugHud()) {
            this.renderIcon(context, scaledWidth - 18 - (client.textRenderer.getWidth(this.activeDebugLine) + 4), client.textRenderer.fontHeight + 1, ColorHelper.getWhite(alpha));
        } else {
            this.renderIcon(context, scaledWidth - 18 - 2, (2 + (hasStatusEffect ? 25 + (hasNonBeneficialEffect ? 25 + 1 : 0) : 0)), ColorHelper.getWhite(alpha));
        }
    }

    public void renderIcon(DrawContext context, int x, int y, int color) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 18, 18, 18, 18, color);
    }

    public void setActiveDebugLine(String activeDebugLine) {
        this.activeDebugLine = activeDebugLine;
    }
}
