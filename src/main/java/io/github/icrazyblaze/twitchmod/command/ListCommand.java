package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.chat.ChatCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class ListCommand implements Command<CommandSourceStack> {

    private static final ListCommand CMD = new ListCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        context.getSource().sendSuccess(new TextComponent("Registered commands: " + ChatCommands.getRegisteredCommands()), false);

        return SINGLE_SUCCESS;
    }
}