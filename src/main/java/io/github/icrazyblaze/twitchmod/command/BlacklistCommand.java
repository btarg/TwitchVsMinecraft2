package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;


public class BlacklistCommand implements Command<CommandSource> {

    private static final BlacklistCommand CMD = new BlacklistCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("blacklist")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD::showMessage)
                .then(Commands.argument("command", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        // Get message and simulate command
        String message = StringArgumentType.getString(context, "command");

        // Add to blacklist
        ChatPicker.addToBlacklist(message);

        showMessage(context);

        return SINGLE_SUCCESS;

    }

    public int showMessage(CommandContext<CommandSource> context) {

        context.getSource().sendFeedback(new StringTextComponent("Blacklisted commands: " + ChatPicker.blacklist.toString()), false);

        return SINGLE_SUCCESS;
    }
}