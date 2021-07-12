package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.bots.irc.TwitchConnectionHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class TwitchConnectCommand implements Command<CommandSource> {

    private static final TwitchConnectCommand CMD = new TwitchConnectCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("connect")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        TwitchConnectionHelper.login();
        return SINGLE_SUCCESS;
    }
}