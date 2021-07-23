package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class QueueCommand implements Command<CommandSource> {

    private static final QueueCommand CMD = new QueueCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("queue")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        context.getSource().sendMessage(new TextComponent("Possible commands: " + ChatPicker.chatBuffer.toString()), Util.NIL_UUID);
        return SINGLE_SUCCESS;
    }
}