package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ListCommand implements Command<CommandSource> {

    private static final ListCommand CMD = new ListCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("allcommands")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        context.getSource().sendFeedback(new StringTextComponent("Registered commands: " + ChatPicker.getRegisteredCommands()), false);

        return SINGLE_SUCCESS;
    }
}