package com.icrazyblaze.twitchmod.command;

import com.icrazyblaze.twitchmod.irc.BotConnection;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class DisconnectCommand implements Command<CommandSource> {

    private static final DisconnectCommand CMD = new DisconnectCommand();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("disconnect")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        if (BotConnection.isConnected()) {
            BotConnection.disconnectBot();
        } else {
            context.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Bot not connected."), false);
        }

        return SINGLE_SUCCESS;
    }
}