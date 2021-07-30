package io.github.icrazyblaze.twitchmod.command.twitch;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.bots.irc.TwitchConnectionHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class TwitchDisconnectCommand implements Command<CommandSourceStack> {

    private static final TwitchDisconnectCommand CMD = new TwitchDisconnectCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("disconnect")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        if (TwitchConnectionHelper.isConnected()) {
            TwitchConnectionHelper.disconnectBot();
        } else {
            context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.status.disconnected_twitch"), false);
        }

        return SINGLE_SUCCESS;
    }
}