package com.icrazyblaze.twitchmod.command;

import com.icrazyblaze.twitchmod.Main;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class TTVCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("ttv")
                .requires((source) -> source.hasPermissionLevel(2))
                        .executes(TTVCommand::checkArgs
                );
    }

    private static int checkArgs(CommandContext<CommandSource> ctx) throws IllegalArgumentException {

        Main.logger.info("FUCK");
        return Command.SINGLE_SUCCESS;

    }

}
