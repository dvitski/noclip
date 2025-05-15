package me.andante.noclip.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor static Map<InputUtil.Key, KeyBinding> getKEY_TO_BINDINGS() { throw new AssertionError(); }

    @Accessor InputUtil.Key getBoundKey();

    @Accessor void setPressed(boolean pressed);

    @Accessor int getTimesPressed();
    @Accessor void setTimesPressed(int timesPressed);
}
