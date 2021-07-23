package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;


public class ClearBlacklistCommand implements Command<CommandSource> {

    private static final ClearBlacklistCommand CMD = new ClearBlacklistCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("clearblacklist")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        ChatPicker.clearBlacklist();
        context.getSource().sendSuccess(new StringTextComponent("Blacklisted commands: " + ChatPicker.blacklist.toString()), false);

        return SINGLE_SUCCESS;

    }

}