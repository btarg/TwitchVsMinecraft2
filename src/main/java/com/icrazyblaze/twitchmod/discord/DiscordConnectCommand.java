package com.icrazyblaze.twitchmod.discord;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import javax.security.auth.login.LoginException;

public class DiscordConnectCommand implements Command<CommandSource> {

    private static final DiscordConnectCommand CMD = new DiscordConnectCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("connect")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        DiscordConnection.login();
        return SINGLE_SUCCESS;
    }
}