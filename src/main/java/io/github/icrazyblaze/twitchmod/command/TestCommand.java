package io.github.icrazyblaze.twitchmod.command;

import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.util.BotConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;


public class TestCommand implements Command<CommandSource> {

    private static final TestCommand CMD = new TestCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("test")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("teststring", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        // Get message and simulate command
        String message = StringArgumentType.getString(context, "teststring");

        // Remove prefix
        if (message.startsWith(BotConfig.prefix)) {
            message = message.substring(BotConfig.prefix.length());
        }

        ChatPicker.forceCommands = true;
        ChatPicker.checkChat(message, "TestUser");

        return SINGLE_SUCCESS;

    }
}