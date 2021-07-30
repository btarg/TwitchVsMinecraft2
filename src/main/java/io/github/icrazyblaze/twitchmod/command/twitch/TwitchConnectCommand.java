package io.github.icrazyblaze.twitchmod.command.twitch;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.bots.irc.TwitchConnectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class TwitchConnectCommand implements Command<CommandSourceStack> {

    private static final TwitchConnectCommand CMD = new TwitchConnectCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("connect")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {

        boolean attempt = TwitchConnectionHelper.login();

        if (!attempt) {
            context.getSource().sendFailure(new TranslatableComponent("exception.twitchmod.no_twitch_key").withStyle(ChatFormatting.RED));
        }

        return SINGLE_SUCCESS;
    }
}