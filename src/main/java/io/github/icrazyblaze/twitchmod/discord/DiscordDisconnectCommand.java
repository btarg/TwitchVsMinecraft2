package io.github.icrazyblaze.twitchmod.discord;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class DiscordDisconnectCommand implements Command<CommandSource> {

    private static final DiscordDisconnectCommand CMD = new DiscordDisconnectCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("disconnect")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        DiscordConnectionHelper.disconnectDiscord();

        return SINGLE_SUCCESS;
    }
}