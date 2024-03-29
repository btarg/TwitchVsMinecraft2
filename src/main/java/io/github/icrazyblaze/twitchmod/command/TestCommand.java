package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.concurrent.ThreadLocalRandom;


public class TestCommand implements Command<CommandSourceStack> {

    private static final TestCommand CMD = new TestCommand();
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("test")
                .requires(cs -> cs.hasPermission(0))
                .then(Commands.argument("runAndIgnoreBlacklist", BoolArgumentType.bool())
                        .then(Commands.argument("command", StringArgumentType.greedyString())

                                .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        // Get message and simulate command
        String message = StringArgumentType.getString(context, "command");

        ChatPicker.forceCommands = BoolArgumentType.getBool(context, "runAndIgnoreBlacklist");

        // Remove prefix
        if (message.startsWith(BotConfig.getCommandPrefix())) {
            message = message.substring(BotConfig.getCommandPrefix().length());
        }
        ChatPicker.checkChat(message, "TestUser" + rand.nextInt(0, 100));

        return SINGLE_SUCCESS;

    }
}