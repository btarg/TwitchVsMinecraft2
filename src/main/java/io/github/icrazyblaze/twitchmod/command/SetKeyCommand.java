package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.util.SecretFileHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;


public class SetKeyCommand implements Command<CommandSource> {

    private static final SetKeyCommand CMD = new SetKeyCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("key")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("keystring", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        // Get key and store in system properties
        String key = StringArgumentType.getString(context, "keystring");

        // UPDATE: check if key is valid (starts with oauth and is 30 characters long)
        // This command should be more idiot-proof now.
        if (key.startsWith("oauth:") && key.length() == 36) {

            //System.setProperty("twitch_oauth_key", key);
            SecretFileHelper.setTwitchKey(key);

        } else {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(() -> ""), () -> "Invalid OAuth key.");
        }

        // Update config
        ConfigManager.updateFromConfig();

        context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GOLD + "Twitch OAuth key set. Use /ttv connect to start!"), false);
        return SINGLE_SUCCESS;

    }
}