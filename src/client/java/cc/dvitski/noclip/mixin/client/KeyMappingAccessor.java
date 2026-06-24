package cc.dvitski.noclip.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    @Accessor static Map<InputConstants.Key, KeyMapping> getALL() { throw new AssertionError(); }

    @Accessor InputConstants.Key getKey();

    @Accessor int getClickCount();
    @Accessor void setClickCount(int timesPressed);
}
