package io.github.icrazyblaze.twitchmod.command;

import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class QueueCommand implements Command<CommandSource> {

    private static final QueueCommand CMD = new QueueCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("queue")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        context.getSource().sendFeedback(new StringTextComponent("Possible commands: " + ChatPicker.newChats.toString()), false);
        return SINGLE_SUCCESS;
    }
}