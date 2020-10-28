package io.github.icrazyblaze.twitchmod.command;

import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;


public class BlacklistCommand implements Command<CommandSource> {

    private static final BlacklistCommand CMD = new BlacklistCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("blacklist")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("toadd", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        // Get message and simulate command
        String message = StringArgumentType.getString(context, "toadd");

        // Add to blacklist
        ChatPicker.addToBlacklist(message);
        context.getSource().sendFeedback(new StringTextComponent("New blacklist: " + ChatPicker.blacklist.toString()), false);

        return SINGLE_SUCCESS;

    }
}