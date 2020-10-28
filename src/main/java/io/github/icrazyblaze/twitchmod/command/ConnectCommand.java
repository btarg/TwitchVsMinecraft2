package io.github.icrazyblaze.twitchmod.command;

import io.github.icrazyblaze.twitchmod.irc.TwitchConnectionHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ConnectCommand implements Command<CommandSource> {

    private static final ConnectCommand CMD = new ConnectCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("connect")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        TwitchConnectionHelper.tryConnect();
        return SINGLE_SUCCESS;
    }
}