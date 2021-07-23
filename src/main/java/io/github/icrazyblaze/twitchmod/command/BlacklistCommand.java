package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;


public class BlacklistCommand implements com.mojang.brigadier.Command<CommandSource> {

    private static final BlacklistCommand CMD = new BlacklistCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("blacklist")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD::showMessage)
                .then(Commands.argument("command", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        // Get message and simulate command
        String message = StringArgumentType.getString(context, "command");

        // Add to blacklist
        ChatPicker.addToBlacklist(message);

        showMessage(context);

        return SINGLE_SUCCESS;

    }

    public int showMessage(CommandContext<CommandSource> context) {

        context.getSource().sendMessage(new TextComponent("Blacklisted commands: " + ChatPicker.blacklist.toString()), Util.NIL_UUID);

        return SINGLE_SUCCESS;
    }
}