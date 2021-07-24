package io.github.icrazyblaze.twitchmod.bots.discord;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DiscordDisconnectCommand implements Command<CommandSourceStack> {

    private static final DiscordDisconnectCommand CMD = new DiscordDisconnectCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("disconnect")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {

        DiscordConnectionHelper.disconnectDiscord();

        return SINGLE_SUCCESS;
    }
}