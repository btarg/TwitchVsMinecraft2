package com.icrazyblaze.twitchmod.command;

import com.icrazyblaze.twitchmod.Main;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;


public class SetKeyCommand implements Command<CommandSource> {

    private static final SetKeyCommand CMD = new SetKeyCommand();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("key")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("keystring", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        // Get key and store in system properties
        String key = StringArgumentType.getString(context, "keystring");
        System.setProperty("twitch_oauth_key", key);

        // Update config
        Main.updateConfig();

        context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GOLD + "Set Twitch OAuth key"), false);
        return SINGLE_SUCCESS;

    }
}