package me.andante.noclip.api.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.andante.noclip.api.NoClip;
import me.andante.noclip.api.client.NoClipClient;
import me.andante.noclip.api.client.NoClipManager;
import me.andante.noclip.impl.client.NoClipClientImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.function.Predicate;

import static net.minecraft.util.math.MathHelper.abs;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.sin;

/**
 * Responsible for rendering an indicator on the hud of the player's current clipping state.
 * <p>The current instance used by the client can be obtained by {@link NoClipClientImpl#NOCLIP_HUD_RENDERER}.</p>
 */
@Environment(EnvType.CLIENT)
public class NoClipHudRenderer implements HudRenderCallback {
    public static final Identifier TEXTURE = new Identifier(NoClip.MOD_ID, "textures/gui/noclip.png");

    private long fade = -1;
    private String activeDebugLine;

    public NoClipHudRenderer() {}

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        RenderSystem.enableBlend();

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
        context.setShaderColor(1.0F, 1.0F, 1.0F, clamp(alpha, 0.0F, 1.0F));

        if (client.inGameHud.getDebugHud().shouldShowDebugHud()) {
            this.renderIcon(context, scaledWidth - 18 - (client.textRenderer.getWidth(this.activeDebugLine) + 4), client.textRenderer.fontHeight + 1);
        } else this.renderIcon(context, scaledWidth - 18 - 2, (2 + (hasStatusEffect ? 25 + (hasNonBeneficialEffect ? 25 + 1 : 0) : 0)));

        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.disableBlend();
    }

    public void renderIcon(DrawContext context, int x, int y) {
        context.drawTexture(TEXTURE, x, y, 0, 0, 18, 18, 18, 18);
    }

    public void setActiveDebugLine(String activeDebugLine) {
        this.activeDebugLine = activeDebugLine;
    }
}
