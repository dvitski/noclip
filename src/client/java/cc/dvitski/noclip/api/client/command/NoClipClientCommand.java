package cc.dvitski.noclip.api.client.command;

import cc.dvitski.noclip.api.NoClip;
import cc.dvitski.noclip.api.client.NoClipClient;
import cc.dvitski.noclip.api.client.config.NoClipConfig;
import cc.dvitski.noclip.api.client.keybinding.NoClipKeyBindings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

@Environment(EnvType.CLIENT)
public interface NoClipClientCommand {
    String CONFIG_RELOAD_KEY = "text.noclip.config_reload_successful";
    SimpleCommandExceptionType CONFIG_SYNTAX_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("text." + NoClip.MOD_ID + ".config_reload_syntax_error"));
    SimpleCommandExceptionType TOGGLE_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("text." + NoClip.MOD_ID + ".not_toggle_error"));

    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(NoClip.MOD_ID)
            .executes(NoClipClientCommand::execute)
            .then(literal("config")
                .executes(NoClipClientCommand::executeConfig)
                .then(literal("reload")
                    .executes(NoClipClientCommand::executeConfigReload)
                )
            )
        );
    }

    static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        if (!NoClipClient.getConfig().keyBehaviors.noClip.toggles()) throw TOGGLE_EXCEPTION.create();
        NoClipKeyBindings.ACTIVATE_NOCLIP.setDown(true);
        return NoClipKeyBindings.ACTIVATE_NOCLIP.isDown() ? 1 : 0;
    }

    static int executeConfig(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        Minecraft client = source.getClient();
        client.schedule(() -> client.setScreen(NoClipConfig.createScreen(client.screen)));
        return 1;
    }

    static int executeConfigReload(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        if (!AutoConfig.getConfigHolder(NoClipConfig.class).load()) throw CONFIG_SYNTAX_EXCEPTION.create();
        context.getSource().sendFeedback(Component.translatable(CONFIG_RELOAD_KEY).setStyle(NoClipClient.getTextStyle()));
        return 1;
    }
}
